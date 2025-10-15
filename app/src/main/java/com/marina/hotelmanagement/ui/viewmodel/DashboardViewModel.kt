package com.marina.hotelmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marina.hotelmanagement.data.entities.Booking
import com.marina.hotelmanagement.data.entities.BookingNote
import com.marina.hotelmanagement.data.entities.Room
import com.marina.hotelmanagement.data.repository.HotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: HotelRepository) : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    private val _activeBookings = MutableStateFlow<List<Booking>>(emptyList())
    val activeBookings: StateFlow<List<Booking>> = _activeBookings.asStateFlow()

    private val _activeAlerts = MutableStateFlow<List<BookingNote>>(emptyList())
    val activeAlerts: StateFlow<List<BookingNote>> = _activeAlerts.asStateFlow()

    private val _dashboardStats = MutableStateFlow(HotelRepository.DashboardStats(0, 0, 0, 0))
    val dashboardStats: StateFlow<HotelRepository.DashboardStats> = _dashboardStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Load rooms
                launch {
                    repository.getAllRooms().collect { rooms ->
                        _rooms.value = rooms
                    }
                }

                // Load active bookings
                launch {
                    repository.getActiveBookings().collect { bookings ->
                        _activeBookings.value = bookings
                    }
                }

                // Load dashboard stats
                launch {
                    repository.getDashboardStats().collect { stats ->
                        _dashboardStats.value = stats
                    }
                }

                // Load active alerts
                loadActiveAlerts()

            } catch (e: Exception) {
                // Handle error appropriately
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadActiveAlerts() {
        viewModelScope.launch {
            try {
                val alerts = repository.getActiveAlerts()
                _activeAlerts.value = alerts
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getHighPriorityAlerts() {
        viewModelScope.launch {
            try {
                val alerts = repository.getHighPriorityAlerts()
                _activeAlerts.value = alerts
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun dismissAlert(noteId: Long) {
        viewModelScope.launch {
            try {
                // Assuming we have a method to deactivate notes
                loadActiveAlerts() // Refresh alerts
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRoomsByFloor(floor: Int): List<Room> {
        val floorPrefix = floor.toString()
        return _rooms.value.filter { room ->
            room.roomNumber.startsWith(floorPrefix)
        }.sortedBy { it.roomNumber }
    }

    fun getRoomColorStatus(status: String): String {
        return when (status) {
            "شاغرة" -> "#198754" // green
            "محجوزة" -> "#dc3545" // red
            "صيانة" -> "#ffc107" // yellow
            else -> "#6c757d" // gray
        }
    }
}