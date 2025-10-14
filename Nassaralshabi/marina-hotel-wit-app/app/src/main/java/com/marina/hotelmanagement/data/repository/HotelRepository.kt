package com.marina.hotelmanagement.data.repository

import androidx.sqlite.db.SupportSQLiteDatabase
import com.marina.hotelmanagement.data.dao.*
import com.marina.hotelmanagement.data.entities.*
import com.marina.hotelmanagement.data.relationships.BookingDetails
import com.marina.hotelmanagement.data.relationships.BookingWithGuest
import com.marina.hotelmanagement.data.relationships.BookingWithPayments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.util.Date

class HotelRepository(
    private val guestDao: GuestDao,
    private val roomDao: RoomDao,
    private val bookingDao: BookingDao,
    private val paymentDao: PaymentDao,
    private val bookingNoteDao: BookingNoteDao,
    private val expenseDao: ExpenseDao,
    private val userDao: UserDao
) {

    // Guest operations
    fun getAllGuests(): Flow<List<Guest>> = guestDao.getAllGuests()

    suspend fun getGuestById(guestId: Long): Guest? = guestDao.getGuestById(guestId)

    suspend fun getGuestByPhone(phone: String): Guest? = guestDao.getGuestByPhone(phone)

    fun searchGuests(query: String): Flow<List<Guest>> = guestDao.searchGuests(query)

    suspend fun createGuest(guest: Guest): Long = guestDao.insertGuest(guest)

    suspend fun updateGuest(guest: Guest) = guestDao.updateGuest(guest)

    suspend fun deleteGuest(guest: Guest) = guestDao.deleteGuest(guest)

    // Room operations
    fun getAllRooms(): Flow<List<Room>> = roomDao.getAllRooms()

    suspend fun getRoomByNumber(roomNumber: String): Room? = roomDao.getRoomByNumber(roomNumber)

    fun getAvailableRooms(): Flow<List<Room>> = roomDao.getAvailableRooms()

    fun getOccupiedRooms(): Flow<List<Room>> = roomDao.getOccupiedRooms()

    fun getMaintenanceRooms(): Flow<List<Room>> = roomDao.getMaintenanceRooms()

    suspend fun updateRoomStatus(roomNumber: String, status: String) = roomDao.updateRoomStatus(roomNumber, status)

    // Booking operations with transaction logic
    suspend fun createBooking(guest: Guest, booking: Booking): Long = withContext(Dispatchers.IO) {
        // 1. Check if room is available
        val room = roomDao.getRoomByNumber(booking.roomNumber)
            ?: throw IllegalStateException("غرفة ${booking.roomNumber} غير موجودة")

        if (room.status != "شاغرة") {
            throw IllegalStateException("غرفة ${booking.roomNumber} غير متوفرة")
        }

        // 2. Check if guest exists by phone, use existing guest if found
        var guestId = guest.guestId
        val existingGuest = guestDao.getGuestByPhone(guest.guestPhone)

        if (existingGuest != null) {
            guestId = existingGuest.guestId
        } else {
            guestId = guestDao.insertGuest(guest)
        }

        // 3. Create booking with correct guestId
        val newBooking = booking.copy(guestId = guestId)
        val bookingId = bookingDao.insertBooking(newBooking)

        // 4. Update room status to occupied
        roomDao.updateRoomStatus(booking.roomNumber, "محجوزة")

        bookingId
    }

    suspend fun checkOut(bookingId: Long) = withContext(Dispatchers.IO) {
        // 1. Get booking details
        val booking = bookingDao.getBookingDetails(bookingId)
            ?: throw IllegalStateException("الحجز غير موجود")

        // 2. Update booking status and set checkout date
        bookingDao.completeBooking(bookingId, Date().time)

        // 3. Update room status back to available
        roomDao.updateRoomStatus(booking.booking.roomNumber, "شاغرة")
    }

    fun getActiveBookingsWithGuests(): Flow<List<BookingWithGuest>> = bookingDao.getActiveBookingsWithGuests()

    suspend fun getBookingDetails(bookingId: Long): BookingDetails? = bookingDao.getBookingDetails(bookingId)

    suspend fun getBookingWithGuest(bookingId: Long): BookingWithGuest? = bookingDao.getBookingWithGuest(bookingId)

    suspend fun getBookingWithPayments(bookingId: Long): BookingWithPayments? = bookingDao.getBookingWithPayments(bookingId)

    fun getActiveBookings(): Flow<List<Booking>> = bookingDao.getActiveBookings()

    fun getBookingsByGuest(guestId: Long): Flow<List<Booking>> = bookingDao.getBookingsByGuest(guestId)

    suspend fun getActiveBookingByRoom(roomNumber: String): Booking? = bookingDao.getActiveBookingByRoom(roomNumber)

    suspend fun completeBooking(bookingId: Long) = bookingDao.completeBooking(bookingId, Date().time)

    // Payment operations
    fun getPaymentsByBooking(bookingId: Long): Flow<List<Payment>> = paymentDao.getPaymentsByBooking(bookingId)

    fun getTotalPaymentsByBooking(bookingId: Long): Flow<Double?> = paymentDao.getTotalPaymentsByBooking(bookingId)

    suspend fun addPayment(payment: Payment): Long = paymentDao.insertPayment(payment)

    suspend fun deletePayment(payment: Payment) = paymentDao.deletePayment(payment)

    // Booking note operations
    fun getNotesByBooking(bookingId: Long): Flow<List<BookingNote>> = bookingNoteDao.getNotesByBooking(bookingId)

    suspend fun getActiveAlerts(): List<BookingNote> = 
        bookingNoteDao.getActiveAlerts(System.currentTimeMillis())

    suspend fun getHighPriorityAlerts(): List<BookingNote> = 
        bookingNoteDao.getHighPriorityAlerts(System.currentTimeMillis())

    suspend fun addBookingNote(note: BookingNote): Long = bookingNoteDao.insertNote(note)

    // Expense operations
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> = 
        expenseDao.getExpensesByDateRange(startDate, endDate)

    fun getExpenseTypes(): Flow<List<String>> = expenseDao.getExpenseTypes()

    suspend fun addExpense(expense: Expense): Long = expenseDao.insertExpense(expense)

    // User operations
    suspend fun authenticateUser(username: String, password: String): User? {
        val user = userDao.getActiveUserByUsername(username)
        return if (user != null && user.password == password) {
            user
        } else {
            null
        }
    }

    fun getAllActiveUsers(): Flow<List<User>> = userDao.getAllActiveUsers()

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)

    suspend fun createUser(user: User): Long = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun deactivateUser(userId: Long) = userDao.deactivateUser(userId)

    // Dashboard statistics
    fun getDashboardStats(): Flow<DashboardStats> {
        return combine(
            roomDao.getRoomCount(),
            roomDao.getAvailableRoomCount(),
            roomDao.getOccupiedRoomCount(),
            bookingDao.getActiveBookingCount(),
            ::DashboardStats
        )
    }

    // Revenue calculation
    suspend fun getRevenueBetweenDates(startDate: Long, endDate: Long): RevenueInfo {
        val totalPayments = paymentDao.getTotalPaymentsByDateRange(startDate, endDate) ?: 0.0
        val cashPayments = paymentDao.getTotalPaymentsByMethodAndDateRange("نقدي", startDate, endDate) ?: 0.0
        val cardPayments = paymentDao.getTotalPaymentsByMethodAndDateRange("بطاقة", startDate, endDate) ?: 0.0
        val totalExpenses = expenseDao.getTotalExpensesByDateRange(startDate, endDate) ?: 0.0
        
        return RevenueInfo(
            totalRevenue = totalPayments,
            cashRevenue = cashPayments,
            cardRevenue = cardPayments,
            totalExpenses = totalExpenses,
            netProfit = totalPayments - totalExpenses
        )
    }

    data class DashboardStats(
        val totalRooms: Int,
        val availableRooms: Int,
        val occupiedRooms: Int,
        val activeBookings: Int
    )

    data class RevenueInfo(
        val totalRevenue: Double,
        val cashRevenue: Double,
        val cardRevenue: Double,
        val totalExpenses: Double,
        val netProfit: Double
    )
}