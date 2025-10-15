package com.marina.hotelmanagement.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marina.hotelmanagement.data.entities.Room
import com.marina.hotelmanagement.databinding.ItemRoomBinding

class RoomsAdapter(
    private val onRoomClick: (Room) -> Unit
) : ListAdapter<Room, RoomsAdapter.RoomViewHolder>(RoomDiffCallback()) {

    inner class RoomViewHolder(
        private val binding: ItemRoomBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(room: Room) {
            binding.roomNumberText.text = room.roomNumber
            binding.roomTypeText.text = room.type
            binding.roomPriceText.text = "${room.price} $"
            
            // Set background color based on room status
            val backgroundColor = when (room.status) {
                "شاغرة" -> android.R.color.holo_green_dark
                "محجوزة" -> android.R.color.holo_red_dark
                "صيانة" -> android.R.color.holo_orange_dark
                else -> android.R.color.darker_gray
            }
            
            binding.root.setBackgroundResource(backgroundColor)
            binding.root.setOnClickListener { onRoomClick(room) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RoomDiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean =
            oldItem.roomNumber == newItem.roomNumber

        override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean =
            oldItem == newItem
    }
}