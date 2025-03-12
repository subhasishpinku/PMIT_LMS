package com.online.course.model

import com.google.gson.annotations.SerializedName

class StudentAssignments {

    @SerializedName("pending_reviews_count")
    var pendingAssignments = 0

    @SerializedName("passed_count")
    var passedAssignments = 0

    @SerializedName("failed_count")
    var failedAssignments = 0

    @SerializedName("assignments")
    var assignments: List<Assignment> = emptyList()
}