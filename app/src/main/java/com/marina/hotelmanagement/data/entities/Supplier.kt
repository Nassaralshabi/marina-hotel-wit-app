package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true)
    val supplierId: Long = 0,
    val supplierName: String,
    val supplierPhone: String,
    val supplierAddress: String? = null,
    val supplierType: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)