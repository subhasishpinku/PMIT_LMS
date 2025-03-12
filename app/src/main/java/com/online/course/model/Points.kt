package com.online.course.model

import com.google.gson.annotations.SerializedName

class Points {

    @SerializedName("total_points")
    var totalPoints = 0

    @SerializedName("spent_points")
    var spentPoints = 0

    @SerializedName("available_points")
    var availablePoints = 0

    @SerializedName("rewards")
    var rewards : List<Reward> = emptyList()
}