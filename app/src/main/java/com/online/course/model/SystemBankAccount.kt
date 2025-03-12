package com.online.course.model

import com.google.gson.annotations.SerializedName

class SystemBankAccount {
    @SerializedName("title")
    lateinit var title: String

    @SerializedName("image")
    var image: String? = null

    @SerializedName("card_id")
    lateinit var cardId: String

    @SerializedName("account_id")
    lateinit var accountId: String

    @SerializedName("iban")
    lateinit var iban: String
}