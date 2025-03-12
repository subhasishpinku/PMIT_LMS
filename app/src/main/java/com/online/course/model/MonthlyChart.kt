package com.online.course.model

import com.google.gson.annotations.SerializedName

class MonthlyChart {

//    @SerializedName("months")
//    lateinit var months : List<String>

    @SerializedName("data")
    var data: List<Double> = emptyList()
}