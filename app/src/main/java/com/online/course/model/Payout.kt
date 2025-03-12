package com.online.course.model

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.model.view.CommonItem

class Payout : CommonItem {

    @SerializedName("amout")
    var amonut = 0.0

    @SerializedName("account_name")
    lateinit var accountName: String

    @SerializedName("account_number")
    lateinit var accountNumber: String

    @SerializedName("account_bank_name")
    lateinit var accountBankName: String

    @SerializedName("created_at")
    var createdAt = 0L

    override fun title(context: Context): String {
        return accountBankName
    }

    override fun img(): String? {
        return null
    }

    override fun imgResource(): Int? {
        return R.drawable.ic_arrow_top_white
    }

    override fun desc(context: Context): String {
        return Utils.getDateTimeFromTimestamp(createdAt)
    }

    override fun status(context: Context): CommonItem.CommonItemStatus? {
        val status = CommonItem.CommonItemStatus()
        status.status = "$+${Utils.formatPrice(context, amonut, false)}"
        status.textColor = ContextCompat.getColor(context, R.color.accent)
        status.textSize = 15f

        return status
    }

    override fun isClickable(): Boolean {
        return false
    }

    override fun imgPadding(context: Context): Int? {
        return Utils.changeDpToPx(context, 25f).toInt()
    }

    override fun cardBg(): Int? {
        return R.color.white
    }
}