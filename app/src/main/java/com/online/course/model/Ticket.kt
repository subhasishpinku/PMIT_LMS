package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils
import com.online.course.model.view.CommonItem

class Ticket() : CommonItem, Parcelable {

    enum class Status(private val value: String) {
        OPEN("open"),
        CLOSE("close"),
        REPLIED("replied"),
        SUPPORTER_REPLIED("supporter_replied");

        fun value(): String {
            return value
        }
    }

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("status")
    var status = Status.OPEN.value()

    @SerializedName("webinar")
    var course: Course? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("conversations")
    var conversations: List<Conversation> = emptyList()

    @SerializedName("Department")
    var department: Department? = null

    @SerializedName("department_id")
    var departmentId: Int? = null

    @SerializedName("webinar_id")
    var courseId: Int? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("attach")
    var attachment: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        status = parcel.readString()!!
        course = parcel.readParcelable(Course::class.java.classLoader)
        createdAt = parcel.readLong()
        conversations = parcel.createTypedArrayList(Conversation)!!
        department = parcel.readParcelable(Department::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(status)
        parcel.writeParcelable(course, flags)
        parcel.writeLong(createdAt)
        parcel.writeTypedList(conversations)
        parcel.writeParcelable(department, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ticket> {
        override fun createFromParcel(parcel: Parcel): Ticket {
            return Ticket(parcel)
        }

        override fun newArray(size: Int): Array<Ticket?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return title
    }

    override fun img(): String? {
        return null
    }

    override fun desc(context: Context): String {
        return Utils.getDateTimeFromTimestamp(createdAt)
    }

    override fun imgResource(): Int? {
        return R.drawable.ic_comments
    }

    override fun status(context: Context): CommonItem.CommonItemStatus? {
        val statusObj = CommonItem.CommonItemStatus()
        statusObj.status = status
        if (status == Status.REPLIED.value() || status == Status.SUPPORTER_REPLIED.value()) {
            statusObj.textColor = ContextCompat.getColor(context, R.color.accent)
            statusObj.textBg = R.drawable.round_view_accent_corner10_op30
        } else if (status == Status.CLOSE.value()) {
            statusObj.textColor = ContextCompat.getColor(context, R.color.gull_gray)
            statusObj.textBg = R.drawable.round_view_light_gray_corner10
        } else {
            statusObj.textColor = ContextCompat.getColor(context, R.color.orange)
            statusObj.textBg = R.drawable.round_view_light_yellow_corner10
        }

        return statusObj
    }

    override fun cardBg(): Int? {
        return R.color.white
    }
}