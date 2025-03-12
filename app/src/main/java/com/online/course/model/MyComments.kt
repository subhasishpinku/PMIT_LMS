package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class MyComments() : Parcelable {

    @SerializedName("blogs")
    lateinit var blogsComments: List<Comment>

    @SerializedName("webinar")
    lateinit var webinarComments: List<Comment>

    constructor(parcel: Parcel) : this() {
        blogsComments = parcel.createTypedArrayList(Comment)!!
        webinarComments = parcel.createTypedArrayList(Comment)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(blogsComments)
        parcel.writeTypedList(webinarComments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyComments> {
        override fun createFromParcel(parcel: Parcel): MyComments {
            return MyComments(parcel)
        }

        override fun newArray(size: Int): Array<MyComments?> {
            return arrayOfNulls(size)
        }
    }
}