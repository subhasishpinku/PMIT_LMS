package com.online.course.manager.adapter

import android.content.Context
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
import com.online.course.databinding.ItemClassInListBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.Course
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.CourseDetailsFrag
import java.util.*

class EndLessClassListRvAdapter(items: List<Course?>, private val activity: MainActivity) :
    EndLessLoadMoreAdapter<Course, EndLessClassListRvAdapter.ViewHolder>(
        items,
        R.layout.item_loading_row
    ) {


    override fun getItemViewType(position: Int): Int {
        return if (items[position] != null) TYPE_ITEMS else TYPE_LOAD
    }

    override fun onCreateViewItem(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemClassInListBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewItem(viewholder: RecyclerView.ViewHolder?, position: Int) {
        if (viewholder is ViewHolder) {
            val course = items[position]
            val context = viewholder.itemView.context
            viewholder.binding.itemClassInListUserNameTv.text = course.teacher.name

            if (course.discount > 0) {
                viewholder.binding.itemClassInListDiscountPctTv.text =
                    (course.discount.toString() + "% " + context.getString(R.string.off))

                viewholder.binding.itemCommonInPriceTv.text =
                    Utils.formatPrice(context, course.priceWithDiscount)

                viewholder.binding.itemCommonInPriceWithDiscountTv.text =
                    Utils.formatPrice(context, course.price)

                viewholder.binding.itemCommonInPriceWithDiscountTv.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG

            } else {
                viewholder.binding.itemCommonInPriceTv.text =
                    Utils.formatPrice(context, course.price)
            }

            if (course.progress != null) {
                if (course.progress!! > 50) {
                    viewholder.binding.itemClassInListProgressBar.progressTintList =
                        ContextCompat.getColorStateList(context, R.color.accent)
                }

                viewholder.binding.itemClassInListProgressBar.visibility = View.VISIBLE
                viewholder.binding.itemClassInListProgressBar.progress = course.progress!!.toInt()
            } else if (course.isLive()) {
                viewholder.binding.itemClassInListProgressBar.visibility = View.VISIBLE
            } else {
                viewholder.binding.itemClassInListProgressBar.visibility = View.GONE
            }

            if (course.isLive() && course.startDate > System.currentTimeMillis() / 1000) {
                viewholder.showAddToCalendar()
            } else {
                viewholder.binding.itemClassInListAddToCalendarImg.visibility = View.GONE
            }

            if (course.img != null) {
                Glide.with(viewholder.itemView.context).load(course.img)
                    .addListener(object : RequestListener<Drawable> {
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
                            viewholder.binding.itemClassInListImgOverlay.visibility = View.VISIBLE
                            return false
                        }

                    }).into(viewholder.binding.itemClassInListImg)
            }
            viewholder.binding.itemClassInListTitleTv.text = course.title
            viewholder.binding.itemClassInListRatingBar.rating = course.rating
            viewholder.binding.itemCommonInListDateTv.text =
                Utils.getDuration(context, course.duration)
        }
    }

    inner class ViewHolder(val binding: ItemClassInListBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.itemClassInListContainer.setOnClickListener(this)
        }

        fun showAddToCalendar() {
            binding.itemClassInListAddToCalendarImg.visibility = View.VISIBLE
            binding.itemClassInListAddToCalendarImg.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.itemClassInListAddToCalendarImg -> {
                    onCalendarClick(itemView.context, items[bindingAdapterPosition])
                }

                R.id.itemClassInListContainer -> {
                    showCourseDetails(items[bindingAdapterPosition])
                }
            }
        }
    }

    private fun showCourseDetails(course: Course) {
        val bundle = Bundle()
        bundle.putParcelable(App.COURSE, course)

        val frag = CourseDetailsFrag()
        frag.arguments = bundle
        activity.transact(frag)
    }

    private fun onCalendarClick(context: Context, course: Course) {
        val calendar = Calendar.getInstance()
        calendar.time = Date(course.startDate * 1000)

        Utils.addToCalendar(
            context,
            context.getString(R.string.webinar),
            calendar.timeInMillis,
            0
        )
    }
}