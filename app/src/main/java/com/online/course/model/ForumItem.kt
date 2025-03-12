package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class ForumItem() : Parcelable {
    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    var title = ""

    @SerializedName("description")
    var description = ""

    @SerializedName("answers_count")
    var answersCount = 0

    @SerializedName("more")
    var activeUsersCount = 0

    @SerializedName("resolved")
    var resolved = false

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("user")
    var user = User()

    @SerializedName("active_users")
    var activeUsers: List<String> = emptyList()

    @SerializedName("last_activity")
    var lastActivity = 0L

    @SerializedName("pin")
    var isPinned = false

    @SerializedName("can")
    var can = Can()

    @SerializedName("attachment")
    var attachment: String? = null

    var isEdit = false

    fun isAnswer(): Boolean {
        return this is ForumItemAnswer
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        description = parcel.readString()!!
        answersCount = parcel.readInt()
        activeUsersCount = parcel.readInt()
        resolved = parcel.readByte() != 0.toByte()
        createdAt = parcel.readLong()
        user = parcel.readParcelable(User::class.java.classLoader)!!
        activeUsers = parcel.createStringArrayList()!!
        lastActivity = parcel.readLong()
        isPinned = parcel.readByte() != 0.toByte()
        isEdit = parcel.readByte() != 0.toByte()
        can = parcel.readParcelable(Can::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(answersCount)
        parcel.writeInt(activeUsersCount)
        parcel.writeByte(if (resolved) 1 else 0)
        parcel.writeLong(createdAt)
        parcel.writeParcelable(user, flags)
        parcel.writeStringList(activeUsers)
        parcel.writeLong(lastActivity)
        parcel.writeByte(if (isPinned) 1 else 0)
        parcel.writeByte(if (isEdit) 1 else 0)
        parcel.writeParcelable(can, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ForumItem> {
        override fun createFromParcel(parcel: Parcel): ForumItem {
            return ForumItem(parcel)
        }

        override fun newArray(size: Int): Array<ForumItem?> {
            return arrayOfNulls(size)
        }
    }

    class Can() : Parcelable {
        @SerializedName("pin")
        var pin = false

        @SerializedName("resolve")
        var resolve = false

        @SerializedName("update")
        var update = false

        constructor(parcel: Parcel) : this() {
            pin = parcel.readByte() != 0.toByte()
            resolve = parcel.readByte() != 0.toByte()
            update = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeByte(if (pin) 1 else 0)
            parcel.writeByte(if (resolve) 1 else 0)
            parcel.writeByte(if (update) 1 else 0)
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