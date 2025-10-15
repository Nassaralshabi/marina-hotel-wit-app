package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class Room(
    @PrimaryKey
    val roomNumber: String,
    val type: String,
    val price: Double,
    val status: String // Allowed values: "شاغرة", "محجوزة", "صيانة"
)