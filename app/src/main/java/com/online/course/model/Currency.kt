package com.online.course.model

import com.google.gson.annotations.SerializedName

class Currency {

    @SerializedName("name")
    lateinit var name: String

    @SerializedName("sign")
    lateinit var sign: String
}