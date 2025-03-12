package com.online.course.manager

import android.animation.ObjectAnimator

import android.widget.TextView


object ExpandableTextViewHelper {

    fun expandTextView(tv: TextView) {
//        val duration = (tv.lineCount - tv.maxLines) * 10
        val animation = ObjectAnimator.ofInt(tv, "maxLines", 100)
        animation.setDuration(600).start()
    }

    fun collapseTextView(tv: TextView, numLines: Int) {
//        val duration = (tv.lineCount - numLines) * 10
        val animation = ObjectAnimator.ofInt(tv, "maxLines", numLines)
        animation.setDuration(400).start()
    }
}