package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class AvailableTimings() : Parcelable {

    @SerializedName("gmt")
    var gmt: String = "GMT -04:00"

    @SerializedName("time_zone")
    var timeZone: String = "Europe/Berlin"

    @SerializedName("times")
    var times: List<Timing> = emptyList()

    constructor(parcel: Parcel) : this() {
        gmt = parcel.readString()!!
        timeZone = parcel.readString()!!
        times = parcel.createTypedArrayList(Timing)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(gmt)
        parcel.writeString(timeZone)
        parcel.writeTypedList(times)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AvailableTimings> {
        override fun createFromParcel(parcel: Parcel): AvailableTimings {
            return AvailableTimings(parcel)
        }

        override fun newArray(size: Int): Array<AvailableTimings?> {
            return arrayOfNulls(size)
        }
    }
}