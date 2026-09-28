package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.QueuePatient
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {
    @Query("SELECT * FROM queue_patients WHERE doctorId = :doctorId ORDER BY sequenceNumber ASC")
    fun getQueueForDoctor(doctorId: String): Flow<List<QueuePatient>>

    @Query("SELECT * FROM queue_patients ORDER BY sequenceNumber ASC")
    fun getAllQueuePatients(): Flow<List<QueuePatient>>

    @Query("SELECT * FROM queue_patients WHERE id = :id LIMIT 1")
    suspend fun getQueuePatientById(id: String): QueuePatient?

    @Query("SELECT * FROM queue_patients WHERE tokenNumber = :tokenNumber LIMIT 1")
    suspend fun getQueuePatientByToken(tokenNumber: String): QueuePatient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueuePatients(patients: List<QueuePatient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueuePatient(patient: QueuePatient)

    @Update
    suspend fun updateQueuePatient(patient: QueuePatient)

    @Query("UPDATE queue_patients SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM queue_patients WHERE id = :id")
    suspend fun deletePatient(id: String)

    @Query("DELETE FROM queue_patients")
    suspend fun clearQueue()
}
