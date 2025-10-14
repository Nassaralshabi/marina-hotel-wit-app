package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guests")
data class Guest(
    @PrimaryKey(autoGenerate = true)
    val guestId: Long = 0,
    val guestName: String,
    val guestIdType: String,
    val guestIdNumber: String,
    val guestPhone: String,
    val guestNationality: String?,
    val createdAt: Long = System.currentTimeMillis()
)