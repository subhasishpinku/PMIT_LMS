package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.online.course.model.view.ChapterListItemFactory
import com.online.course.model.view.CourseCommonItem

class ChapterItem : ChapterCommonFields, Parcelable, CourseCommonItem {

    enum class Type(val value: String) {
        FILE("file"),
        TEXT("text_lesson"),
        SESSION("session"),
        ASSIGNEMENT("assignment"),
        QUIZ("quiz"),
        CERTIFICATE("certificate")
    }

    @SerializedName("auth_status")
    var authStatus = ""

    @SerializedName("question_count")
    var questionCount = 0

    @SerializedName("time")
    var time = 0

    @SerializedName("downloadable")
    var downloadable = 0

    @SerializedName("storage")
    var storage = ""

    @SerializedName("volume")
    var volume = ""

    @SerializedName("summary")
    var summary = ""

    @SerializedName("auth_has_read")
    var authHasRead = false

    @SerializedName("date")
    var date = 0L

    @SerializedName("link")
    var link = ""

    @SerializedName("can_view_error")
    var viewError: String? = null

    @SerializedName("can")
    var can = Can()

    private var mChapterListItem: CourseCommonItem? = null

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        link = parcel.readString()!!
        viewError = parcel.readString()
        can = parcel.readParcelable(Can::class.java.classLoader)!!
        date = parcel.readLong()
        authHasRead = parcel.readByte() != 0.toByte()
        summary = parcel.readString()!!
        volume = parcel.readString()!!
        storage = parcel.readString()!!
        questionCount = parcel.readInt()
        time = parcel.readInt()
        authStatus = parcel.readString()!!
    }

    private fun initFactory() {
        if (mChapterListItem == null){
            mChapterListItem = ChapterListItemFactory.getItem(this)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(link)
        parcel.writeString(viewError)
        parcel.writeParcelable(can, flags)
        parcel.writeLong(date)
        parcel.writeString(summary)
        parcel.writeByte(if (authHasRead) 1 else 0)
        parcel.writeInt(time)
        parcel.writeInt(questionCount)
        parcel.writeString(authStatus)
    }

    companion object CREATOR : Parcelable.Creator<ChapterItem> {
        override fun createFromParcel(parcel: Parcel): ChapterItem {
            return ChapterItem(parcel)
        }

        override fun newArray(size: Int): Array<ChapterItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        initFactory()
        return mChapterListItem!!.title(context)
    }

    override fun desc(context: Context): String {
        initFactory()
        return mChapterListItem!!.desc(context)
    }

    override fun imgResource(context: Context): Int {
        initFactory()
        return mChapterListItem!!.imgResource(context)
    }

    override fun imgBgResource(context: Context): Int {
        initFactory()
        return mChapterListItem!!.imgBgResource(context)
    }

    fun isQuiz(): Boolean {
        return type == Type.QUIZ.value
    }

    fun isCert(): Boolean {
        return type == Type.CERTIFICATE.value
    }

    fun isAssignment(): Boolean {
        return type == Type.ASSIGNEMENT.value
    }

    class Can() : Parcelable {
        @SerializedName("view")
        var view = false

        constructor(parcel: Parcel) : this() {
            view = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeByte(if (view) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Can> {
            override fun createFromParcel(parcel: Parcel): Can {
                return Can(parcel)
            }

            override fun newArray(size: Int): Array<Can?> {
                return arrayOfNulls(size)
            }
        }
    }
}