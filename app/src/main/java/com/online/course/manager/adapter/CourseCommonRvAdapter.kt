package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.online.course.databinding.ItemCourseCommonBinding
import com.online.course.manager.Utils
import com.online.course.model.view.CourseCommonItem

class CourseCommonRvAdapter(items: List<CourseCommonItem>, private val wideClickable: Boolean = false) :
    BaseArrayAdapter<CourseCommonItem, CourseCommonRvAdapter.ViewHolder>(items) {

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

        if (position == 0 || position == itemCount - 1 || wideClickable) {
            val card = viewHolder.binding.itemCourseCommonCard
            val params = card.layoutParams as LinearLayout.LayoutParams

            if (wideClickable) {
                card.isFocusable = true
                card.isClickable = true
                params.width = Utils.changeDpToPx(context, 300f).toInt()
            }
            if (position == 0) {
                params.marginStart = 0
            } else if (position == itemCount - 1) {
                params.marginEnd = 0
            }
            card.requestLayout()
        }

        viewHolder.binding.itemCourseCommonImg.setImageResource(item.imgResource(context))
        viewHolder.binding.itemCourseCommonImg.setBackgroundResource(item.imgBgResource(context))
        viewHolder.binding.itemCourseCommonTitleTv.text = item.title(context)
        viewHolder.binding.itemCourseCommonDescTv.text = item.desc(context)
    }

    inner class ViewHolder(val binding: ItemCourseCommonBinding) :
        RecyclerView.ViewHolder(binding.root)
}