package com.online.course.model

import com.google.gson.annotations.SerializedName

class Language : SelectionItem() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    lateinit var name: String

    @SerializedName("code")
    lateinit var code: String

    @SerializedName("is_default")
    var isDefault: Boolean = false
}