package com.online.course.manager.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemCertificateBinding
import com.online.course.manager.Utils
import com.online.course.model.CompletionCert
import com.online.course.model.Quiz
import com.online.course.model.QuizResult

class CertificateRvAdapter<T : Parcelable>(certificates: List<T>) :
    BaseArrayAdapter<T, CertificateRvAdapter.ViewHolder>(certificates) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCertificateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val item = items[position]
        if (item is QuizResult) {
            viewholder.binding.itemCertificateTitleTv.text = item.quiz.title
            viewholder.binding.itemCertificateDescTv.text = item.quiz.course.title
            viewholder.binding.itemCertificateDateTv.text =
                Utils.getDateTimeFromTimestamp(item.createdAt)
            viewholder.binding.itemCertificateTotalMarkTv.text = item.quiz.totalMark.toString()

            if (item.certificate == null) {
                viewholder.binding.itemCertificateImg.setImageResource(R.drawable.cert_default)
            } else {
                Glide.with(viewholder.itemView.context).load(item.certificate!!.img)
                    .into(viewholder.binding.itemCertificateImg)
            }
        } else if (item is Quiz) {
            viewholder.binding.itemCertificateTitleTv.text = item.title
            viewholder.binding.itemCertificateDescTv.text = item.course.title
            viewholder.binding.itemCertificateDateTv.text =
                Utils.getDateTimeFromTimestamp(item.course.createdAt)
            viewholder.binding.itemCertificateTotalMarkTv.text = item.totalMark.toString()

            if (item.course.img != null) {
                Glide.with(viewholder.itemView.context).load(item.course.img)
                    .into(viewholder.binding.itemCertificateImg)
            }
        } else if (item is CompletionCert) {
            val course = item.course

            viewholder.binding.itemCertificateTitleTv.text = course.title
            viewholder.binding.itemCertificateDescTv.text = Utils.getTextAsHtml(course.description)
            viewholder.binding.itemCertificateDateTv.text =
                Utils.getDateTimeFromTimestamp(item.date)
            viewholder.binding.itemCertificateTotalMarkTv.visibility = View.GONE
            viewholder.binding.itemCertificateBadgeImg.visibility = View.GONE

            if (course.img != null) {
                Glide.with(viewholder.itemView.context).load(course.img)
                    .into(viewholder.binding.itemCertificateImg)
            }
        }
    }

    class ViewHolder(val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root)
}