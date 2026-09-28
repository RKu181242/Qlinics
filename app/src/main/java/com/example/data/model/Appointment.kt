package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey
    val id: String,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val specialty: String,
    val hospitalId: String,
    val hospitalName: String,
    val date: String,
    val timeSlot: String,
    val visitMode: String, // FIXED or LIVE_QUEUE
    val tokenNumber: String,
    val status: String, // CONFIRMED, WAITING, ALMOST_YOUR_TURN, CALLED, WITH_DOCTOR, COMPLETED, CANCELLED
    val estimatedWaitMinutes: Int,
    val peopleAhead: Int,
    val roomNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)
