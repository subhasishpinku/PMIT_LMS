package com.online.course.manager.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.course.R
import com.online.course.databinding.ItemClassInListBinding
import com.online.course.databinding.ItemCourseBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.Course
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.CourseDetailsFrag
import java.util.*

class ClassListGridRvAdapter(
    items: List<Course>,
    private val activity: MainActivity,
    private val layoutManager: RecyclerView.LayoutManager? = null,
    private val showPoints: Boolean = false
) :
    BaseArrayAdapter<Course, RecyclerView.ViewHolder>(items) {

    enum class ViewType(private val vall: Int) {
        GRID(1),
        LIST(2);

        fun value(): Int {
            return vall
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (layoutManager != null && layoutManager is GridLayoutManager) {
            if (layoutManager.spanCount == 1) {
                return ViewType.LIST.value()
            }

            return ViewType.GRID.value()
        }

        return ViewType.LIST.value()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ViewType.LIST.value()) {
            return ListViewHolder(
                ItemClassInListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return GridViewHolder(
                ItemCourseBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        val course = items[position]
        val context = viewholder.itemView.context


        if (viewholder is ListViewHolder) {
            viewholder.binding.itemClassInListUserNameTv.text = course.teacher.name

            if (showPoints) {
                viewholder.binding.itemCommonInPriceTv.text = course.points?.toString()
                viewholder.binding.itemCommonInPriceTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.orange
                    )
                )
            } else {
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

        } else if (viewholder is GridViewHolder) {

            viewholder.binding.itemCourseTitleTv.text = course.title
            viewholder.binding.itemCourseRatingTv.text = course.rating.toString()

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
                            viewholder.binding.itemCourseImgOverlay.visibility = View.VISIBLE
                            return false
                        }


                    }).into(viewholder.binding.itemCourseImg)
            }

             if (course.discount > 0 && !showPoints) {
                viewholder.binding.itemCourseTypeDiscountTv.setBackgroundResource(R.drawable.round_view_red_corner10_op30)
                viewholder.binding.itemCourseTypeDiscountTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
                viewholder.binding.itemCourseTypeDiscountTv.text =
                    (course.discount.toString() + "% " + context.getString(R.string.off))

                viewholder.binding.itemCoursePriceWithDiscountTv.text =
                    Utils.formatPrice(context, course.price)
                viewholder.binding.itemCoursePriceWithDiscountTv.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG
                viewholder.binding.itemCoursePriceWithDiscountTv.visibility = View.VISIBLE

                viewholder.binding.itemCoursePriceTv.text =
                    Utils.formatPrice(context, course.priceWithDiscount)

            } else if (course.isLive()) {
                viewholder.binding.itemCoursePriceTv.text = Utils.formatPrice(context, course.price)

                course.liveCourseStatus?.let {
                    val color: Int
                    val bg: Int
                    val liveStatus: String

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
                viewholder.binding.itemCoursePriceTv.text = Utils.formatPrice(context, course.price)

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

            if (showPoints){
                viewholder.binding.itemCoursePriceTv.text = course.points?.toString()
                viewholder.binding.itemCoursePriceTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.orange
                    )
                )
            }

            if (course.progress != null) {
                if (course.progress!! > 50) {
                    viewholder.binding.itemCourseImgProgressBar.progressTintList =
                        ContextCompat.getColorStateList(context, R.color.accent)
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
    }

    inner class ListViewHolder(val binding: ItemClassInListBinding) :
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

    inner class GridViewHolder(val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            val layoutParams = binding.itemCourseContainer.layoutParams as ViewGroup.LayoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.itemCourseContainer.requestLayout()
            binding.itemCourseContainer.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.itemCourseAddToCalendarImg -> {
                    onCalendarClick(itemView.context, items[bindingAdapterPosition])
                }

                R.id.itemCourseContainer -> {
                    showCourseDetails(items[bindingAdapterPosition])
                }
            }
        }

        fun showAddToCalendar() {
            binding.itemCourseAddToCalendarImg.visibility = View.VISIBLE
            binding.itemCourseAddToCalendarImg.setOnClickListener(this)
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