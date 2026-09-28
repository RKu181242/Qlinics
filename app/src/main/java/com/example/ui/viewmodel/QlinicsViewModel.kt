package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Hospital
import com.example.data.model.HospitalType
import com.example.data.model.NotificationItem
import com.example.data.model.QueuePatient
import com.example.data.model.QueueStatus
import com.example.data.model.Specialist
import com.example.data.model.UserRole
import com.example.data.model.VisitMode
import com.example.data.repository.QlinicsRepository
import com.example.data.repository.RecommendationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Screen {
    HOME,
    SPECIALIST_SEARCH,
    HOSPITAL_MAP,
    HOSPITAL_RECOMMENDATION,
    HOSPITAL_DETAILS,
    DOCTOR_LIST,
    BOOKING,
    CONFIRMATION,
    LIVE_QUEUE,
    DIRECTIONS,
    APPOINTMENTS,
    PROFILE
}

class QlinicsViewModel(application: Application) : AndroidViewModel(application) {
    val repository = QlinicsRepository(application)

    // Current Role
    private val _currentRole = MutableStateFlow(UserRole.PATIENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val navStack = mutableListOf<Screen>()

    // Current Selections
    private val _selectedSpecialist = MutableStateFlow<Specialist?>(null)
    val selectedSpecialist: StateFlow<Specialist?> = _selectedSpecialist.asStateFlow()

    private val _selectedHospital = MutableStateFlow<Hospital?>(null)
    val selectedHospital: StateFlow<Hospital?> = _selectedHospital.asStateFlow()

    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> = _selectedDoctor.asStateFlow()

    private val _recommendation = MutableStateFlow<RecommendationResult?>(null)
    val recommendation: StateFlow<RecommendationResult?> = _recommendation.asStateFlow()

    private val _lastBookedAppointment = MutableStateFlow<Appointment?>(null)
    val lastBookedAppointment: StateFlow<Appointment?> = _lastBookedAppointment.asStateFlow()

    private val _directionsHospital = MutableStateFlow<Hospital?>(null)
    val directionsHospital: StateFlow<Hospital?> = _directionsHospital.asStateFlow()

    // Booking form state
    private val _bookingDate = MutableStateFlow(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()))
    val bookingDate: StateFlow<String> = _bookingDate.asStateFlow()

    private val _bookingTime = MutableStateFlow("10:30 AM")
    val bookingTime: StateFlow<String> = _bookingTime.asStateFlow()

    private val _bookingVisitMode = MutableStateFlow(VisitMode.LIVE_QUEUE)
    val bookingVisitMode: StateFlow<VisitMode> = _bookingVisitMode.asStateFlow()

    // Filters and Search
    private val _homeSearchQuery = MutableStateFlow("")
    val homeSearchQuery: StateFlow<String> = _homeSearchQuery.asStateFlow()

    private val _specialistSearchQuery = MutableStateFlow("")
    val specialistSearchQuery: StateFlow<String> = _specialistSearchQuery.asStateFlow()

    private val _mapSearchQuery = MutableStateFlow("")
    val mapSearchQuery: StateFlow<String> = _mapSearchQuery.asStateFlow()

    private val _mapFilter = MutableStateFlow(HospitalType.ALL)
    val mapFilter: StateFlow<HospitalType> = _mapFilter.asStateFlow()

    // Indian Cities Selection
    private val _selectedCity = MutableStateFlow("All Cities")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    // Google Maps Style Controls
    private val _mapZoom = MutableStateFlow(1.0f)
    val mapZoom: StateFlow<Float> = _mapZoom.asStateFlow()

    private val _mapStyle = MutableStateFlow("Normal") // Normal, Satellite, Terrain
    val mapStyle: StateFlow<String> = _mapStyle.asStateFlow()

    // Banner message
    private val _bannerMessage = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val bannerMessage: SharedFlow<String> = _bannerMessage.asSharedFlow()

    // Active Patient token being tracked
    val patientToken = "A-27"

    // Data Flows from Repository
    val priyaQueue: StateFlow<List<QueuePatient>> = repository.getQueueForDoctor("doc_priya")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQueuePatients: StateFlow<List<QueuePatient>> = repository.getAllQueuePatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<Appointment>> = repository.getAppointmentsForPatient("p_rehan")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Patient's active queue position
    val activeQueuePatient: StateFlow<QueuePatient?> = priyaQueue.combine(priyaQueue) { list, _ ->
        list.find { it.tokenNumber == patientToken }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Calculated people ahead and ETA for A-27
    val queueCalculations = priyaQueue.combine(priyaQueue) { queue, _ ->
        calculateQueueMetrics(queue, patientToken)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(3, 9, QueueStatus.WAITING))

    init {
        // Collect live alerts to banner
        viewModelScope.launch {
            repository.liveAlertEvents.collect { event ->
                _bannerMessage.emit(event)
            }
        }
    }

    private fun calculateQueueMetrics(queue: List<QueuePatient>, token: String): Triple<Int, Int, QueueStatus> {
        val patient = queue.find { it.tokenNumber == token }
        if (patient == null) return Triple(0, 0, QueueStatus.COMPLETED)

        val currentStatus = try {
            QueueStatus.valueOf(patient.status)
        } catch (e: Exception) {
            QueueStatus.WAITING
        }

        if (currentStatus == QueueStatus.COMPLETED || currentStatus == QueueStatus.CANCELLED) {
            return Triple(0, 0, currentStatus)
        }
        if (currentStatus == QueueStatus.WITH_DOCTOR || currentStatus == QueueStatus.CALLED) {
            return Triple(0, 0, currentStatus)
        }

        // Count how many patients in queue are ahead of this patient and not completed/cancelled
        val targetSeq = patient.sequenceNumber
        val aheadCount = queue.count {
            it.sequenceNumber < targetSeq &&
                    (it.status == QueueStatus.WAITING.name ||
                            it.status == QueueStatus.WITH_DOCTOR.name ||
                            it.status == QueueStatus.CALLED.name ||
                            it.status == QueueStatus.ALMOST_YOUR_TURN.name)
        }

        val avgDuration = 3 // 3 mins per patient
        val estWait = aheadCount * avgDuration

        val derivedStatus = when {
            aheadCount <= 1 -> QueueStatus.ALMOST_YOUR_TURN
            else -> QueueStatus.WAITING
        }

        return Triple(aheadCount, estWait, if (currentStatus == QueueStatus.ALMOST_YOUR_TURN) QueueStatus.ALMOST_YOUR_TURN else derivedStatus)
    }

    // Role Switching
    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    // Navigation
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            navStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (navStack.isNotEmpty()) {
            _currentScreen.value = navStack.removeAt(navStack.size - 1)
            true
        } else {
            if (_currentScreen.value != Screen.HOME) {
                _currentScreen.value = Screen.HOME
                true
            } else {
                false
            }
        }
    }

