package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemAssignmentBinding
import com.online.course.manager.Utils
import com.online.course.model.Assignment

class AssignmentsRvAdapter(assigments: List<Assignment>, private val isInstructor: Boolean = false) :
    BaseArrayAdapter<Assignment, AssignmentsRvAdapter.ViewHolder>(assigments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAssignmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val assignment = items[position]
        val binding = viewholder.binding
        val context = binding.root.context

        if (!assignment.courseImage.isNullOrEmpty()) {
            Glide.with(context).load(assignment.courseImage).into(binding.itemAssignmentsImg)
        }
        binding.itemMyAssignmentTitleTv.text = assignment.title
        binding.itemMyAssignmentDescTv.text = assignment.description

        if (isInstructor) {
            binding.itemAssignmentAttemptsImg.setImageResource(R.drawable.ic_more_circle)

            val pendingTxt = "${assignment.pendingCount} ${context.getString(R.string.pending)}"
            binding.itemAssignmentAttemptsTv.text = pendingTxt

            binding.itemAssignmentDeadlineKeyTv.setText(R.string.total_submissions)
            binding.itemAssignmentDeadlineTv.text = assignment.submissionsCount.toString()

            binding.itemAssignmentSecondDescKeyTv.setText(R.string.average_grade)
            binding.itemAssignmentSecondDescTv.text = assignment.averageGrade.toString()

            setInstructorStatus(binding.itemAssignmentStatusTv, assignment.status)

        } else {
            setStatusAndSecDesc(binding, assignment)
            binding.itemAssignmentDeadlineTv.text =
                Utils.getDateTimeFromTimestamp(assignment.deadlineTime)

            val attempts = "${assignment.usedAttemptsCount}/${assignment.totalAttempts}"
            binding.itemAssignmentAttemptsTv.text = attempts
        }

    }

    private fun setInstructorStatus(statusTv: TextView, status: String) {
        val text: String
        val bg: Int
        val context = statusTv.context

        when (status) {
            Assignment.Status.ACTIVE.value -> {
                text = context.getString(R.string.active)
                bg = R.drawable.round_view_green_corner10
            }

            else -> {
                text = context.getString(R.string.disabled)
                bg = R.drawable.round_view_red_corner10
            }
        }

        statusTv.setBackgroundResource(bg)
        statusTv.text = text
    }


    private fun setStatusAndSecDesc(binding: ItemAssignmentBinding, assignment: Assignment) {
        var text = ""
        var bg = R.drawable.round_view_red_corner10
        val context = binding.root.context
        var headerTv = ""
        var descTv = "-"

        when (assignment.userStatus) {
            Assignment.UserStatus.NOT_PASSED.value -> {
                text = context.getString(R.string.failed)
                bg = R.drawable.round_view_red_corner10
                headerTv = context.getString(R.string.grade)
                assignment.grade?.let {
                    descTv = "${assignment.grade}/${assignment.totalGrade}"
                }
            }

            Assignment.UserStatus.PASSED.value -> {
                text = context.getString(R.string.passed)
                bg = R.drawable.round_view_accent_corner10
                headerTv = context.getString(R.string.grade)
                assignment.grade?.let {
                    descTv = "${assignment.grade}/${assignment.totalGrade}"
                }
            }

            Assignment.UserStatus.PENDING.value -> {
                text = context.getString(R.string.pending)
                bg = R.drawable.round_view_orange_corner10
                headerTv = context.getString(R.string.last_submission)
                descTv =
                    assignment.lastSubmission?.let { Utils.getDateFromTimestamp(it) }.toString()
            }

            Assignment.UserStatus.NOT_SUBMITTED.value -> {
                text = context.getString(R.string.not_submitted)
                bg = R.drawable.round_view_red_corner10
                headerTv = context.getString(R.string.last_submission)
            }
        }

        binding.itemAssignmentStatusTv.setBackgroundResource(bg)
        binding.itemAssignmentStatusTv.text = text
        binding.itemAssignmentSecondDescKeyTv.text = headerTv
        binding.itemAssignmentSecondDescTv.text = descTv
    }

    class ViewHolder(val binding: ItemAssignmentBinding) : RecyclerView.ViewHolder(binding.root)
}