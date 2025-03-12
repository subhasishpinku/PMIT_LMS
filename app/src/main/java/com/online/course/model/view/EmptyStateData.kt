package com.online.course.model.view

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class EmptyStateData : Parcelable {

    @DrawableRes
    var img = 0

    @DrawableRes
    var titleRes = 0

    @DrawableRes
    var descRes = 0

    constructor()

    constructor(@DrawableRes img: Int, @StringRes titleRes: Int, @StringRes descRes: Int) {
        this.img = img
        this.titleRes = titleRes
        this.descRes = descRes
    }

    constructor(parcel: Parcel) : this() {
        img = parcel.readInt()
        titleRes = parcel.readInt()
        descRes = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(img)
        parcel.writeInt(titleRes)
        parcel.writeInt(descRes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmptyStateData> {
        override fun createFromParcel(parcel: Parcel): EmptyStateData {
            return EmptyStateData(parcel)
        }

        override fun newArray(size: Int): Array<EmptyStateData?> {
            return arrayOfNulls(size)
        }
    }
}