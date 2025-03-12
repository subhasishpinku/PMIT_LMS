package com.online.course.model

import com.google.gson.annotations.SerializedName

class UserChangeLocalization {

    @SerializedName("country_id")
    var countryId = 0

    @SerializedName("province_id")
    var provinceId = 0

    @SerializedName("city_id")
    var cityId = 0

    @SerializedName("district_id")
    var districtId = 0

    @SerializedName("time_zone")
    var timeZone = ""

    @SerializedName("address")
    var address = ""
}