package com.online.course.model

import android.os.Parcel
import android.os.Parcelable

class Faq() : Parcelable {
    var title = ""

    var answer = ""

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()!!
        answer = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(answer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Faq> {
        override fun createFromParcel(parcel: Parcel): Faq {
            return Faq(parcel)
        }

        override fun newArray(size: Int): Array<Faq?> {
            return arrayOfNulls(size)
        }
    }
}