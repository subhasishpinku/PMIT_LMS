package com.online.course.model

import com.google.gson.annotations.SerializedName

class Count<T> {

    @SerializedName("count")
    var count = 0

    @SerializedName(
        "data",
        alternate = ["courses", "categories", "users", "webinars", "blogs", "instructors",
            "consultants", "organizations", "meetings", "times", "notifications", "results",
            "quizzes", "accounts", "favorites", "history", "teachers", "accounts_type"]
    )
    var items: List<T> = emptyList()
}