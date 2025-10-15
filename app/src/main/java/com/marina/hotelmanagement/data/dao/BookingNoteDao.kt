package com.marina.hotelmanagement.data.dao

import androidx.room.*
import com.marina.hotelmanagement.data.entities.BookingNote
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingNoteDao {
    @Query("SELECT * FROM booking_notes WHERE bookingId = :bookingId ORDER BY noteId DESC")
    fun getNotesByBooking(bookingId: Long): Flow<List<BookingNote>>

    @Query("SELECT * FROM booking_notes WHERE isActive = 1 AND (alertUntil IS NULL OR alertUntil >= :currentTime) ORDER BY noteId DESC")
    suspend fun getActiveAlerts(currentTime: Long): List<BookingNote>

    @Query("SELECT * FROM booking_notes WHERE alertType = :alertType AND isActive = 1 AND (alertUntil IS NULL OR alertUntil >= :currentTime) ORDER BY noteId DESC")
    suspend fun getActiveAlertsByType(alertType: String, currentTime: Long): List<BookingNote>

    @Query("SELECT * FROM booking_notes WHERE alertType = 'عالي' AND isActive = 1 AND (alertUntil IS NULL OR alertUntil >= :currentTime) ORDER BY noteId DESC")
    suspend fun getHighPriorityAlerts(currentTime: Long): List<BookingNote>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNote(note: BookingNote): Long

    @Update
    suspend fun updateNote(note: BookingNote)

    @Delete
    suspend fun deleteNote(note: BookingNote)

    @Query("UPDATE booking_notes SET isActive = 0 WHERE noteId = :noteId")
    suspend fun deactivateNote(noteId: Long)
}