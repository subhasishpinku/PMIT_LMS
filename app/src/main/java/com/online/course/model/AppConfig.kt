package com.online.course.model

import com.google.gson.annotations.SerializedName

class AppConfig {
    enum class CurrencyPosition(val value: String) {
        RIGHT("right"),
        LEFT("left"),
        LEFT_WITH_SPACE("left_with_space"),
        RIGHT_WITH_SPACE("right_with_space")
    }

    @SerializedName("register_method")
    lateinit var registrationMethod: String

    @SerializedName("offline_bank_account")
    lateinit var offLineBankAccounts: List<String>

    @SerializedName("user_language")
    lateinit var supportedLangs: Map<String, String>

    @SerializedName("payment_channels")
    lateinit var activePaymentChannels: ActivePaymentChannels

    @SerializedName("minimum_payout_amount")
    var minimumPayoutAmount: Double = 0.0

    @SerializedName("currency")
    lateinit var currency: Currency

    @SerializedName("currency_position")
    var currencyPosition = CurrencyPosition.RIGHT.value
}