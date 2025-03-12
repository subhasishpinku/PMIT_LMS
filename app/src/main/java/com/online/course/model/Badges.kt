package com.online.course.model

import com.google.gson.annotations.SerializedName

class Badges {

    @SerializedName("next_badge")
    var nextBadge: String? = null

    @SerializedName("earned")
    var earned: String? = null

    @SerializedName("percent")
    var percent = 0f
}