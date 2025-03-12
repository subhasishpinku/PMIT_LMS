package com.online.course.model

import com.google.gson.annotations.SerializedName

class Favorite {

    @SerializedName("id")
    var id = 0

    @SerializedName("webinar")
    lateinit var course: Course

    @SerializedName("created_at")
    var createdAt = 0L
}