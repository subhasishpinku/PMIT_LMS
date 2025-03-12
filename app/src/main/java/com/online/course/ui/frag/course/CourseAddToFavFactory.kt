package com.online.course.ui.frag.course

import com.online.course.model.Course

class CourseAddToFavFactory {
    companion object {
        fun getAddToFavObj(course: Course): BaseCourseAddToFav {
            if (course.isBundle()) {
                return BundleCourseAddToFav(course)
            }

            return NormalCourseAddToFav(course)
        }
    }
}