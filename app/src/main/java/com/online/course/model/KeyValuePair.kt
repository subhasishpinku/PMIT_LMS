package com.online.course.model

import android.os.Parcel
import android.os.Parcelable

class KeyValuePair : Parcelable {
    lateinit var key: String
    lateinit var value: String

    constructor(parcel: Parcel) {
        key = parcel.readString()!!
        value = parcel.readString()!!
    }

    constructor(key: String, value: String) {
        this.key = key
        this.value = value
    }

    constructor()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KeyValuePair> {
        override fun createFromParcel(parcel: Parcel): KeyValuePair {
            return KeyValuePair(parcel)
        }

        override fun newArray(size: Int): Array<KeyValuePair?> {
            return arrayOfNulls(size)
        }
    }
}