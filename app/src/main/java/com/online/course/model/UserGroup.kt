package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserGroup() : Parcelable {

    @SerializedName("name")
    lateinit var name: String

    @SerializedName("discount")
    var discount = 0f

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()!!
        discount = parcel.readFloat()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeFloat(discount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserGroup> {
        override fun createFromParcel(parcel: Parcel): UserGroup {
            return UserGroup(parcel)
        }

        override fun newArray(size: Int): Array<UserGroup?> {
            return arrayOfNulls(size)
        }
    }
}