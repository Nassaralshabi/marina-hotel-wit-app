package com.marina.hotelmanagement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val employeeId: Long = 0,
    val employeeName: String,
    val employeeIdNumber: String,
    val employeePhone: String,
    val employeePosition: String,
    val salary: Double,
    val hireDate: Long,
    val status: String = "نشط" // "نشط", "غير نشط"
)