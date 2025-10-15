package com.marina.hotelmanagement

import android.app.Application
import androidx.work.Configuration
import com.marina.hotelmanagement.data.database.HotelDatabase
import com.marina.hotelmanagement.data.repository.HotelRepository

class HotelApplication : Application(), Configuration.Provider {

    var repository: HotelRepository? = null
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize database and repository
        val database = HotelDatabase.getDatabase(this)
        repository = HotelRepository(
            guestDao = database.guestDao(),
            roomDao = database.roomDao(),
            bookingDao = database.bookingDao(),
            paymentDao = database.paymentDao(),
            bookingNoteDao = database.bookingNoteDao(),
            expenseDao = database.expenseDao(),
            userDao = database.userDao()
        )
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
}