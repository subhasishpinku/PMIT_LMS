package com.online.course.ui.frag

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.online.course.R
import com.online.course.databinding.FragSettingsFinancialBinding
import com.online.course.manager.*
import com.online.course.manager.Utils.toBoolean
import com.online.course.manager.adapter.ItemSpinnerAdapter
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SettingsFinancialPresenterImpl
import com.online.course.ui.widget.LoadingDialog
import java.io.File
import java.util.*


class SettingsFinancialFrag : Fragment(), View.OnClickListener, SettingsFrag.SaveCallback {

    private lateinit var mBinding: FragSettingsFinancialBinding
    private lateinit var mPresenter: Presenter.SettingsFinancialPresenter
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<String>
    private lateinit var mTakePicActivityResultLauncher: ActivityResultLauncher<Uri>
    private lateinit var mLoadingDialog: LoadingDialog

    private var mCertFile: File? = null
    private var mIdScanFile: File? = null
    private var mIdScanCameraPermission = false
    private var mIdScanStoragePermission = false
    private var mCertStoragePermission = false

    private var mFilesSaved = false
    private var mSettingsSaved = false


    private fun setAllSelectionToFalse() {
        mIdScanCameraPermission = false
        mIdScanStoragePermission = false
        mCertStoragePermission = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (mIdScanCameraPermission && permissions[Manifest.permission.CAMERA] == true
                ) {
                    takePic()
                } else if (mIdScanStoragePermission && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                    mActivityResultLauncher.launch("image/*")
                } else {
                    setAllSelectionToFalse()
                }
            }

        mActivityResultLauncher =
            registerForActivityResult(ResultContracts.SelectFile()) { uri ->
                if (uri != null) {
                    if (mCertStoragePermission) {
                        mCertFile = File(UriToPath.getPath(requireContext(), uri))
                        mBinding.settingsFinancialCertFileTv.text = mCertFile!!.name
                        mBinding.settingsFinancialCertFileTv.visibility = View.VISIBLE
                    } else {
                        mIdScanFile = File(UriToPath.getPath(requireContext(), uri))
                        mBinding.settingsFinancialIdFileTv.text = mIdScanFile!!.name
                        mBinding.settingsFinancialIdFileTv.visibility = View.VISIBLE
                    }
                }

                setAllSelectionToFalse()
            }

        mTakePicActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (!success) {
                    mIdScanFile = null
                }

                setAllSelectionToFalse()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSettingsFinancialBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = SettingsFinancialPresenterImpl(this)
        mPresenter.getAccountTypes()

        mBinding.settingsFinancialCameraIdBtn.setOnClickListener(this)
        mBinding.settingsFinancialUploadIdBtn.setOnClickListener(this)
        mBinding.settingsFinancialUploadCertBtn.setOnClickListener(this)

