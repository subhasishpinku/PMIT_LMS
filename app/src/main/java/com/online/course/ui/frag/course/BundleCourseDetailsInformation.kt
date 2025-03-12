package com.online.course.ui.frag.course

import android.content.Context
import android.view.View
import com.online.course.R
import com.online.course.databinding.FragCourseDetailsInformationBinding
import com.online.course.manager.Utils
import com.online.course.model.Course
import com.online.course.model.view.CourseCommonItem

class BundleCourseDetailsInformation(val context: Context, val course: Course) :
    BaseCourseDetailsInformation() {

    override fun getInfoList(): ArrayList<CourseCommonItem> {
        val items = ArrayList<CourseCommonItem>()

        items.add(
            CourseCommonItem.creator(
                course.webinarsCount.toString(),
                context.getString(R.string.courses),
                R.drawable.ic_play_circle_white, R.drawable.round_view_dark_blue2_corner10
            )
        )

        val accessDays = if (course.accessDays == null)
            context.getString(R.string.unlimited) else course.accessDays.toString()

        items.add(
            CourseCommonItem.creator(
                accessDays,
                context.getString(R.string.content_access),
                R.drawable.ic_calendar_border_white, R.drawable.round_view_green_corner10
            )
        )

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

        binding.courseDetailsInformationCapacityMarkImg.setImageResource(R.drawable.ic_calendar)
        binding.courseDetailsInformationChaptersCountMarkKeyTv.text =
            context.getString(R.string.date_created)
        binding.courseDetailsInformationChaptersCountMarkTv.text =
            Utils.getDateFromTimestamp(course.createdAt)

        binding.courseDetailsInformationStartDateMarkImg.setImageResource(R.drawable.ic_time)
        binding.courseDetailsInformationStartDateKeyMarkTv.text =
            context.getString(R.string.duration)
        binding.courseDetailsInformationStartDateMarkTv.text =
            Utils.getDuration(context, course.duration)

        binding.courseDetailsInformationDurationMarkImg.visibility = View.GONE
        binding.courseDetailsInformationDurationMarkTv.visibility = View.GONE
        binding.courseDetailsInformationDurationMarkKeyTv.visibility = View.GONE
    }
}