    // Specialist Selection & Smart Recommendation
    fun selectSpecialist(specialist: Specialist) {
        _selectedSpecialist.value = specialist
        val rec = repository.getRecommendationForSpecialist(specialist.name, _selectedCity.value)
        _recommendation.value = rec
        navigateTo(Screen.HOSPITAL_RECOMMENDATION)
    }

    // City Selection
    fun selectCity(city: String) {
        _selectedCity.value = city
        _selectedSpecialist.value?.let { spec ->
            _recommendation.value = repository.getRecommendationForSpecialist(spec.name, city)
        }
    }

    // Map Controls
    fun zoomIn() {
        _mapZoom.value = (_mapZoom.value + 0.35f).coerceAtMost(2.5f)
    }

    fun zoomOut() {
        _mapZoom.value = (_mapZoom.value - 0.35f).coerceAtLeast(0.7f)
    }

    fun recenterMap() {
        _mapZoom.value = 1.0f
    }

    fun setMapStyle(style: String) {
        _mapStyle.value = style
    }

    // Hospital Selection
    fun selectHospital(hospital: Hospital) {
        _selectedHospital.value = hospital
        navigateTo(Screen.HOSPITAL_DETAILS)
    }

    fun selectDoctor(doctor: Doctor) {
        _selectedDoctor.value = doctor
        navigateTo(Screen.BOOKING)
    }

    fun openDirections(hospital: Hospital) {
        _directionsHospital.value = hospital
        navigateTo(Screen.DIRECTIONS)
    }

    // Booking updates
    fun updateBookingDate(date: String) { _bookingDate.value = date }
    fun updateBookingTime(time: String) { _bookingTime.value = time }
    fun updateBookingMode(mode: VisitMode) { _bookingVisitMode.value = mode }

    fun confirmBooking() {
        val doctor = _selectedDoctor.value ?: repository.doctors.first()
        val hospital = _selectedHospital.value ?: repository.hospitals.first()
        viewModelScope.launch {
            val appt = repository.bookAppointment(
                doctor = doctor,
                hospital = hospital,
                date = _bookingDate.value,
                timeSlot = _bookingTime.value,
                visitMode = _bookingVisitMode.value
            )
            _lastBookedAppointment.value = appt
            navigateTo(Screen.CONFIRMATION)
        }
    }

    fun cancelAppointment(id: String) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
        }
    }

    fun rescheduleAppointment(id: String, newDate: String, newTime: String) {
        viewModelScope.launch {
            repository.rescheduleAppointment(id, newDate, newTime)
        }
    }

    // Queue actions for Receptionist & Doctor
    fun callNextPatient(doctorId: String = "doc_priya") {
        viewModelScope.launch {
            repository.receptionistCallNext(doctorId)
        }
    }

    fun recallPatient(token: String) {
        viewModelScope.launch {
            repository.receptionistRecallPatient(token)
        }
    }

    fun skipPatient(token: String) {
        viewModelScope.launch {
            repository.receptionistSkipPatient(token)
        }
    }

    fun doctorStartConsultation(token: String, doctorId: String = "doc_priya") {
        viewModelScope.launch {
            repository.doctorStartConsultation(doctorId, token)
        }
    }

    fun doctorCompleteConsultation(token: String, doctorId: String = "doc_priya") {
        viewModelScope.launch {
            repository.doctorCompleteConsultation(doctorId, token)
        }
    }

    fun addWalkInPatient(doctorId: String, name: String, phone: String, complaint: String) {
        viewModelScope.launch {
            repository.addWalkInPatient(doctorId, name, phone, complaint)
        }
    }

    fun resetDemoQueue() {
        viewModelScope.launch {
            repository.resetDemoQueue()
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    // Search & Filter setters
    fun setHomeSearch(query: String) { _homeSearchQuery.value = query }
    fun setSpecialistSearch(query: String) { _specialistSearchQuery.value = query }
    fun setMapSearch(query: String) { _mapSearchQuery.value = query }
    fun setMapFilter(filter: HospitalType) { _mapFilter.value = filter }
}
