package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PayoutAccount() : Parcelable {

    @SerializedName("amount")
    var amonut = 0.0

    @SerializedName("minimum_payout")
    var minimumPayout = 0.0

    @SerializedName("account_type")
    var account: String? = null

    @SerializedName("account_id")
    var cardId: String? = null

    @SerializedName("iban")
    var iban: String? = null

    constructor(parcel: Parcel) : this() {
        amonut = parcel.readDouble()
        account = parcel.readString()
        cardId = parcel.readString()
        iban = parcel.readString()
        minimumPayout = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(amonut)
        parcel.writeString(account)
        parcel.writeString(cardId)
        parcel.writeString(iban)
        parcel.writeDouble(minimumPayout)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PayoutAccount> {
        override fun createFromParcel(parcel: Parcel): PayoutAccount {
            return PayoutAccount(parcel)
        }

        override fun newArray(size: Int): Array<PayoutAccount?> {
            return arrayOfNulls(size)
        }
    }
}