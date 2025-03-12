package com.online.course.model

import com.google.gson.annotations.SerializedName

class UserChangeSettings {

    @SerializedName("full_name")
    lateinit var name: String

    @SerializedName("newsletter")
    var hasNewsLetter = false

    @SerializedName("public_message")
    var publicMessage = 0
}