package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class QuizQuestion() : Parcelable {

    enum class Type(val value: String) {
        MULTIPLE("multiple"),
        DESCRIPTIVE("descriptive");
    }

    @SerializedName("id")
    var id = 0

    @SerializedName("grade")
    var grade = 0.0

    @SerializedName("title")
    var title: String = ""

    @SerializedName("type")
    var type: String = ""

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("updated_at")
    var updatedAt = 0L

    @SerializedName("answers")
    var answers: List<QuizQuestionAnswer>? = null

    @SerializedName("correct")
    var descriptiveCorrectAnswer : String? = null

    @SerializedName("correct_answer")
    var multipleCorrectAnswer : QuizQuestionAnswer? = null

    @SerializedName("user_answer")
    var userAnswer : QuizAnswerItem? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        grade = parcel.readDouble()
        title = parcel.readString()!!
        type = parcel.readString()!!
        createdAt = parcel.readLong()
        updatedAt = parcel.readLong()
        answers = parcel.createTypedArrayList(QuizQuestionAnswer)
        descriptiveCorrectAnswer = parcel.readString()
        multipleCorrectAnswer = parcel.readParcelable(QuizQuestionAnswer::class.java.classLoader)
        userAnswer = parcel.readParcelable(QuizAnswerItem::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeDouble(grade)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
        parcel.writeTypedList(answers)
        parcel.writeString(descriptiveCorrectAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizQuestion> {
        override fun createFromParcel(parcel: Parcel): QuizQuestion {
            return QuizQuestion(parcel)
        }

        override fun newArray(size: Int): Array<QuizQuestion?> {
            return arrayOfNulls(size)
        }
    }
}