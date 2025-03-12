package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemQuizBinding
import com.online.course.manager.Utils
import com.online.course.model.Quiz
import com.online.course.model.QuizResult

class QuizRvAdapter(quizzes: List<Quiz>) :
    BaseArrayAdapter<Quiz, QuizRvAdapter.ViewHolder>(quizzes) {

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
        val quiz = items[position]
        val course = quiz.course

        if (quiz.time > 0) {
            viewholder.binding.itemQuizDateTv.text = Utils.getDuration(context, quiz.time)
        } else {
            viewholder.binding.itemQuizDateTv.text = context.getString(R.string.unlimited)
        }

        viewholder.binding.itemQuizTitleTv.text = quiz.title
        viewholder.binding.itemQuizDescTv.text = course.title

        viewholder.binding.itemQuizStatusTv.visibility = View.GONE
        viewholder.binding.itemQuizMarkTv.visibility = View.GONE
        viewholder.binding.itemQuizBadgeImg.visibility = View.GONE
        viewholder.binding.itemQuizCalendarImg.setImageResource(R.drawable.ic_time)

        viewholder.binding.itemQuizQuestionsCountImg.visibility = View.VISIBLE
        viewholder.binding.itemQuizQuestionsCountTv.visibility = View.VISIBLE
        viewholder.binding.itemQuizQuestionsCountTv.text = quiz.questionCount.toString()

        if (course.img != null) {
            Glide.with(viewholder.itemView.context).load(course.img)
                .into(viewholder.binding.itemQuizImg)
        }
    }

    class ViewHolder(val binding: ItemQuizBinding) :
        RecyclerView.ViewHolder(binding.root)
}