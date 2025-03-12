package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Notice() : Parcelable {

    enum class ColorType(val value: String) {
        DANGER("danger"),
        WARNING("warning"),
        NEUTRAL("neutral"),
        INFO("info"),
        SUCCESS("success")
    }

    @SerializedName("title")
    var title = ""

    @SerializedName("message")
    var message = ""

    @SerializedName("color")
    var color = ""

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("creator")
    var creator = User()

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()!!
        message = parcel.readString()!!
        color = parcel.readString()!!
        createdAt = parcel.readLong()
        creator = parcel.readParcelable(User::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(color)
        parcel.writeLong(createdAt)
        parcel.writeParcelable(creator, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notice> {
        override fun createFromParcel(parcel: Parcel): Notice {
            return Notice(parcel)
        }

        override fun newArray(size: Int): Array<Notice?> {
            return arrayOfNulls(size)
        }
    }
}