package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class SessionChapter : ChapterCommonFields, Parcelable {

    @SerializedName("sessions")
    var sessions: List<ChapterSessionItem> = emptyList()

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        sessions = parcel.createTypedArrayList(ChapterSessionItem)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeTypedList(sessions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SessionChapter> {
        override fun createFromParcel(parcel: Parcel): SessionChapter {
            return SessionChapter(parcel)
        }

        override fun newArray(size: Int): Array<SessionChapter?> {
            return arrayOfNulls(size)
        }
    }
}