package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.online.course.manager.player.PlayerHelper

class PlayerState() : Parcelable {

    var currentPosition = 0L
    lateinit var path: String
    lateinit var playerType: PlayerHelper.Type
    var isPlaying = false
    var isLocalFile = false

    constructor(parcel: Parcel) : this() {
        currentPosition = parcel.readLong()
        path = parcel.readString()!!
        isPlaying = parcel.readByte() != 0.toByte()
        isLocalFile = parcel.readByte() != 0.toByte()
        playerType = parcel.readSerializable() as PlayerHelper.Type
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(currentPosition)
        parcel.writeString(path)
        parcel.writeByte(if (isPlaying) 1 else 0)
        parcel.writeByte(if (isLocalFile) 1 else 0)
        parcel.writeSerializable(playerType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayerState> {
        override fun createFromParcel(parcel: Parcel): PlayerState {
            return PlayerState(parcel)
        }

        override fun newArray(size: Int): Array<PlayerState?> {
            return arrayOfNulls(size)
        }
    }
}