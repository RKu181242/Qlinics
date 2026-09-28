package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "queue_patients")
data class QueuePatient(
    @PrimaryKey
    val id: String,
    val doctorId: String,
    val doctorName: String,
    val tokenNumber: String,
    val sequenceNumber: Int,
    val patientName: String,
    val patientPhone: String,
    val chiefComplaint: String,
    val status: String, // JOINED, WAITING, ALMOST_YOUR_TURN, CALLED, WITH_DOCTOR, COMPLETED, SKIPPED, CANCELLED
    val roomNumber: String,
    val joinedTime: Long = System.currentTimeMillis(),
    val estimatedWaitMinutes: Int = 0
)
