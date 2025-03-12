package com.online.course.model

import com.google.gson.annotations.SerializedName

class Meetings {

    @SerializedName("reservations")
    lateinit var reservations: Count<Meeting>

    @SerializedName("requests")
    lateinit var requests: Count<Meeting>
}