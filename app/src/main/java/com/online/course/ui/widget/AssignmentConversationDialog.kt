package com.online.course.ui.widget

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.online.course.R
import com.online.course.databinding.DialogForumQuestionBinding
import com.online.course.manager.*
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.Assignment
import com.online.course.model.BaseResponse
import com.online.course.model.Conversation
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.AssignmentNewConversationPresenterImpl
import java.io.File


class AssignmentConversationDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mPresenter: Presenter.AssignmentNewConversationPresenter
    private lateinit var mBinding: DialogForumQuestionBinding
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mActivityResultLauncherForFile: ActivityResultLauncher<String>
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mPermissionManager: PermissionManager
    private lateinit var mAssignment: Assignment

    private var mSelectedUri: Uri? = null
    private var mCallback: ItemCallback<Any>? = null
    private var mIsInstructorType = false

    companion object {
        val PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

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
        val desc = mBinding.forumQuestionDescEdtx.text.toString()
        mBinding.forumQuestionSendBtn.isEnabled = desc.isNotEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        mActivityResultLauncherForFile =
            registerForActivityResult(ResultContracts.SelectFile()) { uri ->
                if (uri != null) {
                    mSelectedUri = uri
                    enableDisalbleBtn()
                }
            }

        mPermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (mPermissionManager.isGranted()
                ) {
                    mActivityResultLauncherForFile.launch("*/*")
                }
            }

        mPermissionManager =
            PermissionManager(requireContext(), PERMISSIONS, mPermissionResultLauncher)
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
        mAssignment = requireArguments().getParcelable(App.ITEM)!!
        mIsInstructorType = requireArguments().getBoolean(App.INSTRUCTOR_TYPE)

        mPresenter = AssignmentNewConversationPresenterImpl(this)
        initUI()
    }

    private fun initUI() {
        mBinding.forumQuestionHeaderTv.setText(R.string.assignment_submission)
        mBinding.forumQuestionTitleEdtx.setHint(R.string.file_title_optional)

        mBinding.forumQuestionDescEdtx.addTextChangedListener(mTextWatcher)
        mBinding.forumQuestionSendBtn.setOnClickListener(this)
        mBinding.forumQuestionAttachBtn.setOnClickListener(this)
        mBinding.forumQuestionCancelBtn.setOnClickListener(this)
    }

    fun setCallback(
        callback: ItemCallback<Any>,
    ) {
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

                val conversation = Conversation()
                conversation.fileTitle = title
                conversation.message = desc
                if (mIsInstructorType && mAssignment.student != null) {
                    conversation.studentId = mAssignment.student!!.id
                }

                mPresenter.saveConversation(mAssignment.id, conversation, file)
            }

            R.id.forum_question_attach_btn -> {
                if (mPermissionManager.isGranted()) {
                    mActivityResultLauncherForFile.launch("*/*")
                } else {
                    mPermissionManager.request()
                }
            }
        }
    }

    fun onResponse(response: BaseResponse) {
        mLoadingDialog.dismiss()

        if (response.isSuccessful) {
            mCallback?.onItem(Any())
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