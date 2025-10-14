package com.marina.hotelmanagement.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marina.hotelmanagement.data.entities.Payment
import com.marina.hotelmanagement.databinding.ItemPaymentBinding
import java.text.SimpleDateFormat
import java.util.*

class PaymentsAdapter(
    private val onDeleteClick: (Payment) -> Unit
) : ListAdapter<Payment, PaymentsAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class PaymentViewHolder(
        private val binding: ItemPaymentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(payment: Payment) {
            binding.paymentAmountText.text = "${payment.amount} $"
            binding.paymentMethodText.text = payment.paymentMethod
            binding.paymentDateText.text = dateFormat.format(Date(payment.paymentDate))
            
            // Set payment method color
            binding.paymentMethodText.setTextColor(
                when (payment.paymentMethod) {
                    "نقدي" -> android.R.color.holo_green_dark
                    "بطاقة" -> android.R.color.holo_blue_dark
                    else -> android.R.color.darker_gray
                }
            )
            
            binding.deletePaymentButton.setOnClickListener {
                onDeleteClick(payment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PaymentDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean =
            oldItem.paymentId == newItem.paymentId

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean =
            oldItem == newItem
    }
}