package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class TextChapter : ChapterCommonFields, Parcelable {

    @SerializedName("textLessons")
    var textLessons: List<ChapterTextItem> = emptyList()

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        textLessons = parcel.createTypedArrayList(ChapterTextItem)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeTypedList(textLessons)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TextChapter> {
        override fun createFromParcel(parcel: Parcel): TextChapter {
            return TextChapter(parcel)
        }

        override fun newArray(size: Int): Array<TextChapter?> {
            return arrayOfNulls(size)
        }
    }
}