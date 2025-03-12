package com.online.course.manager.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemMyClassesBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.Course
import java.lang.StringBuilder
import kotlin.math.roundToInt

class MyClassesRvAdapter(items: List<Course>, private val purchased: Boolean = false) :
    BaseArrayAdapter<Course, MyClassesRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyClassesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val course = items[position]
        val context = viewholder.itemView.context

        val binding = viewholder.binding

        binding.itemMyClassesTimeDurationTv.text =
            Utils.getDuration(context, course.duration)

        if (purchased && course.progress != null) {
            if (course.progress!! > 50) {
                binding.itemMyClassesProgressBar.progressTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.accent)
                    )
            }

            binding.itemMyClassesProgressBar.progress = course.progress!!.roundToInt()
            binding.itemMyClassesProgressBar.visibility = View.VISIBLE
        } else {
            binding.itemMyClassesProgressBar.visibility = View.GONE
        }

        if (course.isBundle()) {
            val color = ContextCompat.getColor(context, R.color.orange)
            binding.itemFavoriteCourseDiscountPctTv.setTextColor(color)
            binding.itemFavoriteCourseDiscountPctTv.text =
                context.getString(R.string.bundle)
            binding.itemFavoriteCourseDiscountPctTv.visibility = View.VISIBLE
        } else {
            binding.itemFavoriteCourseDiscountPctTv.visibility = View.GONE
        }

        if (course.img != null) {
            Glide.with(viewholder.itemView.context).load(course.img)
                .into(binding.itemMyClassesImg)
        }
        binding.itemMyClassesTitleTv.text = course.title
        binding.itemMyClassesRatingBar.rating = course.rating
        binding.itemMyClassesCategoryTv.text = course.category

        if (course.priceWithDiscount == 0.0) {
            binding.itemFavoriteCoursePriceTv.text = context.getString(R.string.free)
        } else {
            binding.itemFavoriteCoursePriceTv.text =
                ("${App.appConfig.currency.sign}${course.priceWithDiscount}")
        }

        if (course.isLive()) {
            binding.itemMyClassesDateTitleTv.text =
                context.getString(R.string.start_date)
            binding.itemMyClassesDateTv.text =
                Utils.getDateFromTimestamp(course.startDate)
            binding.itemMyClassesProgressBar.max = 100
            binding.itemMyClassesProgressBar.progress = course.progress?.toInt()!!
        } else {
            binding.itemMyClassesDateTitleTv.text =
                context.getString(R.string.publish_date)
            binding.itemMyClassesDateTv.text =
                Utils.getDateFromTimestamp(course.createdAt)
        }

        if (course.expiresOn != null) {
            val imgRes: Int
            val containerBg: Int
            val textColor: Int
            var builder = StringBuilder()
            val expireOn = Utils.getDateFromTimestamp(course.expiresOn!!)

            if (course.expired) {
                imgRes = R.drawable.ic_calendar_border_red
                builder = builder.append(context.getString(R.string.access_expired_on))
                textColor = R.color.red
                containerBg = R.drawable.round_view_light_red_corner15_opacity10
            } else {
                imgRes = R.drawable.ic_calendar_border_orange
                builder = builder.append(context.getString(R.string.access_expires_on))
                textColor = R.color.orange
                containerBg = R.drawable.round_view_light_orange_corner15_opacity10
            }

            builder = builder.append(" ").append(expireOn)

            binding.itemMyClassesExpiresImg.setImageResource(imgRes)
            binding.itemMyClassesExpiresTv.text = builder.toString()
            binding.itemMyClassesExpiresTv.setTextColor(ContextCompat.getColor(context, textColor))
            binding.itemMyClassesExpiresContainer.setBackgroundResource(containerBg)
            binding.itemMyClassesExpiresContainer.visibility = View.VISIBLE
        }
    }

    class ViewHolder(val binding: ItemMyClassesBinding) : RecyclerView.ViewHolder(binding.root)
}