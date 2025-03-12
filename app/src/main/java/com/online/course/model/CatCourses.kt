package com.online.course.model

import com.google.gson.annotations.SerializedName

class CatCourses() {

    @SerializedName("webinars")
    var courses: List<Course>? = null

    @SerializedName("filters")
    var filters: List<CourseFilter>? = null
}