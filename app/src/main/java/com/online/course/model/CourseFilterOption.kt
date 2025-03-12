package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CourseFilterOption() : Parcelable{

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CourseFilterOption> {
        override fun createFromParcel(parcel: Parcel): CourseFilterOption {
            return CourseFilterOption(parcel)
        }

        override fun newArray(size: Int): Array<CourseFilterOption?> {
            return arrayOfNulls(size)
        }
    }
}