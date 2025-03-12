package com.online.course.model.view

import android.os.Parcel
import android.os.Parcelable

class PaymentRedirection() : Parcelable {
    var position = 0
    var isNavDrawer = false
    lateinit var buttonTitle: String

    constructor(parcel: Parcel) : this() {
        position = parcel.readInt()
        isNavDrawer = parcel.readByte() != 0.toByte()
        buttonTitle = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(position)
        parcel.writeByte(if (isNavDrawer) 1 else 0)
        parcel.writeString(buttonTitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentRedirection> {
        override fun createFromParcel(parcel: Parcel): PaymentRedirection {
            return PaymentRedirection(parcel)
        }

        override fun newArray(size: Int): Array<PaymentRedirection?> {
            return arrayOfNulls(size)
        }
    }
}