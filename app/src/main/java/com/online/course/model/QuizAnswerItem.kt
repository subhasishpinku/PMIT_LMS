package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class QuizAnswerItem() : Parcelable {

    @SerializedName("question_id")
    var questionId = 0

    @SerializedName("answer")
    lateinit var answer : String

    @SerializedName("status")
    var hasUserAnsweredCorrectly = false

    @SerializedName("grade")
    var grade = 0.0

    constructor(parcel: Parcel) : this() {
        questionId = parcel.readInt()
        answer = parcel.readString()!!
        grade = parcel.readDouble()
        hasUserAnsweredCorrectly = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(questionId)
        parcel.writeString(answer)
        parcel.writeDouble(grade)
        parcel.writeByte(if (hasUserAnsweredCorrectly) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizAnswerItem> {
        override fun createFromParcel(parcel: Parcel): QuizAnswerItem {
            return QuizAnswerItem(parcel)
        }

        override fun newArray(size: Int): Array<QuizAnswerItem?> {
            return arrayOfNulls(size)
        }
    }
}