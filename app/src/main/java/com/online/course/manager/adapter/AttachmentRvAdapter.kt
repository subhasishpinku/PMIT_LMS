package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.online.course.databinding.ItemAttachmentBinding
import com.online.course.model.Attachment

class AttachmentRvAdapter(attachments: List<Attachment>) :
    BaseArrayAdapter<Attachment, AttachmentRvAdapter.ViewHolder>(attachments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAttachmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val attachment = items[position]
        viewholder.binding.itemAttachmentNameTv.text = attachment.title
        attachment.size?.let {
            viewholder.binding.itemAttachmentSizeTv.text = it
        }
    }

    class ViewHolder(val binding: ItemAttachmentBinding) : RecyclerView.ViewHolder(binding.root)
}