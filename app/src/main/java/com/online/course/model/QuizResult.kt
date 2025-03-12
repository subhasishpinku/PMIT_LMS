package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class QuizResult() : Parcelable {

    enum class Result(val value: String) {
        PASSED("passed"),
        FAILED("failed"),
        WAITING("waiting");
    }

    @SerializedName("quiz_result_id")
    var quizResultId = 0

    @SerializedName("id")
    var id = 0

    @SerializedName("user")
    var user: User? = null

    @SerializedName("user_grade")
    var userGrade = 0.0

    @SerializedName("status")
    var status: String? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("auth_can_try_again")
    var authCanTryAgain = false

    @SerializedName("count_try_again")
    var countTryAgain = 0

    @SerializedName("quiz")
    lateinit var quiz: Quiz

    @SerializedName("quiz_review")
    var quizReview: List<QuizQuestion>? = null

    @SerializedName("certificate")
    var certificate: Certificate? = null

    constructor(parcel: Parcel) : this() {
        quizResultId = parcel.readInt()
        id = parcel.readInt()
        user = parcel.readParcelable(User::class.java.classLoader)
        userGrade = parcel.readDouble()
        status = parcel.readString()
        createdAt = parcel.readLong()
        authCanTryAgain = parcel.readByte() != 0.toByte()
        countTryAgain = parcel.readInt()
        quiz = parcel.readParcelable(Quiz::class.java.classLoader)!!
        quizReview = parcel.createTypedArrayList(QuizQuestion)
        certificate = parcel.readParcelable(Certificate::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(quizResultId)
        parcel.writeInt(id)
        parcel.writeParcelable(user, flags)
        parcel.writeDouble(userGrade)
        parcel.writeString(status)
        parcel.writeLong(createdAt)
        parcel.writeByte(if (authCanTryAgain) 1 else 0)
        parcel.writeInt(countTryAgain)
        parcel.writeParcelable(quiz, flags)
        parcel.writeTypedList(quizReview)
        parcel.writeParcelable(certificate, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizResult> {
        override fun createFromParcel(parcel: Parcel): QuizResult {
            return QuizResult(parcel)
        }

        override fun newArray(size: Int): Array<QuizResult?> {
            return arrayOfNulls(size)
        }
    }


}