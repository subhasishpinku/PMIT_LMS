package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Certificate() : Parcelable {

    enum class Type : Serializable {
        QUIZ,
        COMPLTETION,
        CLASS
    }

    @SerializedName("id")
    var id = 0

    @SerializedName("file")
    var img: String = ""

    @SerializedName("user_grade")
    var userGrade = 0.0

    @SerializedName("created_at")
    var createdAt = 0L

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        img = parcel.readString()!!
        userGrade = parcel.readDouble()
        createdAt = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(img)
        parcel.writeDouble(userGrade)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Certificate> {
        override fun createFromParcel(parcel: Parcel): Certificate {
            return Certificate(parcel)
        }

        override fun newArray(size: Int): Array<Certificate?> {
            return arrayOfNulls(size)
        }
    }
}