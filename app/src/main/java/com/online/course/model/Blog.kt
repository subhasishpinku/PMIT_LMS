package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Blog() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("image")
    var image: String? = null

    @SerializedName("description")
    var description: String = ""

    @SerializedName("content")
    var content: String = ""

    @SerializedName("category")
    var category: String = ""

    @SerializedName("created_at")
    var createdAt: Long = 0

    @SerializedName("author")
    var author: User? = null

    @SerializedName("comment_count")
    var commentCount = 0

    @SerializedName("comments")
    var comments: List<Comment> = emptyList()

    @SerializedName("locale")
    var locale = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        image = parcel.readString()
        description = parcel.readString()!!
        content = parcel.readString()!!
        category = parcel.readString()!!
        createdAt = parcel.readLong()
        author = parcel.readParcelable(User::class.java.classLoader)
        commentCount = parcel.readInt()
        locale = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(content)
        parcel.writeString(category)
        parcel.writeLong(createdAt)
        parcel.writeParcelable(author, flags)
        parcel.writeInt(commentCount)
        parcel.writeString(locale)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Blog> {
        override fun createFromParcel(parcel: Parcel): Blog {
            return Blog(parcel)
        }

        override fun newArray(size: Int): Array<Blog?> {
            return arrayOfNulls(size)
        }
    }
}