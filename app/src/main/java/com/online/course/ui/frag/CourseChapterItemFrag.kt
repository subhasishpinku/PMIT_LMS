package com.online.course.ui.frag

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.online.course.R
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.ChapterFileItem
import com.online.course.model.ChapterItem
import com.online.course.model.ChapterSessionItem
import com.online.course.model.ChapterTextItem
import kotlin.math.roundToInt


class CourseChapterItemFrag : BaseCourseChapterItem() {

    private var mTLPosition = 0
    private lateinit var mChapterItem: ChapterItem

    private val mSessionCallback = object : ItemCallback<ChapterSessionItem> {
        override fun onItem(item: ChapterSessionItem, vararg args: Any) {
            hideLoadingDialog()
            mSessionItem = item
            initSessionItem()
        }
    }

    private val mFileCallback = object : ItemCallback<ChapterFileItem> {
        override fun onItem(item: ChapterFileItem, vararg args: Any) {
            hideLoadingDialog()
            mFileItem = item
            initFileItem()
        }
    }

    private val mTextLessonCallback = object : ItemCallback<List<ChapterTextItem>> {
        override fun onItem(items: List<ChapterTextItem>, vararg args: Any) {
            hideLoadingDialog()
            mTextLessons = items
            mTextItem = getCurrentTextItem(items)
            initTextItem()
        }
    }

    private fun getCurrentTextItem(items: List<ChapterTextItem>): ChapterTextItem? {
        for (item in items) {
            if (item.id == mChapterItem.id) {
                return item
            }

            mTLPosition += 1
        }
        return null
    }

    override fun canViewItem(): Boolean {
        return mChapterItem.can.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mChapterItem = requireArguments().getParcelable(App.ITEM)!!
        loadChapter(mChapterItem)
    }

    private fun loadChapter(chapterItem: ChapterItem) {
        when (chapterItem.type) {
            ChapterItem.Type.SESSION.value -> {
                mPresenter.getSessionItemDetails(chapterItem.link, mSessionCallback)
            }
            ChapterItem.Type.FILE.value -> {
                mPresenter.getFileItemDetails(chapterItem.link, mFileCallback)
            }
            else -> {
                mPresenter.getTextLessons(chapterItem.id, mTextLessonCallback)
            }
        }
    }

    override fun loadTextLesson(next: Boolean?) {

        if (next != null) {
            val previousItem = mTextItem
            val previousItemPosition = mTLPosition

            if (next) {
                mTLPosition += 1
            } else {
                mTLPosition -= 1
            }

            mTextItem = mTextLessons!![mTLPosition]

            if (mTextItem!!.accessibility == ChapterFileItem.Accessibility.PAID.value &&
                !mCourse.hasUserBought
            ) {
                mTLPosition = previousItemPosition
                mTextItem = previousItem
                showBuyAlert()
                return
            }
        }

        loadTextLessonMarks()
        mBinding.courseChapterItemDescTv.text = Utils.getTextAsHtml(mTextItem!!.content)

        showReadSwitch(mTextItem!!.authHasRead)

        loadRelatedFiles()

        if (mTLPosition == mTextLessons!!.size - 1
        ) {
            mBinding.courseChapterItemEndBtn.visibility = View.GONE
        } else {
            mBinding.courseChapterItemEndBtn.visibility = View.VISIBLE
        }

        if (mTLPosition == 0) {
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
        return CourseChapterItemFrag()
    }

    override fun showAccessDenied() {
        val deniedView = mBinding.courseChapterItemAccessDeniedView

        val firstMarkImg = mBinding.courseChapterItemFirstMarkImg
        val secondMarkImg = mBinding.courseChapterItemSecondMarkImg

        val markImg1 = firstMarkImg.layoutParams as ConstraintLayout.LayoutParams
        val markImg2 = secondMarkImg.layoutParams as ConstraintLayout.LayoutParams

        firstMarkImg.requestLayout()
        secondMarkImg.requestLayout()

        markImg1.topToBottom = R.id.course_chapter_item_access_denied_view
        markImg2.topToBottom = R.id.course_chapter_item_access_denied_view

        deniedView.emptyStateImg.setImageResource(R.drawable.access_denied)
        deniedView.emptyStatetitleTv.setText(R.string.access_denied)
        deniedView.emptyStateDescTV.text = mChapterItem.viewError
        deniedView.root.visibility = View.VISIBLE
    }

}