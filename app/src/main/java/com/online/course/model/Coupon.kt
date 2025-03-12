package com.online.course.model

import com.google.gson.annotations.SerializedName

class Coupon {

    @SerializedName("id")
    var id = 0

    @SerializedName("discount_id")
    var discountId = 0

    @SerializedName("coupon")
    var coupon: String? = null
}