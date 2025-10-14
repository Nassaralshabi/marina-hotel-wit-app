package com.marina.hotelmanagement.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.marina.hotelmanagement.HotelApplication
import com.marina.hotelmanagement.R
import com.marina.hotelmanagement.data.entities.Room
import com.marina.hotelmanagement.databinding.FragmentDashboardBinding
import com.marina.hotelmanagement.ui.adapters.AlertsAdapter
import com.marina.hotelmanagement.ui.adapters.RoomsAdapter
import com.marina.hotelmanagement.ui.viewmodel.DashboardViewModel
import com.marina.hotelmanagement.ui.viewmodel.factory.HotelViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private lateinit var viewModelFactory: HotelViewModelFactory

    private lateinit var alertsAdapter: AlertsAdapter
    private lateinit var roomsAdapter: RoomsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        
        val repository = (requireActivity().application as HotelApplication).repository
            ?: throw IllegalStateException("Repository not initialized")
        
        viewModelFactory = HotelViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        setupAdapters()
    }

    private fun setupUI() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadDashboardData()
        }
        
        // Set up room click listener
        roomsAdapter = RoomsAdapter { room ->
            onRoomClicked(room)
        }
        
        alertsAdapter = AlertsAdapter { alert ->
            onAlertDismiss(alert.noteId)
        }
    }

    private fun setupAdapters() {
        // Setup alerts recycler view
        binding.alertsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.alertsRecyclerView.adapter = alertsAdapter

        // Setup room recycler views
        binding.floor1RoomsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.floor2RoomsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.floor3RoomsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        
        binding.floor1RoomsRecyclerView.adapter = roomsAdapter
        binding.floor2RoomsRecyclerView.adapter = roomsAdapter
        binding.floor3RoomsRecyclerView.adapter = roomsAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.isLoading.collect { isLoading ->
                    binding.swipeRefreshLayout.isRefreshing = isLoading
                    binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }

            launch {
                viewModel.dashboardStats.collect { stats ->
                    updateStats(stats)
                }
            }

            launch {
                viewModel.rooms.collect { rooms ->
                    updateRoomStatus(rooms)
                }
            }

            launch {
                viewModel.activeAlerts.collect { alerts ->
                    alertsAdapter.submitList(alerts)
                    binding.noAlertsText.visibility = if (alerts.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun updateStats(stats: HotelRepository.DashboardStats) {
        binding.totalRoomsText.text = stats.totalRooms.toString()
        binding.availableRoomsText.text = stats.availableRooms.toString()
        binding.occupiedRoomsText.text = stats.occupiedRooms.toString()
        binding.activeBookingsText.text = stats.activeBookings.toString()
    }

    private fun updateRoomStatus(rooms: List<Room>) {
        val floor1Rooms = rooms.filter { it.roomNumber.startsWith("1") }
        val floor2Rooms = rooms.filter { it.roomNumber.startsWith("2") }
        val floor3Rooms = rooms.filter { it.roomNumber.startsWith("3") }

        binding.floor1RoomsRecyclerView.visibility = if (floor1Rooms.isEmpty()) View.GONE else View.VISIBLE
        binding.floor2RoomsRecyclerView.visibility = if (floor2Rooms.isEmpty()) View.GONE else View.VISIBLE
        binding.floor3RoomsRecyclerView.visibility = if (floor3Rooms.isEmpty()) View.GONE else View.VISIBLE

        binding.floor1RoomsRecyclerView.adapter = RoomsAdapter { room -> onRoomClicked(room) }
        binding.floor2RoomsRecyclerView.adapter = RoomsAdapter { room -> onRoomClicked(room) }
        binding.floor3RoomsRecyclerView.adapter = RoomsAdapter { room -> onRoomClicked(room) }
    }

    private fun onRoomClicked(room: Room) {
        when (room.status) {
            "شاغرة" -> {
                // Navigate to booking fragment for available room
                val action = DashboardFragmentDirections.actionDashboardToAddBooking(room.roomNumber)
                findNavController().navigate(action)
            }
            "محجوزة" -> {
                // Show booking details for occupied room
                viewModel.getActiveBookingByRoom(room.roomNumber)?.let { booking ->
                    val action = DashboardFragmentDirections.actionDashboardToBookingDetails(booking.bookingId)
                    findNavController().navigate(action)
                }
            }
            else -> {
                // Handle maintenance room
            }
        }
    }

    private fun onAlertDismiss(noteId: Long) {
        viewModel.dismissAlert(noteId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}