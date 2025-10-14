package com.marina.hotelmanagement.data.dao

import androidx.room.*
import com.marina.hotelmanagement.data.entities.Room
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms ORDER BY roomNumber")
    fun getAllRooms(): Flow<List<Room>>

    @Query("SELECT * FROM rooms WHERE roomNumber = :roomNumber")
    suspend fun getRoomByNumber(roomNumber: String): Room?

    @Query("SELECT * FROM rooms WHERE status = 'شاغرة' ORDER BY roomNumber")
    fun getAvailableRooms(): Flow<List<Room>>

    @Query("SELECT * FROM rooms WHERE status = 'محجوزة' ORDER BY roomNumber")
    fun getOccupiedRooms(): Flow<List<Room>>

    @Query("SELECT * FROM rooms WHERE status = 'صيانة' ORDER BY roomNumber")
    fun getMaintenanceRooms(): Flow<List<Room>>

    @Query("UPDATE rooms SET status = :status WHERE roomNumber = :roomNumber")
    suspend fun updateRoomStatus(roomNumber: String, status: String)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRoom(room: Room)

    @Update
    suspend fun updateRoom(room: Room)

    @Delete
    suspend fun deleteRoom(room: Room)

    @Query("SELECT COUNT(*) FROM rooms")
    fun getRoomCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM rooms WHERE status = 'شاغرة'")
    fun getAvailableRoomCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM rooms WHERE status = 'محجوزة'")
    fun getOccupiedRoomCount(): Flow<Int>
}