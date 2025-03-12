package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Timing() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("day_label")
    var dayLabel: String? = null

    @SerializedName("time")
    lateinit var time: String

    @SerializedName("can_reserve")
    var canReserve = true

    @SerializedName("meeting")
    lateinit var meeting: Meeting

    @SerializedName("meeting_type")
    var meetingType = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        dayLabel = parcel.readString()
        time = parcel.readString()!!
        meetingType = parcel.readString()!!
        canReserve = parcel.readByte() != 0.toByte()
        meeting = parcel.readParcelable(Meeting::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(dayLabel)
        parcel.writeString(time)
        parcel.writeByte(if (canReserve) 1 else 0)
        parcel.writeParcelable(meeting, flags)
        parcel.writeString(meetingType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Timing> {
        override fun createFromParcel(parcel: Parcel): Timing {
            return Timing(parcel)
        }

        override fun newArray(size: Int): Array<Timing?> {
            return arrayOfNulls(size)
        }
    }
}