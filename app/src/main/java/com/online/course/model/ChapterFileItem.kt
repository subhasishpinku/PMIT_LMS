package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.course.model.view.ChapterListItemFactory
import com.online.course.model.view.CourseCommonItem

class ChapterFileItem : ChapterCommonFields, Parcelable, CourseCommonItem {

    enum class Storage(val value: String) {
        UPLOAD("upload"),
        YOUTUBE("youtube"),
        VIMEO("vimeo"),
        EXTERNAL_LINK("external_link"),
        S3("s3"),
    }

    enum class Accessibility(val value: String) {
        FREE("free"),
        PAID("paid")
    }

    @SerializedName("description")
    var description: String? = null

    @SerializedName("file")
    var file = ""

    @SerializedName("volume")
    var volume = ""

    @SerializedName("file_type")
     var fileType = ""

    @SerializedName("storage")
     var storage = ""

    @SerializedName("status")
     var status = ""

    @SerializedName("accessibility")
    var accessibility = ""

    @SerializedName("interactive_type")
    var interactiveType: String? = null

    @SerializedName("interactive_file_path")
    var interactiveFilePath: String? = null

    @SerializedName("downloadable")
    var downloadable = 0

    @SerializedName("auth_has_read")
    var authHasRead = false

    private var mCommonItem: CourseCommonItem? = null

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        description = parcel.readString()
        file = parcel.readString()!!
        volume = parcel.readString()!!
        fileType = parcel.readString()!!
        storage = parcel.readString()!!
        status = parcel.readString()!!
        accessibility = parcel.readString()!!
        downloadable = parcel.readInt()
        authHasRead = parcel.readByte() != 0.toByte()
        interactiveType = parcel.readString()
        interactiveFilePath = parcel.readString()
    }

    private fun initFactory() {
        if (mCommonItem == null) {
            val item = ChapterItem()
            item.title = title
            item.storage = storage
            item.volume = volume
            item.downloadable = downloadable
            item.authHasRead = authHasRead

            mCommonItem = ChapterListItemFactory.getItem(item)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(description)
        parcel.writeString(file)
        parcel.writeString(volume)
        parcel.writeString(fileType)
        parcel.writeString(storage)
        parcel.writeString(status)
        parcel.writeString(accessibility)
        parcel.writeInt(downloadable)
        parcel.writeByte(if (authHasRead) 1 else 0)
        parcel.writeString(interactiveType)
        parcel.writeString(interactiveFilePath)
    }

    override fun title(context: Context): String {
        initFactory()
        return mCommonItem!!.title(context)
    }

    override fun desc(context: Context): String {
        initFactory()
        return mCommonItem!!.desc(context)
    }

    override fun imgResource(context: Context): Int {
        initFactory()
        return mCommonItem!!.imgResource(context)
    }

    override fun imgBgResource(context: Context): Int {
        initFactory()
        return mCommonItem!!.imgBgResource(context)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterFileItem> {
        const val VIDEO_FILE_TYPE = "VIDEO"
        const val PDF_FILE_TYPE = "PDF"

        override fun createFromParcel(parcel: Parcel): ChapterFileItem {
            return ChapterFileItem(parcel)
        }

        override fun newArray(size: Int): Array<ChapterFileItem?> {
            return arrayOfNulls(size)
        }
    }
}