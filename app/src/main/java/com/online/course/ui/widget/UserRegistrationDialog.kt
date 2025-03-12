package com.online.course.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.online.course.R
import com.online.course.databinding.DialogUserRegistrationBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.db.AppDb
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.presenterImpl.UserRegistrationPresenterImpl
import com.online.course.ui.MainActivity


class UserRegistrationDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    //    private lateinit var mToken: String
    private lateinit var mPresenter: Presenter.UserRegistrationPresenter
    private lateinit var mBinding: DialogUserRegistrationBinding
    private var mUser: ThirdPartyLogin? = null
    private var mUserId = 0

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableContinueBtn()
        }
    }

    private fun enableDisableContinueBtn() {
        val name = mBinding.userRegistrationNameEdtx.text.toString()
        val continueBtn = mBinding.userRegistrationContinueBtn

        if (name.isNotEmpty()) {
            if (!continueBtn.isEnabled) {
                continueBtn.isEnabled = true
            }

        } else if (continueBtn.isEnabled) {
            continueBtn.isEnabled = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogUserRegistrationBinding.inflate(inflater, container, false)
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
        mPresenter = UserRegistrationPresenterImpl(this)

        mBinding.userRegistrationNameEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.userRegistrationContinueBtn.setOnClickListener(this)

        mUser = requireArguments().getParcelable(App.USER)
        mUserId = requireArguments().getInt(App.USER_ID)

        mBinding.userRegistrationNameEdtx.requestFocus()

        if (mUser != null) {
            mBinding.userRegistrationNameEdtx.setText(mUser!!.name)
            mBinding.userRegistrationNameEdtx.setSelection(mUser!!.name.length)
            mUserId = mUser!!.userId
        }
//        Utils.showKeyboard(requireContext())
    }

    override fun onClick(v: View) {
        val name = mBinding.userRegistrationNameEdtx.text.toString()
        val referralCode = mBinding.userRegistrationReferralEdtx.text.toString()

        if (name.isEmpty()) {
            mBinding.userRegistrationNameEdtx.error = ""
            return
        }

        val user = User()
        user.userId = mUserId
        user.name = name
        if (referralCode.isNotEmpty()) {
            user.referral = referralCode
        }

        mPresenter.register(user)
    }

    fun onErrorOccured(error: BaseResponse) {
        ToastMaker.show(
            requireContext(),
            getString(R.string.error),
            error.message,
            ToastMaker.Type.ERROR
        )
    }

    fun onRegistrationSaved(
        response: Data<Response>,
        user: User
    ) {
        if (response.isSuccessful) {
            val commonPresenter = CommonApiPresenterImpl.getInstance()
            commonPresenter.getUserInfo(user.userId, object : ItemCallback<UserProfile> {
                override fun onItem(item: UserProfile, vararg args: Any) {
                    App.saveToLocal(
                        Gson().toJson(item),
                        requireContext(),
                        AppDb.DataType.USER
                    )
                    App.saveToLocal(response.data!!.token, requireContext(), AppDb.DataType.TOKEN)
                    App.loggedInUser = item
                    ApiService.createAuthorizedApiService(response.data!!.token)
                    (activity as MainActivity).onUserRegistered()
                    dismiss()
                }
            })
        } else {
            onErrorOccured(response)
        }
    }
}