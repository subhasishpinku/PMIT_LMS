package com.online.course.ui.frag.course

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.online.course.R
import com.online.course.databinding.FragCourseDetailsInformationBinding
import com.online.course.manager.Utils
import com.online.course.model.Course
import com.online.course.model.view.CourseCommonItem

class NormalCourseDetailsInformation(val context: Context, val course: Course) :
    BaseCourseDetailsInformation() {

    override fun getInfoList(): ArrayList<CourseCommonItem> {
        val items = ArrayList<CourseCommonItem>()

        if (course.certificates.isEmpty()) {
            items.add(
                CourseCommonItem.creator(
                    context.getString(R.string.certificate),
                    context.getString(R.string.included),
                    R.drawable.ic_badge_white, R.drawable.round_view_light_red_corner10
                )
            )
        }

        if (course.quizzes.isNotEmpty()) {
            items.add(
                CourseCommonItem.creator(
                    context.getString(R.string.quiz),
                    context.getString(R.string.included),
                    R.drawable.ic_quizzes, R.drawable.round_view_green_corner10
                )
            )
        }

        if (course.supported) {
            items.add(
                CourseCommonItem.creator(
                    context.getString(R.string.supported),
                    context.getString(R.string._class),
                    R.drawable.ic_headphone, R.drawable.round_view_blue_corner10
                )
            )
        }

        if (course.isDownloadable) {
            items.add(
                CourseCommonItem.creator(
                    context.getString(R.string.downloadable),
                    context.getString(R.string.content),
                    R.drawable.ic_download2, R.drawable.round_view_light_green_corner10
                )
            )
        }

        return items
    }

    override fun setMarkInfo(binding: FragCourseDetailsInformationBinding) {
        binding.courseDetailsInformationStudentMarkTv.text = course.studentsCount.toString()

        val count = course.sessionesCount + course.filesCount + course.textLessonsCount
        binding.courseDetailsInformationChaptersCountMarkTv.text = count.toString()

        if (course.isLive()) {
            binding.courseDetailsInformationLiveClassMarkImg.visibility = View.VISIBLE
            binding.courseDetailsInformationLiveClassKeyMarkTv.visibility = View.VISIBLE
            binding.courseDetailsInformationLiveClassMarkTv.visibility = View.VISIBLE

            binding.courseDetailsInformationStatusMarkImg.visibility = View.VISIBLE
            binding.courseDetailsInformationStatusKeyMarkTv.visibility = View.VISIBLE
            binding.courseDetailsInformationStatusMarkTv.visibility = View.VISIBLE

            course.liveCourseStatus?.let {
                val liveStatus: String

                when (course.liveCourseStatus) {
                    Course.WebinarStatus.FINISHED.value -> {
                        binding.courseDetailsInformationStatusMarkTv.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.red
                            )
                        )
                        liveStatus = context.getString(R.string.finished)
                    }
                    Course.WebinarStatus.NOT_CONDUCTED.value -> {
                        binding.courseDetailsInformationStatusMarkTv.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.accent
                            )
                        )
                        liveStatus = context.getString(R.string.not_conducted)
                    }
                    else -> {
                        liveStatus = context.getString(R.string.in_progress)
                    }
                }

                binding.courseDetailsInformationStatusMarkTv.text = liveStatus
            }

            binding.courseDetailsInformationStartDateMarkTv.text =
                Utils.getDateFromTimestamp(course.startDate)
        } else {
            binding.courseDetailsInformationStartDateMarkTv.text =
                Utils.getDateFromTimestamp(course.createdAt)
            binding.courseDetailsInformationStartDateKeyMarkTv.text =
                context.getString(R.string.publish_date)
        }

        binding.courseDetailsInformationDurationMarkTv.text =
            Utils.getDuration(context, course.duration)

    }
}