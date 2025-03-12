package com.online.course.model

import com.google.gson.annotations.SerializedName

class ChapterItemMark {

    enum class Item(val value: String) {
        SESSION("session_id"),
        FILE("file_id"),
        TEXT_LESSON("text_lesson_id")
    }

    var courseId = 0

    @SerializedName("item_id")
    var itemId = 0

    @SerializedName("item")
    lateinit var item: String

    @SerializedName("status")
    var status = 0
}