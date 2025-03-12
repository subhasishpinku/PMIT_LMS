package com.online.course.model

import com.google.gson.annotations.SerializedName

class Amounts {

    @SerializedName("sub_total")
    var subTotal = 0.0

    @SerializedName("total_discount")
    var totalDiscount = 0.0

    @SerializedName("tax")
    var tax = 0f

    @SerializedName("tax_price")
    var taxPrice = 0.0

    @SerializedName("total")
    var total = 0.0
}