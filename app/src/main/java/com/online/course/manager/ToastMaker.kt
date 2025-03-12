package com.online.course.manager

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.online.course.R
import com.online.course.model.BaseResponse

object ToastMaker {

    const val SHORT_DURATION = 0
    const val LOGN_DURATION = 1

    enum class Type {
        SUCCESS, ERROR
    }

    @JvmStatic
    fun show(
        context: Context,
        title: String,
        message: String,
        type: Type = Type.SUCCESS,
        duration: Int = SHORT_DURATION
    ) {
        val toast = Toast(context)
        val toastView =
            LayoutInflater.from(context).inflate(R.layout.layout_toast, null)
        toast.view = toastView
        toast.duration = duration
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0);
        val toastTitleTv = toastView.findViewById<TextView>(R.id.toastTitleTv)
        val toastDescTv = toastView.findViewById<TextView>(R.id.toastDescTv)
        val toastImg = toastView.findViewById<ImageView>(R.id.toastImg)
        toastTitleTv.text = title
        toastDescTv.text = message

        if (type == Type.SUCCESS) {
            toastImg.setBackgroundResource(R.drawable.circle_accent)
            toastImg.setImageResource(R.drawable.ic_check)
        } else {
            toastImg.setBackgroundResource(R.drawable.circle_red)
            toastImg.setImageResource(R.drawable.ic_x_white)
        }

        toast.show()
    }

    fun show(context: Context, response: BaseResponse) {
        val title: String
        val type: Type

        if (response.isSuccessful) {
            title = context.getString(R.string.success)
            type = Type.SUCCESS
        } else {
            title = context.getString(R.string.error)
            type = Type.ERROR
        }

        show(context, title, response.message, type)
    }
}