package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class QuizAnswer() : Parcelable {

    @SerializedName("quiz_result_id")
    var quizResultId = 0

    @SerializedName("answer_sheet")
    var answers: List<QuizAnswerItem>? = null

    constructor(parcel: Parcel) : this() {
        quizResultId = parcel.readInt()
        answers = parcel.createTypedArrayList(QuizAnswerItem)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(quizResultId)
        parcel.writeTypedList(answers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizAnswer> {
        override fun createFromParcel(parcel: Parcel): QuizAnswer {
            return QuizAnswer(parcel)
        }

        override fun newArray(size: Int): Array<QuizAnswer?> {
            return arrayOfNulls(size)
        }
    }

}