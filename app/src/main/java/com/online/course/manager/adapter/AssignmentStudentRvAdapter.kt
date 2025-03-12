package com.online.course.manager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemStudentResultBinding
import com.online.course.manager.Utils
import com.online.course.model.Assignment
import com.online.course.model.QuizResult

class AssignmentStudentRvAdapter(assignments: List<Assignment>) :
    BaseArrayAdapter<Assignment, AssignmentStudentRvAdapter.ViewHolder>(assignments) {

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
        val binding = viewholder.binding
        val context = binding.root.context
        val item = items[position]

        val student = item.student!!

        if (student.avatar != null) {
            Glide.with(viewholder.itemView.context).load(student.avatar)
                .into(binding.itemStudentResultImg)
        }

        binding.itemStudentResultNameTv.text = student.name
        student.email?.let {
            binding.itemStudentResultTitleTv.text = it
        }

        item.lastSubmission?.let {
            binding.itemStudentResultDateTv.text = Utils.getDateFromTimestamp(it)
        }

        setStatus(item, binding, context)
    }

    private fun setStatus(
        assignment: Assignment,
        binding: ItemStudentResultBinding,
        context: Context
    ) {
        val statusRes: Int
        val statusBg: Int
        val markImg: Int
        val markTxt: String

        when (assignment.userStatus) {
            Assignment.UserStatus.NOT_PASSED.value -> {
                binding.itemStudentResultQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )

                markTxt = "${assignment.grade}/${assignment.totalGrade}"

                statusRes = R.string.failed
                statusBg = R.drawable.round_view_red_corner10
                markImg = R.drawable.ic_badge_red
            }

            Assignment.UserStatus.PASSED.value -> {
                binding.itemStudentResultQuizMarkTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.accent
                    )
                )
                markTxt = "${assignment.grade}/${assignment.totalGrade}"

                statusRes = R.string.passed
                statusBg = R.drawable.round_view_accent_corner10
                markImg = R.drawable.ic_badge_green
            }

            Assignment.UserStatus.PENDING.value -> {
                markTxt =
                    "${assignment.usedAttemptsCount}/${assignment.totalAttempts} ${
                        context.getString(
                            R.string.attempts
                        )
                    }"

                statusRes = R.string.pending
                statusBg = R.drawable.round_view_orange_corner10
                markImg = R.drawable.ic_plus2
            }

            else -> {
                markTxt =
                    "${assignment.usedAttemptsCount}/${assignment.totalAttempts} ${
                        context.getString(
                            R.string.attempts
                        )
                    }"

                statusRes = R.string.not_submitted
                statusBg = R.drawable.round_view_red_corner10
                markImg = R.drawable.ic_plus2
            }
        }

        binding.itemStudentResultQuizBadgeImg.setImageResource(markImg)
        binding.itemStudentResultStatusTv.setText(statusRes)
        binding.itemStudentResultStatusTv.setBackgroundResource(statusBg)
        binding.itemStudentResultQuizMarkTv.text = markTxt
        binding.itemStudentResultStatusTv.visibility = View.VISIBLE
    }

    class ViewHolder(val binding: ItemStudentResultBinding) : RecyclerView.ViewHolder(binding.root)
}