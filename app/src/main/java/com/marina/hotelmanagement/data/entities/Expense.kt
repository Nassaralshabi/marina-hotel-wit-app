package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Long = 0,
    val expenseType: String,
    val description: String,
    val amount: Double,
    val date: Long,
    val relatedSupplierId: Long? = null
)