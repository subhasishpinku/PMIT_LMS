package com.online.course.model

import com.google.gson.annotations.SerializedName

class PaymentRequest {

    @SerializedName("gateway_id")
    var gatewayId = 0

    @SerializedName("order_id")
    var orderId = 0

    @SerializedName("amount")
    var amount = 0.0
}