package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemSubscriptionBinding
import com.online.course.manager.Utils
import com.online.course.model.SaasPackageItem
import com.online.course.ui.frag.SaasPackageFrag
import java.lang.StringBuilder

class SaasPackageAdapter(
    items: List<SaasPackageItem>,
    private val frag: SaasPackageFrag,
) :
    BaseArrayAdapter<SaasPackageItem, SaasPackageAdapter.ViewHolder>(items) {

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

        if (item.icon.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(item.icon)
                .into(holder.binding.itemSubscriptionImg)
        }

        binding.itemSubscriptionTitleTv.text = item.title
        binding.itemSubscriptionDescriptionTv.text = item.description
        binding.itemSubscriptionPriceTv.text = Utils.formatPrice(context, item.price)

        val builder = StringBuilder()
        builder.append("○ ${item.days} ${context.getString(R.string.days_of_subscription)}")
        builder.append("\n\n○ ${item.coursesCount} ${context.getString(R.string.courses)}")
        builder.append("\n\n○ ${item.coursesCapacity} ${context.getString(R.string.live_class_capacity)}")
        builder.append("\n\n○ ${item.meetingCount} ${context.getString(R.string.meeting_time_slots)}")
        builder.append("\n\n○ ${item.studentsCount} ${context.getString(R.string.students)}")
        builder.append("\n\n○ ${item.instructorsCount} ${context.getString(R.string.instructors)}")

        binding.itemSubscriptionDaysTv.text = builder.toString()
    }

    inner class ViewHolder(val binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.itemSubscriptionClassesTv.visibility = View.GONE
            binding.itemSubscriptionPurchaseBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            frag.onItemSelected(items[bindingAdapterPosition])
        }
    }
}