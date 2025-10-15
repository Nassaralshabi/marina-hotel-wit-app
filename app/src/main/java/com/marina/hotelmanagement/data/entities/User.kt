package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val username: String,
    val password: String, // Should be hashed in real implementation
    val fullName: String,
    val role: String, // "مدير", "موظف استقبال", "محاسب"
    val phone: String? = null,
    val email: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)