package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.databinding.ItemReviewBinding
import com.online.course.manager.Utils
import com.online.course.model.Review

class ReviewRvAdapter(reviews: List<Review>) :
    BaseArrayAdapter<Review, ReviewRvAdapter.ViewHolder>(reviews) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val review = items[position]
        val context = viewholder.itemView.context

        viewholder.binding.itemReviewTv.text = review.description
        viewholder.binding.itemReviewDateTv.text = Utils.getDateFromTimestamp(review.createdAt)

        val user = review.user
        viewholder.binding.itemReviewUserNameTv.text = user?.name
        viewholder.binding.itemReviewUserRatingBar.rating = review.rating
        if (user?.avatar != null) {
            Glide.with(context).load(user.avatar).into(viewholder.binding.itemReviewUserImg)
        }
    }

    inner class ViewHolder(val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root)
}