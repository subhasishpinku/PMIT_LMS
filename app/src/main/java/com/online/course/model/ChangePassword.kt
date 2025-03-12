package com.online.course.model

import com.google.gson.annotations.SerializedName

class ChangePassword {
    @SerializedName("current_password")
    lateinit var currentPassword: String

    @SerializedName("new_password")
    lateinit var newPassword: String
}