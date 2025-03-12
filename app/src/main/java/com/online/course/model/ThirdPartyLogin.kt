package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ThirdPartyLogin : Parcelable {

    @SerializedName("user_id")
    var userId = 0

    @SerializedName("id")
    lateinit var id: String

    @SerializedName("email")
    lateinit var email: String

    @SerializedName("name")
    lateinit var name: String

    constructor()

    constructor(parcel: Parcel) {
        userId = parcel.readInt()
        id = parcel.readString()!!
        email = parcel.readString()!!
        name = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeString(id)
        parcel.writeString(email)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ThirdPartyLogin> {
        override fun createFromParcel(parcel: Parcel): ThirdPartyLogin {
            return ThirdPartyLogin(parcel)
        }

        override fun newArray(size: Int): Array<ThirdPartyLogin?> {
            return arrayOfNulls(size)
        }
    }

}