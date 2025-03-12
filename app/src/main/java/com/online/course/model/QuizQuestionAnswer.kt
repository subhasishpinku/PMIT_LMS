package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class QuizQuestionAnswer() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("correct")
    var correct = 0

    var isUserAnswer = false

    @SerializedName("title")
    var title: String = ""

    @SerializedName("image")
    var image: String? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("updated_at")
    var updatedAt = 0L

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        correct = parcel.readInt()
        title = parcel.readString()!!
        image = parcel.readString()
        createdAt = parcel.readLong()
        updatedAt = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(correct)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizQuestionAnswer> {
        override fun createFromParcel(parcel: Parcel): QuizQuestionAnswer {
            return QuizQuestionAnswer(parcel)
        }

        override fun newArray(size: Int): Array<QuizQuestionAnswer?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        if (other !is QuizQuestionAnswer)
            return false

        return other.id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + correct
        result = 31 * result + title.hashCode()
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}