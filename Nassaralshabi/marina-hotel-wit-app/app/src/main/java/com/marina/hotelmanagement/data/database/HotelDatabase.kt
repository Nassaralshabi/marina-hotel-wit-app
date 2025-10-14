package com.marina.hotelmanagement.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.marina.hotelmanagement.data.Converters
import com.marina.hotelmanagement.data.dao.*
import com.marina.hotelmanagement.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Database(
    entities = [
        Guest::class,
        Room::class,
        Booking::class,
        Payment::class,
        BookingNote::class,
        Expense::class,
        Employee::class,
        SalaryWithdrawal::class,
        User::class,
        Supplier::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HotelDatabase : RoomDatabase() {

    abstract fun guestDao(): GuestDao
    abstract fun roomDao(): RoomDao
    abstract fun bookingDao(): BookingDao
    abstract fun paymentDao(): PaymentDao
    abstract fun bookingNoteDao(): BookingNoteDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: HotelDatabase? = null

        fun getDatabase(context: Context): HotelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HotelDatabase::class.java,
                    "hotel_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }

            private suspend fun populateDatabase(database: HotelDatabase) {
                // Insert default admin user
                database.userDao().insertUser(
                    User(
                        username = "admin",
                        password = "admin123", // In real app, hash this password
                        fullName = "مدير النظام",
                        role = "مدير",
                        phone = "0000000000",
                        isActive = true
                    )
                )

                // Insert some default rooms if none exist
                val rooms = database.roomDao().getAllRooms()
                if (rooms.first().isEmpty()) {
                    insertDefaultRooms(database)
                }
            }

            private suspend fun insertDefaultRooms(database: HotelDatabase) {
                val defaultRooms = listOf(
                    Room(roomNumber = "101", type = "ثنائي", price = 150.0, status = "شاغرة"),
                    Room(roomNumber = "102", type = "ثنائي", price = 150.0, status = "شاغرة"),
                    Room(roomNumber = "103", type = "فردي", price = 100.0, status = "شاغرة"),
                    Room(roomNumber = "201", type = "ثلاثي", price = 200.0, status = "شاغرة"),
                    Room(roomNumber = "202", type = "ثنائي", price = 150.0, status = "شاغرة"),
                    Room(roomNumber = "203", type = "جناح", price = 300.0, status = "شاغرة"),
                    Room(roomNumber = "301", type = "ثنائي", price = 150.0, status = "شاغرة"),
                    Room(roomNumber = "302", type = "ثلاثي", price = 200.0, status = "شاغرة"),
                    Room(roomNumber = "303", type = "فردي", price = 100.0, status = "شاغرة")
                )

                defaultRooms.forEach { room ->
                    database.roomDao().insertRoom(room)
                }
            }
        }
    }
}