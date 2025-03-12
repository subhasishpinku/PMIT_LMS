package com.online.course.model

import com.google.gson.annotations.SerializedName

class MyClasses {

    @SerializedName("my_classes")
    var myClasses: List<Course> = emptyList()

    @SerializedName("purchases")
    var purchases: List<Course> = emptyList()

    @SerializedName("organizations")
    var organizations: List<Course> = emptyList()

    @SerializedName("invitations")
    var invitations: List<Course> = emptyList()
}