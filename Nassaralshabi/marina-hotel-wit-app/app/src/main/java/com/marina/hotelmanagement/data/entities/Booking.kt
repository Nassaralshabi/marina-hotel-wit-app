package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = Guest::class,
            parentColumns = ["guestId"],
            childColumns = ["guestId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Room::class,
            parentColumns = ["roomNumber"],
            childColumns = ["roomNumber"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["guestId"]),
        Index(value = ["roomNumber"])
    ]
)
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val bookingId: Long = 0,
    val guestId: Long,
    val roomNumber: String,
    val checkinDate: Long,
    val checkoutDate: Long? = null,
    val status: String, // Allowed values: "محجوزة", "مكتملة", "ملغاة"
    val notes: String? = null,
    val calculatedNights: Int = 1
)