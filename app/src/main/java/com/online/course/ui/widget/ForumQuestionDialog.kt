package com.online.course.ui.widget

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.online.course.R
import com.online.course.databinding.DialogForumQuestionBinding
import com.online.course.manager.App
import com.online.course.manager.ResultContracts
import com.online.course.manager.ToastMaker
import com.online.course.manager.UriToPath
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.ForumItem
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ForumQuestionPresenterImpl
import java.io.File


class ForumQuestionDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mPresenter: Presenter.ForumQuestionPresenter
    private lateinit var mBinding: DialogForumQuestionBinding
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mActivityResultLauncherForFile: ActivityResultLauncher<String>
    private lateinit var mLoadingDialog: LoadingDialog

    private var mCourseId = 0
    private var mForumItem: ForumItem? = null
    private var mSelectedUri: Uri? = null
    private var mCallback: ItemCallback<Any>? = null
    private var mPreviousDialog: BottomSheetDialogFragment? = null

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisalbleBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisalbleBtn()
        }

    }

    private fun enableDisalbleBtn() {
        val title = mBinding.forumQuestionTitleEdtx.text.toString()
        val desc = mBinding.forumQuestionDescEdtx.text.toString()

        mBinding.forumQuestionSendBtn.isEnabled = title.isNotEmpty() && desc.isNotEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        mActivityResultLauncherForFile =
            registerForActivityResult(ResultContracts.SelectFile()) { uri ->
                if (uri != null) {
                    mSelectedUri = uri
                }
            }

        mPermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                ) {
                    mActivityResultLauncherForFile.launch("*/*")
                }
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
        mBinding = DialogForumQuestionBinding.inflate(inflater, container, false)
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
        mForumItem = requireArguments().getParcelable(App.ITEM)
        mCourseId = requireArguments().getInt(App.ID)

        mPresenter = ForumQuestionPresenterImpl(this)
        initUI()
    }

    private fun initUI() {
        mBinding.forumQuestionTitleEdtx.addTextChangedListener(mTextWatcher)
        mBinding.forumQuestionDescEdtx.addTextChangedListener(mTextWatcher)
        mBinding.forumQuestionSendBtn.setOnClickListener(this)
        mBinding.forumQuestionAttachBtn.setOnClickListener(this)
        mBinding.forumQuestionCancelBtn.setOnClickListener(this)

        if (mForumItem != null) {
            mBinding.forumQuestionTitleEdtx.setText(mForumItem!!.title)
            mBinding.forumQuestionDescEdtx.setText(mForumItem!!.description)
            mBinding.forumQuestionSendBtn.text = getString(R.string.edit)
            mBinding.forumQuestionHeaderTv.text = getString(R.string.edit_question)
        }
    }

    fun setCallback(callback: ItemCallback<Any>, previousDialog: BottomSheetDialogFragment? = null) {
        mPreviousDialog = previousDialog
        mCallback = callback
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.forum_question_cancel_btn -> {
                dismiss()
            }

            R.id.forum_question_send_btn -> {
                val title = mBinding.forumQuestionTitleEdtx.text.toString()
                val desc = mBinding.forumQuestionDescEdtx.text.toString()

                mLoadingDialog = LoadingDialog()
                mLoadingDialog.show(childFragmentManager, null)
                var file: File? = null
                if (mSelectedUri != null) {
                    file = File(UriToPath.getPath(requireContext(), mSelectedUri))
                }

                if (mForumItem == null) {
                    mForumItem = ForumItem()
                    mForumItem!!.title = title
                    mForumItem!!.description = desc
                    mForumItem!!.id = mCourseId

                    mPresenter.sendQuestion(mForumItem!!.id, mForumItem!!, file)

                } else {
                    mForumItem!!.title = title
                    mForumItem!!.description = desc

                    mPresenter.editQuestion(mForumItem!!, file)
                }
            }

            R.id.forum_question_attach_btn -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mPermissionResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                } else {
                    mActivityResultLauncherForFile.launch("*/*")
                }
            }
        }
    }

    fun onResponse(response: BaseResponse) {
        if (response.isSuccessful) {
            mCallback?.onItem(Any())
            mPreviousDialog?.dismiss()
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