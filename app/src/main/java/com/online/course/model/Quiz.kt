package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.model.view.CourseCommonItem

class Quiz() : Parcelable, CourseCommonItem {

    @SerializedName("id")
    var id = 0

    @SerializedName("total_mark")
    var totalMark = 0.0

    @SerializedName("pass_mark")
    var passMark = 0.0

    @SerializedName("average_grade")
    var averageGrade = 0.0

    @SerializedName("attempt")
    var attempt = 0

    @SerializedName("time")
    var time = 0

    @SerializedName("auth_can_try_again")
    var authCanTryAgain = false

    @SerializedName("participated_count")
    var participatedCount = 0

    @SerializedName("question_count")
    var questionCount = 0

    @SerializedName("success_rate")
    var successRate = 0f

    @SerializedName("status")
    var status = ""

    @SerializedName("title")
    var title = ""

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("auth_status")
    var authStatus = ""

    @SerializedName("questions")
    var questions: List<QuizQuestion> = emptyList()

    @SerializedName("webinar")
    lateinit var course: Course

    var isCertificate = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        totalMark = parcel.readDouble()
        passMark = parcel.readDouble()
        averageGrade = parcel.readDouble()
        attempt = parcel.readInt()
        time = parcel.readInt()
        authCanTryAgain = parcel.readByte() != 0.toByte()
        participatedCount = parcel.readInt()
        questionCount = parcel.readInt()
        successRate = parcel.readFloat()
        status = parcel.readString()!!
        title = parcel.readString()!!
        createdAt = parcel.readLong()
        questions = parcel.createTypedArrayList(QuizQuestion)!!
        course = parcel.readParcelable(Course::class.java.classLoader)!!
        authStatus = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeDouble(totalMark)
        parcel.writeDouble(passMark)
        parcel.writeDouble(averageGrade)
        parcel.writeInt(attempt)
        parcel.writeInt(time)
        parcel.writeByte(if (authCanTryAgain) 1 else 0)
        parcel.writeInt(participatedCount)
        parcel.writeInt(questionCount)
        parcel.writeFloat(successRate)
        parcel.writeString(status)
        parcel.writeString(title)
        parcel.writeLong(createdAt)
        parcel.writeTypedList(questions)
        parcel.writeParcelable(course, flags)
        parcel.writeString(authStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quiz> {
        const val NOT_PARTICIPATED = "not_participated"

        override fun createFromParcel(parcel: Parcel): Quiz {
            return Quiz(parcel)
        }

        override fun newArray(size: Int): Array<Quiz?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return title
    }

    override fun desc(context: Context): String {
        return if (isCertificate) {
            Utils.getDateFromTimestamp(createdAt)
        } else {
            "$questionCount ${context.getString(R.string.questions)} | ${
                Utils.getDuration(
                    context,
                    time
                )
            }"
        }
    }

    override fun imgResource(context: Context): Int {
        return if (isCertificate) {
            R.drawable.ic_certificate
        } else {
            R.drawable.ic_shield_done
        }
    }

    override fun imgBgResource(context: Context): Int {
        return if (isCertificate) {
            R.drawable.round_view_light_red_corner10
        } else {
            R.drawable.round_view_light_green2_corner10
        }
    }

    override fun status(): String {
        if (authStatus == "passed" || authStatus == "failed") {
            return authStatus
        }
        return super.status()
    }

    override fun statusBg(): Int {
        if (!isCertificate) {
            if (authStatus == "passed") {
                return R.drawable.round_view_accent_corner10
            } else if (authStatus == "failed") {
                return R.drawable.round_view_red_corner10
            }
        }

        return super.statusBg()
    }
}