package com.online.course.ui.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.FragSettingsSecurityBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.ToastMaker
import com.online.course.manager.db.AppDb
import com.online.course.manager.net.ApiService
import com.online.course.model.ChangePassword
import com.online.course.model.Data
import com.online.course.model.Response
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SettingsSecurityPresenterImpl

class SettingsSecurityFrag : Fragment(), SettingsFrag.SaveCallback {

    private lateinit var mBinding: FragSettingsSecurityBinding
    private lateinit var mPresenter: Presenter.SettingsSecurityPresenter

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisableSaveBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun enableDisableSaveBtn() {
        val currentPassword = mBinding.settingsSecurityCurrentPassEdtx.text.toString()
        val newPassword = mBinding.settingsSecurityNewPassEdtx.text.toString()
        val retypeNewPassword = mBinding.settingsSecurityRetypeNewPassEdtx.text.toString()

        val enable = (currentPassword.isNotEmpty() && newPassword.isNotEmpty()
                && retypeNewPassword.isNotEmpty() && newPassword == retypeNewPassword)

        (parentFragment as SettingsFrag).changeSaveBtnEnable(enable)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSettingsSecurityBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        if (BuildVars.LOGS_ENABLED) {
            Log.d(SettingsSecurityFrag::class.java.name, "init")
        }

        mPresenter = SettingsSecurityPresenterImpl(this)

        mBinding.settingsSecurityCurrentPassEdtx.addTextChangedListener(mTextWatcher)
        mBinding.settingsSecurityNewPassEdtx.addTextChangedListener(mTextWatcher)
        mBinding.settingsSecurityRetypeNewPassEdtx.addTextChangedListener(mTextWatcher)
    }

    fun onPasswordChanges(response: Data<Response>) {
        if (context == null) return

        if (response.isSuccessful) {
            val token = response.data!!.token
            App.saveToLocal(token, requireContext(), AppDb.DataType.TOKEN)
            ApiService.createAuthorizedApiService(token)

            mBinding.settingsSecurityCurrentPassEdtx.setText("")
            mBinding.settingsSecurityNewPassEdtx.setText("")
            mBinding.settingsSecurityRetypeNewPassEdtx.setText("")
            ToastMaker.show(
                requireContext(),
                getString(R.string.success),
                response.message,
                ToastMaker.Type.SUCCESS
            )
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    override fun onSaveClicked() {
        val currentPassword = mBinding.settingsSecurityCurrentPassEdtx.text.toString()
        val newPassword = mBinding.settingsSecurityNewPassEdtx.text.toString()

        val changePassword = ChangePassword()
        changePassword.currentPassword = currentPassword
        changePassword.newPassword = newPassword

        mPresenter.changePassword(changePassword)
    }

    override fun initTab() {
        (parentFragment as SettingsFrag).changeSaveBtnEnable(false)
        (parentFragment as SettingsFrag).changeSaveBtnVisibility(true)
    }
}