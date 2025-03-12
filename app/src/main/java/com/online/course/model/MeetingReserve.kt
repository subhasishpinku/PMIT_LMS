package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.lang.Exception

class MeetingReserve() : Parcelable {

    enum class Status(val value: String) {
        PENDING("pending"),
        OPEN("open"),
        FINISHED("finished"),
        CANCELED("canceled")
    }

    @SerializedName("status")
    var status = 0

    @SerializedName("discount")
    var discount = 0f

    @SerializedName("price")
    var price = 0.0

    @SerializedName("disabled")
    var disabled = 0

    @SerializedName("price_with_discount")
    var priceWithDiscount = 0.0

    @SerializedName("timing_group_by_day")
    lateinit var timings: HashMap<String, List<Timing>>

    constructor(parcel: Parcel) : this() {
        status = parcel.readInt()
        discount = parcel.readFloat()
        price = parcel.readDouble()
        disabled = parcel.readInt()
        priceWithDiscount = parcel.readDouble()
        timings = parcel.readSerializable() as HashMap<String, List<Timing>>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        try {
            parcel.writeInt(status)
            parcel.writeFloat(discount)
            parcel.writeDouble(price)
            parcel.writeInt(disabled)
            parcel.writeDouble(priceWithDiscount)
            parcel.writeSerializable(timings)
        } catch (ex: Exception) {
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MeetingReserve> {
        override fun createFromParcel(parcel: Parcel): MeetingReserve {
            return MeetingReserve(parcel)
        }

        override fun newArray(size: Int): Array<MeetingReserve?> {
            return arrayOfNulls(size)
        }
    }
}