package com.example.data.model

data class Hospital(
    val id: String,
    val name: String,
    val city: String = "Delhi NCR",
    val state: String = "Delhi",
    val type: HospitalType,
    val rating: Double,
    val reviewsCount: String,
    val distanceKm: Double,
    val waitingCount: Int,
    val estWaitMinutes: Int,
    val emergencyAvailable: Boolean = true,
    val address: String,
    val phone: String = "+91 11 2658 8500",
    val landmark: String = "Near Metro Station",
    val openingHours: String = "Open 24 Hours (OPD 08:00 AM - 08:00 PM)",
    val specialties: List<String>,
    val facilities: List<String>,
    val lat: Double,
    val lng: Double,
    val imageDrawableName: String = "hospital_sunrise"
)
