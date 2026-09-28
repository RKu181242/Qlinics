package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val type: String, // QUEUE_UPDATE, TURN_APPROACHING, ROOM_CALL, APPOINTMENT_CONFIRMED, COMPLETED
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
