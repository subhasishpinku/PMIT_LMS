package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PricingPlan() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("sub_title")
    var description: String? = null

    @SerializedName("discount")
    var discount = 0f

    @SerializedName("price_with_ticket_discount")
    var price = 0.0

    @SerializedName("is_valid")
    var isValid = false

    var isPointsItem = false

    var removed = false

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()!!
        description = parcel.readString()
        discount = parcel.readFloat()
        price = parcel.readDouble()
        isValid = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeFloat(discount)
        parcel.writeDouble(price)
        parcel.writeByte(if (isValid) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PricingPlan> {
        override fun createFromParcel(parcel: Parcel): PricingPlan {
            return PricingPlan(parcel)
        }

        override fun newArray(size: Int): Array<PricingPlan?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        if (other !is PricingPlan)
            return false

        return other.id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + discount.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + isValid.hashCode()
        return result
    }

}