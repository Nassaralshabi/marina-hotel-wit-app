package com.marina.hotelmanagement.data.relationships

import androidx.room.Embedded
import androidx.room.Relation
import com.marina.hotelmanagement.data.entities.Booking
import com.marina.hotelmanagement.data.entities.Guest
import com.marina.hotelmanagement.data.entities.Payment
import com.marina.hotelmanagement.data.entities.Room

data class BookingDetails(
    @Embedded val booking: Booking,
    @Relation(
        parentColumn = "guestId",
        entityColumn = "guestId"
    )
    val guest: Guest,
    @Relation(
        parentColumn = "roomNumber",
        entityColumn = "roomNumber"
    )
    val room: Room,
    @Relation(
        parentColumn = "bookingId",
        entityColumn = "bookingId"
    )
    val payments: List<Payment>
)