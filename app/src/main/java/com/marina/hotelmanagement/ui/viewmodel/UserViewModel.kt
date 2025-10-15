package com.marina.hotelmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marina.hotelmanagement.data.entities.User
import com.marina.hotelmanagement.data.repository.HotelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(private val repository: HotelRepository) : ViewModel() {

    // Users list
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    // User management
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _role = MutableStateFlow("موظف استقبال")
    val role: StateFlow<String> = _role.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

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
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            repository.getAllActiveUsers().collect { users ->
                _users.value = users
            }
        }
    }

    fun updateUsername(username: String) {
        _username.value = username
        validateUsername()
    }

    fun updatePassword(password: String) {
        _password.value = password
        validatePassword()
        validateConfirmPassword()
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
        validateConfirmPassword()
    }

    fun updateFullName(fullName: String) {
        _fullName.value = fullName
        validateFullName()
    }

    fun updateRole(role: String) {
        _role.value = role
    }

    fun updatePhone(phone: String) {
        _phone.value = phone
        validatePhone()
    }

    fun updateEmail(email: String) {
        _email.value = email
        validateEmail()
    }

    private fun validateUsername(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        if (_username.value.isBlank()) {
            errors["username"] = "اسم المستخدم مطلوب"
        } else if (_username.value.length < 3) {
            errors["username"] = "اسم المستخدم يجب أن يكون 3 أحرف على الأقل"
        } else {
            errors.remove("username")
        }
        
        _formErrors.value = errors
        return !errors.containsKey("username")
    }

    private fun validatePassword(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        if (_password.value.isBlank()) {
            errors["password"] = "كلمة المرور مطلوبة"
        } else if (_password.value.length < 6) {
            errors["password"] = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
        } else {
            errors.remove("password")
        }
        
        _formErrors.value = errors
        return !errors.containsKey("password")
    }

    private fun validateConfirmPassword(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        if (_confirmPassword.value != _password.value) {
            errors["confirmPassword"] = "كلمات المرور غير متطابقة"
        } else {
            errors.remove("confirmPassword")
        }
        
        _formErrors.value = errors
        return !errors.containsKey("confirmPassword")
    }

    private fun validateFullName(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        if (_fullName.value.isBlank()) {
            errors["fullName"] = "الاسم الكامل مطلوب"
        } else {
            errors.remove("fullName")
        }
        
        _formErrors.value = errors
        return !errors.containsKey("fullName")
    }

    private fun validatePhone(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        if (_phone.value.isNotBlank() && _phone.value.length < 10) {
            errors["phone"] = "رقم الهاتف غير صالح"
        } else {
            errors.remove("phone")
        }
        
        _formErrors.value = errors
        return !errors.containsKey("phone")
    }

    private fun validateEmail(): Boolean {
        val errors = _formErrors.value.toMutableMap()
        
        if (_email.value.isNotBlank() && !isValidEmail(_email.value)) {
            errors["email"] = "البريد الإلكتروني غير صالح"
        } else {
            errors.remove("email")
        }
        
        _formErrors.value = errors
        return !errors.containsKey("email")
    }

    private fun validateForm(): Boolean {
        val usernameValid = validateUsername()
        val passwordValid = validatePassword()
        val confirmPasswordValid = validateConfirmPassword()
        val fullNameValid = validateFullName()
        val phoneValid = validatePhone()
        val emailValid = validateEmail()
        
        return usernameValid && passwordValid && confirmPasswordValid && 
               fullNameValid && phoneValid && emailValid
    }

    fun createUser() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Check if username already exists
                val existingUser = repository.getUserById(_username.value.toLongOrNull() ?: 0)
                if (existingUser != null) {
                    _errorMessage.value = "اسم المستخدم موجود بالفعل"
                    return@launch
                }

                val user = User(
                    username = _username.value,
                    password = _password.value, // In real app, hash this password
                    fullName = _fullName.value,
                    role = _role.value,
                    phone = _phone.value.ifEmpty { null },
                    email = _email.value.ifEmpty { null },
                    isActive = true
                )

                val userId = repository.createUser(user)
                if (userId > 0) {
                    _submitSuccess.value = true
                    clearForm()
                    loadUsers() // Refresh users list
                } else {
                    _errorMessage.value = "فشل في إنشاء المستخدم"
                }

            } catch (e: Exception) {
                _errorMessage.value = "حدث خطأ أثناء إنشاء المستخدم"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deactivateUser(userId: Long) {
        viewModelScope.launch {
            try {
                repository.deactivateUser(userId)
                loadUsers() // Refresh users list
            } catch (e: Exception) {
                _errorMessage.value = "فشل في تعطيل المستخدم"
            }
        }
    }

    fun clearForm() {
        _username.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _fullName.value = ""
        _role.value = "موظف استقبال"
        _phone.value = ""
        _email.value = ""
        _formErrors.value = emptyMap()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getRoles(): List<String> = listOf(
        "مدير", "موظف استقبال", "محاسب", "مراقب"
    )
}