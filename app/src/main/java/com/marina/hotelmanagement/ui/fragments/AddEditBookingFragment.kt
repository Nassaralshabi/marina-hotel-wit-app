package com.marina.hotelmanagement.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.marina.hotelmanagement.HotelApplication
import com.marina.hotelmanagement.R
import com.marina.hotelmanagement.databinding.FragmentAddEditBookingBinding
import com.marina.hotelmanagement.ui.viewmodel.BookingViewModel
import com.marina.hotelmanagement.ui.viewmodel.factory.HotelViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class AddEditBookingFragment : Fragment() {

    private var _binding: FragmentAddEditBookingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingViewModel
    private lateinit var viewModelFactory: HotelViewModelFactory
    private val args: AddEditBookingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBookingBinding.inflate(inflater, container, false)
        
        val repository = (requireActivity().application as HotelApplication).repository
            ?: throw IllegalStateException("Repository not initialized")
        
        viewModelFactory = HotelViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[BookingViewModel::class.java]
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        
        // Load booking details if in edit mode
        args.bookingId?.let { bookingId ->
            viewModel.loadBookingDetails(bookingId)
        }

        // Pre-select room if provided
        args.roomNumber?.let { roomNumber ->
            // This would update the room selection in the viewmodel
        }
    }

    private fun setupUI() {
        setupDropdowns()
        setupDatePicker()
        
        binding.saveButton.setOnClickListener {
            viewModel.saveBooking()
        }
    }

    private fun setupDropdowns() {
        // Setup nationality dropdown
        val nationalities = viewModel.getNationalities()
        val nationalityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            nationalities
        )
        binding.nationalityAutoComplete.setAdapter(nationalityAdapter)

        // Setup ID type dropdown
        val idTypes = viewModel.getIdTypes()
        val idTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            idTypes
        )
        binding.idTypeAutoComplete.setAdapter(idTypeAdapter)

        // Setup room numbers dropdown
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableRooms.collect { rooms ->
                val roomNumbers = rooms.map { it.roomNumber }
                val roomAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    roomNumbers
                )
                binding.roomNumberAutoComplete.setAdapter(roomAdapter)
            }
        }
    }

    private fun setupDatePicker() {
        binding.checkinDateEditText.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create and show date picker dialog
        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                viewModel.updateCheckinDate(selectedDate.timeInMillis)
                
                val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.checkinDateEditText.setText(dateString)
            },
            year, month, day
        )
        
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.formErrors.collect { errors ->
                    // Update error messages for each field
                    binding.guestNameLayout.error = errors["guestName"]
                    binding.idNumberLayout.error = errors["guestIdNumber"]
                    binding.phoneLayout.error = errors["guestPhone"]
                    binding.roomNumberLayout.error = errors["room"]
                }
            }

            launch {
                viewModel.errorMessage.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    }
                }
            }

            launch {
                viewModel.bookingSuccess.collect { success ->
                    if (success) {
                        Toast.makeText(requireContext(), 
                            if (viewModel.isEditMode) getString(R.string.booking_update_success) 
                            else getString(R.string.booking_create_success), 
                            Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    binding.saveButton.isEnabled = !isLoading
                }
            }

            launch {
                viewModel.guestName.collect { name ->
                    if (binding.guestNameEditText.text.toString() != name) {
                        binding.guestNameEditText.setText(name)
                    }
                }
            }

            launch {
                viewModel.selectedRoom.collect { room ->
                    binding.roomNumberAutoComplete.setText(room?.roomNumber ?: "")
                }
            }

            launch {
                viewModel.checkinDate.collect { date ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = date
                    val dateString = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                    binding.checkinDateEditText.setText(dateString)
                }
            }
        }
    }
}