package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class FileChapter : ChapterCommonFields {

    @SerializedName("files")
    var files: List<ChapterFileItem> = emptyList()

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        files = parcel.createTypedArrayList(ChapterFileItem)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeTypedList(files)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileChapter> {
        override fun createFromParcel(parcel: Parcel): FileChapter {
            return FileChapter(parcel)
        }

        override fun newArray(size: Int): Array<FileChapter?> {
            return arrayOfNulls(size)
        }
    }
}