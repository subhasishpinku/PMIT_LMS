package com.online.course.model.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toBoolean
import com.online.course.model.ChapterFileItem
import com.online.course.model.ChapterItem

class QuizChapterListItem : CourseCommonItem {

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

    companion object CREATOR : Parcelable.Creator<QuizChapterListItem> {
        override fun createFromParcel(parcel: Parcel): QuizChapterListItem {
            return QuizChapterListItem(parcel)
        }

        override fun newArray(size: Int): Array<QuizChapterListItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return chapterItem.title
    }

    override fun desc(context: Context): String {
        return "${chapterItem.questionCount} ${context.getString(R.string.questions)} | ${
            Utils.getDuration(
                context,
                chapterItem.time
            )
        }"
    }

    override fun imgResource(context: Context): Int {
        return R.drawable.ic_shield_done
    }

    override fun imgBgResource(context: Context): Int {
       return R.drawable.round_view_light_green2_corner10
    }

    override fun status(): String {
        if (chapterItem.authStatus == "passed" || chapterItem.authStatus == "failed") {
            return chapterItem.authStatus
        }
        return super.status()
    }

    override fun statusBg(): Int {
        if (chapterItem.authStatus == "passed") {
            return R.drawable.round_view_accent_corner10
        } else if (chapterItem.authStatus == "failed") {
            return R.drawable.round_view_red_corner10
        }
        return super.statusBg()
    }

}