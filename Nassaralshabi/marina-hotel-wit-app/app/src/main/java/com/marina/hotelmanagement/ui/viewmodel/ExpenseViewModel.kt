package com.marina.hotelmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marina.hotelmanagement.data.entities.Expense
import com.marina.hotelmanagement.data.repository.HotelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class ExpenseViewModel(private val repository: HotelRepository) : ViewModel() {

    // Form fields
    private val _expenseType = MutableStateFlow("")
    val expenseType: StateFlow<String> = _expenseType.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _selectedDate = MutableStateFlow(Date().time)
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val _supplierId = MutableStateFlow<Long?>(null)
    val supplierId: StateFlow<Long?> = _supplierId.asStateFlow()

    // Expenses list
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _expenseTypes = MutableStateFlow<List<String>>(emptyList())
    val expenseTypes: StateFlow<List<String>> = _expenseTypes.asStateFlow()

    // Form validation
    private val _formErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val formErrors: StateFlow<Map<String, String>> = _formErrors.asStateFlow()

    // Loading and success states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _submitSuccess = MutableStateFlow(false)
    val submitSuccess: StateFlow<Boolean> = _submitSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadExpenseTypes()
        loadExpenses()
    }

    fun loadExpenseTypes() {
        viewModelScope.launch {
            repository.getExpenseTypes().collect { types ->
                _expenseTypes.value = types
                if (types.isNotEmpty() && _expenseType.value.isEmpty()) {
                    _expenseType.value = types[0]
                }
            }
        }
    }

    fun loadExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expenses ->
                _expenses.value = expenses
            }
        }
    }

    fun updateExpenseType(type: String) {
        _expenseType.value = type
    }

    fun updateDescription(description: String) {
        _description.value = description
        validateDescription()
    }

    fun updateAmount(amount: String) {
        _amount.value = amount
        validateAmount()
    }

    fun updateSelectedDate(date: Long) {
        _selectedDate.value = date
    }

    fun updateSupplierId(supplierId: Long?) {
        _supplierId.value = supplierId
    }

    private fun validateDescription(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        if (_description.value.isBlank()) {
            errors["description"] = "الوصف مطلوب"
        } else {
            errors.remove("description")
        }
        
        _formErrors.value = errors
        return !errors.containsKey("description")
    }

    private fun validateAmount(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        val amountValue = _amount.value.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            errors["amount"] = "المبلغ غير صالح"
        } else {
            errors.remove("amount")
        }

        _formErrors.value = errors
        return !errors.containsKey("amount")
    }

    private fun validateForm(): Boolean {
        val descriptionValid = validateDescription()
        val amountValid = validateAmount()
        
        val errors = _formErrors.value.toMutableMap()
        if (_expenseType.value.isBlank()) {
            errors["expenseType"] = "نوع المصاريف مطلوب"
        }
        
        _formErrors.value = errors
        return descriptionValid && amountValid && !errors.containsKey("expenseType")
    }

    fun submitExpense() {
        if (!validateForm()) {
            return
        }

        val amountValue = _amount.value.toDoubleOrNull() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val expense = Expense(
                    expenseType = _expenseType.value,
                    description = _description.value,
                    amount = amountValue,
                    date = _selectedDate.value,
                    relatedSupplierId = _supplierId.value
                )

                val expenseId = repository.addExpense(expense)
                if (expenseId > 0) {
                    _submitSuccess.value = true
                    clearForm()
                    loadExpenseTypes() // Refresh expense types
                } else {
                    _errorMessage.value = "فشل في إضافة المصاريف"
                }

            } catch (e: Exception) {
                _errorMessage.value = "حدث خطأ أثناء إضافة المصاريف"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearForm() {
        _expenseType.value = _expenseTypes.value.firstOrNull() ?: ""
        _description.value = ""
        _amount.value = ""
        _selectedDate.value = Date().time
        _supplierId.value = null
        _formErrors.value = emptyMap()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getTotalExpenses(): Double = _expenses.value.sumOf { it.amount }

    fun getExpensesByType(type: String): List<Expense> = 
        _expenses.value.filter { it.expenseType == type }

    fun getDefaultExpenseTypes(): List<String> = listOf(
        "كهرباء", "ماء", "انترنت", "هاتف", "نظافة", "صيانة", 
        "مواد تنظيف", "مستلزمات مكتبية", "رواتب", "ضرائب", "تأمين"
    )
}