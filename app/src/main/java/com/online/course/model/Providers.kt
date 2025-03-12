package com.online.course.model

import com.google.gson.annotations.SerializedName

class Providers {

    @SerializedName("count")
    var count = 0

    @SerializedName("instructors")
    lateinit var instructors: Count<User>

    @SerializedName("consultations")
    lateinit var consultants: Count<User>

    @SerializedName("organizations")
    lateinit var organizations: Count<User>
}