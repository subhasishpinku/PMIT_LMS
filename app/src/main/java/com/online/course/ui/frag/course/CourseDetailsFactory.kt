package com.online.course.ui.frag.course

import com.online.course.model.Course

class CourseDetailsFactory {
    companion object {
        fun getDetails(course: Course): BaseCourseDetails {
            if (course.isBundle()) {
                return BundleCourseDetails(course)
            }

            return NormalCourseDetails(course)
        }
    }
}