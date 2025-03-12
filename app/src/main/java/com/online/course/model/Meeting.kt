package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Meeting() : Parcelable {

    enum class Type(val value: String) {
        ALL("all"),
        IN_PERSON("in_person"),
        ONLINE("online"),
    }

    @SerializedName("id")
    var id = 0

    @SerializedName("status")
    var status = ""

    @SerializedName("time_zone")
    var timeZone = ""

    @SerializedName("gmt")
    var gmt = ""

    @SerializedName("user_paid_amount")
    var userPaidAmount = 0.0

    @SerializedName("price")
    var onlinePrice = 0.0

    @SerializedName("price_with_discount")
    var onlinePriceWithDiscount = 0.0

    @SerializedName("in_person_price")
    var inPersonPrice = 0.0

    @SerializedName("in_person_price_with_discount")
    var inPersonPriceWithDiscount = 0.0

    @SerializedName("amount")
    var amount = 0.0

    @SerializedName("date")
    var date = 0L

    @SerializedName("day")
    var day = ""

    @SerializedName("time")
    lateinit var time: Time

    @SerializedName("user")
    lateinit var user: User

    @SerializedName("link")
    var link: String? = null

    @SerializedName("in_person")
    var inPerson = 0

    @SerializedName("group_meeting")
    var groupMeeting = 0

    @SerializedName("description")
    var description = ""

    @SerializedName("student_count")
    var studentCount = 0

    @SerializedName("online_group_min_student")
    var onlineGroupMinStudent = 0

    @SerializedName("online_group_max_student")
    var onlineGroupMaxStudent = 0

    @SerializedName("in_person_group_min_student")
    var inPersonGroupMinStudent = 0

    @SerializedName("in_person_group_max_student")
    var inPersonGroupMaxStudent = 0

    @SerializedName("in_person_group_amount")
    var inPersonGroupAmount = 0.0

    @SerializedName("online_group_amount")
    var onlineGroupAmount = 0.0

    @SerializedName("meeting")
    var meeting: Meeting? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        status = parcel.readString()!!
        userPaidAmount = parcel.readDouble()
        amount = parcel.readDouble()
        date = parcel.readLong()
        day = parcel.readString()!!
        time = parcel.readParcelable(Time::class.java.classLoader)!!
        link = parcel.readString()
        user = parcel.readParcelable(User::class.java.classLoader)!!
        meeting = parcel.readParcelable(Meeting::class.java.classLoader)!!
        timeZone = parcel.readString()!!
        gmt = parcel.readString()!!
        inPerson = parcel.readInt()
        groupMeeting = parcel.readInt()
        description = parcel.readString()!!
        studentCount = parcel.readInt()
        onlinePrice = parcel.readDouble()
        onlinePriceWithDiscount = parcel.readDouble()
        inPersonPrice = parcel.readDouble()
        inPersonPriceWithDiscount = parcel.readDouble()
        onlineGroupMinStudent = parcel.readInt()
        onlineGroupMaxStudent = parcel.readInt()
        inPersonGroupMinStudent = parcel.readInt()
        inPersonGroupMaxStudent = parcel.readInt()
        inPersonGroupAmount = parcel.readDouble()
        onlineGroupAmount = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(status)
        parcel.writeDouble(userPaidAmount)
        parcel.writeDouble(amount)
        parcel.writeLong(date)
        parcel.writeString(day)
        parcel.writeParcelable(time, flags)
        parcel.writeString(link)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(meeting, flags)
        parcel.writeString(timeZone)
        parcel.writeString(gmt)
        parcel.writeInt(inPerson)
        parcel.writeInt(groupMeeting)
        parcel.writeString(description)
        parcel.writeInt(studentCount)
        parcel.writeDouble(onlinePrice)
        parcel.writeDouble(onlinePriceWithDiscount)
        parcel.writeDouble(inPersonPrice)
        parcel.writeDouble(inPersonPriceWithDiscount)
        parcel.writeInt(onlineGroupMinStudent)
        parcel.writeInt(onlineGroupMaxStudent)
        parcel.writeInt(inPersonGroupMinStudent)
        parcel.writeInt(inPersonGroupMaxStudent)
        parcel.writeDouble(inPersonGroupAmount)
        parcel.writeDouble(onlineGroupAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Meeting> {
        const val FINISHED = "finished"
        const val CANCELED = "canceled"

        override fun createFromParcel(parcel: Parcel): Meeting {
            return Meeting(parcel)
        }

        override fun newArray(size: Int): Array<Meeting?> {
            return arrayOfNulls(size)
        }
    }
}