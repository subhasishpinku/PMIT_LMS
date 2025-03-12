package com.online.course.manager.adapter

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemCartMeetingBinding
import com.online.course.databinding.ItemFavoriteBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.CartItem
import com.online.course.model.Course
import com.online.course.ui.frag.CartFrag
import com.online.course.ui.widget.AppDialog

class CartRvAdapter(items: List<CartItem>, private val frag: CartFrag) :
    BaseArrayAdapter<CartItem, RecyclerView.ViewHolder>(items) {

    companion object {
        const val TYPE_MEETING = 1
        const val TYPE_COURSE = 2
    }

    override fun getItemViewType(position: Int): Int {
        val type = items[position].type
        return if (type == Course.Type.WEBINAR.value || type == Course.Type.BUNDLE.value)
            TYPE_COURSE else TYPE_MEETING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_MEETING) {
            return CartMeetingViewHolder(
                ItemCartMeetingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return CartCourseViewHolder(
                ItemFavoriteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        var item = items[position]
        val context = viewholder.itemView.context

        if (viewholder is CartCourseViewHolder) {
            item = item!!
            if (item.discount > 0) {
                viewholder.binding.itemFavoriteCourseDiscountPctTv.text =
                    (item.discount.toString() + "%" + context.getString(R.string.off))

                viewholder.binding.itemFavoriteCoursePriceTv.text =
                    Utils.formatPrice(context, item.priceWithDiscount)

                viewholder.binding.itemFavoriteCoursePriceWithDiscountTv.text =
                    Utils.formatPrice(context, item.price)

                if (item.price > 0) {
                    viewholder.binding.itemFavoriteCoursePriceWithDiscountTv.paintFlags =
                        Paint.STRIKE_THRU_TEXT_FLAG
                }

            } else {
                viewholder.binding.itemFavoriteCoursePriceTv.text =
                    Utils.formatPrice(context, item.price)
            }

            if (item.img != null) {
                Glide.with(viewholder.itemView.context).load(item.img)
                    .into(viewholder.binding.itemFavoriteCourseImg)
            }
            viewholder.binding.itemFavoriteCourseTitleTv.text = item.title
            viewholder.binding.itemFavoriteCourseRatingBar.rating = item.rating
            viewholder.binding.itemFavoriteCourseDateTv.text =
                Utils.getDateFromTimestamp(item.createdAt)
        } else if (viewholder is CartMeetingViewHolder) {

            if (item.img != null) {
                Glide.with(viewholder.itemView.context).load(item.img)
                    .into(viewholder.binding.itemCartMeetingImg)
            }

            val date = Utils.getDateFromTimestamp(item.date, "yyyy-MM-dd")

            var descBuilder = StringBuilder(context.getString(R.string.meeting_reservation))

            if (item.time.start.isNotBlank()) {
                val startTime = Utils.getCurrentDateTime(
                    "$date ${item.time.start}",
                    item.timeZone,
                    "yyyy-MM-dd HH:mma",
                    "yyyy-MM-dd HH:mma"
                )!!
                descBuilder =
                    descBuilder.append("\n").append(startTime[0]).append(" | ").append(" | ")
                        .append(startTime[1])
            }

            if (item.time.end.isNotBlank()) {
                val endTime = Utils.getCurrentDateTime(
                    "$date ${item.time.end}",
                    item.timeZone,
                    "yyyy-MM-dd HH:mma",
                    "yyyy-MM-dd HH:mma"
                )!![1]
                descBuilder = descBuilder.append("-").append(endTime)
            }

            viewholder.binding.itemCartMeetingReservationDesc.text = descBuilder.toString()
            viewholder.binding.itemCartMeetingNameTv.text = item.user.name
            viewholder.binding.itemCartMeetingDateTv.text = Utils.getDateFromTimestamp(item.date)

            viewholder.binding.itemCartMeetingPriceTv.text =
                Utils.formatPrice(context, item.amount)
        }
    }

    inner class CartCourseViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {

        init {
            binding.itemFavoriteCourseContainer.setOnLongClickListener(this)
            binding.itemFavoriteRemoveContainer.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            removeItem(itemView.context, items[bindingAdapterPosition], bindingAdapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            binding.itemFavoriteSwipeLayout.open()
            return true
        }
    }


    inner class CartMeetingViewHolder(val binding: ItemCartMeetingBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.itemCartMeetingContainer.setOnLongClickListener(this)
            binding.itemCartMeetingRemoveContainer.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            removeItem(itemView.context, items[bindingAdapterPosition], bindingAdapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            binding.itemCartMeetingSwipeLayout.open()
            return true
        }
    }

    private fun removeItem(context: Context, cartItem: CartItem, position: Int) {
        val dialog = AppDialog.instance
        val bundle = Bundle()

        bundle.putString(App.TITLE, context.getString(R.string.delete))
        bundle.putString(App.TEXT, context.getString(R.string.delete_from_fav))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                }

                override fun onOk() {
                    frag.removeItem(cartItem.id, position)
                }
            })

        dialog.show(frag.childFragmentManager, null)
    }
}