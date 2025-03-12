package com.online.course.model

import com.google.gson.annotations.SerializedName

class Subscription {

    @SerializedName("subscribes")
    lateinit var subscritionItems : List<SubscriptionItem>

    @SerializedName("subscribed")
    var subscribed = false

    @SerializedName("subscribed_title")
    var subscribedTitle: String? = null

    @SerializedName("remained_downloads")
    var remainedDownloads: Int? = null

    @SerializedName("days_remained")
    var daysRemained: Int? = null
}