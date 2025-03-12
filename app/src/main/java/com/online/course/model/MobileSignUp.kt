package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class MobileSignUp() : Parcelable {

    @SerializedName("mobile")
    lateinit var mobile: String

    @SerializedName("country_code")
    lateinit var countryCode: String

    @SerializedName("password")
    lateinit var password: String

    @SerializedName("password_confirmation")
    lateinit var passwordConfirmation: String

    constructor(parcel: Parcel) : this() {
        mobile = parcel.readString()!!
        countryCode = parcel.readString()!!
        password = parcel.readString()!!
        passwordConfirmation = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mobile)
        parcel.writeString(countryCode)
        parcel.writeString(password)
        parcel.writeString(passwordConfirmation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MobileSignUp> {
        override fun createFromParcel(parcel: Parcel): MobileSignUp {
            return MobileSignUp(parcel)
        }

        override fun newArray(size: Int): Array<MobileSignUp?> {
            return arrayOfNulls(size)
        }
    }
}