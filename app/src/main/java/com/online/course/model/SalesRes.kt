package com.online.course.model

import com.google.gson.annotations.SerializedName

class SalesRes {

    @SerializedName("sales")
    lateinit var payouts: List<SalesItem>

    @SerializedName("total_sales")
    var totalSales = 0.0

    @SerializedName("students_count")
    var studentsSalesCount = 0

    @SerializedName("webinars_count")
    var coursesSalesCount = 0

    @SerializedName("meetings_count")
    var meetingsSalesCount = 0

    @SerializedName("meeting_sales")
    var meetingSales = 0.0

    @SerializedName("class_sales")
    var classSales = 0.0
}