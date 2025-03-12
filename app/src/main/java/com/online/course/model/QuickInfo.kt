package com.online.course.model

import com.google.gson.annotations.SerializedName

class QuickInfo {
    @SerializedName("balance")
    var balance = 0.0

    @SerializedName("unread_notifications")
    var unreadNotifs: Count<Notif> = Count()

    @SerializedName("unread_noticeboards")
    var unreadNoticeboards: List<Notif> = emptyList()

    @SerializedName("webinarsCount")
    var upcomingLiveSessions = 0

    @SerializedName("supportsCount")
    var supportsCount = 0

    @SerializedName("reserveMeetingsCount")
    var reserveMeetingsCount = 0

    @SerializedName("commentsCount")
    var commentsCount = 0

    @SerializedName("monthlySalesCount")
    var monthlySalesCount = 0

    @SerializedName("badges")
    var badges: Badges? = null

    @SerializedName("count_cart_items")
    var cartItemsCount = 0

    @SerializedName("financial_approval")
    var financialApproval = 0

    @SerializedName("available_points")
    var availablePoints = 0

    @SerializedName("monthlyChart")
    var monthlyChart: MonthlyChart = MonthlyChart()
}