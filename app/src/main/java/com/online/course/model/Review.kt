package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Review() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("item")
    var item = ""

    @SerializedName("webinar_id")
    var webinarId = 0

    @SerializedName("created_at")
    var createdAt: Long = 0

    @SerializedName("user")
    var user: User? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("reason")
    var reason: String? = null

    @SerializedName("content_quality")
    var contentQuality = 0f

    @SerializedName("instructor_skills")
    var instructorSkills = 0f

    @SerializedName("purchase_worth")
    var purchaseWorth = 0f

    @SerializedName("support_quality")
    var supportQuality = 0f

    @SerializedName("rate")
    var rating = 0f

    @SerializedName("auth")
    var auth = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        createdAt = parcel.readLong()
        user = parcel.readParcelable(User::class.java.classLoader)
        description = parcel.readString()
        reason = parcel.readString()
        contentQuality = parcel.readFloat()
        instructorSkills = parcel.readFloat()
        purchaseWorth = parcel.readFloat()
        supportQuality = parcel.readFloat()
        rating = parcel.readFloat()
        auth = parcel.readByte() != 0.toByte()
        item = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(createdAt)
        parcel.writeParcelable(user, flags)
        parcel.writeString(description)
        parcel.writeString(reason)
        parcel.writeFloat(contentQuality)
        parcel.writeFloat(instructorSkills)
        parcel.writeFloat(purchaseWorth)
        parcel.writeFloat(supportQuality)
        parcel.writeFloat(rating)
        parcel.writeByte(if (auth) 1 else 0)
        parcel.writeString(item)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }

}