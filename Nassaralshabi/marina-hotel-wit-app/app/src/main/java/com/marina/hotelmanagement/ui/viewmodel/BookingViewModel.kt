package com.marina.hotelmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marina.hotelmanagement.data.entities.Booking
import com.marina.hotelmanagement.data.entities.Guest
import com.marina.hotelmanagement.data.entities.Room
import com.marina.hotelmanagement.data.repository.HotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class BookingViewModel(private val repository: HotelRepository) : ViewModel() {

    // Form fields
    private val _guestName = MutableStateFlow("")
    val guestName: StateFlow<String> = _guestName.asStateFlow()

    private val _guestIdType = MutableStateFlow("بطاقة شخصية")
    val guestIdType: StateFlow<String> = _guestIdType.asStateFlow()

    private val _guestIdNumber = MutableStateFlow("")
    val guestIdNumber: StateFlow<String> = _guestIdNumber.asStateFlow()

    private val _guestPhone = MutableStateFlow("")
    val guestPhone: StateFlow<String> = _guestPhone.asStateFlow()

    private val _guestNationality = MutableStateFlow("")
    val guestNationality: StateFlow<String> = _guestNationality.asStateFlow()

    private val _selectedRoom = MutableStateFlow<Room?>(null)
    val selectedRoom: StateFlow<Room?> = _selectedRoom.asStateFlow()

    private val _checkinDate = MutableStateFlow(Date().time)
    val checkinDate: StateFlow<Long> = _checkinDate.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    // Available rooms
    private val _availableRooms = MutableStateFlow<List<Room>>(emptyList())
    val availableRooms: StateFlow<List<Room>> = _availableRooms.asStateFlow()

    // Form validation
    private val _formErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val formErrors: StateFlow<Map<String, String>> = _formErrors.asStateFlow()

    // Loading and success states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _bookingSuccess = MutableStateFlow(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Edit mode
    var isEditMode = false
        private set

    private var editingBookingId: Long? = null

    init {
        loadAvailableRooms()
    }

    fun loadAvailableRooms() {
        viewModelScope.launch {
            repository.getAvailableRooms().first { rooms ->
                _availableRooms.value = rooms
                true
            }
        }
    }

    fun loadBookingDetails(bookingId: Long) {
        viewModelScope.launch {
            try {
                val bookingDetails = repository.getBookingDetails(bookingId)
                if (bookingDetails != null) {
                    isEditMode = true
                    editingBookingId = bookingId
                    
                    // Populate form with existing data
                    _guestName.value = bookingDetails.guest.guestName
                    _guestIdType.value = bookingDetails.guest.guestIdType
                    _guestIdNumber.value = bookingDetails.guest.guestIdNumber
                    _guestPhone.value = bookingDetails.guest.guestPhone
                    _guestNationality.value = bookingDetails.guest.guestNationality ?: ""
                    _selectedRoom.value = bookingDetails.room
                    _checkinDate.value = bookingDetails.booking.checkinDate
                    _notes.value = bookingDetails.booking.notes ?: ""
                }
            } catch (e: Exception) {
                _errorMessage.value = "فشل في تحميل بيانات الحجز"
            }
        }
    }

    fun updateGuestName(name: String) {
        _guestName.value = name
        validateForm()
    }

    fun updateGuestIdType(type: String) {
        _guestIdType.value = type
    }

    fun updateGuestIdNumber(number: String) {
        _guestIdNumber.value = number
        validateForm()
    }

    fun updateGuestPhone(phone: String) {
        _guestPhone.value = phone
        validateForm()
    }

    fun updateGuestNationality(nationality: String) {
        _guestNationality.value = nationality
    }

    fun updateSelectedRoom(room: Room) {
        _selectedRoom.value = room
    }

    fun updateCheckinDate(date: Long) {
        _checkinDate.value = date
    }

    fun updateNotes(notes: String) {
        _notes.value = notes
    }

    private fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (_guestName.value.isBlank()) {
            errors["guestName"] = "اسم النزيل مطلوب"
        }

        if (_guestIdNumber.value.isBlank()) {
            errors["guestIdNumber"] = "رقم الهوية مطلوب"
        }

        if (_guestPhone.value.isBlank()) {
            errors["guestPhone"] = "رقم الهاتف مطلوب"
        }

        if (_selectedRoom.value == null) {
            errors["room"] = "اختر الغرفة"
        }

        _formErrors.value = errors
        return errors.isEmpty()
    }

    fun saveBooking() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val guest = Guest(
                    guestName = _guestName.value,
                    guestIdType = _guestIdType.value,
                    guestIdNumber = _guestIdNumber.value,
                    guestPhone = _guestPhone.value,
                    guestNationality = _guestNationality.value.ifEmpty { null }
                )

                val booking = Booking(
                    guestId = 0, // Will be set in repository
                    roomNumber = _selectedRoom.value!!.roomNumber,
                    checkinDate = _checkinDate.value,
                    status = "محجوزة",
                    notes = _notes.value.ifEmpty { null }
                )

                if (isEditMode) {
                    // Update existing booking
                    // repository.updateBooking(booking)
                    _bookingSuccess.value = true
                } else {
                    // Create new booking
                    val bookingId = repository.createBooking(guest, booking)
                    if (bookingId > 0) {
                        _bookingSuccess.value = true
                    } else {
                        _errorMessage.value = "فشل في إنشاء الحجز"
                    }
                }

            } catch (e: Exception) {
                _errorMessage.value = when (e.message) {
                    "فشل في إنشاء الحجز" -> e.message
                    else -> "حدث خطأ أثناء حفظ الحجز"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getNationalities(): List<String> = listOf(
        "سوري", "لبناني", "مصري", "عراقي", "أردني", "فلسطيني", "سعودي", "إماراتي", 
        "كويتي", "قطري", "بحريني", "عماني", "يمني", "ليبي", "مغربي", "جزائري", 
        "تونسي", "سوداني", "صومالي", "جيبوتي", "موريتاني", "جزر القمر", "أمريكي", 
        "بريطاني", "فرنسي", "ألماني", "إيطالي", "إسباني", "روسي", "صيني", "هندي", "أفغاني"
    )

    fun getIdTypes(): List<String> = listOf(
        "بطاقة شخصية", "جواز سفر", "بطاقة إقامة", "رخصة قيادة"
    )
}