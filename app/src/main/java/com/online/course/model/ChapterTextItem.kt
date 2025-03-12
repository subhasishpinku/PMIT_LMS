package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.model.view.CourseCommonItem
import java.util.*

class ChapterTextItem : ChapterCommonFields, Parcelable, CourseCommonItem {

    @SerializedName("image")
    var image: String? = null

    @SerializedName("study_time")
    var studyTime = 0

    @SerializedName("summary")
    var summary = ""

    @SerializedName("content")
    var content = ""

    @SerializedName("status")
    var status = ""

    @SerializedName("accessibility")
    var accessibility = ""

    @SerializedName("locale")
    var locale: String = Locale.ENGLISH.language

    @SerializedName("attachments")
    var attachments: List<ChapterFileItem> = emptyList()

    @SerializedName("auth_has_read")
    var authHasRead = false

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        image = parcel.readString()
        studyTime = parcel.readInt()
        summary = parcel.readString()!!
        content = parcel.readString()!!
        status = parcel.readString()!!
        accessibility = parcel.readString()!!
        attachments = parcel.createTypedArrayList(ChapterFileItem)!!
        authHasRead = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeInt(studyTime)
        parcel.writeString(summary)
        parcel.writeString(content)
        parcel.writeString(status)
        parcel.writeString(accessibility)
        parcel.writeLong(createdAt)
        parcel.writeTypedList(attachments)
        parcel.writeByte(if (authHasRead) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterTextItem> {
        override fun createFromParcel(parcel: Parcel): ChapterTextItem {
            return ChapterTextItem(parcel)
        }

        override fun newArray(size: Int): Array<ChapterTextItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return title
    }

    override fun desc(context: Context): String {
        return summary
    }

    override fun imgResource(context: Context): Int {
        return R.drawable.ic_paper
    }

    override fun imgBgResource(context: Context): Int {
        return R.drawable.round_view_orange_corner10
    }

    override fun passed(): Boolean {
        return authHasRead
    }
}