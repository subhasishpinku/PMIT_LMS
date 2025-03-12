package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemStudentResultBinding
import com.online.course.manager.Utils
import com.online.course.model.QuizResult

class QuizStudentResultRvAdapter(results: List<QuizResult>) :
    BaseArrayAdapter<QuizResult, QuizStudentResultRvAdapter.ViewHolder>(results) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStudentResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val result = items[position]
        val quiz = result.quiz
        val course = quiz.course
        val context = viewholder.binding.root.context

        if (result.user!!.avatar != null) {
            Glide.with(viewholder.itemView.context).load(result.user!!.avatar)
                .into(viewholder.binding.itemStudentResultImg)
        }

        viewholder.binding.itemStudentResultNameTv.text = result.user!!.name
        viewholder.binding.itemStudentResultDateTv.text =
            Utils.getDateFromTimestamp(result.createdAt)
        viewholder.binding.itemStudentResultTitleTv.text = course.title
        viewholder.binding.itemStudentResultQuizMarkTv.text =
            ("${result.userGrade}/${quiz.totalMark}")

        when (result.status) {
            QuizResult.Result.FAILED.value -> {
                viewholder.binding.itemStudentResultQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
                viewholder.binding.itemStudentResultQuizBadgeImg.setImageResource(R.drawable.ic_badge_red)
            }

            QuizResult.Result.PASSED.value -> {
                viewholder.binding.itemStudentResultQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.accent
                    )
                )
                viewholder.binding.itemStudentResultQuizBadgeImg.setImageResource(R.drawable.ic_badge_green)
            }

            QuizResult.Result.WAITING.value -> {
                viewholder.binding.itemStudentResultQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.gold
                    )
                )
                viewholder.binding.itemStudentResultQuizBadgeImg.setImageResource(R.drawable.ic_badge_yellow)
                viewholder.binding.itemStudentResultStatusTv.text = result.status
                viewholder.binding.itemStudentResultStatusTv.visibility = View.VISIBLE
            }
        }

    }

    class ViewHolder(val binding: ItemStudentResultBinding) :
        RecyclerView.ViewHolder(binding.root)
}