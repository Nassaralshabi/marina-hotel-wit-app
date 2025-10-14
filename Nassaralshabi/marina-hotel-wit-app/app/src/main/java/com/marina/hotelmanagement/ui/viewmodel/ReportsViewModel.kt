package com.marina.hotelmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marina.hotelmanagement.data.entities.Booking
import com.marina.hotelmanagement.data.entities.Expense
import com.marina.hotelmanagement.data.entities.Payment
import com.marina.hotelmanagement.data.repository.HotelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ReportsViewModel(private val repository: HotelRepository) : ViewModel() {

    // Date range
    private val _startDate = MutableStateFlow(getStartOfMonth())
    val startDate: StateFlow<Long> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow(getEndOfDay())
    val endDate: StateFlow<Long> = _endDate.asStateFlow()

    // Report data
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _revenueInfo = MutableStateFlow<HotelRepository.RevenueInfo?>(null)
    val revenueInfo: StateFlow<HotelRepository.RevenueInfo?> = _revenueInfo.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Chart data
    private val _dailyRevenueData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val dailyRevenueData: StateFlow<List<Pair<String, Double>>> = _dailyRevenueData.asStateFlow()

    private val _occupancyData = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val occupancyData: StateFlow<List<Pair<String, Int>>> = _occupancyData.asStateFlow()

    init {
        loadReportsData()
    }

    fun updateDateRange(startDate: Long, endDate: Long) {
        _startDate.value = startDate
        _endDate.value = endDate
        loadReportsData()
    }

    fun loadReportsData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                loadPayments()
                loadExpenses()
                loadRevenueInfo()
                loadChartData()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadPayments() {
        viewModelScope.launch {
            repository.getPaymentsByDateRange(_startDate.value, _endDate.value).collect { payments ->
                _payments.value = payments
            }
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getExpensesByDateRange(_startDate.value, _endDate.value).collect { expenses ->
                _expenses.value = expenses
            }
        }
    }

    private fun loadRevenueInfo() {
        viewModelScope.launch {
            val revenueInfo = repository.getRevenueBetweenDates(_startDate.value, _endDate.value)
            _revenueInfo.value = revenueInfo
        }
    }

    private fun loadChartData() {
        viewModelScope.launch {
            // Generate sample daily revenue data
            val dailyData = generateDailyRevenueData()
            _dailyRevenueData.value = dailyData

            // Generate occupancy data
            val occupancyData = generateOccupancyData()
            _occupancyData.value = occupancyData
        }
    }

    private fun generateDailyRevenueData(): List<Pair<String, Double>> {
        val data = mutableListOf<Pair<String, Double>>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _startDate.value
        
        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = _endDate.value
        
        while (calendar.timeInMillis <= endCalendar.timeInMillis) {
            val dateStr = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}"
            val revenue = (100..500).random().toDouble() + Math.random()
            data.add(dateStr to revenue)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return data
    }

    private fun generateOccupancyData(): List<Pair<String, Int>> {
        return listOf(
            "الأحد" to (70..90).random(),
            "الإثنين" to (65..85).random(),
            "الثلاثاء" to (60..80).random(),
            "الأربعاء" to (55..75).random(),
            "الخميس" to (80..95).random(),
            "الجمعة" to (90..100).random(),
            "السبت" to (75..90).random()
        )
    }

    fun getTotalRevenue(): Double = _revenueInfo.value?.totalRevenue ?: 0.0
    fun getCashRevenue(): Double = _revenueInfo.value?.cashRevenue ?: 0.0
    fun getCardRevenue(): Double = _revenueInfo.value?.cardRevenue ?: 0.0
    fun getTotalExpenses(): Double = _revenueInfo.value?.totalExpenses ?: 0.0
    fun getNetProfit(): Double = _revenueInfo.value?.netProfit ?: 0.0

    fun getPaymentCount(): Int = _payments.value.size
    fun getExpenseCount(): Int = _expenses.value.size

    private fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}