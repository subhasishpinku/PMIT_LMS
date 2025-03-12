package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemQuizBinding
import com.online.course.manager.Utils
import com.online.course.model.QuizResult

class QuizResultRvAdapter(results: List<QuizResult>, private val isClickable: Boolean = true) :
    BaseArrayAdapter<QuizResult, QuizResultRvAdapter.ViewHolder>(results) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemQuizBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val context = viewholder.binding.root.context
        val result = items[position]
        val quiz = result.quiz
        val course = quiz.course

        viewholder.binding.itemQuizTitleTv.text = quiz.title
        viewholder.binding.itemQuizDateTv.text = Utils.getDateFromTimestamp(result.createdAt)
        viewholder.binding.itemQuizDescTv.text = course.title

        viewholder.binding.itemQuizMarkTv.text = ("${result.userGrade}/${quiz.totalMark}")
        viewholder.binding.itemQuizStatusTv.text = result.status

        when (result.status) {
            QuizResult.Result.FAILED.value -> {
                viewholder.binding.itemQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
                viewholder.binding.itemQuizBadgeImg.setImageResource(R.drawable.ic_badge_red)
                viewholder.binding.itemQuizStatusTv.setBackgroundResource(R.drawable.round_view_red_corner10)
            }

            QuizResult.Result.PASSED.value -> {
                viewholder.binding.itemQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.accent
                    )
                )
                viewholder.binding.itemQuizBadgeImg.setImageResource(R.drawable.ic_badge_green)
                viewholder.binding.itemQuizStatusTv.setBackgroundResource(R.drawable.round_view_accent_corner10)
            }

            QuizResult.Result.WAITING.value -> {
                viewholder.binding.itemQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.gold
                    )
                )
                viewholder.binding.itemQuizBadgeImg.setImageResource(R.drawable.ic_badge_yellow)
                viewholder.binding.itemQuizStatusTv.setBackgroundResource(R.drawable.round_view_gold_corner10)
            }
        }

        if (course.img != null) {
            Glide.with(viewholder.itemView.context).load(course.img)
                .into(viewholder.binding.itemQuizImg)
        }

    }

    inner class ViewHolder(val binding: ItemQuizBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            if (!isClickable) {
                binding.itemQuizCard.isClickable = false
                binding.itemQuizCard.isFocusable = false
            }
        }
    }
}