package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemUserBinding
import com.online.course.model.User

class UsersOrganizationGridAdapter(items: List<User>) :
    BaseArrayAdapter<User, UsersOrganizationGridAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item.avatar != null)
            Glide.with(holder.itemView.context).load(item.avatar).into(holder.binding.itemUserImg)
        holder.binding.itemUserNameTv.text = item.name
        holder.binding.itemUserRoleTv.text = item.roleName
        holder.binding.itemUserRatingBar.rating = item.rating

        when (item?.meetingStatus) {
            User.MeetingStatus.NO.status -> {
                holder.binding.itemUserMeetingImg.setBackgroundResource(R.drawable.circle_gray)
                holder.binding.itemUserMeetingImg.setImageResource(R.drawable.ic_calendar)
            }
            User.MeetingStatus.AVAILABLE.status -> {
                holder.binding.itemUserMeetingImg.setBackgroundResource(R.drawable.circle_light_green)
                holder.binding.itemUserMeetingImg.setImageResource(R.drawable.ic_calendar_green)
            }
            User.MeetingStatus.UNAVAILABLE.status -> {
                holder.binding.itemUserMeetingImg.setBackgroundResource(R.drawable.circle_light_red)
                holder.binding.itemUserMeetingImg.setImageResource(R.drawable.ic_calendar_red)
            }
        }
    }

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
}