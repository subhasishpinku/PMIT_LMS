package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Department() : ItemPicker, Parcelable {
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

    companion object CREATOR : Parcelable.Creator<Department> {
        override fun createFromParcel(parcel: Parcel): Department {
            return Department(parcel)
        }

        override fun newArray(size: Int): Array<Department?> {
            return arrayOfNulls(size)
        }
    }

    override fun itemTitle(): String {
        return title
    }
}