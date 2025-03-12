package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Sales() : Parcelable {

    @SerializedName("count")
    var count = 0

    @SerializedName("amount")
    var amount = 0.0

    constructor(parcel: Parcel) : this() {
        count = parcel.readInt()
        amount = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(count)
        parcel.writeDouble(amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sales> {
        override fun createFromParcel(parcel: Parcel): Sales {
            return Sales(parcel)
        }

        override fun newArray(size: Int): Array<Sales?> {
            return arrayOfNulls(size)
        }
    }
}