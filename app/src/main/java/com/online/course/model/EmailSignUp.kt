package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class EmailSignUp() : Parcelable  {

    @SerializedName("email")
    lateinit var email: String

    @SerializedName("password")
    lateinit var password: String

    @SerializedName("password_confirmation")
    lateinit var passwordConfirmation: String

    constructor(parcel: Parcel) : this() {
        email = parcel.readString()!!
        password = parcel.readString()!!
        passwordConfirmation = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(passwordConfirmation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmailSignUp> {
        override fun createFromParcel(parcel: Parcel): EmailSignUp {
            return EmailSignUp(parcel)
        }

        override fun newArray(size: Int): Array<EmailSignUp?> {
            return arrayOfNulls(size)
        }
    }
}