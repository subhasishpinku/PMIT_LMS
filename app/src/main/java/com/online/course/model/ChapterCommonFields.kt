package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class ChapterCommonFields : Parcelable {
    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    var title = ""

    @SerializedName("type")
    var type = ""

    @SerializedName("created_at")
    var createdAt = 0L

    constructor()

    constructor(parcel: Parcel) {
        id = parcel.readInt()
        title = parcel.readString()!!
        type = parcel.readString()!!
        createdAt = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterCommonFields> {
        override fun createFromParcel(parcel: Parcel): ChapterCommonFields {
            return ChapterCommonFields(parcel)
        }

        override fun newArray(size: Int): Array<ChapterCommonFields?> {
            return arrayOfNulls(size)
        }
    }
}