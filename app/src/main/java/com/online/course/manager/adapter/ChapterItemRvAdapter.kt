package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemCourseCommonBinding
import com.online.course.manager.Utils
import com.online.course.model.view.CourseCommonItem
import kotlin.math.roundToInt

class ChapterItemRvAdapter(
    items: List<CourseCommonItem>,
    private val windowManager: WindowManager
) :
    BaseArrayAdapter<CourseCommonItem, ChapterItemRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCourseCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        val context = viewHolder.itemView.context

        if (position == 0 || position == itemCount - 1) {
            val card = viewHolder.binding.itemCourseCommonCard
            val params = card.layoutParams as LinearLayout.LayoutParams
            val margin16 = context.resources.getDimension(R.dimen.margin_16).roundToInt()

            if (position == 0) {
                params.marginStart = margin16
            } else if (position == itemCount - 1) {
                params.marginEnd = margin16
            }
            card.requestLayout()
        }

        viewHolder.binding.itemCourseCommonImg.setImageResource(item.imgResource(context))
        viewHolder.binding.itemCourseCommonImg.setBackgroundResource(item.imgBgResource(context))
        viewHolder.binding.itemCourseCommonTitleTv.text = item.title(context)
        viewHolder.binding.itemCourseCommonDescTv.text = item.desc(context)
    }

    inner class ViewHolder(val binding: ItemCourseCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            val context = itemView.context
            val card = binding.itemCourseCommonCard
            val params = card.layoutParams as LinearLayout.LayoutParams

            val margin8 = context.resources.getDimension(R.dimen.margin_8).roundToInt()
            val margin100 = context.resources.getDimension(R.dimen.margin_100).roundToInt()

            val screenWidth = Utils.getScreenWidth(windowManager)

            card.isFocusable = true
            card.isClickable = true
            params.marginStart = margin8
            params.marginEnd = margin8
            params.width = screenWidth - margin100 - 3 * margin8
            card.requestLayout()
        }
    }
}