package com.marina.hotelmanagement.data.relationships

import androidx.room.Embedded
import androidx.room.Relation
import com.marina.hotelmanagement.data.entities.Booking
import com.marina.hotelmanagement.data.entities.Payment

data class BookingWithPayments(
    @Embedded val booking: Booking,
    @Relation(
        parentColumn = "bookingId",
        entityColumn = "bookingId"
    )
    val payments: List<Payment>
)