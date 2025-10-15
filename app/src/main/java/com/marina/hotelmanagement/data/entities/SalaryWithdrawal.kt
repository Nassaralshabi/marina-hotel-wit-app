package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "salary_withdrawals",
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = ["employeeId"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["employeeId"])
    ]
)
data class SalaryWithdrawal(
    @PrimaryKey(autoGenerate = true)
    val withdrawalId: Long = 0,
    val employeeId: Long,
    val amount: Double,
    val withdrawalDate: Long,
    val month: String, // Format: "YYYY-MM"
    val notes: String? = null
)