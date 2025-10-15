package com.marina.hotelmanagement.data.relationships

import androidx.room.Embedded
import androidx.room.Relation
import com.marina.hotelmanagement.data.entities.Booking
import com.marina.hotelmanagement.data.entities.Guest

data class BookingWithGuest(
    @Embedded val booking: Booking,
    @Relation(
        parentColumn = "guestId",
        entityColumn = "guestId"
    )
    val guest: Guest
)