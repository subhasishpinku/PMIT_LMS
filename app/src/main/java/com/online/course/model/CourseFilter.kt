package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CourseFilter() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("options")
    lateinit var options: List<CourseFilterOption>

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

    companion object CREATOR : Parcelable.Creator<CourseFilter> {
        override fun createFromParcel(parcel: Parcel): CourseFilter {
            return CourseFilter(parcel)
        }

        override fun newArray(size: Int): Array<CourseFilter?> {
            return arrayOfNulls(size)
        }
    }
}