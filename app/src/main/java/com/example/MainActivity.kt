package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.model.UserRole
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MedicalBackground
import com.example.ui.theme.QlinicsTheme
import com.example.ui.viewmodel.QlinicsViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: QlinicsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QlinicsTheme {
                QlinicsApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun QlinicsApp(viewModel: QlinicsViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val queueCalculations by viewModel.queueCalculations.collectAsState()
    val (aheadCount, _, queueStatus) = queueCalculations

    var showNotificationSheet by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf<String?>(null) }

    val unreadCount = remember(notifications) { notifications.count { !it.isRead } }

    // Listen to live alerts
    LaunchedEffect(Unit) {
        viewModel.bannerMessage.collect { msg ->
            bannerMessage = msg
            delay(4000)
            bannerMessage = null
        }
    }

    val isPatient = currentRole == UserRole.PATIENT
    val showBack = isPatient && currentScreen != Screen.HOME

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MedicalBackground,
        topBar = {
            QlinicsTopBar(
                currentRole = currentRole,
                onRoleSelected = { role -> viewModel.setRole(role) },
                unreadNotificationCount = unreadCount,
                onOpenNotifications = { showNotificationSheet = true },
                showBackButton = showBack,
                onBackClick = { viewModel.navigateBack() }
            )
        },
        bottomBar = {
            if (isPatient) {
                PatientBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    hasActiveQueue = queueStatus != com.example.data.model.QueueStatus.COMPLETED &&
                            queueStatus != com.example.data.model.QueueStatus.CANCELLED
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen switcher with animated transitions
            AnimatedContent(
                targetState = Pair(currentRole, currentScreen),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "role_screen_transition"
            ) { (role, screen) ->
                when (role) {
                    UserRole.RECEPTIONIST -> {
                        ReceptionistDashboardScreen(viewModel = viewModel)
                    }
                    UserRole.DOCTOR -> {
                        DoctorDashboardScreen(viewModel = viewModel)
                    }
                    UserRole.PATIENT -> {
                        when (screen) {
                            Screen.HOME -> PatientHomeScreen(viewModel = viewModel)
                            Screen.SPECIALIST_SEARCH -> SpecialistSearchScreen(viewModel = viewModel)
                            Screen.HOSPITAL_MAP -> HospitalMapScreen(viewModel = viewModel)
                            Screen.HOSPITAL_RECOMMENDATION -> HospitalRecommendationScreen(viewModel = viewModel)
                            Screen.HOSPITAL_DETAILS -> HospitalDetailsScreen(viewModel = viewModel)
                            Screen.DOCTOR_LIST -> DoctorListScreen(viewModel = viewModel)
                            Screen.BOOKING -> BookingScreen(viewModel = viewModel)
                            Screen.CONFIRMATION -> AppointmentConfirmationScreen(viewModel = viewModel)
                            Screen.LIVE_QUEUE -> LiveQueueTrackerScreen(viewModel = viewModel)
                            Screen.DIRECTIONS -> DirectionsScreen(viewModel = viewModel)
                            Screen.APPOINTMENTS -> AppointmentsScreen(viewModel = viewModel)
                            Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }

            // Real-time floating Notification Banner
            LiveNotificationBanner(
                message = bannerMessage,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Notifications Dialog / Sheet
    if (showNotificationSheet) {
        NotificationSheet(
            notifications = notifications,
            onDismiss = { showNotificationSheet = false },
            onNotificationClick = { item ->
                viewModel.markNotificationAsRead(item.id)
                showNotificationSheet = false
                if (item.type == "ROOM_CALL" || item.type == "TURN_APPROACHING" || item.type == "QUEUE_UPDATE") {
                    viewModel.setRole(UserRole.PATIENT)
                    viewModel.navigateTo(Screen.LIVE_QUEUE)
                }
            }
        )
    }
}
