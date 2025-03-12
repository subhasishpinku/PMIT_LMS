package com.online.course.ui.frag

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.online.course.R
import com.online.course.databinding.FragSettingsGeneralBinding
import com.online.course.manager.*
import com.online.course.manager.Utils.toBoolean
import com.online.course.manager.Utils.toInt
import com.online.course.manager.db.AppDb
import com.online.course.manager.net.ApiService
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SettingsGeneralPresenterImpl
import com.online.course.ui.BaseActivity
import com.online.course.ui.MainActivity


class SettingsGeneralFrag : Fragment(), View.OnClickListener,
    SelectionDialog.ItemSelection<Language>, SettingsFrag.SaveCallback {

    private lateinit var mBinding: FragSettingsGeneralBinding
    private lateinit var mPresenter: Presenter.SettingsGeneralPresenter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSettingsGeneralBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = SettingsGeneralPresenterImpl(this)

        val user = App.loggedInUser!!
        mBinding.settingsUserNameEdtx.setText(user.name)

        val prefManager = PrefManager(requireContext())
        mBinding.settingsUserLngTv.text = prefManager.language!!.name

        if (user.referral != null) {
            mBinding.settingsUserReferralTv.text = user.referral
            setReferralCopyListener(user.referral!!)
        } else {
            mBinding.settingsUserReferralKeyTv.visibility = View.GONE
            mBinding.settingsUserReferralTv.visibility = View.GONE
        }

        if (user.email != null) {
            mBinding.settingsUserEmailTv.text = user.email
        } else {
            mBinding.settingsUserEmailKeyTv.visibility = View.GONE
            mBinding.settingsUserEmailTv.visibility = View.GONE
        }

        if (user.mobile != null) {
            mBinding.settingsUserPhoneNumberTv.text = user.mobile
        } else {
            mBinding.settingsUserPhoneNumberKeyTv.visibility = View.GONE
            mBinding.settingsUserPhoneNumberTv.visibility = View.GONE
        }

        mBinding.settingsJoinNewsletterSwitch.isChecked = user.hasNewsLetter
        mBinding.settingsProfileMessagesSwitch.isChecked = user.publicMessage.toBoolean()
        mBinding.settingsUserLngTv.setOnClickListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setReferralCopyListener(referral: String) {
        mBinding.settingsUserReferralTv.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if ((mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_RIGHT] != null && event.rawX >= mBinding.settingsUserReferralTv.right - mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) ||
                    (mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_LEFT] != null && event.rawX <= (mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_LEFT].bounds.width()))
                ) {
                    Utils.copyToClipbaord(
                        requireContext(),
                        getString(R.string.referral_url),
                        referral
                    )
                    return@OnTouchListener true
                }
            }
            v?.performClick()
            false
        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settingsUserLngTv -> {
                val bundle = Bundle()
                bundle.putSerializable(App.SELECTION_TYPE, SelectionDialog.Selection.Language)

                val instance = SelectionDialog.getInstance<Language>()
                instance.setOnItemSelected(this)
                instance.arguments = bundle
                instance.show(childFragmentManager, null)
            }
        }
    }

    override fun onItemSelected(language: Language) {
        if (BaseActivity.language!!.code != language.code) {
            mBinding.settingsUserLngTv.text = language.name

            val prefManager = PrefManager(requireContext())
            prefManager.language = language
            BaseActivity.language = language

            ApiService.createApiServiceWithLocale(prefManager.language!!.code)

            startActivity(Intent(requireContext(), MainActivity::class.java))
            activity?.finish()
        }
    }

    override fun onSaveClicked() {
        val name = mBinding.settingsUserNameEdtx.text.toString()
        if (name.isEmpty()) {
            mBinding.settingsUserNameEdtx.error = ""
            return
        }

        val changeSettings = UserChangeSettings()
        changeSettings.name = name
        changeSettings.hasNewsLetter = mBinding.settingsJoinNewsletterSwitch.isChecked
        changeSettings.publicMessage = mBinding.settingsProfileMessagesSwitch.isChecked.toInt()

        mPresenter.changeProfileSettings(changeSettings)
    }

    override fun initTab() {
        (parentFragment as SettingsFrag).changeSaveBtnEnable(true)
        (parentFragment as SettingsFrag).changeSaveBtnVisibility(true)
    }

    fun onSettingsChanged(
        response: BaseResponse,
        changeSettings: UserChangeSettings
    ) {
        if (context == null) return

        if (response.isSuccessful) {
            val loggedInUser = App.loggedInUser!!
            loggedInUser.name = changeSettings.name
            loggedInUser.hasNewsLetter = changeSettings.hasNewsLetter
            loggedInUser.publicMessage = changeSettings.publicMessage
            App.loggedInUser = loggedInUser
            App.saveToLocal(
                Gson().toJson(loggedInUser, UserProfile::class.java),
                requireContext(),
                AppDb.DataType.USER
            )

            (activity as MainActivity).initUserInfo()

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
}