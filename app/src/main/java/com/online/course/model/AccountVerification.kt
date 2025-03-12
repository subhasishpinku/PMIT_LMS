package com.online.course.model

import com.google.gson.annotations.SerializedName

class AccountVerification {

    @SerializedName("user_id")
    var userId = 0

    @SerializedName("code")
    lateinit var code: String
}