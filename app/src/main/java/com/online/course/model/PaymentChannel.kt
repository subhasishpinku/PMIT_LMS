package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PaymentChannel() : Parcelable {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("class_name")
    var className: String? = null

    @SerializedName("image")
    var image: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        className = parcel.readString()
        image = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(className)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentChannel> {
        override fun createFromParcel(parcel: Parcel): PaymentChannel {
            return PaymentChannel(parcel)
        }

        override fun newArray(size: Int): Array<PaymentChannel?> {
            return arrayOfNulls(size)
        }
    }
}