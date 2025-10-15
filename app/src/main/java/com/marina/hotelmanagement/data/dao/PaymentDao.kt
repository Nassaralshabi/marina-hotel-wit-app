package com.marina.hotelmanagement.data.dao

import androidx.room.*
import com.marina.hotelmanagement.data.entities.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE bookingId = :bookingId ORDER BY paymentDate DESC")
    fun getPaymentsByBooking(bookingId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE paymentDate >= :startDate AND paymentDate <= :endDate ORDER BY paymentDate DESC")
    fun getPaymentsByDateRange(startDate: Long, endDate: Long): Flow<List<Payment>>

    @Query("SELECT SUM(amount) FROM payments WHERE bookingId = :bookingId")
    fun getTotalPaymentsByBooking(bookingId: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM payments WHERE paymentDate >= :startDate AND paymentDate <= :endDate")
    suspend fun getTotalPaymentsByDateRange(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM payments WHERE paymentMethod = :paymentMethod AND paymentDate >= :startDate AND paymentDate <= :endDate")
    suspend fun getTotalPaymentsByMethodAndDateRange(paymentMethod: String, startDate: Long, endDate: Long): Double?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPayment(payment: Payment): Long

    @Update
    suspend fun updatePayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)

    @Query("SELECT COUNT(*) FROM payments WHERE bookingId = :bookingId")
    suspend fun getPaymentCountByBooking(bookingId: Long): Int
}