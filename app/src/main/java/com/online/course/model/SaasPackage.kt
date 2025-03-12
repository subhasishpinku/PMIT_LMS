package com.online.course.model

import com.google.gson.annotations.SerializedName

class SaasPackage {

    @SerializedName("packages")
    var packages: List<SaasPackageItem> = emptyList()

    @SerializedName("active_package")
    lateinit var activePackage: ActiveSaasPackage

    @SerializedName("account_courses_count")
    var accountCoursesCount : String? = null

    @SerializedName("account_meeting_count")
    var accountMeetingCount : String? = null

    @SerializedName("account_courses_capacity")
    var accountCoursesCapacity : String? = null

    @SerializedName("account_instructors_count")
    var accountInstructorsCount : String? = null

    @SerializedName("account_students_count")
    var accountStudentsCount : String? = null
}