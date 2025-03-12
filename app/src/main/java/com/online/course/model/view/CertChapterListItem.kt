package com.online.course.model.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toBoolean
import com.online.course.model.ChapterFileItem
import com.online.course.model.ChapterItem

class CertChapterListItem : CourseCommonItem {

    private var chapterItem: ChapterItem

    constructor(chpaterItem: ChapterItem) {
        chapterItem = chpaterItem
    }

    constructor(parcel: Parcel) {
        chapterItem = parcel.readParcelable(ChapterItem::class.java.classLoader)!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(chapterItem, flags)
    }

    companion object CREATOR : Parcelable.Creator<CertChapterListItem> {
        override fun createFromParcel(parcel: Parcel): CertChapterListItem {
            return CertChapterListItem(parcel)
        }

        override fun newArray(size: Int): Array<CertChapterListItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return chapterItem.title
    }

    override fun desc(context: Context): String {
        return Utils.getDateFromTimestamp(chapterItem.createdAt)
    }

    override fun imgResource(context: Context): Int {
        return R.drawable.ic_certificate
    }

    override fun imgBgResource(context: Context): Int {
        return R.drawable.round_view_light_red_corner10
    }
}