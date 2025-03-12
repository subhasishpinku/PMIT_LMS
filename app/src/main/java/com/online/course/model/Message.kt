package com.online.course.model

import com.google.gson.annotations.SerializedName

class Message {

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("email")
    lateinit var email: String

    @SerializedName("message")
    lateinit var message: String
}