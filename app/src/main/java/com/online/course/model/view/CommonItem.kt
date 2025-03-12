package com.online.course.model.view

import android.content.Context
import android.graphics.drawable.Drawable


interface CommonItem {
    fun title(context: Context): String
    fun img(): String?
    fun desc(context: Context): String

    fun showUnseenStatus(): Boolean {
        return false
    }

    fun status(context: Context): CommonItemStatus? {
        return null
    }

    fun imgResource(): Int? {
        return null
    }

    fun imgBgDrawable(context: Context): Drawable? {
        return null
    }

    fun cardBg(): Int? {
        return null
    }

    fun imgBgResource(): Int? {
        return null
    }

    fun imgPadding(context: Context): Int? {
        return null
    }

    fun isClickable(): Boolean {
        return true
    }

    class CommonItemStatus {
        lateinit var status: String
        var textColor = 0
        var textBg = 0
        var textSize = 0f
    }
}