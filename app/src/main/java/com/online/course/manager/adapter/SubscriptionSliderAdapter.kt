package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemSubscriptionBinding
import com.online.course.manager.Utils
import com.online.course.model.SubscriptionItem
import com.online.course.ui.frag.SubscriptionFrag

class SubscriptionSliderAdapter(
    items: List<SubscriptionItem>,
    private val frag: SubscriptionFrag,
    private val disable: Boolean = false
) :
    BaseArrayAdapter<SubscriptionItem, SubscriptionSliderAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubscriptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding
        val context = holder.itemView.context

        if (item.image != null) {
            Glide.with(holder.itemView.context).load(item.image)
                .into(holder.binding.itemSubscriptionImg)
        }

        binding.itemSubscriptionTitleTv.text = item.title
        binding.itemSubscriptionDescriptionTv.text = item.description
        binding.itemSubscriptionPriceTv.text = Utils.formatPrice(context, item.price)
        binding.itemSubscriptionDaysTv.text =
            ("○ ${item.days} ${context.getString(R.string.days_of_subscription)}")
        binding.itemSubscriptionClassesTv.text =
            ("○ ${item.usableCount} ${context.getString(R.string.classes_subscription)}")

        if (disable) {
            binding.itemSubscriptionPurchaseBtn.visibility = View.GONE
        }
    }

    inner class ViewHolder(val binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.itemSubscriptionPurchaseBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            frag.onItemSelected(items[bindingAdapterPosition])
        }
    }
}