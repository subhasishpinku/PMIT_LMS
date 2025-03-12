package com.online.course.ui.frag.course

import com.online.course.databinding.FragCourseDetailsInformationBinding
import com.online.course.model.view.CourseCommonItem

abstract class BaseCourseDetailsInformation() {
    abstract fun getInfoList(): ArrayList<CourseCommonItem>
    abstract fun setMarkInfo(binding: FragCourseDetailsInformationBinding)
}