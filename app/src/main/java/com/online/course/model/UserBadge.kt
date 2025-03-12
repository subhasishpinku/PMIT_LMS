package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserBadge() : Parcelable {

    @SerializedName("image")
    var image: String? = null

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("description")
    lateinit var description: String

    constructor(parcel: Parcel) : this() {
        image = parcel.readString()
        title = parcel.readString()!!
        description = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(image)
        parcel.writeString(title)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserBadge> {
        override fun createFromParcel(parcel: Parcel): UserBadge {
            return UserBadge(parcel)
        }

        override fun newArray(size: Int): Array<UserBadge?> {
            return arrayOfNulls(size)
        }
    }
}