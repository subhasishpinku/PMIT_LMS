package com.online.course.ui.frag

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.view.isVisible
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.ChapterFileItem
import com.online.course.model.ChapterSessionItem
import com.online.course.model.ChapterTextItem
import kotlin.math.roundToInt


class OfflineCourseChapterItemFrag : BaseCourseChapterItem() {

    private var mChapterPosition = 0
    private var mChapterItemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOfflineMode = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mChapterPosition = requireArguments().getInt(App.CHAPTER_POSITION)
        mChapterItemPosition = requireArguments().getInt(App.CHAPTER_ITEM_POSITION)

        mBinding.courseChapterItemStartBtn.setOnClickListener(this)
        mBinding.courseChapterItemEndBtn.setOnClickListener(this)
        mBinding.courseChapterItemPlayBtn.setOnClickListener(this)

        mBinding.courseChapterItemCourseTv.text = mCourse.title

        when (val chapterItem = requireArguments().getParcelable<Parcelable>(App.ITEM)!!) {
            is ChapterSessionItem -> {
                mSessionItem = chapterItem
                initSessionItem()
            }

            is ChapterFileItem -> {
                mChapterPosition -= mCourse.sessionChapters.size
                mFileItem = chapterItem
                initFileItem()
            }

            is ChapterTextItem -> {
                mChapterPosition -= mCourse.sessionChapters.size + mCourse.filesChapters.size
                mTextItem = chapterItem
                initTextItem()
            }
        }
    }

    override fun canViewItem(): Boolean {
        return true
    }

    override fun showAccessDenied() {
    }

    override fun loadTextLesson(next: Boolean?) {
        val textChapterLessons = mCourse.textLessonChapters
        if (next != null) {
            val previousItem = mTextItem
            val previousChapterPosition = mChapterPosition
            val previousChapterItemPosition = mChapterItemPosition
            if (next) {
                if (mChapterItemPosition == textChapterLessons[mChapterPosition].textLessons.size - 1
                ) {
                    mChapterItemPosition = 0
                    mChapterPosition += 1
                } else {
                    mChapterItemPosition += 1
                }
            } else {
                if (mChapterItemPosition == 0
                ) {
                    mChapterItemPosition = 0
                    mChapterPosition -= 1
                } else {
                    mChapterItemPosition -= 1
                }
            }
            mTextItem = textChapterLessons[mChapterPosition].textLessons[mChapterItemPosition]
            if (mTextItem!!.accessibility == ChapterFileItem.Accessibility.PAID.value && !mCourse.hasUserBought) {
                mChapterPosition = previousChapterPosition
                mChapterItemPosition = previousChapterItemPosition
                mTextItem = previousItem
                showBuyAlert()
                return
            }
        }
        mBinding.courseChapterItemTitleTv.text = mTextItem!!.title
        mBinding.courseChapterItemDescTv.text = Utils.getTextAsHtml(mTextItem!!.content)
        mBinding.courseChapterItemSecondMarkValueTv.text = mTextItem!!.attachments.size.toString()
        mBinding.courseChapterItemThirdMarkValueTv.text =
            Utils.getDateFromTimestamp(mTextItem!!.createdAt)
        mBinding.courseChapterItemForthMarkValueTv.text =
            Utils.getDuration(requireContext(), mTextItem!!.studyTime)

        loadRelatedFiles()
        if (mChapterPosition == textChapterLessons.size - 1 &&
            mChapterItemPosition == textChapterLessons[mChapterPosition].textLessons.size - 1
        ) {
            mBinding.courseChapterItemEndBtn.visibility = View.GONE
        } else {
            mBinding.courseChapterItemEndBtn.visibility = View.VISIBLE
        }
        if (mChapterPosition == 0 && mChapterItemPosition == 0) {
            mBinding.courseChapterItemStartBtnContainer.visibility = View.GONE
        } else {
            mBinding.courseChapterItemStartBtnContainer.visibility = View.VISIBLE
        }
        if (!mBinding.courseChapterItemStartBtnContainer.isVisible || !mBinding.courseChapterItemEndBtn.isVisible) {
            setBtnMargin(0)
        } else {
            setBtnMargin(Utils.changeDpToPx(requireContext(), 8f).roundToInt())
        }
        initTTS()
    }

    override fun getTransactionFrag(): BaseCourseChapterItem {
        return OfflineCourseChapterItemFrag()
    }


}