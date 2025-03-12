package com.online.course.manager.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemCourseCommon2Binding
import com.online.course.model.view.CourseCommonItem

class CourseCommon2RvAdapter(items: List<CourseCommonItem>) :
    BaseArrayAdapter<CourseCommonItem, CourseCommon2RvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCourseCommon2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        val context = viewHolder.itemView.context

        viewHolder.binding.itemCourseCommon2Img.setImageResource(item.imgResource(context))
        viewHolder.binding.itemCourseCommon2Img.setBackgroundResource(item.imgBgResource(context))
        viewHolder.binding.itemCourseCommon2TitleTv.text = item.title(context)
        viewHolder.binding.itemCourseCommon2DescTv.text = item.desc(context)

        if (item.status() != "") {
            viewHolder.binding.itemCourseCommon2StatusTv.text = item.status()
            viewHolder.binding.itemCourseCommon2StatusTv.setBackgroundResource(item.statusBg())
        }
    }

    inner class ViewHolder(val binding: ItemCourseCommon2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemCourseCommonCard2.setBackgroundResource(R.drawable.round_view_white_corner15)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.itemCourseCommonCard2.foreground =
                    ContextCompat.getDrawable(itemView.context, R.drawable.ripple_effect_corner_15)
            }
        }
    }
}