package com.online.course.model

import com.google.gson.annotations.SerializedName

class ReserveMeeting() {
    @SerializedName("link")
    lateinit var link: String

    @SerializedName("reserved_meeting_id")
    var reservedMeetingId = 0

    @SerializedName("password")
    var password: String? = null
}