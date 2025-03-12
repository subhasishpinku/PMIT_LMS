package com.online.course.model

import com.google.gson.annotations.SerializedName

class Country : SelectionItem {
    @SerializedName("country_id")
    var countryId: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("flag")
    var flag: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("calling_code")
    var callingCode: String? = null

    var iso: String? = null

    constructor(
        iso: String,
        phoneCode: String,
        name: String
    ) {
        this.iso = iso
        this.callingCode = phoneCode
        this.name = name
    }

    override fun toString(): String {
        return name!!
    }
}