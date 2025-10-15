package com.marina.hotelmanagement.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marina.hotelmanagement.data.entities.BookingNote
import com.marina.hotelmanagement.databinding.ItemAlertBinding

class AlertsAdapter(
    private val onDismissClick: (BookingNote) -> Unit
) : ListAdapter<BookingNote, AlertsAdapter.AlertViewHolder>(AlertDiffCallback()) {

    inner class AlertViewHolder(
        private val binding: ItemAlertBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(alert: BookingNote) {
            binding.alertText.text = alert.noteText
            binding.roomNumberText.text = "غرفة ${alert.bookingId}" // Note: This should be real room number
            
            // Set priority indicator color
            val priorityColor = when (alert.alertType) {
                "عالي" -> android.R.color.holo_red_dark
                "متوسط" -> android.R.color.holo_orange_dark
                "منخفض" -> android.R.color.darker_gray
                else -> android.R.color.darker_gray
            }
            binding.priorityIndicator.setBackgroundResource(priorityColor)
            
            binding.dismissButton.setOnClickListener {
                onDismissClick(alert)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<BookingNote>() {
        override fun areItemsTheSame(oldItem: BookingNote, newItem: BookingNote): Boolean =
            oldItem.noteId == newItem.noteId

        override fun areContentsTheSame(oldItem: BookingNote, newItem: BookingNote): Boolean =
            oldItem == newItem
    }
}