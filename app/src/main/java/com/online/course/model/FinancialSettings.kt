package com.online.course.model

import com.google.gson.annotations.SerializedName

class FinancialSettings {

    @SerializedName("account_type")
    lateinit var accountType: String

    @SerializedName("iban")
    lateinit var iban: String

    @SerializedName("account_id")
    lateinit var accountId: String

    @SerializedName("address")
    lateinit var address: String
}