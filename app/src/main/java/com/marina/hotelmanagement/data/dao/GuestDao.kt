package com.marina.hotelmanagement.data.dao

import androidx.room.*
import com.marina.hotelmanagement.data.entities.Guest
import kotlinx.coroutines.flow.Flow

@Dao
interface GuestDao {
    @Query("SELECT * FROM guests ORDER BY guestId DESC")
    fun getAllGuests(): Flow<List<Guest>>

    @Query("SELECT * FROM guests WHERE guestId = :guestId")
    suspend fun getGuestById(guestId: Long): Guest?

    @Query("SELECT * FROM guests WHERE guestPhone = :phone LIMIT 1")
    suspend fun getGuestByPhone(phone: String): Guest?

    @Query("SELECT * FROM guests WHERE guestName LIKE '%' || :query || '%' OR guestPhone LIKE '%' || :query || '%'")
    fun searchGuests(query: String): Flow<List<Guest>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGuest(guest: Guest): Long

    @Update
    suspend fun updateGuest(guest: Guest)

    @Delete
    suspend fun deleteGuest(guest: Guest)
}