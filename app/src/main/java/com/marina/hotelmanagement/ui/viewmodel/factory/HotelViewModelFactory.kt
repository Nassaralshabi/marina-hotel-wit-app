package com.marina.hotelmanagement.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marina.hotelmanagement.data.repository.HotelRepository
import com.marina.hotelmanagement.ui.viewmodel.*

class HotelViewModelFactory(private val repository: HotelRepository) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> 
                LoginViewModel(repository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> 
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(BookingViewModel::class.java) -> 
                BookingViewModel(repository) as T
            modelClass.isAssignableFrom(PaymentViewModel::class.java) -> 
                PaymentViewModel(repository) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> 
                ReportsViewModel(repository) as T
            modelClass.isAssignableFrom(ExpenseViewModel::class.java) -> 
                ExpenseViewModel(repository) as T
            modelClass.isAssignableFrom(UserViewModel::class.java) -> 
                UserViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}