package com.online.course.model

import com.google.gson.annotations.SerializedName

class Forums {

    @SerializedName("forums")
    var items : List<ForumItem> = emptyList()

    @SerializedName("questions_count")
    var questionsCount = 0

    @SerializedName("resolved_count")
    var resolvedCount = 0

    @SerializedName("open_questions_count")
    var openQuestionsCount = 0

    @SerializedName("comments_count")
    var commentsCount = 0

    @SerializedName("active_users_count")
    var activeUsersCount = 0
}