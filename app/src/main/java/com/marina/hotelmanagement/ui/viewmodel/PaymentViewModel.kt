package com.marina.hotelmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marina.hotelmanagement.data.entities.BookingDetails
import com.marina.hotelmanagement.data.entities.Payment
import com.marina.hotelmanagement.data.repository.HotelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class PaymentViewModel(private val repository: HotelRepository) : ViewModel() {

    private var bookingId: Long = 0

    // Booking details
    private val _bookingDetails = MutableStateFlow<BookingDetails?>(null)
    val bookingDetails: StateFlow<BookingDetails?> = _bookingDetails.asStateFlow()

    // Payments
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _totalPayments = MutableStateFlow<Double>(0.0)
    val totalPayments: StateFlow<Double> = _totalPayments.asStateFlow()

    // Form fields
    private val _paymentAmount = MutableStateFlow("")
    val paymentAmount: StateFlow<String> = _paymentAmount.asStateFlow()

    private val _paymentMethod = MutableStateFlow("نقدي")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    private val _paymentNotes = MutableStateFlow("")
    val paymentNotes: StateFlow<String> = _paymentNotes.asStateFlow()

    // Form validation
    private val _formErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val formErrors: StateFlow<Map<String, String>> = _formErrors.asStateFlow()

    // Loading and success states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _paymentSuccess = MutableStateFlow(false)
    val paymentSuccess: StateFlow<Boolean> = _paymentSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Checkout state
    private val _canCheckout = MutableStateFlow(false)
    val canCheckout: StateFlow<Boolean> = _canCheckout.asStateFlow()

    fun loadBookingDetails(bookingId: Long) {
        this.bookingId = bookingId
        loadBookingData()
    }

    private fun loadBookingData() {
        viewModelScope.launch {
            try {
                // Load booking details
                launch {
                    repository.getBookingDetails(bookingId).collect { details ->
                        _bookingDetails.value = details
                        checkCheckoutCondition()
                    }
                }

                // Load payments
                launch {
                    repository.getPaymentsByBooking(bookingId).collect { payments ->
                        _payments.value = payments
                    }
                }

                // Load total payments
                launch {
                    repository.getTotalPaymentsByBooking(bookingId).collect { total ->
                        _totalPayments.value = total ?: 0.0
                    }
                }

            } catch (e: Exception) {
                _errorMessage.value = "فشل في تحميل بيانات الحجز"
            }
        }
    }

    fun updatePaymentAmount(amount: String) {
        _paymentAmount.value = amount
        validatePaymentAmount()
    }

    fun updatePaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun updatePaymentNotes(notes: String) {
        _paymentNotes.value = notes
    }

    private fun validatePaymentAmount(): Boolean {
        val errors = mutableMapOf<String, String>()
        
        val amount = _paymentAmount.value.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            errors["amount"] = "المبلغ غير صالح"
        }

        _formErrors.value = errors
        return errors.isEmpty()
    }

    fun addPayment() {
        if (!validatePaymentAmount()) {
            return
        }

        val amount = _paymentAmount.value.toDoubleOrNull() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val payment = Payment(
                    bookingId = bookingId,
                    amount = amount,
                    paymentDate = Date().time,
                    paymentMethod = _paymentMethod.value,
                    notes = _paymentNotes.value.ifEmpty { null }
                )

                val paymentId = repository.addPayment(payment)
                if (paymentId > 0) {
                    _paymentSuccess.value = true
                    // Clear form
                    _paymentAmount.value = ""
                    _paymentNotes.value = ""
                    _formErrors.value = emptyMap()
                    
                    // Refresh data
                    loadBookingData()
                } else {
                    _errorMessage.value = "فشل في إضافة الدفعة"
                }

            } catch (e: Exception) {
                _errorMessage.value = "حدث خطأ أثناء إضافة الدفعة"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            try {
                repository.deletePayment(payment)
                loadBookingData() // Refresh data
            } catch (e: Exception) {
                _errorMessage.value = "فشل في حذف الدفعة"
            }
        }
    }

    fun checkOut() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.checkOut(bookingId)
                loadBookingData() // Refresh data
            } catch (e: Exception) {
                _errorMessage.value = "فشل في إتمام تسجيل الخروج"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkCheckoutCondition() {
        val booking = _bookingDetails.value?.booking
        val totalPaid = _totalPayments.value
        val roomPrice = _bookingDetails.value?.room?.price ?: 0.0
        val calculatedTotal = roomPrice * (_bookingDetails.value?.booking?.calculatedNights ?: 1)
        
        // Can checkout when fully paid and booking is active
        _canCheckout.value = totalPaid >= calculatedTotal && booking?.status == "محجوزة"
    }

    fun getRemainingAmount(): Double {
        val booking = _bookingDetails.value?.booking
        val totalPaid = _totalPayments.value
        val roomPrice = _bookingDetails.value?.room?.price ?: 0.0
        val calculatedTotal = roomPrice * (_bookingDetails.value?.booking?.calculatedNights ?: 1)
        return maxOf(0.0, calculatedTotal - totalPaid)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getPaymentMethods(): List<String> = listOf("نقدي", "بطاقة")
}