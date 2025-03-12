package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CheckoutRes() : Parcelable{

    @SerializedName("total")
    var total = 0.0

    @SerializedName("userCharge")
    var userCharge = 0.0

    @SerializedName("paymentChannels")
    lateinit var paymentChannels : List<PaymentChannel>

    @SerializedName("order")
    lateinit var mOrder: Order

    constructor(parcel: Parcel) : this() {
        total = parcel.readDouble()
        userCharge = parcel.readDouble()
        paymentChannels = parcel.createTypedArrayList(PaymentChannel)!!
        mOrder = parcel.readParcelable(Order::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(total)
        parcel.writeDouble(userCharge)
        parcel.writeTypedList(paymentChannels)
        parcel.writeParcelable(mOrder, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutRes> {
        override fun createFromParcel(parcel: Parcel): CheckoutRes {
            return CheckoutRes(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutRes?> {
            return arrayOfNulls(size)
        }
    }
}