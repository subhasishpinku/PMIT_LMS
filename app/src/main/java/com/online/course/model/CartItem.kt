package com.online.course.model

import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

class CartItem {

    @SerializedName("id")
    var id = 0

    @SerializedName("type")
    var type = ""

    @SerializedName("title")
    var title = ""

    @SerializedName("image")
    var img: String? = null

    @SerializedName("price")
    var price = 0.0

    @SerializedName("best_ticket_price")
    var priceWithDiscount = 0.0

    @SerializedName("rate")
    var rating = 0f

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("discount_percent")
    var actualDiscount = 0f

    val discount get() = actualDiscount.roundToInt()

    @SerializedName("status")
    var status = ""

    @SerializedName("time_zone")
    var timeZone = ""

    @SerializedName("amount")
    var amount = 0.0

    @SerializedName("date")
    var date = 0L

    @SerializedName("time")
    var time = Time()

    @SerializedName("user")
    var user = User()

    @SerializedName("link")
    var link: String? = null


}