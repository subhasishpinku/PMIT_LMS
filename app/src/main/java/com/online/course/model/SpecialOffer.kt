package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class SpecialOffer() : Parcelable {

    @SerializedName("percent")
    var percent = 0

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("to_date")
    var toDate = 0L

    constructor(parcel: Parcel) : this() {
        percent = parcel.readInt()
        createdAt = parcel.readLong()
        toDate = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(percent)
        parcel.writeLong(createdAt)
        parcel.writeLong(toDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpecialOffer> {
        override fun createFromParcel(parcel: Parcel): SpecialOffer {
            return SpecialOffer(parcel)
        }

        override fun newArray(size: Int): Array<SpecialOffer?> {
            return arrayOfNulls(size)
        }
    }
}