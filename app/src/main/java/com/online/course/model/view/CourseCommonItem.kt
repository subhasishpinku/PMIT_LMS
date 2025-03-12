package com.online.course.model.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable


interface CourseCommonItem : Parcelable {

    companion object {
        fun creator(
            title: String,
            desc: String,
            imgResource: Int,
            imgBgResource: Int
        ): CourseCommonItem {
            return object : CourseCommonItem {
                override fun title(context: Context): String {
                    return title
                }

                override fun desc(context: Context): String {
                    return desc
                }

                override fun imgResource(context: Context): Int {
                    return imgResource
                }

                override fun imgBgResource(context: Context): Int {
                    return imgBgResource
                }

                override fun writeToParcel(dest: Parcel?, flags: Int) {

                }

                override fun describeContents(): Int {
                    return 0
                }

            }
        }
    }

    fun title(context: Context): String

    fun desc(context: Context): String

    fun imgResource(context: Context): Int

    fun imgBgResource(context: Context): Int

    fun passed(): Boolean {
        return false
    }

    fun status(): String {
        return ""
    }

    fun statusBg(): Int {
        return 0
    }
}