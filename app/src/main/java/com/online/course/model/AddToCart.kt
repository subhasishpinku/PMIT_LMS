package com.online.course.model

import com.google.gson.annotations.SerializedName

class AddToCart {

    enum class ItemType(val value: String) {
        BUNDLE("bundle"),
        WEBINAR("webinar")
    }

    @SerializedName("item_id")
    var itemId = 0

    @SerializedName("item_name")
    var itemName = ""

    @SerializedName("ticket_id")
    var pricingPlanId: Int? = null
}