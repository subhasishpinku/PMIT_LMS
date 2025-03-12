package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Comment() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("create_at")
    var createdAt: Long = 0

    @SerializedName("user")
    var user: User? = null

    @SerializedName("comment")
    var comment: String? = null

    @SerializedName("replies")
    var replies: List<Comment>? = null

    @SerializedName("item_name")
    var itemName: String? = null

    @SerializedName("reply")
    var reply: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("blog")
    var blog: Blog? = null

    @SerializedName("webinar")
    var course: Course? = null

    @SerializedName("item_id")
    var itemId = 0

    @SerializedName("status")
    var status: String? = null

    @SerializedName("reason")
    var reason: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        createdAt = parcel.readLong()
        user = parcel.readParcelable(User::class.java.classLoader)
        comment = parcel.readString()
        replies = parcel.createTypedArrayList(CREATOR)
        itemName = parcel.readString()
        reply = parcel.readString()
        message = parcel.readString()
        status = parcel.readString()
        blog = parcel.readParcelable(Blog::class.java.classLoader)
        course = parcel.readParcelable(Course::class.java.classLoader)
        itemId = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(createdAt)
        parcel.writeParcelable(user, flags)
        parcel.writeString(comment)
        parcel.writeTypedList(replies)
        parcel.writeString(itemName)
        parcel.writeString(reply)
        parcel.writeString(message)
        parcel.writeParcelable(blog, flags)
        parcel.writeParcelable(course, flags)
        parcel.writeInt(itemId)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }

}