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
import com.online.course.R
import com.online.course.databinding.DialogNewTicketBinding
import com.online.course.manager.*
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.NewTicketPresenterImpl
import java.io.File
import java.io.Serializable


class NewTicketDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private var mDepartmentId: Int? = null
    private var mCourseId: Int? = null

    private lateinit var mPresenter: Presenter.NewTicketPresenter
    private var mSelectedUri: Uri? = null
    private var mTicketId: Int = 0
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mActivityResultLauncherForFile: ActivityResultLauncher<String>
    private var mOnTickeChatSaved: ItemCallback<Conversation>? = null
    private var mOnTickeSaved: ItemCallback<Ticket>? = null
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mBinding: DialogNewTicketBinding
    private lateinit var mDepartmentPickerDialog: ItemPickerDialog<Department>
    private lateinit var mClassPickerDialog: ItemPickerDialog<Course>
    private lateinit var mType: Type

    private val mDepartmentCallback = object : ItemCallback<Department> {
        override fun onItem(department: Department, vararg args: Any) {
            mBinding.ticketDialogSelectClassDepartmentEdtx.setText(department.title)
            mBinding.ticketDialogSelectClassDepartmentEdtx.tag = department.id
            mDepartmentId = department.id
        }
    }

    private val mCourseCallback = object : ItemCallback<Course> {
        override fun onItem(course: Course, vararg args: Any) {
            mBinding.ticketDialogSelectClassDepartmentEdtx.setText(course.title)
            mBinding.ticketDialogSelectClassDepartmentEdtx.tag = course.id
            mCourseId = course.id
        }
    }

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisableBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun enableDisableBtn() {
        val subject = mBinding.ticketDialogSubjectEdtx.text.toString()
        val departmentClass = mBinding.ticketDialogSelectClassDepartmentEdtx.text.toString()
        val message = mBinding.ticketDialogMessageEdtx.text.toString()

        if (mTicketId > 0) {
            mBinding.ticketDialogSendBtn.isEnabled = message.isNotEmpty()
        } else {
            mBinding.ticketDialogSendBtn.isEnabled =
                subject.isNotEmpty() && departmentClass.isNotEmpty() && message.isNotEmpty()
        }
    }

    enum class Type(val value: String) : Serializable {
        PLATFORM_SUPPORT("platform_support"),
        COURSE_SUPPORT("course_support");
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
        mBinding = DialogNewTicketBinding.inflate(inflater, container, false)
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
        mType = requireArguments().getSerializable(App.SELECTION_TYPE) as Type

        if (requireArguments().getInt(App.ID) > 0) {
            mTicketId = requireArguments().getInt(App.ID)
            mBinding.ticketDialogSubjectEdtx.visibility = View.GONE
            mBinding.ticketDialogSelectClassDepartmentEdtx.visibility = View.GONE
            mBinding.ticketDialogHeaderTv.text = getString(R.string.reply_to_support)
            mBinding.ticketDialogSendBtn.text = getString(R.string.reply)
        }

        if (mType == Type.COURSE_SUPPORT) {
            mBinding.ticketDialogSelectClassDepartmentEdtx.hint = getString(R.string.select_class)
        } else {
            mBinding.ticketDialogSelectClassDepartmentEdtx.hint =
                getString(R.string.select_department)
        }

        mBinding.ticketDialogSubjectEdtx.addTextChangedListener(mTextWatcher)
        mBinding.ticketDialogMessageEdtx.addTextChangedListener(mTextWatcher)
        mBinding.ticketDialogSelectClassDepartmentEdtx.addTextChangedListener(mTextWatcher)

        mBinding.ticketDialogSendBtn.setOnClickListener(this)
        mBinding.ticketDialogSelectClassDepartmentEdtx.setOnClickListener(this)
        mBinding.ticketDialogAttachBtn.setOnClickListener(this)
        mBinding.ticketDialogCancelBtn.setOnClickListener(this)

        mPresenter = NewTicketPresenterImpl(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ticketDialogSendBtn -> {
                val title = mBinding.ticketDialogSubjectEdtx.text.toString()
                val message = mBinding.ticketDialogMessageEdtx.text.toString()

                mLoadingDialog = LoadingDialog()
                mLoadingDialog.show(childFragmentManager, null)
                var file: File? = null
                if (mSelectedUri != null) {
                    file = File(UriToPath.getPath(requireContext(), mSelectedUri))
                }

                if (mTicketId == 0) {
                    val ticket = Ticket()
                    ticket.title = title
                    ticket.message = message
                    ticket.type = mType.value

                    if (mDepartmentId != null) {
                        ticket.departmentId = mDepartmentId!!
                    } else if (mCourseId != null) {
                        ticket.courseId = mCourseId!!
                    }

                    mPresenter.addTicket(ticket, file)

                } else {
                    val conversation = Conversation()
                    conversation.message = message
                    conversation.id = mTicketId
                    mPresenter.addTicketChat(conversation, file)
                }
            }

            R.id.ticketDialogSelectClassDepartmentEdtx -> {
                val bundle = Bundle()

                if (mType == Type.COURSE_SUPPORT) {
                    bundle.putString(App.TITLE, getString(R.string.select_class))
                    mClassPickerDialog = ItemPickerDialog()
                    mClassPickerDialog.arguments = bundle
                    mClassPickerDialog.show(childFragmentManager, null)

                    mPresenter.getCourses()

                } else {
                    bundle.putString(App.TITLE, getString(R.string.select_department))
                    mDepartmentPickerDialog = ItemPickerDialog()
                    mDepartmentPickerDialog.arguments = bundle
                    mDepartmentPickerDialog.show(childFragmentManager, null)

                    mPresenter.getDepartments()
                }
            }

            R.id.ticketDialogAttachBtn -> {
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

            R.id.ticketDialogCancelBtn -> {
                dismissAllowingStateLoss()
            }
        }
    }

    fun setOnTicketChatSavedListener(onTicketChatSaved: ItemCallback<Conversation>) {
        mOnTickeChatSaved = onTicketChatSaved
    }

    fun onTicketConversationSaved(conversation: Conversation, response: Data<Response>) {
        if (onSaved(response)) {
            conversation.attachment = response.data?.attachment
            mOnTickeChatSaved?.onItem(conversation)
        }
    }

    fun onTicketConversationSaved(conversation: Conversation, response: BaseResponse) {
        if (onSaved(response)) {
            mOnTickeChatSaved?.onItem(conversation)
        }
    }

    fun onTicketSaved(ticket: Ticket, response: Data<Response>) {
        ticket.createdAt = System.currentTimeMillis() / 1000
        if (onSaved(response)) {
            ticket.attachment = response.data?.attachment
            mOnTickeSaved?.onItem(ticket)
        }
    }

    fun onTicketSaved(ticket: Ticket, response: BaseResponse) {
        ticket.createdAt = System.currentTimeMillis() / 1000
        if (onSaved(response)) {
            mOnTickeSaved?.onItem(ticket)
        }
    }

    fun onDepartmentsReceived(departments: List<Department>) {
        mDepartmentPickerDialog.onItemsReceived(departments, mDepartmentCallback)
    }

    fun onMyClassesReceived(myClasses: List<Course>) {
        mClassPickerDialog.onItemsReceived(myClasses, mCourseCallback)
    }

    fun setOnTicketAdded(callback: ItemCallback<Ticket>) {
        mOnTickeSaved = callback
    }

    fun onSaved(response: BaseResponse): Boolean {
        if (context == null) return response.isSuccessful

        mLoadingDialog.dismiss()
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

        return response.isSuccessful
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }
}