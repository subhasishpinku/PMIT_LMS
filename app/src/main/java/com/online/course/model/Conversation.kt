package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Conversation() : Parcelable {

    var id = 0

    @SerializedName("message")
    var message = ""

    @SerializedName("sender")
    var sender: User? = null

    @SerializedName("supporter")
    var supporter: User? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("attach")
    var attachment: String? = null

    @SerializedName("file_title")
    var fileTitle = ""

    @SerializedName("file_path")
    var filePath: String? = null

    var fileSize: Long? = null

    var studentId = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        message = parcel.readString()!!
        sender = parcel.readParcelable(User::class.java.classLoader)
        supporter = parcel.readParcelable(User::class.java.classLoader)
        createdAt = parcel.readLong()
        attachment = parcel.readString()
        fileTitle = parcel.readString()!!
        filePath = parcel.readString()
        fileSize = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(message)
        parcel.writeParcelable(sender, flags)
        parcel.writeParcelable(supporter, flags)
        parcel.writeLong(createdAt)
        parcel.writeString(attachment)
        parcel.writeString(fileTitle)
        parcel.writeString(filePath)
        parcel.writeValue(fileSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Conversation> {
        override fun createFromParcel(parcel: Parcel): Conversation {
            return Conversation(parcel)
        }

        override fun newArray(size: Int): Array<Conversation?> {
            return arrayOfNulls(size)
        }
    }


}