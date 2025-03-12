package com.online.course.model

import com.google.gson.annotations.SerializedName

class PayoutRes {

    @SerializedName("payouts")
    lateinit var payouts: List<Payout>

    @SerializedName("current_payout")
    lateinit var payoutAccount: PayoutAccount
}