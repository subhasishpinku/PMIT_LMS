package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.model.view.CommonItem

class Notif : CommonItem, Parcelable {

    enum class Type(private val value: String) {
        READ("read"),
        UNREAD("unread");

        fun value(): String {
            return value
        }
    }

    @SerializedName("id")
    var id = 0

    @SerializedName("status")
    var status: String? = null

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("message")
    lateinit var message: String

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("sender")
    var sender: String? = null

    constructor()

    constructor(parcel: Parcel) {
        status = parcel.readString()
        title = parcel.readString()!!
        message = parcel.readString()!!
        createdAt = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notif> {
        override fun createFromParcel(parcel: Parcel): Notif {
            return Notif(parcel)
        }

        override fun newArray(size: Int): Array<Notif?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return title
    }

    override fun img(): String? {
        return null
    }

    override fun desc(context: Context): String {
        return Utils.getDateTimeFromTimestamp(createdAt)
    }

    override fun showUnseenStatus(): Boolean {
        return status == Type.UNREAD.value()
    }

    override fun cardBg(): Int? {
        return R.color.white
    }
}