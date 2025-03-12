package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ReserveTimeMeeting() : Parcelable {
    @SerializedName("date")
    var date: String = ""

    lateinit var time: Timing

    @SerializedName("time_id")
    var timeId = 0

    @SerializedName("meeting_type")
    var meetingType = ""

    @SerializedName("student_count")
    var studentCount = 0

    @SerializedName("description")
    var description: String? = null

    constructor(parcel: Parcel) : this() {
        date = parcel.readString()!!
        time = parcel.readParcelable(Timing::class.java.classLoader)!!
        timeId = parcel.readInt()
        meetingType = parcel.readString()!!
        studentCount = parcel.readInt()
        description = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeParcelable(time, flags)
        parcel.writeInt(timeId)
        parcel.writeString(meetingType)
        parcel.writeInt(studentCount)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReserveTimeMeeting> {
        override fun createFromParcel(parcel: Parcel): ReserveTimeMeeting {
            return ReserveTimeMeeting(parcel)
        }

        override fun newArray(size: Int): Array<ReserveTimeMeeting?> {
            return arrayOfNulls(size)
        }
    }

}