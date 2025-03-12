package com.online.course.model

import com.google.gson.annotations.SerializedName

class AddToFav {

    enum class ItemType(val value: String) {
        BUNDLE("bundle"),
        WEBINAR("webinar")
    }

    @SerializedName("id")
    var itemId = 0

    @SerializedName("item")
    var itemName = ""
}