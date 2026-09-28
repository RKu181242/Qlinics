package com.example.data.model

data class Doctor(
    val id: String,
    val hospitalId: String,
    val hospitalName: String,
    val city: String = "Delhi NCR",
    val name: String,
    val specialty: String,
    val title: String,
    val rating: Double,
    val reviewsCount: Int,
    val experienceYears: Int,
    val fee: String,
    val roomNumber: String,
    val isAvailable: Boolean,
    val currentQueueSize: Int,
    val estWaitMinutes: Int,
    val nextAvailableSlot: String,
    val imageDrawableName: String,
    val education: String = "MBBS, MD - Cardiology (AIIMS)",
    val bio: String = "Dedicated specialist with over a decade of clinical OPD experience in preventive and interventional cardiovascular medicine."
)
