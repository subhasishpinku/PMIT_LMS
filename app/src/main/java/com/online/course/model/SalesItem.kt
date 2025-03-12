package com.online.course.model

import com.google.gson.annotations.SerializedName

class SalesItem {

    enum class Type(val type: String) {
        COURSE("webinar"),
        MEETING("meeting");
    }

    @SerializedName("buyer")
    lateinit var buyer: User

    @SerializedName("type")
    lateinit var type: String

    @SerializedName("webinar")
    var course: Course? = null

    @SerializedName("meeting")
    var meeting: Meeting? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("income")
    var income = 0.0
}