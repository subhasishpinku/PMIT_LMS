package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.databinding.ItemBankInfoBinding
import com.online.course.model.SystemBankAccount

class BankInfoRvAdapter(info: List<SystemBankAccount>) :
    BaseArrayAdapter<SystemBankAccount, BankInfoRvAdapter.ViewHolder>(info) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBankInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val info = items[position]

        if (info.image != null) {
            Glide.with(viewholder.itemView.context).load(info.image)
                .into(viewholder.binding.itemBankInfoGatewayImg)
        }

        viewholder.binding.itemBankInfoGatewayTitleTv.text = info.title
        viewholder.binding.itemBankInfoCardIdValueTv.text = info.cardId
        viewholder.binding.itemBankInfoAccountValueTv.text = info.accountId
        viewholder.binding.itemBankInfoIBANValueTv.text = info.iban
    }

    class ViewHolder(val binding: ItemBankInfoBinding) : RecyclerView.ViewHolder(binding.root)
}