package com.online.course.manager.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.databinding.ItemCommonBinding
import com.online.course.model.view.CommonItem


class CommonRvAdapter(items: List<CommonItem>) :
    BaseArrayAdapter<CommonItem, CommonRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        val context = viewHolder.binding.root.context

        viewHolder.binding.itemCommonTitleTv.text = item.title(context)
        viewHolder.binding.itemCommonDescTv.text = item.desc(context)

        if (item.cardBg() != null) {
            viewHolder.binding.itemCommonCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    item.cardBg()!!
                )
            )
        }

        if (item.showUnseenStatus()) {
            viewHolder.binding.itemCommonUnseenCircle.visibility = View.VISIBLE
        }

        if (item.img() != null) {
            Glide.with(context).load(item.img())
                .into(viewHolder.binding.itemCommonImg)
        } else if (item.imgResource() != null) {
            viewHolder.binding.itemCommonImg.setImageResource(item.imgResource()!!)
        }

        val imgPadding = item.imgPadding(context)
        if (imgPadding != null) {
            viewHolder.binding.itemCommonImg.setPadding(
                imgPadding,
                imgPadding,
                imgPadding,
                imgPadding
            )
        }

        if (item.imgBgResource() != null) {
            viewHolder.binding.itemCommonImg.setBackgroundResource(item.imgBgResource()!!)
        } else if (item.imgBgDrawable(context) != null) {
            viewHolder.binding.itemCommonImg.background = item.imgBgDrawable(context)
        }

        if (!item.isClickable()) {
            viewHolder.binding.itemCommonCard.isFocusable = false
            viewHolder.binding.itemCommonCard.isClickable = false
            viewHolder.binding.itemCommonCard.foreground = null
        }

        if (item.status(context) != null) {
            val itemCommonStatusTv = viewHolder.binding.itemCommonStatusTv
            val status = item.status(context)!!
            itemCommonStatusTv.text = status.status
            itemCommonStatusTv.setTextColor(status.textColor)

            if (status.textBg != 0) {
                itemCommonStatusTv.setBackgroundResource(status.textBg)
            }

            if (status.textSize != 0f) {
                itemCommonStatusTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, status.textSize)
            }

            itemCommonStatusTv.visibility = View.VISIBLE
        }
    }

    inner class ViewHolder(val binding: ItemCommonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun hideSatusCircle() {
            binding.itemCommonUnseenCircle.visibility = View.GONE
        }
    }
}