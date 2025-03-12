package com.online.course.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.online.course.R
import com.online.course.databinding.DialogNewMessageBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.Message
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.NewMessagePresenterImpl


class NewMessageDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mPresenter: Presenter.NewMessagePresenter
    private lateinit var mBinding: DialogNewMessageBinding
    private var mUserId = 0

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisalbleBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun enableDisalbleBtn() {
        val title = mBinding.newMessageTitleEdtx.text.toString()
        val email = mBinding.newMessageEmailEdtx.text.toString()
        val meesage = mBinding.newMessageEdtx.text.toString()

        mBinding.newMessageSendBtn.isEnabled =
            title.isNotEmpty() && email.isNotEmpty() && meesage.isNotEmpty()
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogNewMessageBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        mUserId = requireArguments().getInt(App.USER_ID)

        mBinding.newMessageTitleEdtx.addTextChangedListener(mTextWatcher)
        mBinding.newMessageEmailEdtx.addTextChangedListener(mTextWatcher)
        mBinding.newMessageEdtx.addTextChangedListener(mTextWatcher)

        mBinding.newMessageSendBtn.setOnClickListener(this)
        mBinding.newMessageCancelBtn.setOnClickListener(this)

        mPresenter = NewMessagePresenterImpl(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.new_message_cancel_btn -> {
                dismiss()
            }

            R.id.new_message_send_btn -> {
                val title = mBinding.newMessageTitleEdtx.text.toString()
                val email = mBinding.newMessageEmailEdtx.text.toString()
                val meesage = mBinding.newMessageEdtx.text.toString()

                val messageObj = Message()
                messageObj.title = title
                messageObj.email = email
                messageObj.message = meesage

                mPresenter.addMessage(mUserId, messageObj)
            }
        }
    }

    fun onMessageAdded(response: BaseResponse) {
        if (context == null) return

        if (response.isSuccessful) {
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
}