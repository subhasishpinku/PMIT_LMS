package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.online.course.databinding.ItemTopDashboardRvBinding
import com.online.course.manager.Utils
import com.online.course.model.view.CommonItem

class CardStatisticsRvAdapter(items: List<CommonItem>) :
    BaseArrayAdapter<CommonItem, CardStatisticsRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTopDashboardRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context

        if (position == 0) {
            val layoutParams =
                holder.binding.itemDashboardTopCard.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginStart = Utils.changeDpToPx(context, 16f).toInt()
            holder.binding.itemDashboardTopCard.requestLayout()
        }else if (position == itemCount - 1) {
            val layoutParams =
                holder.binding.itemDashboardTopCard.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginEnd = Utils.changeDpToPx(context, 16f).toInt()
            holder.binding.itemDashboardTopCard.requestLayout()
        }

        holder.binding.itemDashboardTopImg.setImageResource(item.imgResource()!!)
        holder.binding.itemDashboardTopImg.setBackgroundResource(item.imgBgResource()!!)
        holder.binding.itemDashboardTopTitleTv.text = item.title(context)
        holder.binding.itemDashboardTopDescTv.text = item.desc(context)
    }

    inner class ViewHolder(val binding: ItemTopDashboardRvBinding) :
        RecyclerView.ViewHolder(binding.root)
}
