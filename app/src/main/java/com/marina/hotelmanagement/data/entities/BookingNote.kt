package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "booking_notes",
    foreignKeys = [
        ForeignKey(
            entity = Booking::class,
            parentColumns = ["bookingId"],
            childColumns = ["bookingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookingId"])
    ]
)
data class BookingNote(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long = 0,
    val bookingId: Long,
    val noteText: String,
    val alertType: String, // "عالي", "متوسط", "منخفض"
    val alertUntil: Long? = null,
    val isActive: Boolean = true
)