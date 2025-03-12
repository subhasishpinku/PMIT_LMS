package com.online.course.model.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toBoolean
import com.online.course.model.ChapterFileItem
import com.online.course.model.ChapterItem

class AssignmentChapterListItem : CourseCommonItem {

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

    companion object CREATOR : Parcelable.Creator<AssignmentChapterListItem> {
        override fun createFromParcel(parcel: Parcel): AssignmentChapterListItem {
            return AssignmentChapterListItem(parcel)
        }

        override fun newArray(size: Int): Array<AssignmentChapterListItem?> {
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
        return R.drawable.ic_paper
    }

    override fun imgBgResource(context: Context): Int {
        return R.drawable.round_view_dark_blue2_corner10
    }

}