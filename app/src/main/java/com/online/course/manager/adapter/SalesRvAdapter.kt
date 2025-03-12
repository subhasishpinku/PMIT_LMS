package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemSalesBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.SalesItem

class SalesRvAdapter(sales: List<SalesItem>) :
    BaseArrayAdapter<SalesItem, SalesRvAdapter.ViewHolder>(sales) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSalesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val sales = items[position]
        val context = viewholder.itemView.context

        if (sales.buyer.avatar != null) {
            Glide.with(viewholder.itemView.context).load(sales.buyer.avatar)
                .into(viewholder.binding.itemSalesBuyerImg)
        }

        viewholder.binding.itemSalesBuyerNameTv.text = sales.buyer.name
        viewholder.binding.itemSalesDateTv.text = Utils.getDateFromTimestamp(sales.createdAt)
        viewholder.binding.itemSalesPriceTv.text = Utils.formatPrice(context, sales.income, false)
        viewholder.binding.itemSalesTypeTv.text = sales.type

        when (sales.type) {
            SalesItem.Type.COURSE.type -> {
                viewholder.binding.itemSalesTitleTv.text = sales.course!!.title
                viewholder.binding.itemSalesTypeTv.setBackgroundResource(R.drawable.round_view_light_green_corner10)
            }

            SalesItem.Type.MEETING.type -> {
                viewholder.binding.itemSalesTitleTv.text =
                    context.getString(R.string.meeting_reservation)
                viewholder.binding.itemSalesTypeTv.setBackgroundResource(R.drawable.round_view_blue_corner10)
            }
        }
    }

    class ViewHolder(val binding: ItemSalesBinding) : RecyclerView.ViewHolder(binding.root)
}