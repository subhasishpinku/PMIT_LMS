package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PrerequisiteCourse() : Parcelable {

    @SerializedName("required")
    var required = 0

    @SerializedName("webinar")
    lateinit var course: Course

    constructor(parcel: Parcel) : this() {
        required = parcel.readInt()
        course = parcel.readParcelable(Course::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(required)
        parcel.writeParcelable(course, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PrerequisiteCourse> {
        override fun createFromParcel(parcel: Parcel): PrerequisiteCourse {
            return PrerequisiteCourse(parcel)
        }

        override fun newArray(size: Int): Array<PrerequisiteCourse?> {
            return arrayOfNulls(size)
        }
    }
}