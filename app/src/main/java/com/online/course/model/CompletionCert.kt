package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CompletionCert() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("date")
    var date = 0L

    @SerializedName("link")
    var link = ""

    @SerializedName("webinar")
    lateinit var course : Course

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        date = parcel.readLong()
        link = parcel.readString()!!
        course = parcel.readParcelable(Course::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(date)
        parcel.writeString(link)
        parcel.writeParcelable(course, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CompletionCert> {
        override fun createFromParcel(parcel: Parcel): CompletionCert {
            return CompletionCert(parcel)
        }

        override fun newArray(size: Int): Array<CompletionCert?> {
            return arrayOfNulls(size)
        }
    }
}