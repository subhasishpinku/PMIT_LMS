package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemOfflinePaymentBinding
import com.online.course.manager.Utils
import com.online.course.model.OfflinePayment

class OfflinePaymentRvAdapter(items: List<OfflinePayment>) :
    BaseArrayAdapter<OfflinePayment, OfflinePaymentRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOfflinePaymentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val offlinePayment = items[position]
        viewHolder.binding.itemOfflinePaymentTitleTv.text = offlinePayment.bank
        viewHolder.binding.itemOfflinePaymentRefTv.text = offlinePayment.referenceNumber
        viewHolder.binding.itemOfflinePaymentDateTimeTv.text =
            Utils.getDateTimeWithDayFromTimestamp(offlinePayment.payDate)
        viewHolder.binding.itemOfflinePaymentAmountTv.text =
            Utils.formatPrice(viewHolder.itemView.context, offlinePayment.amount, false)

        val statusTv = viewHolder.binding.itemOfflinePaymentStatusTv
        val paymentImg = viewHolder.binding.itemOfflinePaymentImg

        when (offlinePayment.status) {
            OfflinePayment.Status.REJECTED.value -> {
                statusTv.text = offlinePayment.status
                statusTv.setBackgroundResource(R.drawable.round_view_red_corner10)
                paymentImg.setBackgroundResource(R.drawable.round_view_red_corner20)
            }

            OfflinePayment.Status.WAITING.value -> {
                statusTv.text = offlinePayment.status
                statusTv.setBackgroundResource(R.drawable.round_view_gold_corner10)
                paymentImg.setBackgroundResource(R.drawable.round_view_gold_corner20)
            }
        }
    }

    inner class ViewHolder(val binding: ItemOfflinePaymentBinding) :
        RecyclerView.ViewHolder(binding.root)
}