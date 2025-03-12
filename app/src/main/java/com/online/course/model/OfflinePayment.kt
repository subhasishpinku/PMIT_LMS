package com.online.course.model

import com.google.gson.annotations.SerializedName

class OfflinePayment {

    enum class Status(val value: String) {
        APPROVED("approved"),
        WAITING("waiting"),
        REJECTED("reject")
    }

    @SerializedName("amount")
    var amount = 0.0

    @SerializedName("bank")
    lateinit var bank: String

    @SerializedName("reference_number")
    lateinit var referenceNumber: String

    @SerializedName("status")
    var status: String? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("pay_date")
    var payDate = 0L
}