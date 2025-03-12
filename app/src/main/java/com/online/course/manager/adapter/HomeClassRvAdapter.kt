package com.online.course.manager.adapter

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.course.R
import com.online.course.databinding.ItemCourseBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.Course
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.CourseDetailsFrag
import java.util.*
import kotlin.math.roundToInt

class HomeClassRvAdapter(items: List<Course>, private val activity: MainActivity) :
    BaseArrayAdapter<Course, HomeClassRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val course = items[position]
        val context = viewholder.itemView.context

        viewholder.binding.itemCourseTitleTv.text = course.title
        viewholder.binding.itemCourseRatingTv.text = course.rating.toString()

        if (position == 0 || position == itemCount - 1) {
            val itemFeaturedContainer = viewholder.binding.itemCourseContainer
            val layoutParams = itemFeaturedContainer.layoutParams as RecyclerView.LayoutParams
            val margin16 = context.resources.getDimension(R.dimen.margin_16).roundToInt()

            if (position == 0) {
                layoutParams.marginStart = margin16
            } else {
                layoutParams.marginEnd = margin16
            }
            itemFeaturedContainer.requestLayout()
        }

        if (course.img != null) {
            Glide.with(viewholder.itemView.context).load(course.img)
                .addListener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewholder.binding.itemCourseImgOverlay.visibility = View.VISIBLE
                        return false
                    }


                }).into(viewholder.binding.itemCourseImg)
        }

        if (course.discount > 0) {
            viewholder.binding.itemCourseTypeDiscountTv.setBackgroundResource(R.drawable.round_view_red_corner10_op30)
            viewholder.binding.itemCourseTypeDiscountTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.red
                )
            )
            viewholder.binding.itemCourseTypeDiscountTv.text =
                (course.discount.toString() + "% " + context.getString(R.string.off))

            viewholder.binding.itemCoursePriceWithDiscountTv.visibility = View.VISIBLE
            viewholder.binding.itemCoursePriceWithDiscountTv.text =
                Utils.formatPrice(context, course.price)
            viewholder.binding.itemCoursePriceWithDiscountTv.post {
                viewholder.binding.itemCoursePriceWithDiscountTv.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG
            }

            viewholder.binding.itemCoursePriceTv.text =
                Utils.formatPrice(context, course.priceWithDiscount)

        } else if (course.isLive()) {
            viewholder.binding.itemCoursePriceTv.text =
                Utils.formatPrice(context, course.price)

            viewholder.binding.itemCourseTypeDiscountTv.setBackgroundResource(R.drawable.round_view_dark_blue_corner10)

            course.liveCourseStatus?.let {
                val color: Int
                val bg : Int
                val liveStatus : String

                when (course.liveCourseStatus) {
                    Course.WebinarStatus.FINISHED.value -> {
                        color = R.color.red
                        bg = R.drawable.round_view_red_corner10_op30
                        liveStatus = context.getString(R.string.finished)
                    }
                    Course.WebinarStatus.NOT_CONDUCTED.value -> {
                        color = R.color.accent
                        bg = R.drawable.round_view_accent_corner10_op30
                        liveStatus = context.getString(R.string.not_conducted)
                    }
                    else -> {
                        color = R.color.darkBlue
                        bg = R.drawable.round_view_light_blue_corner10
                        liveStatus = context.getString(R.string.in_progress)
                    }
                }

                viewholder.binding.itemCourseTypeDiscountTv.text = liveStatus
                viewholder.binding.itemCourseTypeDiscountTv.setBackgroundResource(bg)
                viewholder.binding.itemCourseTypeDiscountTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        color
                    )
                )
            }

        } else {
            viewholder.binding.itemCoursePriceTv.text =
                Utils.formatPrice(context, course.price)
            viewholder.binding.itemCourseTypeDiscountTv.setBackgroundResource(R.drawable.round_view_accent_corner10_op30)

            viewholder.binding.itemCourseTypeDiscountTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.accent
                )
            )

            val type = when (course.type) {
                Course.Type.WEBINAR.value -> {
                    context.getString(R.string.webinar)
                }
                Course.Type.COURSE.value -> {
                    context.getString(R.string.course)
                }
                else -> {
                    context.getString(R.string.text_lessson)
                }
            }

            viewholder.binding.itemCourseTypeDiscountTv.text = type
        }

        if (course.progress != null) {
            if (course.progress!! > 50) {
                viewholder.binding.itemCourseImgProgressBar.progressTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.accent)
                    )
            }

            viewholder.binding.itemCourseImgProgressBar.visibility = View.VISIBLE
            viewholder.binding.itemCourseImgProgressBar.progress = course.progress!!.toInt()
        } else if (course.isLive()) {
            viewholder.binding.itemCourseImgProgressBar.visibility = View.VISIBLE
        } else {
            viewholder.binding.itemCourseImgProgressBar.visibility = View.GONE
        }

        if (course.isLive() && course.startDate > System.currentTimeMillis() / 1000) {
            viewholder.showAddToCalendar()
        } else {
            viewholder.binding.itemCourseAddToCalendarImg.visibility = View.GONE
        }

        viewholder.binding.itemCourseDateDurationTv.text =
            Utils.getDuration(context, course.duration)

        viewholder.binding.itemCourseUserNameTv.text = course.teacher.name
    }

    inner class ViewHolder(val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.itemCourseContainer.setOnClickListener(this)
        }

        fun showAddToCalendar() {
            binding.itemCourseAddToCalendarImg.visibility = View.VISIBLE
            binding.itemCourseAddToCalendarImg.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.itemCourseAddToCalendarImg -> {
                    val item = items[bindingAdapterPosition]
                    val context = binding.root.context

                    val calendar = Calendar.getInstance()
                    calendar.time = Date(item.startDate * 1000)

                    Utils.addToCalendar(
                        context,
                        context.getString(R.string.webinar),
                        calendar.timeInMillis,
                        0
                    )
                }

                R.id.itemCourseContainer -> {
                    val bundle = Bundle()
                    bundle.putParcelable(App.COURSE, items[bindingAdapterPosition])

                    val frag = CourseDetailsFrag()
                    frag.arguments = bundle
                    activity.transact(frag)
                }
            }
        }
    }
}