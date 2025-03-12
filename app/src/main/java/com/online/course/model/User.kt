package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class User : Parcelable {

    enum class MeetingStatus(val status: String) {
        NO("no"),
        AVAILABLE("available"),
        UNAVAILABLE("unavailable");
    }

    var date = 0L

    @SerializedName("id")
    var id = 0

    @SerializedName("user_id")
    var userId = 0

    @SerializedName("full_name")
    var name: String = ""

    @SerializedName("role_name")
    var roleName: String = ""

    @SerializedName("email")
    var email: String? = null

    @SerializedName("mobile")
    var mobile: String? = null

    @SerializedName("bio")
    var bio: String? = null

    @SerializedName("rate")
    var rating = 0f

    @SerializedName("avatar")
    var avatar: String? = null

    @SerializedName("meeting_status")
    var meetingStatus: String? = null

    @SerializedName("referral_code")
    var referral: String? = null

    @SerializedName("newsletter")
    var hasNewsLetter = false

    @SerializedName("public_message")
    var publicMessage = 0

    @SerializedName("offline")
    var offline = 0

    @SerializedName("address")
    var address = ""

    constructor()

    constructor(name: String, roleName: String) {
        this.name = name
        this.roleName = roleName
    }

    constructor(parcel: Parcel) {
        userId = parcel.readInt()
        name = parcel.readString()!!
        roleName = parcel.readString()!!
        email = parcel.readString()
        mobile = parcel.readString()
        bio = parcel.readString()
        rating = parcel.readFloat()
        offline = parcel.readInt()
        avatar = parcel.readString()
        meetingStatus = parcel.readString()
        referral = parcel.readString()
        hasNewsLetter = parcel.readByte() != 0.toByte()
        publicMessage = parcel.readInt()
        address = parcel.readString()!!
    }

    fun isInstructor(): Boolean {
        return roleName == Role.INSTRUCTOR.value()
    }

    fun isUser(): Boolean {
        return roleName == Role.USER.value()
    }

    fun isOrganizaton(): Boolean {
        return roleName == Role.ORGANIZATION.value()
    }

    enum class Role(private val type: String) {
        USER("user"), INSTRUCTOR("teacher"), ORGANIZATION("organization");

        fun value(): String {
            return type
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeString(name)
        parcel.writeString(roleName)
        parcel.writeString(email)
        parcel.writeString(mobile)
        parcel.writeString(bio)
        parcel.writeFloat(rating)
        parcel.writeInt(offline)
        parcel.writeString(avatar)
        parcel.writeString(meetingStatus)
        parcel.writeString(referral)
        parcel.writeByte(if (hasNewsLetter) 1 else 0)
        parcel.writeInt(publicMessage)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}