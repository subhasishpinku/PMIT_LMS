package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Attachment() : Parcelable {

    @SerializedName("url")
    var url = ""

    @SerializedName("title")
    var title = ""

    @SerializedName("ticket_id")
    var size: String? = null

    constructor(parcel: Parcel) : this() {
        url = parcel.readString()!!
        title = parcel.readString()!!
        size = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(title)
        parcel.writeString(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attachment> {
        override fun createFromParcel(parcel: Parcel): Attachment {
            return Attachment(parcel)
        }

        override fun newArray(size: Int): Array<Attachment?> {
            return arrayOfNulls(size)
        }
    }
}