package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Assignment() : Parcelable {

    enum class UserStatus(val value: String) {
        PENDING("pending"),
        PASSED("passed"),
        NOT_PASSED("not_passed"),
        NOT_SUBMITTED("not_submitted")
    }

    enum class Status(val value: String) {
        ACTIVE("active"),
        INACTIVE("inactive"),
    }

    @SerializedName("student")
    var student: User? = null

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    var title = ""

    @SerializedName("description")
    var description = ""

    @SerializedName("webinar_title")
    var courseTitle = ""

    @SerializedName("webinar_image")
    var courseImage: String? = null

    @SerializedName("deadline_time")
    var deadlineTime = 0L

    @SerializedName("deadline")
    var deadlineDueTimeInDays = 0

    @SerializedName("first_submission")
    var firstSubmission: Long? = null

    @SerializedName("last_submission")
    var lastSubmission: Long? = null

    @SerializedName("attempts")
    var totalAttempts = 0

    @SerializedName("used_attempts_count")
    var usedAttemptsCount = 0

    @SerializedName("grade")
    var grade: Int? = null

    @SerializedName("pass_grade")
    var passGrade = 0

    @SerializedName("total_grade")
    var totalGrade = 0

    @SerializedName("submissions_count")
    var submissionsCount = 0

    @SerializedName("passed_count")
    var passedCount = 0

    @SerializedName("failed_count")
    var failedCount = 0

    @SerializedName("avg_grade")
    var averageGrade = 0

    @SerializedName("pending_count")
    var pendingCount = 0

    @SerializedName("user_status")
    var userStatus = ""

    @SerializedName("status")
    var status = ""

    @SerializedName("attachments")
    var attachments: List<Attachment> = emptyList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        description = parcel.readString()!!
        courseTitle = parcel.readString()!!
        courseImage = parcel.readString()
        deadlineTime = parcel.readLong()
        firstSubmission = parcel.readValue(Long::class.java.classLoader) as? Long
        lastSubmission = parcel.readValue(Long::class.java.classLoader) as? Long
        totalAttempts = parcel.readInt()
        usedAttemptsCount = parcel.readInt()
        grade = parcel.readValue(Int::class.java.classLoader) as? Int
        passGrade = parcel.readInt()
        totalGrade = parcel.readInt()
        userStatus = parcel.readString()!!
        status = parcel.readString()!!
        attachments = parcel.createTypedArrayList(Attachment)!!
        deadlineDueTimeInDays = parcel.readInt()
        submissionsCount = parcel.readInt()
        pendingCount = parcel.readInt()
        averageGrade = parcel.readInt()
        passedCount = parcel.readInt()
        failedCount = parcel.readInt()
        student = parcel.readParcelable(User::class.java.classLoader)
    }


    fun isPassed(): Boolean {
        return userStatus == UserStatus.PASSED.value
    }

    fun isClosed(): Boolean {
        // deadlineTime is in seconds
        return usedAttemptsCount == totalAttempts || deadlineTime * 1000 < System.currentTimeMillis()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(courseTitle)
        parcel.writeString(courseImage)
        parcel.writeLong(deadlineTime)
        parcel.writeValue(firstSubmission)
        parcel.writeValue(lastSubmission)
        parcel.writeInt(totalAttempts)
        parcel.writeInt(usedAttemptsCount)
        parcel.writeValue(grade)
        parcel.writeInt(passGrade)
        parcel.writeInt(totalGrade)
        parcel.writeString(userStatus)
        parcel.writeTypedList(attachments)
        parcel.writeInt(deadlineDueTimeInDays)
        parcel.writeString(status)
        parcel.writeInt(submissionsCount)
        parcel.writeInt(pendingCount)
        parcel.writeInt(averageGrade)
        parcel.writeInt(passedCount)
        parcel.writeInt(failedCount)
        parcel.writeParcelable(student, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Assignment> {
        override fun createFromParcel(parcel: Parcel): Assignment {
            return Assignment(parcel)
        }

        override fun newArray(size: Int): Array<Assignment?> {
            return arrayOfNulls(size)
        }
    }


}