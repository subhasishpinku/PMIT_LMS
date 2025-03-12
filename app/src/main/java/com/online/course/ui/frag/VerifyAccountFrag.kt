package com.online.course.ui.frag

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.online.course.R
import com.online.course.databinding.FragVerifyAccountBinding
import com.online.course.manager.App
import com.online.course.manager.ResponseStatus
import com.online.course.manager.ToastMaker
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.VerifyAccountPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.LoadingDialog


class VerifyAccountFrag : NetworkObserverFragment(), View.OnClickListener {

    private lateinit var mPresenter: Presenter.VerifyAccountPresenter
    private var mLoadingDialog: LoadingDialog? = null
    private lateinit var mBinding: FragVerifyAccountBinding
    private var mUserId: Int = 0
    private var mEmailSignUp: EmailSignUp? = null
    private var mMobileSignUp: MobileSignUp? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragVerifyAccountBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = VerifyAccountPresenterImpl(this)
        mUserId = requireArguments().getInt(App.USER_ID)
        val parcelable = requireArguments().getParcelable<Parcelable>(App.SIGN_UP)!!
        if (parcelable is EmailSignUp) {
            mEmailSignUp = parcelable
        } else {
            mMobileSignUp = parcelable as MobileSignUp
        }

        addTextWatcher(mBinding.verificationCodeFirstEdtx, mBinding.verificationCodeSecondEdtx)
        addTextWatcher(mBinding.verificationCodeSecondEdtx, mBinding.verificationCodeThirdEdtx)
        addTextWatcher(mBinding.verificationCodeThirdEdtx, mBinding.verificationCodeFourthEdtx)
        addTextWatcher(mBinding.verificationCodeFourthEdtx, mBinding.verificationCodeFifthEdtx)

        addTextWatcherForLastEdtx()
        mBinding.verifyAccountResendBtn.setOnClickListener(this)
        mBinding.verifyAccountBtn.setOnClickListener(this)
        mBinding.verificationCodeFirstEdtx.requestFocus()
    }


    private fun addTextWatcher(edtx: TextInputEditText, next: TextInputEditText) {
        edtx.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    edtx.clearFocus()
                    next.requestFocus()
                }
            }
        })
    }

    private fun addTextWatcherForLastEdtx() {
        mBinding.verificationCodeFifthEdtx.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (mBinding.verificationCodeFifthEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeFirstEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeSecondEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeThirdEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeFourthEdtx.text.toString().isNotEmpty()
                ) {
                    val code =
                        mBinding.verificationCodeFirstEdtx.text.toString() + mBinding.verificationCodeSecondEdtx.text.toString() +
                                mBinding.verificationCodeThirdEdtx.text.toString() + mBinding.verificationCodeFourthEdtx.text.toString() +
                                mBinding.verificationCodeFifthEdtx.text.toString()

                    mLoadingDialog?.dismiss()
                    mLoadingDialog = LoadingDialog()
                    mLoadingDialog!!.show(childFragmentManager, null)

                    val accountVerification = AccountVerification()
                    accountVerification.code = code
                    accountVerification.userId = mUserId

                    mPresenter.verifyAccount(accountVerification)
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.verifyAccountResendBtn -> {
                if (mEmailSignUp != null) {
                    mPresenter.signUp(mEmailSignUp!!)
                } else {
                    mPresenter.signUp(mMobileSignUp!!)
                }
            }
            R.id.verifyAccountBtn -> {
                if (mBinding.verificationCodeFifthEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeFirstEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeSecondEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeThirdEdtx.text.toString().isNotEmpty() &&
                    mBinding.verificationCodeFourthEdtx.text.toString().isNotEmpty()
                ) {
                    val code =
                        mBinding.verificationCodeFirstEdtx.text.toString() + mBinding.verificationCodeSecondEdtx.text.toString() +
                                mBinding.verificationCodeThirdEdtx.text.toString() + mBinding.verificationCodeFourthEdtx.text.toString() +
                                mBinding.verificationCodeFifthEdtx.text.toString()

                    mLoadingDialog?.dismiss()
                    mLoadingDialog = LoadingDialog()
                    mLoadingDialog!!.show(childFragmentManager, null)

                    val accountVerification = AccountVerification()
                    accountVerification.code = code
                    accountVerification.userId = mUserId

                    mPresenter.verifyAccount(accountVerification)
                }
            }
        }
    }

    fun onCodeSent(response: BaseResponse) {
        if (response.isSuccessful) {

            mBinding.verificationCodeFirstEdtx.setText("")
            mBinding.verificationCodeSecondEdtx.setText("")
            mBinding.verificationCodeThirdEdtx.setText("")
            mBinding.verificationCodeFourthEdtx.setText("")
            mBinding.verificationCodeFifthEdtx.setText("")

            mBinding.verificationCodeFirstEdtx.requestFocus()

        } else {
            onErrorOccured(response)
        }
    }

    fun onErrorOccured(error: BaseResponse?) {
        mLoadingDialog?.dismiss()

        if (error != null) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                error.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onVerificationReceived(data: Data<User>) {
        if (data.isSuccessful || data.status == ResponseStatus.AUTH_GO_TO_STEP3.value()) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(App.SHOULD_REGISTER, true)
            intent.putExtra(App.USER_ID, mUserId)
            startActivity(intent)
            activity?.finish()
        } else {
            onErrorOccured(data)
        }
    }
}