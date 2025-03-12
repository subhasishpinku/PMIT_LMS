package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class SubscriptionItem() : Parcelable{

    @SerializedName("id")
    var id = 0

    @SerializedName("subscribe_id")
    var subscribeId = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("description")
    lateinit var description: String

    @SerializedName("image")
    var image: String? = null

    @SerializedName("usable_count")
    var usableCount = 0

    @SerializedName("days")
    var days = 0

    @SerializedName("price")
    var price = 0.0

    @SerializedName("created_at")
    var createdAt = 0L

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        description = parcel.readString()!!
        image = parcel.readString()
        usableCount = parcel.readInt()
        days = parcel.readInt()
        price = parcel.readDouble()
        createdAt = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeInt(usableCount)
        parcel.writeInt(days)
        parcel.writeDouble(price)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionItem> {
        override fun createFromParcel(parcel: Parcel): SubscriptionItem {
            return SubscriptionItem(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionItem?> {
            return arrayOfNulls(size)
        }
    }
}