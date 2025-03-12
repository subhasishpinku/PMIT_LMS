package com.online.course.model

import com.google.gson.annotations.SerializedName

class SaasPackageItem() {

    @SerializedName("id")
    var id = 0

    @SerializedName("package_id")
    var packageId = 0

    @SerializedName("title")
    var title = ""

    @SerializedName("description")
    var description = ""

    @SerializedName("days")
    var days = 0

    @SerializedName("price")
    var price = 0.0

    @SerializedName("icon")
    var icon = ""

    @SerializedName("role")
    var role = ""

    @SerializedName("instructors_count")
    var instructorsCount = ""

    @SerializedName("students_count")
    var studentsCount = ""

    @SerializedName("courses_capacity")
    var coursesCapacity = ""

    @SerializedName("courses_count")
    var coursesCount = ""

    @SerializedName("meeting_count")
    var meetingCount = ""
}