package com.online.course.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.online.course.R
import com.online.course.databinding.DialogReviewBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.Review
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CourseReviewPresenterImpl

class CourseReviewDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogReviewBinding
    private lateinit var mPresenter: Presenter.CourseReviewPresenter
    private lateinit var mReview : Review
    private var mReviewAdded: ItemCallback<Review>? = null

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisableBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private val mRatingChangeListener =
        SimpleRatingBar.OnRatingBarChangeListener { _, _, _ ->
            enableDisableBtn()
        }

    private fun enableDisableBtn() {
        val message = mBinding.reviewMessageEdtx.text.toString()
        val contentQualityRating = mBinding.reviewContentQualityRatingBar.rating
        val instructorSkillsRating = mBinding.reviewInstructorSkillsRatingBar.rating
        val purchaseWorthRating = mBinding.reviewPurchaseWorthRatingBar.rating
        val supportQualityRating = mBinding.reviewSupportQualityRatingBar.rating

        mBinding.reviewSendBtn.isEnabled = message.isNotEmpty() && contentQualityRating >= 1 &&
                instructorSkillsRating >= 1 && purchaseWorthRating >= 1 && supportQualityRating >= 1
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogReviewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mReview = requireArguments().getParcelable(App.ITEM)!!

        mPresenter = CourseReviewPresenterImpl(this)

        initUI()
    }

    private fun initUI() {
        mBinding.reviewCancelBtn.setOnClickListener(this)
        mBinding.reviewMessageEdtx.addTextChangedListener(mTextWatcher)
        mBinding.reviewSendBtn.setOnClickListener(this)
        mBinding.reviewContentQualityRatingBar.setOnRatingBarChangeListener(mRatingChangeListener)
        mBinding.reviewInstructorSkillsRatingBar.setOnRatingBarChangeListener(mRatingChangeListener)
        mBinding.reviewPurchaseWorthRatingBar.setOnRatingBarChangeListener(mRatingChangeListener)
        mBinding.reviewSupportQualityRatingBar.setOnRatingBarChangeListener(mRatingChangeListener)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.review_cancel_btn -> {
                dismiss()
            }

            R.id.review_send_btn -> {
                val message = mBinding.reviewMessageEdtx.text.toString()
                val contentQualityRating = mBinding.reviewContentQualityRatingBar.rating
                val instructorSkillsRating = mBinding.reviewInstructorSkillsRatingBar.rating
                val purchaseWorthRating = mBinding.reviewPurchaseWorthRatingBar.rating
                val supportQualityRating = mBinding.reviewSupportQualityRatingBar.rating

                mReview.description = message
                mReview.contentQuality = contentQualityRating
                mReview.instructorSkills = instructorSkillsRating
                mReview.purchaseWorth = purchaseWorthRating
                mReview.supportQuality = supportQualityRating

                mPresenter.addReview(mReview)
            }
        }
    }

    fun onReviewSaved(response: BaseResponse, review: Review) {
        if (response.isSuccessful) {
            review.createdAt = System.currentTimeMillis() / 1000
            mReviewAdded?.onItem(review)
            if (context == null) return
            dismiss()
        } else {
            if (context == null) return
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun setOnReviewSavedListener(reviewAdded: ItemCallback<Review>) {
        mReviewAdded = reviewAdded
    }
}