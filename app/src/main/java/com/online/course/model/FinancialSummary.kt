package com.online.course.model

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.model.view.CommonItem

class FinancialSummary : CommonItem {

    enum class BalanceType(val value: String) {
        ADDITION("addition"),
        DEDUCTION("deduction"),
    }

    @SerializedName("type")
    var type: String = ""

    @SerializedName("balance_type")
    var balanceType: String = ""

    @SerializedName("description")
    var description: String = ""

    @SerializedName("amount")
    var amount = 0.0

    @SerializedName("created_at")
    var createdAt = 0L

    override fun title(context: Context): String {
        if (description.isNullOrBlank()) {
            return context.getString(R.string.manual_balance)
        }

        return description
    }

    override fun img(): String? {
        return null
    }

    override fun desc(context: Context): String {
        return Utils.getDateTimeFromTimestamp(createdAt)
    }

    override fun imgPadding(context: Context): Int? {
        return Utils.changeDpToPx(context, 25f).toInt()
    }

    override fun imgResource(): Int? {
        return if (balanceType == BalanceType.ADDITION.value) {
            R.drawable.ic_arrow_top_white
        } else {
            R.drawable.ic_arrow_bottom_white
        }
    }

    override fun imgBgResource(): Int? {
        return if (balanceType == BalanceType.ADDITION.value) {
            R.drawable.round_view_accent_corner20
        } else {
            R.drawable.round_view_red_corner20
        }
    }

    override fun isClickable(): Boolean {
        return false
    }

    override fun status(context: Context): CommonItem.CommonItemStatus? {
        val color: Int
        val type: String

        if (balanceType == BalanceType.ADDITION.value) {
            color = ContextCompat.getColor(context, R.color.accent)
            type = "+"
        } else {
            color = ContextCompat.getColor(context, R.color.red)
            type = "-"
        }

        val amountItem = CommonItem.CommonItemStatus()
        amountItem.status = "${type}${Utils.formatPrice(context, amount, false)}"
        amountItem.textColor = color
        amountItem.textSize = 15f

        return amountItem
    }

    override fun cardBg(): Int? {
        return R.color.white
    }
}