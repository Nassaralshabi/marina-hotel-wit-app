package com.marina.hotelmanagement.data.dao

import androidx.room.*
import androidx.transaction.Transaction
import com.marina.hotelmanagement.data.entities.Booking
import com.marina.hotelmanagement.data.relationships.BookingDetails
import com.marina.hotelmanagement.data.relationships.BookingWithGuest
import com.marina.hotelmanagement.data.relationships.BookingWithPayments
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Transaction
    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    suspend fun getBookingDetails(bookingId: Long): BookingDetails?

    @Transaction
    @Query("SELECT * FROM bookings WHERE status = 'محجوزة' ORDER BY checkinDate DESC")
    fun getActiveBookingsWithGuests(): Flow<List<BookingWithGuest>>

    @Transaction
    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    suspend fun getBookingWithGuest(bookingId: Long): BookingWithGuest?

    @Transaction
    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId")
    suspend fun getBookingWithPayments(bookingId: Long): BookingWithPayments?

    @Query("SELECT * FROM bookings WHERE status = 'محجوزة'")
    fun getActiveBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE status = 'مكتملة' ORDER BY checkoutDate DESC")
    fun getCompletedBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE status = 'ملغاة' ORDER BY checkinDate DESC")
    fun getCancelledBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE guestId = :guestId ORDER BY checkinDate DESC")
    fun getBookingsByGuest(guestId: Long): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE roomNumber = :roomNumber AND status = 'محجوزة' LIMIT 1")
    suspend fun getActiveBookingByRoom(roomNumber: String): Booking?

    @Query("UPDATE bookings SET status = 'مكتملة', checkoutDate = :checkoutDate WHERE bookingId = :bookingId")
    suspend fun completeBooking(bookingId: Long, checkoutDate: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBooking(booking: Booking): Long

    @Update
    suspend fun updateBooking(booking: Booking)

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Query("SELECT COUNT(*) FROM bookings WHERE status = 'محجوزة'")
    fun getActiveBookingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM bookings WHERE status = 'مكتملة' AND checkoutDate >= :startDate AND checkoutDate <= :endDate")
    suspend fun getCompletedBookingCountByDateRange(startDate: Long, endDate: Long): Int
}