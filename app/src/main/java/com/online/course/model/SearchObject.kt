package com.online.course.model

import com.google.gson.annotations.SerializedName

class SearchObject {

    @SerializedName("webinars")
    lateinit var courses: Count<Course>

    @SerializedName("teachers")
    lateinit var users: Count<User>

    @SerializedName("organizations")
    lateinit var organizations: Count<User>
}