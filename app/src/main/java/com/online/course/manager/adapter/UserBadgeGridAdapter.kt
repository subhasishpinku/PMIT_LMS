package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.databinding.ItemBadgeBinding
import com.online.course.model.UserBadge

class UserBadgeGridAdapter(items: List<UserBadge>) :
    BaseArrayAdapter<UserBadge, UserBadgeGridAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBadgeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userBadge = items[position]
        if (userBadge.image != null)
            Glide.with(holder.itemView.context).load(userBadge.image)
                .into(holder.binding.itemBadgeImg)
        holder.binding.itemBadgeTitleTv.text = userBadge.title
        holder.binding.itemBadgeDescTv.text = userBadge.description
    }

    class ViewHolder(val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root)
}