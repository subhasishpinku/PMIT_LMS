package com.online.course.manager.adapter

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.course.R
import com.online.course.databinding.ItemQuizAnswerBinding
import com.online.course.manager.Utils.toBoolean
import com.online.course.model.QuizQuestionAnswer

class QuizQuestionAnswerRvAdapter(
    answers: List<QuizQuestionAnswer>,
    private val addAnswer: Boolean = false,
    private val isInstructorReview: Boolean = false
) :
    BaseArrayAdapter<QuizQuestionAnswer, QuizQuestionAnswerRvAdapter.ViewHolder>(answers) {

    private var mSelectedCard: View? = null
    private var mSelectedItem: QuizQuestionAnswer? = null

    fun getSelectedItem(): QuizQuestionAnswer? {
        return mSelectedItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemQuizAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val answer = items[position]
        val context = viewholder.itemView.context
        viewholder.binding.itemQuizAnswerTv.text = answer.title
        viewholder.init(answer)
        if (answer.image != null) {
            viewholder.binding.itemQuizAnswerImgProgressBar.visibility = View.VISIBLE

            viewholder.binding.itemQuizAnswerTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            Glide.with(context).load(answer.image).addListener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    viewholder.binding.itemQuizAnswerImgProgressBar.visibility = View.GONE
                    return false
                }

            }).into(viewholder.binding.itemQuizAnswerImg)
        } else {
            viewholder.binding.itemQuizAnswerImgProgressBar.visibility = View.GONE
        }

        if (addAnswer && answer.isUserAnswer) {
            if (isInstructorReview) {
                viewholder.binding.itemQuizAnswerBadgeTv.text =
                    context.getString(R.string.student_answer)
            } else {
                viewholder.binding.itemQuizAnswerBadgeTv.text =
                    context.getString(R.string.your_answer)
            }
            if (answer.correct.toBoolean()) {
                viewholder.binding.itemQuizAnswerBadgeTv.setBackgroundResource(R.drawable.round_view_accent_corner10_op30)
                viewholder.binding.itemQuizAnswerBadgeTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.accent
                    )
                )
            } else {
                viewholder.binding.itemQuizAnswerBadgeTv.setBackgroundResource(R.drawable.round_view_red_corner10_op30)
                viewholder.binding.itemQuizAnswerBadgeTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            }
        }
    }

    inner class ViewHolder(val binding: ItemQuizAnswerBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var init = false

        fun init(quizAnswer: QuizQuestionAnswer) {
            if (!init) {
                init = true
                var transitionDrawableResource = R.drawable.bg_transition_white_to_green

                if (addAnswer && quizAnswer.isUserAnswer && !quizAnswer.correct.toBoolean()) {
                    transitionDrawableResource = R.drawable.bg_transition_white_to_red
                }

                val transitionDrawable = ContextCompat.getDrawable(
                    itemView.context,
                    transitionDrawableResource
                ) as TransitionDrawable

                binding.itemQuizAnswerCard.background = transitionDrawable

                if (addAnswer) {
                    binding.itemQuizAnswerCardContainer.isFocusable = false
                    binding.itemQuizAnswerCardContainer.isClickable = false

                    if (quizAnswer.correct.toBoolean() || quizAnswer.isUserAnswer) {
                        transitionDrawable.startTransition(300)
                    }
                } else {
                    itemView.setOnClickListener(this)
                }
            }
        }

        override fun onClick(v: View?) {
            val quizAnswer = items[bindingAdapterPosition]
            itemClick(quizAnswer)
        }

        fun itemClick(quizAnswer: QuizQuestionAnswer) {
            if (mSelectedItem != null && mSelectedItem!!.equals(quizAnswer)) {
                (binding.itemQuizAnswerCard.background as TransitionDrawable).reverseTransition(300)
                mSelectedItem = null
                mSelectedCard = null
                return
            }

            if (mSelectedCard != null) {
                (mSelectedCard!!.background as TransitionDrawable).reverseTransition(300)
            }

            val transition = binding.itemQuizAnswerCard.background as TransitionDrawable
            transition.startTransition(300)

            mSelectedItem = quizAnswer
            mSelectedCard = binding.itemQuizAnswerCard
        }
    }
}