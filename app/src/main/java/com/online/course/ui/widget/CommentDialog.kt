package com.online.course.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.online.course.R
import com.online.course.databinding.DialogBaseInputBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.adapter.ItemSpinnerAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.Comment
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ReportReplyCommentPresenterImpl
import java.io.Serializable

class CommentDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {
    private lateinit var mBinding: DialogBaseInputBinding
    private lateinit var mType: Type
    private lateinit var mPresenter: Presenter.ReportReplyCommentPresenter

    private var mCallback: ItemCallback<Comment>? = null
    private var mId = 0
    private var mItem: String? = null

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableBtn()
        }
    }

    private fun enableDisableBtn() {
        val input = mBinding.baseInputEdtx.text.toString()
        val resaonAdapter = mBinding.baseInputReasonSpinner.adapter

        if (mType == Type.REPORT_COURSE) {
            mBinding.baseInputBtn.isEnabled = input.isNotEmpty() && resaonAdapter != null
        } else {
            mBinding.baseInputBtn.isEnabled = input.isNotEmpty()
        }
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
        mBinding = DialogBaseInputBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = ReportReplyCommentPresenterImpl(this)
        mBinding.baseInputEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.baseInputBtn.isEnabled = false

        mType = requireArguments().getSerializable(App.SELECTION_TYPE) as Type
        mItem = requireArguments().getString(App.ITEM)
        mId = requireArguments().getInt(App.ID)

        when (mType) {
            Type.REPLY -> {
                mBinding.baseInputBtn.text = getString(R.string.submit_comment)
                mBinding.baseInputHeaderTv.text = getString(R.string.reply_to_comment)
            }
            Type.COMMENT_BLOG, Type.COMMENT_COURSE -> {
                mBinding.baseInputBtn.text = getString(R.string.submit_comment)
                mBinding.baseInputHeaderTv.text = getString(R.string.comment)
            }

            Type.EDIT -> {
                val comment = requireArguments().getParcelable<Comment>(App.COMMENT)!!
                mBinding.baseInputBtn.text = getString(R.string.save)
                mBinding.baseInputHeaderTv.text = getString(R.string.edit_comment)
                mBinding.baseInputEdtx.setText(comment.comment)
            }

            Type.REPORT_COURSE -> {
                mPresenter.getReasons()
                mBinding.baseInputReasonContainer.visibility = View.VISIBLE
                mBinding.baseInputBtn.text = getString(R.string.report)
                mBinding.baseInputHeaderTv.text = getString(R.string.message_to_reviewer)
            }

            else -> {
                mBinding.baseInputBtn.text = getString(R.string.report)
                mBinding.baseInputHeaderTv.text = getString(R.string.message_to_reviewer)
            }
        }

        mBinding.baseInputCancelBtn.setOnClickListener(this)
        mBinding.baseInputBtn.setOnClickListener(this)
    }

    fun setOnCommentSavedListener(callback: ItemCallback<Comment>) {
        mCallback = callback
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.baseInputCancelBtn -> {
                dismiss()
            }

            R.id.baseInputBtn -> {
                val text = mBinding.baseInputEdtx.text.toString()
                val comment = Comment()
                when (mType) {
                    Type.REPLY -> {
                        comment.id = mId
                        comment.reply = text
                        mPresenter.reply(comment)
                    }
                    Type.COMMENT_BLOG, Type.COMMENT_COURSE -> {
                        comment.itemId = mId
                        comment.itemName = mItem
                        comment.comment = text
                        mPresenter.comment(comment)
                    }
                    Type.EDIT -> {
                        comment.id = mId
                        comment.comment = text
                        mPresenter.editComment(comment)
                    }

                    Type.REPORT_COURSE -> {
                        val reason = mBinding.baseInputReasonSpinner.selectedItem as String

                        comment.id = mId
                        comment.reason = reason
                        comment.message = text
                        mPresenter.reportCourse(comment)
                    }

                    else -> {
                        comment.id = mId
                        comment.message = text
                        mPresenter.reportComment(comment)
                    }
                }
            }
        }
    }

    fun onRsp(
        response: BaseResponse,
        comment: Comment
    ) {
        if (response.isSuccessful) {
            if (mType != Type.REPORT_COMMENT && mType != Type.REPORT_COURSE) {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.success),
                    getString(R.string.your_comment_will_be_published_after_review),
                    ToastMaker.Type.SUCCESS
                )
            } else {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.success),
                    getString(R.string.your_report_will_be_reviewd_by_our_moderators),
                    ToastMaker.Type.SUCCESS
                )
            }

            comment.createdAt = System.currentTimeMillis() / 1000
            mCallback?.onItem(comment)
            dismiss()
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onReasonsReceived(reasons: Collection<String>) {
        val adapter = ItemSpinnerAdapter(requireContext(), reasons.toList())
        mBinding.baseInputReasonSpinner.adapter = adapter
        enableDisableBtn()
    }


    enum class Type : Serializable {
        REPORT_COMMENT,
        REPORT_COURSE,
        REPLY,
        EDIT,
        COMMENT_BLOG,
        COMMENT_COURSE
    }
}