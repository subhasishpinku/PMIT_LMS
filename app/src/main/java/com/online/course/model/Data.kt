package com.online.course.model

import com.google.gson.annotations.SerializedName

class Data<T> : BaseResponse() {
    @SerializedName(
        "data",
        alternate = ["user", "result", "cart", "order", "amounts", "quizzes", "teachers",
            "users", "webinars", "bundles", "bundle", "certificates", "answers", "assignments"]
    )
    var data: T? = null
}