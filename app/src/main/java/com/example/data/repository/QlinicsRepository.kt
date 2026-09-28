package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.Hospital
import com.example.data.model.HospitalType
import com.example.data.model.NotificationItem
import com.example.data.model.QueuePatient
import com.example.data.model.QueueStatus
import com.example.data.model.Specialist
import com.example.data.model.VisitMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class RecommendationResult(
    val hospital: Hospital,
    val score: Int,
    val reasons: List<String>,
    val specialistName: String,
    val doctorCount: Int
)

class QlinicsRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val appointmentDao = database.appointmentDao()
    private val queueDao = database.queueDao()
    private val notificationDao = database.notificationDao()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    val indianCities: List<String> = listOf(
        "All Cities",
        "Delhi NCR",
        "Mumbai",
        "Bengaluru",
        "Hyderabad",
        "Chennai",
        "Kolkata",
        "Pune",
        "Ahmedabad",
        "Jaipur",
        "Lucknow",
        "Chandigarh",
        "Kochi",
        "Indore",
        "Bhopal"
    )

    // Extensive Catalog of Top Hospitals in India
    val hospitals: List<Hospital> = listOf(
        // DELHI NCR
        Hospital(
            id = "hosp_sunrise",
            name = "Sunrise Multi-Speciality Hospital",
            city = "Delhi NCR",
            state = "Delhi",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.6,
            reviewsCount = "2.3k",
            distanceKm = 2.1,
            waitingCount = 6,
            estWaitMinutes = 18,
            emergencyAvailable = true,
            address = "Sector 62, Near Metro Station, Saket, New Delhi",
            phone = "+91 11 4123 4567",
            landmark = "Opposite City Mall, Saket Metro Gate 3",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Dentistry", "Orthopedics", "Ophthalmology", "Pediatrics", "Neurology", "General Medicine"),
            facilities = listOf("Advanced Cath Lab", "Cardiac ICU", "24/7 Trauma Care", "Automated Queue Tokens", "In-House Pharmacy"),
            lat = 28.5244,
            lng = 77.2066,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_aiims_delhi",
            name = "AIIMS New Delhi",
            city = "Delhi NCR",
            state = "Delhi",
            type = HospitalType.GOVERNMENT,
            rating = 4.8,
            reviewsCount = "12.4k",
            distanceKm = 4.2,
            waitingCount = 18,
            estWaitMinutes = 45,
            emergencyAvailable = true,
            address = "Sri Aurobindo Marg, Ansari Nagar, New Delhi",
            phone = "+91 11 2658 8500",
            landmark = "AIIMS Metro Station (Yellow Line)",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 04:30 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "General Medicine", "Pediatrics", "Oncology"),
            facilities = listOf("Premier National Apex Medical Institute", "Subsidized Care", "Emergency Trauma Center", "Research Labs"),
            lat = 28.5672,
            lng = 77.2100,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_medanta_gurugram",
            name = "Medanta - The Medicity",
            city = "Delhi NCR",
            state = "Haryana",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.9,
            reviewsCount = "8.6k",
            distanceKm = 6.4,
            waitingCount = 7,
            estWaitMinutes = 20,
            emergencyAvailable = true,
            address = "CH Bakhtawar Singh Rd, Medicity, Islampur Colony, Sector 38, Gurugram",
            phone = "+91 124 414 1414",
            landmark = "Near Rajiv Chowk, NH 48, Gurugram",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Gastroenterology", "Pulmonology", "Oncology"),
            facilities = listOf("Heart Institute Led by Dr. Naresh Trehan", "Robotic Da Vinci System", "Air Ambulance", "Organ Transplant"),
            lat = 28.4390,
            lng = 77.0425,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_max_saket",
            name = "Max Super Speciality Hospital",
            city = "Delhi NCR",
            state = "Delhi",
            type = HospitalType.PRIVATE,
            rating = 4.7,
            reviewsCount = "4.1k",
            distanceKm = 2.8,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "1, 2 Press Enclave Marg, Saket, New Delhi",
            phone = "+91 11 2651 5050",
            landmark = "Near Select Citywalk Mall",
            openingHours = "Open 24 Hours • OPD: 09:00 AM - 07:00 PM",
            specialties = listOf("Cardiology", "Oncology", "Neurology", "Orthopedics", "Gastroenterology"),
            facilities = listOf("Robotic Surgery Suite", "JCI Accredited", "Comprehensive Cancer Center", "Valet Parking"),
            lat = 28.5284,
            lng = 77.2120,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_gangaram_delhi",
            name = "Sir Ganga Ram Hospital",
            city = "Delhi NCR",
            state = "Delhi",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "5.3k",
            distanceKm = 5.0,
            waitingCount = 9,
            estWaitMinutes = 25,
            emergencyAvailable = true,
            address = "Rajinder Nagar, New Delhi",
            phone = "+91 11 2575 0000",
            landmark = "Near Karol Bagh Metro",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 07:00 PM",
            specialties = listOf("Cardiology", "Nephrology", "General Medicine", "Pediatrics"),
            facilities = listOf("NABH Accredited", "Renowned Nephrology Unit", "Extensive OPD Complex"),
            lat = 28.6385,
            lng = 77.1895,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_fortis_escorts",
            name = "Fortis Escorts Heart Institute",
            city = "Delhi NCR",
            state = "Delhi",
            type = HospitalType.PRIVATE,
            rating = 4.9,
            reviewsCount = "3.8k",
            distanceKm = 5.6,
            waitingCount = 7,
            estWaitMinutes = 20,
            emergencyAvailable = true,
            address = "Okhla Road, Sukhdev Vihar Metro, New Delhi",
            phone = "+91 11 4713 5000",
            landmark = "Adjacent to Sukhdev Vihar Metro",
            openingHours = "Open 24 Hours • Heart OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Pulmonology", "Nephrology"),
            facilities = listOf("Asia's Leading Heart Institute", "Pediatric Cardiology", "TAVI & Valve Clinic"),
            lat = 28.5604,
            lng = 77.2798,
            imageDrawableName = "hospital_sunrise"
        ),

        // MUMBAI
        Hospital(
            id = "hosp_kokilaben_mumbai",
            name = "Kokilaben Dhirubhai Ambani Hospital",
            city = "Mumbai",
            state = "Maharashtra",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "5.6k",
            distanceKm = 3.2,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Rao Saheb Achutrao Patwardhan Marg, Four Bungalows, Andheri West, Mumbai",
            phone = "+91 22 4269 6969",
            landmark = "Near Versova Metro Station",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Oncology", "Dentistry"),
            facilities = listOf("Full Time Specialist System", "Children's Heart Center", "Robotic Rehabilitation"),
            lat = 19.1314,
            lng = 72.8252,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_lilavati_mumbai",
            name = "Lilavati Hospital & Research Centre",
            city = "Mumbai",
            state = "Maharashtra",
            type = HospitalType.PRIVATE,
            rating = 4.7,
            reviewsCount = "4.2k",
            distanceKm = 4.5,
            waitingCount = 8,
            estWaitMinutes = 24,
            emergencyAvailable = true,
            address = "A-791, Bandra Reclamation, Bandra West, Mumbai",
            phone = "+91 22 2675 1000",
            landmark = "Bandra Reclamation, Near Sea Link",
            openingHours = "Open 24 Hours • OPD: 09:00 AM - 06:00 PM",
            specialties = listOf("Cardiology", "General Medicine", "Gynecology", "Orthopedics"),
            facilities = listOf("Multi-Super Speciality", "NABH Accredited", "Advanced Intensive Cardiac Unit"),
            lat = 19.0514,
            lng = 72.8290,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_hinduja_mumbai",
            name = "P. D. Hinduja Hospital",
            city = "Mumbai",
            state = "Maharashtra",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "3.9k",
            distanceKm = 5.1,
            waitingCount = 6,
            estWaitMinutes = 18,
            emergencyAvailable = true,
            address = "Veer Savarkar Marg, Mahim West, Mumbai",
            phone = "+91 22 2445 1515",
            landmark = "Near Mahim Railway Station",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Nephrology", "Pulmonology"),
            facilities = listOf("CAP Accredited Pathology", "Comprehensive Cardiac Care", "Day Care Surgery"),
            lat = 19.0330,
            lng = 72.8397,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_breachcandy_mumbai",
            name = "Breach Candy Hospital",
            city = "Mumbai",
            state = "Maharashtra",
            type = HospitalType.PRIVATE,
            rating = 4.8,
            reviewsCount = "3.1k",
            distanceKm = 6.2,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "60 A, Bhulabhai Desai Road, Cumballa Hill, Mumbai",
            phone = "+91 22 2366 7788",
            landmark = "Near Mahalaxmi Temple, South Mumbai",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Dentistry", "Orthopedics", "Gynecology"),
            facilities = listOf("Prestigious South Mumbai Medical Hub", "Boutique Executive Rooms", "Advanced Endoscopy"),
            lat = 18.9723,
            lng = 72.8058,
            imageDrawableName = "hospital_sunrise"
        ),

        // BENGALURU
        Hospital(
            id = "hosp_manipal_blr",
            name = "Manipal Hospital Old Airport Road",
            city = "Bengaluru",
            state = "Karnataka",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "6.1k",
            distanceKm = 2.9,
            waitingCount = 6,
            estWaitMinutes = 18,
            emergencyAvailable = true,
            address = "98, HAL Old Airport Rd, Kodihalli, Bengaluru",
            phone = "+91 80 2502 4444",
            landmark = "Near Leela Palace, HAL Airport Road",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Orthopedics", "Dentistry", "Pediatrics", "Neurology", "General Medicine"),
            facilities = listOf("Level 1 Trauma Care", "Comprehensive Heart Center", "Robotic Joint Replacement"),
            lat = 12.9592,
            lng = 77.6499,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_narayana_blr",
            name = "Narayana Institute of Cardiac Sciences",
            city = "Bengaluru",
            state = "Karnataka",
            type = HospitalType.PRIVATE,
            rating = 4.9,
            reviewsCount = "7.8k",
            distanceKm = 8.2,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "258/A, Bommasandra Industrial Area, Anekal Taluk, Bengaluru",
            phone = "+91 80 7122 2222",
            landmark = "Electronic City Hosur Road",
            openingHours = "Open 24 Hours • Heart OPD: 08:00 AM - 07:00 PM",
            specialties = listOf("Cardiology", "Pulmonology", "Vascular Surgery"),
            facilities = listOf("World's Largest Cardiac Care Center", "Thrombosis Research", "Subsidized Pediatric Surgeries"),
            lat = 12.8122,
            lng = 77.6917,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_fortis_blr",
            name = "Fortis Hospital Bannerghatta",
            city = "Bengaluru",
            state = "Karnataka",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "4.9k",
            distanceKm = 5.3,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "154/9, Bannerghatta Main Rd, Opposite IIM-B, Bengaluru",
            phone = "+91 80 6621 4444",
            landmark = "Opposite IIM Bangalore",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Orthopedics", "Neurology", "Pediatrics"),
            facilities = listOf("Center of Excellence in Cardiology", "Robotic Joint Reconstruction", "Express Pharmacy"),
            lat = 12.8950,
            lng = 77.5990,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_aster_blr",
            name = "Aster CMI Hospital",
            city = "Bengaluru",
            state = "Karnataka",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "3.4k",
            distanceKm = 4.8,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "No. 43/42, Bellary Rd, NH 44, Sahakar Nagar, Hebbal, Bengaluru",
            phone = "+91 80 4342 0100",
            landmark = "Near Hebbal Flyover",
            openingHours = "Open 24 Hours • OPD: 09:00 AM - 07:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Pediatrics", "Gynecology"),
            facilities = listOf("Comprehensive Liver & Heart Institute", "Advanced Neonatal ICU"),
            lat = 13.0569,
            lng = 77.5922,
            imageDrawableName = "hospital_sunrise"
        ),

        // HYDERABAD
        Hospital(
            id = "hosp_apollo_hyd",
            name = "Apollo Health City",
            city = "Hyderabad",
            state = "Telangana",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "5.2k",
            distanceKm = 3.6,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Road No 72, Film Nagar, Jubilee Hills, Hyderabad",
            phone = "+91 40 2360 7777",
            landmark = "Jubilee Hills Check Post",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Dentistry", "General Medicine"),
            facilities = listOf("First Health City in Asia", "PET-CT & 3T MRI", "Dedicated Stroke Unit"),
            lat = 17.4239,
            lng = 78.4116,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_yashoda_hyd",
            name = "Yashoda Hospitals Somajiguda",
            city = "Hyderabad",
            state = "Telangana",
            type = HospitalType.PRIVATE,
            rating = 4.7,
            reviewsCount = "4.6k",
            distanceKm = 4.2,
            waitingCount = 7,
            estWaitMinutes = 20,
            emergencyAvailable = true,
            address = "Alexander Rd, Somajiguda, Raj Bhavan Rd, Hyderabad",
            phone = "+91 40 4567 4567",
            landmark = "Near Raj Bhavan",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Pulmonology", "Orthopedics", "Gastroenterology"),
            facilities = listOf("Cath Labs with IVUS", "Heart Transplant Center", "Express OPD Queue"),
            lat = 17.4265,
            lng = 78.4552,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_kims_hyd",
            name = "KIMS Hospitals Secunderabad",
            city = "Hyderabad",
            state = "Telangana",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "4.1k",
            distanceKm = 4.8,
            waitingCount = 6,
            estWaitMinutes = 18,
            emergencyAvailable = true,
            address = "1-8-31/1, Minister Rd, Krishna Nagar Colony, Begumpet, Secunderabad",
            phone = "+91 40 4488 5000",
            landmark = "Minister Road, Near Paradise",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Neurology", "Nephrology", "Oncology"),
            facilities = listOf("Multi-Organ Transplant Unit", "Cardiothoracic Critical Care", "24/7 Dialysis"),
            lat = 17.4385,
            lng = 78.4867,
            imageDrawableName = "hospital_sunrise"
        ),

        // CHENNAI
        Hospital(
            id = "hosp_apollo_chennai",
            name = "Apollo Main Hospital Greams Road",
            city = "Chennai",
            state = "Tamil Nadu",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.9,
            reviewsCount = "8.4k",
            distanceKm = 2.6,
            waitingCount = 6,
            estWaitMinutes = 18,
            emergencyAvailable = true,
            address = "21 Greams Lane, Off Greams Road, Thousand Lights, Chennai",
            phone = "+91 44 2829 0200",
            landmark = "Thousand Lights Metro Station",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Oncology", "Pediatrics"),
            facilities = listOf("Pioneer in Indian Cardiac Care", "CyberKnife Suite", "Heart & Lung Transplant"),
            lat = 13.0583,
            lng = 80.2508,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_fortis_chennai",
            name = "Fortis Malar Hospital",
            city = "Chennai",
            state = "Tamil Nadu",
            type = HospitalType.PRIVATE,
            rating = 4.6,
            reviewsCount = "2.9k",
            distanceKm = 4.1,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "No. 52, 1st Main Rd, Gandhi Nagar, Adyar, Chennai",
            phone = "+91 44 4289 2222",
            landmark = "Adyar Bridge",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Dentistry", "General Medicine", "Pediatrics"),
            facilities = listOf("Comprehensive Heart Failure Program", "Critical Care ICU", "Walk-in OPD"),
            lat = 13.0067,
            lng = 80.2570,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_miot_chennai",
            name = "MIOT International",
            city = "Chennai",
            state = "Tamil Nadu",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "3.8k",
            distanceKm = 5.5,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "4/112, Mount Poonamallee Rd, Manapakkam, Chennai",
            phone = "+91 44 4200 2288",
            landmark = "Manapakkam",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 07:00 PM",
            specialties = listOf("Orthopedics", "Cardiology", "Neurology"),
            facilities = listOf("Pioneer in Joint Replacements", "Bi-Plane Cath Lab", "Automated Queue Systems"),
            lat = 13.0185,
            lng = 80.1780,
            imageDrawableName = "hospital_sunrise"
        ),

        // KOLKATA
        Hospital(
            id = "hosp_apollo_kolkata",
            name = "Apollo Gleneagles Hospitals",
            city = "Kolkata",
            state = "West Bengal",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "4.8k",
            distanceKm = 3.5,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "58, Canal Circular Rd, Kadapara, Phool Bagan, Kolkata",
            phone = "+91 33 2320 3040",
            landmark = "EM Bypass, Kadapara",
            openingHours = "Open 24 Hours • OPD: 09:00 AM - 07:00 PM",
            specialties = listOf("Cardiology", "Orthopedics", "Neurology", "Pediatrics"),
            facilities = listOf("JCI Accredited", "Eastern India Heart Center", "Emergency Response"),
            lat = 22.5768,
            lng = 88.4014,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_fortis_kolkata",
            name = "Fortis Hospital Anandapur",
            city = "Kolkata",
            state = "West Bengal",
            type = HospitalType.PRIVATE,
            rating = 4.6,
            reviewsCount = "3.5k",
            distanceKm = 4.8,
            waitingCount = 6,
            estWaitMinutes = 18,
            emergencyAvailable = true,
            address = "730, Anandapur, E.M. Bypass Road, Kolkata",
            phone = "+91 33 6628 4444",
            landmark = "Near Ruby General Hospital",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Pulmonology", "Nephrology"),
            facilities = listOf("Advanced Coronary Care", "Renal Sciences Unit", "Express OPD"),
            lat = 22.5186,
            lng = 88.4020,
            imageDrawableName = "hospital_sunrise"
        ),

        // PUNE
        Hospital(
            id = "hosp_ruby_pune",
            name = "Ruby Hall Clinic",
            city = "Pune",
            state = "Maharashtra",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "5.1k",
            distanceKm = 2.4,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "40, Sassoon Road, Sangamvadi, Pune",
            phone = "+91 20 6645 5100",
            landmark = "Near Pune Railway Station",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Dentistry", "General Medicine"),
            facilities = listOf("NABH & NABL Accredited", "Dedicated Heart OPD", "Diagnostic MRI & CT"),
            lat = 18.5314,
            lng = 73.8770,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_jehangir_pune",
            name = "Jehangir Hospital",
            city = "Pune",
            state = "Maharashtra",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.6,
            reviewsCount = "3.9k",
            distanceKm = 2.8,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "32, Sassoon Rd, Opposite Pune Railway Station, Pune",
            phone = "+91 20 6681 1000",
            landmark = "Opposite Pune Station",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Pediatrics", "Orthopedics"),
            facilities = listOf("Trusted Healthcare Legacy", "Specialized Cardiac Wing"),
            lat = 18.5292,
            lng = 73.8735,
            imageDrawableName = "hospital_sunrise"
        ),

        // AHMEDABAD
        Hospital(
            id = "hosp_zydus_ahmedabad",
            name = "Zydus Hospital",
            city = "Ahmedabad",
            state = "Gujarat",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "4.5k",
            distanceKm = 3.8,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Zydus Hospitals Road, Near Sola Bridge, SG Highway, Thaltej, Ahmedabad",
            phone = "+91 79 6619 0201",
            landmark = "Sola Bridge, SG Highway",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Gastroenterology"),
            facilities = listOf("Super-Speciality Quaternary Care", "Advanced Cath Labs", "Critical Care ICU"),
            lat = 23.0722,
            lng = 72.5186,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_apollo_ahmedabad",
            name = "Apollo Hospitals International",
            city = "Ahmedabad",
            state = "Gujarat",
            type = HospitalType.PRIVATE,
            rating = 4.7,
            reviewsCount = "3.8k",
            distanceKm = 5.2,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "Plot No.1 A, Bhat GIDC Estate, Gandhinagar / Ahmedabad",
            phone = "+91 79 6670 1800",
            landmark = "Near Airport Road",
            openingHours = "Open 24 Hours • OPD: 09:00 AM - 07:00 PM",
            specialties = listOf("Cardiology", "Oncology", "Pediatrics", "General Medicine"),
            facilities = listOf("Joint Commission International (JCI)", "Heart & Lung Center"),
            lat = 23.1198,
            lng = 72.6322,
            imageDrawableName = "hospital_sunrise"
        ),

        // JAIPUR
        Hospital(
            id = "hosp_eternal_jaipur",
            name = "Eternal Hospital (EHCC)",
            city = "Jaipur",
            state = "Rajasthan",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "4.2k",
            distanceKm = 3.4,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "3 A, Jagatpura Rd, Near Jawahar Circle, Jaipur",
            phone = "+91 141 517 4000",
            landmark = "Jawahar Circle, Malviya Nagar",
            openingHours = "Open 24 Hours • Heart OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "General Medicine"),
            facilities = listOf("Mount Sinai Affiliated Cardiac Institute", "Advanced Cath Lab", "Emergency Trauma"),
            lat = 26.8488,
            lng = 75.8078,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_fortis_jaipur",
            name = "Fortis Escorts Hospital Jaipur",
            city = "Jaipur",
            state = "Rajasthan",
            type = HospitalType.PRIVATE,
            rating = 4.7,
            reviewsCount = "3.6k",
            distanceKm = 4.1,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Jawahar Lal Nehru Marg, Malviya Nagar, Jaipur",
            phone = "+91 141 254 7000",
            landmark = "JLN Marg, Near World Trade Park",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Dentistry", "Pediatrics", "Orthopedics"),
            facilities = listOf("Center of Excellence in Cardiac Care", "Robotic Knee Surgery", "Day Care OPD"),
            lat = 26.8525,
            lng = 75.8115,
            imageDrawableName = "hospital_sunrise"
        ),

        // DELHI NCR (Additional)
        Hospital(
            id = "hosp_apollo_delhi",
            name = "Indraprastha Apollo Hospital",
            city = "Delhi NCR",
            state = "Delhi",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "6.7k",
            distanceKm = 3.4,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "Sarita Vihar, Delhi Mathura Road, New Delhi",
            phone = "+91 11 2692 5858",
            landmark = "Jasola Apollo Metro Station",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Oncology", "Pediatrics"),
            facilities = listOf("First Hospital in India to be JCI Accredited", "Liver & Multi-Organ Transplant Hub", "24/7 Stroke Unit"),
            lat = 28.5363,
            lng = 77.2882,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_blk_max_delhi",
            name = "BLK-Max Super Speciality Hospital",
            city = "Delhi NCR",
            state = "Delhi",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "5.2k",
            distanceKm = 4.6,
            waitingCount = 6,
            estWaitMinutes = 16,
            emergencyAvailable = true,
            address = "Pusa Road, Radha Soami Satsang, Rajendra Place, New Delhi",
            phone = "+91 11 3040 3040",
            landmark = "Rajendra Place Metro Station",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Orthopedics", "Dentistry", "Gastroenterology", "Neurology"),
            facilities = listOf("650 Bed Multi-Super Speciality", "Asia's Largest Bone Marrow Transplant Unit", "Cyberknife Suite"),
            lat = 28.6433,
            lng = 77.1793,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_artemis_gurugram",
            name = "Artemis Hospital Gurugram",
            city = "Delhi NCR",
            state = "Haryana",
            type = HospitalType.PRIVATE,
            rating = 4.7,
            reviewsCount = "3.9k",
            distanceKm = 5.8,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Sector 51, Gurugram, Haryana",
            phone = "+91 124 451 1111",
            landmark = "Near Mayfield Gardens, Sector 51",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Orthopedics", "Neurology", "Pediatrics"),
            facilities = listOf("JCI and NABH Accredited", "Advanced Cath Lab & Electrophysiology", "Express OPD Pharmacy"),
            lat = 28.4312,
            lng = 77.0715,
            imageDrawableName = "hospital_sunrise"
        ),

        // MUMBAI (Additional)
        Hospital(
            id = "hosp_reliance_mumbai",
            name = "Sir H. N. Reliance Foundation Hospital",
            city = "Mumbai",
            state = "Maharashtra",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.9,
            reviewsCount = "4.8k",
            distanceKm = 3.5,
            waitingCount = 3,
            estWaitMinutes = 10,
            emergencyAvailable = true,
            address = "Raja Rammohan Roy Rd, Prarthana Samaj, Girgaon, Mumbai",
            phone = "+91 22 6130 5005",
            landmark = "Near Charni Road Railway Station",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Oncology", "Dentistry"),
            facilities = listOf("Quaternary Care Medical Hub", "Hybrid Cath Lab", "Automated Robot Pharmacy"),
            lat = 18.9576,
            lng = 72.8184,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_nanavati_mumbai",
            name = "Nanavati Max Super Speciality Hospital",
            city = "Mumbai",
            state = "Maharashtra",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "4.5k",
            distanceKm = 3.9,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Swami Vivekananda Rd, Besant Montessori School, Vile Parle West, Mumbai",
            phone = "+91 22 2626 7500",
            landmark = "SV Road, Vile Parle West",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Pediatrics", "Orthopedics"),
            facilities = listOf("350 Bed Iconic Hospital", "Heart & Lung Institute", "Level 1 Trauma"),
            lat = 19.0965,
            lng = 72.8406,
            imageDrawableName = "hospital_sunrise"
        ),

        // BENGALURU (Additional)
        Hospital(
            id = "hosp_stjohns_blr",
            name = "St. John's Medical College Hospital",
            city = "Bengaluru",
            state = "Karnataka",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "6.2k",
            distanceKm = 3.1,
            waitingCount = 7,
            estWaitMinutes = 20,
            emergencyAvailable = true,
            address = "Sarjapur - Marathahalli Rd, John Nagar, Koramangala, Bengaluru",
            phone = "+91 80 2206 5000",
            landmark = "Opposite Forum Mall Koramangala",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 04:30 PM",
            specialties = listOf("Cardiology", "General Medicine", "Pediatrics", "Orthopedics", "Dentistry"),
            facilities = listOf("NABH Accredited Apex Hospital", "Affordable High-Tech Healthcare", "Emergency ICU"),
            lat = 12.9345,
            lng = 77.6190,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_sakra_blr",
            name = "Sakra World Hospital",
            city = "Bengaluru",
            state = "Karnataka",
            type = HospitalType.PRIVATE,
            rating = 4.8,
            reviewsCount = "4.1k",
            distanceKm = 5.6,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "SY NO 52/2 & 52/3, Devarabeesanahalli, Outer Ring Rd, Bellandur, Bengaluru",
            phone = "+91 80 4969 4969",
            landmark = "Opposite Intel, Outer Ring Road",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Orthopedics", "Neurology", "Dentistry"),
            facilities = listOf("Indo-Japanese Medical Collaboration", "Biplane Neurovascular Cath Lab", "Fast Track OPD"),
            lat = 12.9260,
            lng = 77.6833,
            imageDrawableName = "hospital_sunrise"
        ),

        // HYDERABAD (Additional)
        Hospital(
            id = "hosp_care_hyd",
            name = "Care Hospitals Banjara Hills",
            city = "Hyderabad",
            state = "Telangana",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "4.4k",
            distanceKm = 3.8,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Road No 1, Prem Nagar, Banjara Hills, Hyderabad",
            phone = "+91 40 6165 6565",
            landmark = "Near Taj Krishna, Banjara Hills",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pulmonology"),
            facilities = listOf("Pioneer in Heart Stents in India", "Comprehensive Cardiac Rehabilitation", "Express OPD"),
            lat = 17.4156,
            lng = 78.4482,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_continental_hyd",
            name = "Continental Hospitals",
            city = "Hyderabad",
            state = "Telangana",
            type = HospitalType.PRIVATE,
            rating = 4.8,
            reviewsCount = "3.7k",
            distanceKm = 6.2,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "Plot No 3, Road No 2, IT & Financial District, Nanakramguda, Gachibowli, Hyderabad",
            phone = "+91 40 6700 0000",
            landmark = "Gachibowli Financial District",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Oncology", "Orthopedics", "Dentistry"),
            facilities = listOf("JCI Accredited Facility", "State-of-the-Art Heart Center", "Green Hospital Campus"),
            lat = 17.4180,
            lng = 78.3490,
            imageDrawableName = "hospital_sunrise"
        ),

        // CHENNAI (Additional)
        Hospital(
            id = "hosp_kauvery_chennai",
            name = "Kauvery Hospital Alwarpet",
            city = "Chennai",
            state = "Tamil Nadu",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "4.2k",
            distanceKm = 3.0,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "No. 81, TTK Road, Alwarpet, Chennai",
            phone = "+91 44 4000 6000",
            landmark = "Near Alwarpet Signal",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics"),
            facilities = listOf("Comprehensive Heart Institute", "Advanced Geriatric Care", "Queue-Managed OPD"),
            lat = 13.0335,
            lng = 78.2530,
            imageDrawableName = "hospital_sunrise"
        ),

        // KOLKATA (Additional)
        Hospital(
            id = "hosp_medica_kolkata",
            name = "Medica Superspecialty Hospital",
            city = "Kolkata",
            state = "West Bengal",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "4.3k",
            distanceKm = 4.2,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "127, Mukundapur, E.M. Bypass, Kolkata",
            phone = "+91 33 6652 0000",
            landmark = "Near Metro Cash & Carry, EM Bypass",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "General Medicine"),
            facilities = listOf("NABH & NABL Accredited", "Cardiothoracic Surgery Excellence", "24/7 Dialysis"),
            lat = 22.4965,
            lng = 88.3985,
            imageDrawableName = "hospital_sunrise"
        ),

        // PUNE (Additional)
        Hospital(
            id = "hosp_sahyadri_pune",
            name = "Sahyadri Super Speciality Hospital",
            city = "Pune",
            state = "Maharashtra",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "4.6k",
            distanceKm = 2.9,
            waitingCount = 5,
            estWaitMinutes = 14,
            emergencyAvailable = true,
            address = "Plot No. 30 C, Erandwane, Karve Rd, Deccan Gymkhana, Pune",
            phone = "+91 20 6721 5000",
            landmark = "Deccan Gymkhana, Karve Road",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Dentistry"),
            facilities = listOf("Largest Healthcare Chain in Maharashtra", "Dedicated Neuro & Cardiac Wings"),
            lat = 18.5085,
            lng = 73.8340,
            imageDrawableName = "hospital_sunrise"
        ),

        // AHMEDABAD (Additional)
        Hospital(
            id = "hosp_cims_ahmedabad",
            name = "Marengo CIMS Hospital",
            city = "Ahmedabad",
            state = "Gujarat",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "5.1k",
            distanceKm = 4.1,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "Off Science City Road, Sola, Ahmedabad",
            phone = "+91 79 3010 1200",
            landmark = "Science City Road, Sola",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pulmonology"),
            facilities = listOf("Heart Transplant Center", "Green Operation Theatres", "Express OPD Token Hub"),
            lat = 23.0815,
            lng = 72.5080,
            imageDrawableName = "hospital_sunrise"
        ),

        // JAIPUR (Additional)
        Hospital(
            id = "hosp_manipal_jaipur",
            name = "Manipal Hospital Jaipur",
            city = "Jaipur",
            state = "Rajasthan",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.7,
            reviewsCount = "3.8k",
            distanceKm = 4.5,
            waitingCount = 4,
            estWaitMinutes = 14,
            emergencyAvailable = true,
            address = "Sector 5, Main Sikar Road, Vidhyadhar Nagar, Jaipur",
            phone = "+91 141 516 4000",
            landmark = "Near Alka Cinema, Sikar Road",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics"),
            facilities = listOf("Comprehensive Cancer & Cardiac Care", "Modular Operation Theatres", "24/7 Trauma"),
            lat = 26.9660,
            lng = 75.7725,
            imageDrawableName = "hospital_sunrise"
        ),

        // LUCKNOW
        Hospital(
            id = "hosp_sgpgi_lucknow",
            name = "SGPGI Lucknow",
            city = "Lucknow",
            state = "Uttar Pradesh",
            type = HospitalType.GOVERNMENT,
            rating = 4.8,
            reviewsCount = "7.8k",
            distanceKm = 5.1,
            waitingCount = 14,
            estWaitMinutes = 35,
            emergencyAvailable = true,
            address = "New PMSSY Complex, Raebareli Rd, Haibat Mau Mawaiya, Lucknow",
            phone = "+91 522 266 8004",
            landmark = "Raebareli Road, Lucknow",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 04:30 PM",
            specialties = listOf("Cardiology", "Nephrology", "Neurology", "Gastroenterology", "General Medicine"),
            facilities = listOf("Premier Apex Medical Institute of North India", "Super-Specialty Research Hospital"),
            lat = 26.7458,
            lng = 80.9385,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_medanta_lucknow",
            name = "Medanta Hospital Lucknow",
            city = "Lucknow",
            state = "Uttar Pradesh",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.9,
            reviewsCount = "4.5k",
            distanceKm = 4.2,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Sector A, Pocket 1, Amar Shaheed Path, Golf City, Lucknow",
            phone = "+91 522 450 5050",
            landmark = "Amar Shaheed Path, Near Lulu Mall",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Dentistry", "Pediatrics"),
            facilities = listOf("1000 Bed World-Class Healthcare", "Dr. Trehan Heart Institute", "Advanced Robotic Surgery"),
            lat = 26.7760,
            lng = 80.9980,
            imageDrawableName = "hospital_sunrise"
        ),

        // CHANDIGARH
        Hospital(
            id = "hosp_pgimer_chandigarh",
            name = "PGIMER Chandigarh",
            city = "Chandigarh",
            state = "Chandigarh",
            type = HospitalType.GOVERNMENT,
            rating = 4.8,
            reviewsCount = "9.4k",
            distanceKm = 3.9,
            waitingCount = 16,
            estWaitMinutes = 38,
            emergencyAvailable = true,
            address = "Madhya Marg, Sector 12, Chandigarh",
            phone = "+91 172 274 7585",
            landmark = "Opposite Panjab University, Sector 12",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 04:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pulmonology", "General Medicine"),
            facilities = listOf("National Apex Medical Institute", "Advanced Cardiac Centre", "Subsidized High-End Care"),
            lat = 30.7650,
            lng = 76.7780,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_fortis_mohali",
            name = "Fortis Hospital Mohali",
            city = "Chandigarh",
            state = "Punjab",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "4.9k",
            distanceKm = 4.8,
            waitingCount = 5,
            estWaitMinutes = 15,
            emergencyAvailable = true,
            address = "Sector 62, Phase VIII, Mohali, Chandigarh Tricity",
            phone = "+91 172 502 1222",
            landmark = "Phase VIII, Near PCA Stadium Mohali",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 07:30 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Dentistry"),
            facilities = listOf("JCI Accredited Tricity Hospital", "TAVI Valve Clinic", "Express OPD Pharmacy"),
            lat = 30.7020,
            lng = 76.7210,
            imageDrawableName = "hospital_sunrise"
        ),

        // KOCHI
        Hospital(
            id = "hosp_aster_kochi",
            name = "Aster Medcity Kochi",
            city = "Kochi",
            state = "Kerala",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "5.3k",
            distanceKm = 4.1,
            waitingCount = 4,
            estWaitMinutes = 12,
            emergencyAvailable = true,
            address = "Kuttisahib Road, South Chittoor, Cheranalloor, Kochi",
            phone = "+91 484 669 9999",
            landmark = "Cheranalloor, Waterfront Campus",
            openingHours = "Open 24 Hours • OPD: 08:00 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Dentistry"),
            facilities = listOf("JCI & NABH Accredited 670-Bed Hub", "Comprehensive Heart Failure Program", "Helipad"),
            lat = 10.0540,
            lng = 76.2730,
            imageDrawableName = "hospital_sunrise"
        ),
        Hospital(
            id = "hosp_amrita_kochi",
            name = "Amrita Institute of Medical Sciences",
            city = "Kochi",
            state = "Kerala",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "6.7k",
            distanceKm = 3.8,
            waitingCount = 8,
            estWaitMinutes = 20,
            emergencyAvailable = true,
            address = "Ponekkara, AIMS PO, Edappally, Kochi",
            phone = "+91 484 285 1234",
            landmark = "Edappally, Near Metro",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 06:30 PM",
            specialties = listOf("Cardiology", "Orthopedics", "Neurology", "General Medicine"),
            facilities = listOf("1350 Bed Premier Healthcare Institution", "Pediatric Heart Center", "Robotic Da Vinci"),
            lat = 10.0330,
            lng = 76.2940,
            imageDrawableName = "hospital_sunrise"
        ),

        // INDORE
        Hospital(
            id = "hosp_medanta_indore",
            name = "Medanta Super Speciality Hospital Indore",
            city = "Indore",
            state = "Madhya Pradesh",
            type = HospitalType.MULTI_SPECIALITY,
            rating = 4.8,
            reviewsCount = "4.2k",
            distanceKm = 3.2,
            waitingCount = 4,
            estWaitMinutes = 14,
            emergencyAvailable = true,
            address = "Plot No. 8, PU-4, Commercial Scheme 54, Vijay Nagar, Indore",
            phone = "+91 731 474 7000",
            landmark = "Vijay Nagar Square, Indore",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 08:00 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Gastroenterology"),
            facilities = listOf("Heart Institute Under Medanta", "Biplane Cath Lab", "Express Token OPD"),
            lat = 22.7533,
            lng = 75.8937,
            imageDrawableName = "hospital_sunrise"
        ),

        // BHOPAL
        Hospital(
            id = "hosp_aiims_bhopal",
            name = "AIIMS Bhopal",
            city = "Bhopal",
            state = "Madhya Pradesh",
            type = HospitalType.GOVERNMENT,
            rating = 4.8,
            reviewsCount = "5.8k",
            distanceKm = 4.4,
            waitingCount = 12,
            estWaitMinutes = 32,
            emergencyAvailable = true,
            address = "Saket Nagar, AIIMS Campus, Bhopal",
            phone = "+91 755 267 2355",
            landmark = "Saket Nagar, Near Barkatullah University",
            openingHours = "Open 24 Hours • OPD: 08:30 AM - 04:30 PM",
            specialties = listOf("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "General Medicine"),
            facilities = listOf("Institute of National Importance", "Trauma and Emergency Wing", "Subsidized Medicines"),
            lat = 23.2080,
            lng = 77.4580,
            imageDrawableName = "hospital_sunrise"
        )
    )

    // Doctors with realistic Indian credentials and fees in INR
    val doctors: List<Doctor> = listOf(
        // Delhi NCR Doctors
        Doctor(
            id = "doc_priya",
            hospitalId = "hosp_sunrise",
            hospitalName = "Sunrise Multi-Speciality Hospital",
            city = "Delhi NCR",
            name = "Dr. Priya Mehta",
            specialty = "Cardiology",
            title = "Senior Cardiologist",
            rating = 4.8,
            reviewsCount = 2140,
            experienceYears = 12,
            fee = "₹800",
            roomNumber = "Room 2",
            isAvailable = true,
            currentQueueSize = 6,
            estWaitMinutes = 18,
            nextAvailableSlot = "10:30 AM",
            imageDrawableName = "doctor_priya",
            education = "MBBS, MD - Cardiology (AIIMS New Delhi), FACC",
            bio = "Senior Cardiologist specializing in preventive cardiology, heart rhythm disorders, and non-invasive hemodynamic assessments. Dedicated to patient-first OPD care."
        ),
        Doctor(
            id = "doc_rajesh",
            hospitalId = "hosp_sunrise",
            hospitalName = "Sunrise Multi-Speciality Hospital",
            city = "Delhi NCR",
            name = "Dr. Rajesh Sharma",
            specialty = "Cardiology",
            title = "Interventional Cardiologist",
            rating = 4.7,
            reviewsCount = 1430,
            experienceYears = 15,
            fee = "₹900",
            roomNumber = "Room 4",
            isAvailable = true,
            currentQueueSize = 4,
            estWaitMinutes = 12,
            nextAvailableSlot = "11:00 AM",
            imageDrawableName = "doctor_rajesh",
            education = "MBBS, DM - Cardiology (PGIMER Chandigarh)",
            bio = "Specialist in coronary interventions, angioplasty, and clinical cardiovascular diagnostics."
        ),
        Doctor(
            id = "doc_anjali",
            hospitalId = "hosp_sunrise",
            hospitalName = "Sunrise Multi-Speciality Hospital",
            city = "Delhi NCR",
            name = "Dr. Anjali Verma",
            specialty = "Cardiology",
            title = "Associate Cardiologist",
            rating = 4.6,
            reviewsCount = 820,
            experienceYears = 8,
            fee = "₹700",
            roomNumber = "Room 1",
            isAvailable = true,
            currentQueueSize = 5,
            estWaitMinutes = 15,
            nextAvailableSlot = "11:30 AM",
            imageDrawableName = "doctor_priya",
            education = "MBBS, DNB - Cardiology",
            bio = "Dedicated to women's cardiovascular health, hypertension management, and outpatient preventive checkups."
        ),
        Doctor(
            id = "doc_sandeep",
            hospitalId = "hosp_sunrise",
            hospitalName = "Sunrise Multi-Speciality Hospital",
            city = "Delhi NCR",
            name = "Dr. Sandeep Reddy",
            specialty = "Cardiology",
            title = "Cardiac Electrophysiologist",
            rating = 4.9,
            reviewsCount = 980,
            experienceYears = 14,
            fee = "₹1,000",
            roomNumber = "Room 3",
            isAvailable = true,
            currentQueueSize = 3,
            estWaitMinutes = 9,
            nextAvailableSlot = "01:00 PM",
            imageDrawableName = "doctor_rajesh",
            education = "MBBS, MD, Fellow in Electrophysiology (Hopkins)",
            bio = "Expert in arrhythmia mapping, pacemaker implants, and advanced cardiac syncope diagnostics."
        ),
        Doctor(
            id = "doc_ananya",
            hospitalId = "hosp_sunrise",
            hospitalName = "Sunrise Multi-Speciality Hospital",
            city = "Delhi NCR",
            name = "Dr. Ananya Singh",
            specialty = "Dentistry",
            title = "Senior Orthodontist",
            rating = 4.8,
            reviewsCount = 1120,
            experienceYears = 10,
            fee = "₹600",
            roomNumber = "Room 5",
            isAvailable = true,
            currentQueueSize = 4,
            estWaitMinutes = 10,
            nextAvailableSlot = "10:00 AM",
            imageDrawableName = "doctor_priya",
            education = "BDS, MDS - Orthodontics (Maulana Azad)",
            bio = "Specializing in cosmetic dentistry, invisible aligners, and painless dental surgery."
        ),
        Doctor(
            id = "doc_vikram",
            hospitalId = "hosp_sunrise",
            hospitalName = "Sunrise Multi-Speciality Hospital",
            city = "Delhi NCR",
            name = "Dr. Vikram Rao",
            specialty = "Orthopedics",
            title = "Joint Replacement Specialist",
            rating = 4.7,
            reviewsCount = 1310,
            experienceYears = 16,
            fee = "₹850",
            roomNumber = "Room 6",
            isAvailable = true,
            currentQueueSize = 11,
            estWaitMinutes = 42,
            nextAvailableSlot = "02:00 PM",
            imageDrawableName = "doctor_rajesh",
            education = "MBBS, MS - Orthopedics, MCh",
            bio = "Expert in robotic knee and hip replacements, arthroscopy, and sports trauma management."
        ),

        // Mumbai Doctors
        Doctor(
            id = "doc_kokila_suresh",
            hospitalId = "hosp_kokilaben_mumbai",
            hospitalName = "Kokilaben Dhirubhai Ambani Hospital",
            city = "Mumbai",
            name = "Dr. Suresh Rao",
            specialty = "Cardiology",
            title = "Director & Chief Cardiologist",
            rating = 4.9,
            reviewsCount = 2840,
            experienceYears = 22,
            fee = "₹1,200",
            roomNumber = "Cardiac Suite 1",
            isAvailable = true,
            currentQueueSize = 5,
            estWaitMinutes = 15,
            nextAvailableSlot = "11:15 AM",
            imageDrawableName = "doctor_rajesh",
            education = "MBBS, MD, DM - Cardiology (KEM Mumbai)",
            bio = "Pioneer in complex adult cardiovascular surgery and catheter-based valve therapies."
        ),

        // Bengaluru Doctors
        Doctor(
            id = "doc_manipal_aravind",
            hospitalId = "hosp_manipal_blr",
            hospitalName = "Manipal Hospital Old Airport Road",
            city = "Bengaluru",
            name = "Dr. Aravind Hegde",
            specialty = "Cardiology",
            title = "Senior Consultant Cardiologist",
            rating = 4.8,
            reviewsCount = 1950,
            experienceYears = 17,
            fee = "₹900",
            roomNumber = "Cardio Room 204",
            isAvailable = true,
            currentQueueSize = 6,
            estWaitMinutes = 18,
            nextAvailableSlot = "10:45 AM",
            imageDrawableName = "doctor_rajesh",
            education = "MBBS, MD - Medicine, DM - Cardiology (BMCRI)",
            bio = "Expert in preventive coronary health, cardiac imaging, and outpatient management."
        ),

        // Ahmedabad Doctors
        Doctor(
            id = "doc_zydus_pranav",
            hospitalId = "hosp_zydus_ahmedabad",
            hospitalName = "Zydus Hospital",
            city = "Ahmedabad",
            name = "Dr. Pranav Patel",
            specialty = "Cardiology",
            title = "Chief Interventional Cardiologist",
            rating = 4.8,
            reviewsCount = 1620,
            experienceYears = 16,
            fee = "₹850",
            roomNumber = "Suite 2B",
            isAvailable = true,
            currentQueueSize = 5,
            estWaitMinutes = 15,
            nextAvailableSlot = "10:30 AM",
            imageDrawableName = "doctor_rajesh",
            education = "MBBS, DM - Cardiology (BJ Medical College)",
            bio = "Specialist in radial angiographies, coronary stents, and preventive heart care in Gujarat."
        ),

        // Jaipur Doctors
        Doctor(
            id = "doc_eternal_sanjeev",
            hospitalId = "hosp_eternal_jaipur",
            hospitalName = "Eternal Hospital (EHCC)",
            city = "Jaipur",
            name = "Dr. Sanjeev Sharma",
            specialty = "Cardiology",
            title = "Senior Director Cardiology",
            rating = 4.9,
            reviewsCount = 2100,
            experienceYears = 20,
            fee = "₹800",
            roomNumber = "Heart OPD 105",
            isAvailable = true,
            currentQueueSize = 4,
            estWaitMinutes = 12,
            nextAvailableSlot = "11:00 AM",
            imageDrawableName = "doctor_rajesh",
            education = "MBBS, MD, DM (SMS Medical College Jaipur)",
            bio = "Leading clinician in non-invasive cardiac imaging, valve management, and outpatient consultation."
        )
    )

    val specialists: List<Specialist> = listOf(
        Specialist("spec_cardio", "Cardiology", doctorCount = 18, availableCount = 15, "Heart, Blood Vessels & BP", "Favorite"),
        Specialist("spec_dental", "Dentistry", doctorCount = 10, availableCount = 9, "Teeth, Gums & Oral Health", "Face"),
        Specialist("spec_ortho", "Orthopedics", doctorCount = 12, availableCount = 10, "Bones, Joints & Spine Care", "Accessibility"),
        Specialist("spec_opht", "Ophthalmology", doctorCount = 8, availableCount = 7, "Eye Care, Vision & Lasik", "Visibility"),
        Specialist("spec_ped", "Pediatrics", doctorCount = 11, availableCount = 10, "Infant, Child & Adolescent Care", "ChildCare"),
        Specialist("spec_neuro", "Neurology", doctorCount = 9, availableCount = 8, "Brain, Nerves & Headache", "Psychology"),
        Specialist("spec_gyn", "Gynecology", doctorCount = 10, availableCount = 9, "Women's Health & Maternity", "PregnantWoman"),
        Specialist("spec_genmed", "General Medicine", doctorCount = 15, availableCount = 13, "Fever, Infections & Primary Care", "MedicalServices"),
        Specialist("spec_pulmo", "Pulmonology", doctorCount = 8, availableCount = 7, "Lungs, Asthma & Respiratory", "Air"),
        Specialist("spec_derma", "Dermatology", doctorCount = 9, availableCount = 8, "Skin, Hair & Cosmetic Care", "Spa"),
        Specialist("spec_ent", "ENT", doctorCount = 8, availableCount = 7, "Ear, Nose, Throat & Sinus", "Hearing"),
        Specialist("spec_gastro", "Gastroenterology", doctorCount = 7, availableCount = 6, "Digestion, Liver & Stomach", "LocalPharmacy"),
        Specialist("spec_nephro", "Nephrology", doctorCount = 7, availableCount = 6, "Kidney Function & Dialysis", "Bloodtype"),
        Specialist("spec_endo", "Endocrinology", doctorCount = 6, availableCount = 5, "Diabetes, Thyroid & Hormones", "Biotech"),
        Specialist("spec_uro", "Urology", doctorCount = 6, availableCount = 5, "Urinary Tract & Men's Health", "Sanitizer"),
        Specialist("spec_onco", "Oncology", doctorCount = 7, availableCount = 6, "Cancer Diagnostics & Therapy", "Healing"),
        Specialist("spec_psych", "Psychiatry", doctorCount = 8, availableCount = 7, "Mental Health & Wellness", "SelfImprovement")
    )

    private val _liveAlertEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val liveAlertEvents: SharedFlow<String> = _liveAlertEvents.asSharedFlow()

    init {
        repositoryScope.launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        withContext(Dispatchers.IO) {
            val existing = queueDao.getQueuePatientByToken("A-27")
            if (existing == null) {
                val initialQueue = listOf(
                    QueuePatient(
                        id = "qp_1",
                        doctorId = "doc_priya",
                        doctorName = "Dr. Priya Mehta",
                        tokenNumber = "A-24",
                        sequenceNumber = 24,
                        patientName = "Mr. Amit Kapoor",
                        patientPhone = "+91 98111 01010",
                        chiefComplaint = "Chest tightness & follow-up ECG",
                        status = QueueStatus.WITH_DOCTOR.name,
                        roomNumber = "Room 2",
                        joinedTime = System.currentTimeMillis() - 18 * 60 * 1000,
                        estimatedWaitMinutes = 0
                    ),
                    QueuePatient(
                        id = "qp_2",
                        doctorId = "doc_priya",
                        doctorName = "Dr. Priya Mehta",
                        tokenNumber = "A-25",
                        sequenceNumber = 25,
                        patientName = "Mrs. Kavita Sen",
                        patientPhone = "+91 98222 01020",
                        chiefComplaint = "Hypertension medication review",
                        status = QueueStatus.WAITING.name,
                        roomNumber = "Room 2",
                        joinedTime = System.currentTimeMillis() - 14 * 60 * 1000,
                        estimatedWaitMinutes = 3
                    ),
                    QueuePatient(
                        id = "qp_3",
                        doctorId = "doc_priya",
                        doctorName = "Dr. Priya Mehta",
                        tokenNumber = "A-26",
                        sequenceNumber = 26,
                        patientName = "Mr. David Miller",
                        patientPhone = "+91 98333 01030",
                        chiefComplaint = "Routine cardiac checkup & treadmill test",
                        status = QueueStatus.WAITING.name,
                        roomNumber = "Room 2",
                        joinedTime = System.currentTimeMillis() - 10 * 60 * 1000,
                        estimatedWaitMinutes = 6
                    ),
                    QueuePatient(
                        id = "qp_4",
                        doctorId = "doc_priya",
                        doctorName = "Dr. Priya Mehta",
                        tokenNumber = "A-27",
                        sequenceNumber = 27,
                        patientName = "Rehan",
                        patientPhone = "+91 98444 01990",
                        chiefComplaint = "Palpitations & mild shortness of breath",
                        status = QueueStatus.WAITING.name,
                        roomNumber = "Room 2",
                        joinedTime = System.currentTimeMillis() - 6 * 60 * 1000,
                        estimatedWaitMinutes = 9
                    ),
                    QueuePatient(
                        id = "qp_5",
                        doctorId = "doc_priya",
                        doctorName = "Dr. Priya Mehta",
                        tokenNumber = "A-28",
                        sequenceNumber = 28,
                        patientName = "Ms. Neha Patel",
                        patientPhone = "+91 98555 01040",
                        chiefComplaint = "Cholesterol lab panel review",
                        status = QueueStatus.WAITING.name,
                        roomNumber = "Room 2",
                        joinedTime = System.currentTimeMillis() - 3 * 60 * 1000,
                        estimatedWaitMinutes = 12
                    ),
                    QueuePatient(
                        id = "qp_6",
                        doctorId = "doc_priya",
                        doctorName = "Dr. Priya Mehta",
                        tokenNumber = "A-29",
                        sequenceNumber = 29,
                        patientName = "Mr. Robert Chen",
                        patientPhone = "+91 98666 01050",
                        chiefComplaint = "Post-angioplasty 6-month checkup",
                        status = QueueStatus.WAITING.name,
                        roomNumber = "Room 2",
                        joinedTime = System.currentTimeMillis() - 1 * 60 * 1000,
                        estimatedWaitMinutes = 15
                    )
                )
                queueDao.insertQueuePatients(initialQueue)

                val todayStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                val demoAppointment = Appointment(
                    id = "CQ-4821",
                    patientId = "p_rehan",
                    patientName = "Rehan",
                    doctorId = "doc_priya",
                    doctorName = "Dr. Priya Mehta",
                    specialty = "Cardiology",
                    hospitalId = "hosp_sunrise",
                    hospitalName = "Sunrise Multi-Speciality Hospital",
                    date = todayStr,
                    timeSlot = "10:30 AM",
                    visitMode = VisitMode.LIVE_QUEUE.name,
                    tokenNumber = "A-27",
                    status = "CONFIRMED",
                    estimatedWaitMinutes = 9,
                    peopleAhead = 3,
                    roomNumber = "Room 2"
                )
                appointmentDao.insertAppointment(demoAppointment)

                val initNotif = NotificationItem(
                    id = "notif_welcome",
                    title = "Appointment Confirmed",
                    message = "Your live queue token A-27 with Dr. Priya Mehta has been issued. 3 people ahead (~9 min).",
                    type = "APPOINTMENT_CONFIRMED",
                    timestamp = System.currentTimeMillis() - 5 * 60 * 1000,
                    isRead = false
                )
                notificationDao.insertNotification(initNotif)
            }
        }
    }

    fun getQueueForDoctor(doctorId: String): Flow<List<QueuePatient>> = queueDao.getQueueForDoctor(doctorId)
    fun getAllQueuePatients(): Flow<List<QueuePatient>> = queueDao.getAllQueuePatients()
    fun getAppointmentsForPatient(patientId: String = "p_rehan"): Flow<List<Appointment>> = appointmentDao.getAppointmentsForPatient(patientId)
    fun getAllNotifications(): Flow<List<NotificationItem>> = notificationDao.getAllNotifications()

    fun getRecommendationForSpecialist(specialistName: String, city: String? = null): RecommendationResult? {
        var eligibleHospitals = hospitals.filter { hosp ->
            hosp.specialties.any { it.equals(specialistName, ignoreCase = true) }
        }
        if (city != null && city != "All Cities") {
            val cityFiltered = eligibleHospitals.filter { it.city.equals(city, ignoreCase = true) }
            if (cityFiltered.isNotEmpty()) {
                eligibleHospitals = cityFiltered
            }
        }
        if (eligibleHospitals.isEmpty()) return null

        val scoredList = eligibleHospitals.map { hosp ->
            val docs = doctors.filter { it.hospitalId == hosp.id && it.specialty.equals(specialistName, ignoreCase = true) }
            val docCount = docs.size
            val availableDocs = docs.count { it.isAvailable }

            var score = 30
            score += (availableDocs * 4).coerceAtMost(20)
            score += (25 - hosp.waitingCount).coerceIn(0, 25)
            score += ((10 - hosp.distanceKm) * 1.5).toInt().coerceIn(0, 15)
            score += ((hosp.rating - 3.0) * 5).toInt().coerceIn(0, 10)

            val reasons = mutableListOf<String>()
            reasons.add("✓ $specialistName available in ${hosp.city}")
            if (docCount > 1) {
                reasons.add("✓ Multiple experienced $specialistName specialists ($docCount doctors)")
            } else {
                reasons.add("✓ Dedicated senior specialist available")
            }
            if (hosp.waitingCount <= 7) {
                reasons.add("✓ Shorter current OPD queue (${hosp.waitingCount} waiting)")
            } else {
                reasons.add("• Current queue: ${hosp.waitingCount} waiting")
            }
            reasons.add("✓ Convenient distance (${hosp.distanceKm} km away)")
            reasons.add("✓ Top patient rating (${hosp.rating} ★ from ${hosp.reviewsCount} reviews)")

            RecommendationResult(
                hospital = hosp,
                score = score,
                reasons = reasons,
                specialistName = specialistName,
                doctorCount = docCount
            )
        }

        return scoredList.maxByOrNull { it.score }
    }

    suspend fun receptionistCallNext(doctorId: String) {
        updateDoctorQueueNext(doctorId)
    }

    suspend fun updateDoctorQueueNext(doctorId: String) {
        withContext(Dispatchers.IO) {
            val p24 = queueDao.getQueuePatientByToken("A-24")
            val p25 = queueDao.getQueuePatientByToken("A-25")
            val p26 = queueDao.getQueuePatientByToken("A-26")
            val p27 = queueDao.getQueuePatientByToken("A-27")
            val p28 = queueDao.getQueuePatientByToken("A-28")

            if (p24 != null && p24.status == QueueStatus.WITH_DOCTOR.name) {
                queueDao.updateStatus(p24.id, QueueStatus.COMPLETED.name)
                if (p25 != null) {
                    queueDao.updateStatus(p25.id, QueueStatus.WITH_DOCTOR.name)
                }
                sendNotification(
                    title = "Queue Updated",
                    message = "Patient A-24 completed consultation. Queue is moving forward.",
                    type = "QUEUE_UPDATE"
                )
                _liveAlertEvents.emit("Queue updated: A-24 completed")
                updatePatientAppointmentStatus(2, 6)
                return@withContext
            }

            if (p25 != null && p25.status == QueueStatus.WITH_DOCTOR.name) {
                queueDao.updateStatus(p25.id, QueueStatus.COMPLETED.name)
                if (p26 != null) {
                    queueDao.updateStatus(p26.id, QueueStatus.WITH_DOCTOR.name)
                }
                if (p27 != null) {
                    queueDao.updateStatus(p27.id, QueueStatus.ALMOST_YOUR_TURN.name)
                }
                sendNotification(
                    title = "Your Turn is Approaching! 🔔",
                    message = "You are next in queue! Please proceed near Room 2 for Dr. Priya Mehta.",
                    type = "TURN_APPROACHING"
                )
                _liveAlertEvents.emit("🔔 Your turn is approaching!")
                updatePatientAppointmentStatus(1, 3)
                return@withContext
            }

            if (p26 != null && p26.status == QueueStatus.WITH_DOCTOR.name) {
                queueDao.updateStatus(p26.id, QueueStatus.COMPLETED.name)
                if (p27 != null) {
                    queueDao.updateStatus(p27.id, QueueStatus.CALLED.name)
                }
                sendNotification(
                    title = "Please Proceed to Room 2 🩺",
                    message = "Token A-27 called! Dr. Priya Mehta is ready for your consultation.",
                    type = "ROOM_CALL"
                )
                _liveAlertEvents.emit("📢 Token A-27: Please proceed to Room 2!")
                updatePatientAppointmentStatus(0, 0, "CALLED")
                return@withContext
            }

            if (p27 != null && p27.status == QueueStatus.CALLED.name) {
                queueDao.updateStatus(p27.id, QueueStatus.WITH_DOCTOR.name)
                sendNotification(
                    title = "Consultation In Progress",
                    message = "You are currently consulting with Dr. Priya Mehta in Room 2.",
                    type = "ROOM_CALL"
                )
                _liveAlertEvents.emit("Consultation started with Dr. Priya Mehta")
                updatePatientAppointmentStatus(0, 0, "WITH_DOCTOR")
                return@withContext
            }

            if (p27 != null && p27.status == QueueStatus.WITH_DOCTOR.name) {
                queueDao.updateStatus(p27.id, QueueStatus.COMPLETED.name)
                if (p28 != null) {
                    queueDao.updateStatus(p28.id, QueueStatus.WITH_DOCTOR.name)
                }
                sendNotification(
                    title = "Consultation Completed",
                    message = "Thank you Rehan! Your consultation with Dr. Priya Mehta is complete. Digital prescription is ready.",
                    type = "COMPLETED"
                )
                _liveAlertEvents.emit("✓ Consultation completed!")
                updatePatientAppointmentStatus(0, 0, "COMPLETED")
                return@withContext
            }
        }
    }

    private suspend fun updatePatientAppointmentStatus(ahead: Int, waitMins: Int, queueStatus: String = "WAITING") {
        val appt = appointmentDao.getAppointmentById("CQ-4821")
        if (appt != null) {
            val updated = appt.copy(
                peopleAhead = ahead,
                estimatedWaitMinutes = waitMins,
                status = queueStatus
            )
            appointmentDao.updateAppointment(updated)
        }
    }

    suspend fun doctorStartConsultation(doctorId: String, patientToken: String) {
        withContext(Dispatchers.IO) {
            val patient = queueDao.getQueuePatientByToken(patientToken)
            if (patient != null) {
                queueDao.updateStatus(patient.id, QueueStatus.WITH_DOCTOR.name)
                if (patientToken == "A-27") {
                    updatePatientAppointmentStatus(0, 0, "WITH_DOCTOR")
                    sendNotification(
                        title = "Consultation In Progress",
                        message = "Dr. Priya Mehta has started your consultation in Room 2.",
                        type = "ROOM_CALL"
                    )
                }
                _liveAlertEvents.emit("Doctor started consultation for $patientToken")
            }
        }
    }

    suspend fun doctorCompleteConsultation(doctorId: String, patientToken: String) {
        withContext(Dispatchers.IO) {
            val patient = queueDao.getQueuePatientByToken(patientToken)
            if (patient != null) {
                queueDao.updateStatus(patient.id, QueueStatus.COMPLETED.name)
                if (patientToken == "A-27") {
                    updatePatientAppointmentStatus(0, 0, "COMPLETED")
                    sendNotification(
                        title = "Consultation Completed",
                        message = "Consultation completed. We hope you have a speedy recovery!",
                        type = "COMPLETED"
                    )
                }
                _liveAlertEvents.emit("Doctor completed consultation for $patientToken")
            }
            updateDoctorQueueNext(doctorId)
        }
    }

    suspend fun receptionistRecallPatient(patientToken: String) {
        withContext(Dispatchers.IO) {
            val patient = queueDao.getQueuePatientByToken(patientToken)
            if (patient != null) {
                queueDao.updateStatus(patient.id, QueueStatus.CALLED.name)
                sendNotification(
                    title = "Final Call: Token $patientToken",
                    message = "Please proceed immediately to ${patient.roomNumber}.",
                    type = "ROOM_CALL"
                )
                _liveAlertEvents.emit("Recalling patient $patientToken")
            }
        }
    }

    suspend fun receptionistSkipPatient(patientToken: String) {
        withContext(Dispatchers.IO) {
            val patient = queueDao.getQueuePatientByToken(patientToken)
            if (patient != null) {
                queueDao.updateStatus(patient.id, QueueStatus.SKIPPED.name)
                sendNotification(
                    title = "Queue Notice: Token $patientToken Skipped",
                    message = "Patient was temporarily skipped. Please check in with front desk upon arrival.",
                    type = "QUEUE_UPDATE"
                )
                _liveAlertEvents.emit("Skipped patient $patientToken")
            }
        }
    }

    suspend fun addWalkInPatient(
        doctorId: String,
        patientName: String,
        patientPhone: String,
        chiefComplaint: String
    ): QueuePatient {
        return withContext(Dispatchers.IO) {
            val tokenNum = "A-" + ((30..99).random())
            val patient = QueuePatient(
                id = UUID.randomUUID().toString(),
                doctorId = doctorId,
                doctorName = doctors.find { it.id == doctorId }?.name ?: "Doctor",
                tokenNumber = tokenNum,
                sequenceNumber = (30..99).random(),
                patientName = patientName,
                patientPhone = patientPhone,
                chiefComplaint = chiefComplaint,
                status = QueueStatus.WAITING.name,
                roomNumber = "Room 2",
                joinedTime = System.currentTimeMillis(),
                estimatedWaitMinutes = 18
            )
            queueDao.insertQueuePatient(patient)
            sendNotification(
                title = "New Patient Added",
                message = "Walk-in patient $patientName added as token $tokenNum.",
                type = "QUEUE_UPDATE"
            )
            patient
        }
    }

    suspend fun bookAppointment(
        patientId: String = "p_rehan",
        patientName: String = "Rehan",
        doctor: Doctor,
        hospital: Hospital,
        date: String,
        timeSlot: String,
        visitMode: VisitMode
    ): Appointment {
        return withContext(Dispatchers.IO) {
            val appointmentId = "CQ-" + (1000..9999).random()
            val tokenNum = "A-" + (27)
            val estWait = if (visitMode == VisitMode.LIVE_QUEUE) 9 else 0
            val ahead = if (visitMode == VisitMode.LIVE_QUEUE) 3 else 0

            val appt = Appointment(
                id = appointmentId,
                patientId = patientId,
                patientName = patientName,
                doctorId = doctor.id,
                doctorName = doctor.name,
                specialty = doctor.specialty,
                hospitalId = hospital.id,
                hospitalName = hospital.name,
                date = date,
                timeSlot = timeSlot,
                visitMode = visitMode.name,
                tokenNumber = tokenNum,
                status = "CONFIRMED",
                estimatedWaitMinutes = estWait,
                peopleAhead = ahead,
                roomNumber = doctor.roomNumber
            )
            appointmentDao.insertAppointment(appt)

            sendNotification(
                title = "Appointment Confirmed ✓",
                message = "${doctor.name} at ${hospital.name}. Mode: ${visitMode.title}. Token: $tokenNum.",
                type = "APPOINTMENT_CONFIRMED"
            )
            _liveAlertEvents.emit("Appointment Confirmed: $appointmentId")
            appt
        }
    }

    suspend fun cancelAppointment(appointmentId: String) {
        withContext(Dispatchers.IO) {
            appointmentDao.updateAppointmentStatus(appointmentId, "CANCELLED")
            sendNotification(
                title = "Appointment Cancelled",
                message = "Your appointment ($appointmentId) has been cancelled.",
                type = "QUEUE_UPDATE"
            )
        }
    }

    suspend fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        withContext(Dispatchers.IO) {
            appointmentDao.rescheduleAppointment(appointmentId, newDate, newTime)
            sendNotification(
                title = "Appointment Rescheduled",
                message = "Your appointment ($appointmentId) is rescheduled to $newDate at $newTime.",
                type = "APPOINTMENT_CONFIRMED"
            )
        }
    }

    suspend fun sendNotification(title: String, message: String, type: String) {
        val item = NotificationItem(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        notificationDao.insertNotification(item)
    }

    suspend fun markNotificationAsRead(id: String) {
        withContext(Dispatchers.IO) {
            notificationDao.markAsRead(id)
        }
    }

    suspend fun resetDemoQueue() {
        withContext(Dispatchers.IO) {
            queueDao.clearQueue()
            seedInitialDataIfEmpty()
            _liveAlertEvents.emit("Demo Queue Reset to Initial State")
        }
    }

    fun getDoctorsForHospital(hospitalId: String): List<Doctor> {
        val specific = doctors.filter { it.hospitalId == hospitalId }
        if (specific.isNotEmpty()) return specific
        val hosp = hospitals.find { it.id == hospitalId } ?: return emptyList()

        val doctorRoster = listOf(
            Triple("Dr. Rajesh Malhotra", "Senior Consultant", "doctor_rajesh"),
            Triple("Dr. Sunita Kulkarni", "Director & HOD", "doctor_priya"),
            Triple("Dr. Arvind Swaminathan", "Associate Specialist", "doctor_rajesh"),
            Triple("Dr. Sneha Mukherjee", "Consultant Specialist", "doctor_priya")
        )

        return hosp.specialties.take(3).mapIndexed { idx, spec ->
            val roster = doctorRoster[idx % doctorRoster.size]
            Doctor(
                id = "doc_${hosp.id}_$idx",
                hospitalId = hosp.id,
                hospitalName = hosp.name,
                city = hosp.city,
                name = roster.first,
                specialty = spec,
                title = "${roster.second} $spec",
                rating = 4.7 + (idx * 0.1).coerceAtMost(0.2),
                reviewsCount = 1100 + idx * 350,
                experienceYears = 12 + idx * 3,
                fee = "₹${750 + idx * 100}",
                roomNumber = "OPD Room ${idx + 2}",
                isAvailable = true,
                currentQueueSize = (hosp.waitingCount - idx).coerceAtLeast(2),
                estWaitMinutes = (hosp.estWaitMinutes - idx * 3).coerceAtLeast(6),
                nextAvailableSlot = "10:${30 + idx * 15} AM",
                imageDrawableName = roster.third,
                education = "MBBS, MD / DNB - $spec (Premier Medical Faculty)",
                bio = "Senior medical consultant at ${hosp.name} specialized in outpatient diagnostics, patient counseling, and priority clinical treatments."
            )
        }
    }
}