        initInfo()
    }

    private fun initInfo() {
        val loggedInUser = App.loggedInUser!!

        if (App.quickInfo != null && App.quickInfo!!.financialApproval.toBoolean()) {
            val params =
                mBinding.settingsFinancialUploadIdBtn.layoutParams as ConstraintLayout.LayoutParams
            params.marginStart = 0
            mBinding.settingsFinancialUploadIdBtn.requestLayout()

            mBinding.settingsFinancialUploadTv.text = getText(R.string.approved)
            mBinding.settingsFinancialUploadCertTv.text = getText(R.string.approved)

            mBinding.settingsFinancialCameraIdBtn.visibility = View.GONE

            mBinding.settingsFinancialUploadCameraImg.visibility = View.GONE
            mBinding.settingsFinancialUploadCertCameraImg.visibility = View.GONE

            mBinding.settingsFinancialAccountTypeSpinner.isEnabled = false
            mBinding.settingsFinancialIBANEdtx.isEnabled = false
            mBinding.settingsFinancialAccountIDEdtx.isEnabled = false
            mBinding.settingsFinancialAddressEdtx.isEnabled = false
            mBinding.settingsFinancialUploadIdBtn.isEnabled = false
            mBinding.settingsFinancialCameraIdBtn.isEnabled = false
            mBinding.settingsFinancialUploadCertBtn.isEnabled = false

        }else if (loggedInUser.accountId != null) {
            mBinding.settingsFinancialNotApprovedContainer.HeaderInfoImg.setBackgroundResource(R.drawable.circle_red)
            mBinding.settingsFinancialNotApprovedContainer.HeaderInfoImg.setImageResource(R.drawable.ic_danger)
            mBinding.settingsFinancialNotApprovedContainer.HeaderInfoTitleTv.text =
                getString(R.string.financial_approval)
            mBinding.settingsFinancialNotApprovedContainer.HeaderInfoDescTv.text =
                getString(R.string.financial_approval_not_desc)

            mBinding.settingsFinancialNotApprovedContainer.root.visibility = View.VISIBLE
        }

        if (loggedInUser.accountId != null) {
            mBinding.settingsFinancialAccountIDEdtx.setText(loggedInUser.accountId)
        }

        if (loggedInUser.iban != null) {
            mBinding.settingsFinancialIBANEdtx.setText(loggedInUser.iban)
        }

        if (loggedInUser.accountId != null) {
            mBinding.settingsFinancialAddressEdtx.setText(loggedInUser.address)
        }
    }

    private fun takePic() {
        val dir = File(requireContext().cacheDir, "Documents")
        if (!dir.exists())
            dir.mkdirs()

        mIdScanFile = File(dir, UUID.randomUUID().toString() + ".jpg")

        val uriForFile = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            mIdScanFile!!
        )

        mTakePicActivityResultLauncher.launch(uriForFile)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settingsFinancialCameraIdBtn -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mIdScanCameraPermission = true
                    mPermissionResultLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                } else {
                    takePic()
                }
            }

            R.id.settingsFinancialUploadIdBtn -> {
                mIdScanStoragePermission = true
                selectImg()
            }

            R.id.settingsFinancialUploadCertBtn -> {
                mCertStoragePermission = true
                selectImg()
            }
        }
    }

    private fun selectImg() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mPermissionResultLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        } else {
            mActivityResultLauncher.launch("image/*")
        }
    }

    override fun onSaveClicked() {
        val accountType = mBinding.settingsFinancialAccountTypeSpinner.selectedItem
        val iban = mBinding.settingsFinancialIBANEdtx.text.toString()
        val accountId = mBinding.settingsFinancialAccountIDEdtx.text.toString()
        val address = mBinding.settingsFinancialAddressEdtx.text.toString()

        if (accountType == null) {
            mBinding.settingsFinancialAccountTypeSpinner.requestFocus()
            return
        }

        if (iban.isEmpty()) {
            mBinding.settingsFinancialIBANEdtx.error = ""
            mBinding.settingsFinancialIBANEdtx.requestFocus()
            return
        }

        if (accountId.isEmpty()) {
            mBinding.settingsFinancialAccountIDEdtx.error = ""
            mBinding.settingsFinancialAccountIDEdtx.requestFocus()
            return
        }

        if (address.isEmpty()) {
            mBinding.settingsFinancialAddressEdtx.error = ""
            mBinding.settingsFinancialAddressEdtx.requestFocus()
            return
        }

        if (mIdScanFile == null || mCertFile == null) {
            val message = if (mIdScanFile == null) {
                getString(R.string.choose_your_id_scan_file)
            } else {
                getString(R.string.choose_your_id_certificate_file)
            }

            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                message,
                ToastMaker.Type.ERROR
            )

            return
        }

        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        val financialSettings = FinancialSettings()
        financialSettings.accountId = accountId
        financialSettings.accountType = accountType as String
        financialSettings.address = address
        financialSettings.iban = iban

        mPresenter.uploadFinancialSettings(financialSettings)
        mPresenter.uploadFinancialSettingsFiles(mIdScanFile!!, mCertFile!!)
    }

    override fun initTab() {
        (parentFragment as SettingsFrag).changeSaveBtnEnable(true)

        if (App.quickInfo != null && App.quickInfo!!.financialApproval.toBoolean()) {
            (parentFragment as SettingsFrag).changeSaveBtnVisibility(false)
        } else {
            (parentFragment as SettingsFrag).changeSaveBtnVisibility(true)
        }
    }

    fun onAccountTypesReceived(types: Count<String>) {
        val adapter = ItemSpinnerAdapter(requireContext(), types.items)
        mBinding.settingsFinancialAccountTypeSpinner.adapter = adapter

        val loggedInUser = App.loggedInUser!!
        if (loggedInUser.accountType != null) {
            for ((index, item) in types.items.withIndex()) {
                if (item == loggedInUser.accountType) {
                    mBinding.settingsFinancialAccountTypeSpinner.setSelection(index)
                    break
                }
            }
        }
//        mBinding.settingsFinancialAccountTypeSpinner.onItemSelectedListener = this
    }

    fun onFilesSaved(response: BaseResponse) {
        if (response.isSuccessful) {
            mFilesSaved = true
            checkIfBothAreSaved(response)
        } else {
            mLoadingDialog.dismiss()
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    private fun checkIfBothAreSaved(response: BaseResponse) {
        if (mFilesSaved && mSettingsSaved) {

            mLoadingDialog.dismiss()

            ToastMaker.show(
                requireContext(),
                getString(R.string.success),
                response.message,
                ToastMaker.Type.SUCCESS
            )

            mFilesSaved = false
            mSettingsSaved = false
        }
    }

    fun onSettingsSaved(
        response: BaseResponse,
        financialSettings: FinancialSettings
    ) {
        if (response.isSuccessful) {
            val loggedInUser = App.loggedInUser!!
            loggedInUser.accountId = financialSettings.accountId
            loggedInUser.accountType = financialSettings.accountType
            loggedInUser.address = financialSettings.address
            loggedInUser.iban = financialSettings.iban

            mSettingsSaved = true
            checkIfBothAreSaved(response)
        } else {
            mLoadingDialog.dismiss()
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}