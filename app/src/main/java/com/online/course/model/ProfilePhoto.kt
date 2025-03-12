package com.online.course.model

import com.google.gson.annotations.SerializedName

class ProfilePhoto {
    @SerializedName("profile_image")
    lateinit var profileImage: String
}