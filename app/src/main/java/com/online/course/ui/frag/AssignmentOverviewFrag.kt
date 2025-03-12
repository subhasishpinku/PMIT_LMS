package com.online.course.ui.frag

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.course.R
import com.online.course.databinding.FragAssignmentOverviewBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.adapter.AttachmentRvAdapter
import com.online.course.manager.adapter.StudentRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Assignment
import com.online.course.model.Attachment
import com.online.course.model.ToolbarOptions
import com.online.course.model.User
import com.online.course.presenterImpl.AssignmentOverviewPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.LoadingDialog
import com.online.course.ui.widget.ProgressiveLoadingDialog

class AssignmentOverviewFrag : NetworkObserverFragment(), View.OnClickListener,
    OnItemClickListener {

    private lateinit var mBinding: FragAssignmentOverviewBinding
    private lateinit var mAssignment: Assignment
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mAssignmentStudents: ArrayList<Assignment>
    private var mIsInstructorType = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragAssignmentOverviewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initToolbar()
        val assignment = requireArguments().getParcelable<Assignment>(App.ITEM)
        val id = requireArguments().getInt(App.ID)
        mIsInstructorType = requireArguments().getBoolean(App.INSTRUCTOR_TYPE)

        if (assignment == null) {
            showLoadingDialog()

            val presenter = AssignmentOverviewPresenterImpl(this)
            presenter.getAssignment(id)

        } else {
            mAssignment = assignment
            initUI()
        }
    }

    private fun showLoadingDialog() {
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)
    }

    private fun initUI() {
        initBase()

        if (mIsInstructorType) {
            initInstructor()
        } else {
            initStudent()
        }
    }

    private fun initBase() {
        mBinding.assignmentOverviewTitleTv.text = mAssignment.title
        mBinding.assignmentOverviewCourseTitleTv.text = mAssignment.courseTitle
        mBinding.assignmentOverviewBtn.setOnClickListener(this)

        if (mAssignment.courseImage != null) {
            Glide.with(requireContext()).load(mAssignment.courseImage)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        mBinding.assignmentOverviewCourseImgOverlay.visibility = View.VISIBLE
                        return false
                    }

                }).into(mBinding.assignmentOverviewCourseImg)
        }
    }

    private fun initStudent() {
        mBinding.assignmentOverviewDescTv.text = mAssignment.description
        mBinding.assignmentOverviewDescTv.visibility = View.VISIBLE
        mBinding.assignmentOverviewBtn.text = getString(R.string.view_assignment)
        mBinding.assignmentOverviewRvHeaderTv.text = getString(R.string.attachments)

        initStudentMarks()
        initStudentRv()
    }

    private fun initStudentRv() {
        val rv = mBinding.assignmentOverviewRv

        if (mAssignment.attachments.isNotEmpty()) {
            rv.adapter = AttachmentRvAdapter(mAssignment.attachments)
            rv.addOnItemTouchListener(ItemClickListener(rv, this))

        } else {
            mBinding.assignmentOverviewRvHeaderTv.visibility = View.GONE
            rv.visibility = View.GONE
        }
    }

    private fun initInstructor() {
        showLoadingDialog()

        val presenter = AssignmentOverviewPresenterImpl(this)
        presenter.getAssignmentStudents(mAssignment.id)

        mBinding.assignmentOverviewBtn.text = getString(R.string.review_submissions)

        initInstructorMarks()
        setAssignmentStatus()
    }

    private fun initStudentMarks() {
        mBinding.assignmentOverviewFirstMarkImg.setImageResource(R.drawable.ic_info_gray)
        mBinding.assignmentOverviewSecondMarkImg.setImageResource(R.drawable.ic_plus2)
        mBinding.assignmentOverviewThirdMarkImg.setImageResource(R.drawable.ic_calendar)
        mBinding.assignmentOverviewForthMarkImg.setImageResource(R.drawable.ic_calendar_done)
        mBinding.assignmentOverviewFifthMarkImg.setImageResource(R.drawable.ic_star_circle)
        mBinding.assignmentOverviewSixthMarkImg.setImageResource(R.drawable.ic_done)
        mBinding.assignmentOverviewSeventhMarkImg.setImageResource(R.drawable.ic_chart)
        mBinding.assignmentOverviewEighthMarkImg.setImageResource(R.drawable.ic_more_circle)

        mBinding.assignmentOverviewFirstMarkKeyTv.text = getString(R.string.deadline)
        mBinding.assignmentOverviewSecondMarkKeyTv.text = getString(R.string.attempts)
        mBinding.assignmentOverviewThirdMarkKeyTv.text = getString(R.string.first_submission)
        mBinding.assignmentOverviewForthMarkKeyTv.text = getString(R.string.last_submission)
        mBinding.assignmentOverviewFifthMarkKeyTv.text = getString(R.string.total_grade)
        mBinding.assignmentOverviewSixthMarkKeyTv.text = getString(R.string.pass_grade)
        mBinding.assignmentOverviewSeventhMarkKeyTv.text = getString(R.string.your_grade)
        mBinding.assignmentOverviewEighthMarkKeyTv.text = getString(R.string.status)

        mBinding.assignmentOverviewFirstMarkTv.text =
            Utils.getDateFromTimestamp(mAssignment.deadlineTime)
        val attempt = "${mAssignment.usedAttemptsCount}/${mAssignment.totalAttempts}"
        mBinding.assignmentOverviewSecondMarkTv.text = attempt

        if (mAssignment.firstSubmission != null) {
            mBinding.assignmentOverviewThirdMarkTv.text =
                Utils.getDateFromTimestamp(mAssignment.firstSubmission!!)
        } else {
            mBinding.assignmentOverviewThirdMarkTv.text = "-"
        }

        if (mAssignment.lastSubmission != null) {
            mBinding.assignmentOverviewForthMarkTv.text =
                Utils.getDateFromTimestamp(mAssignment.lastSubmission!!)
        } else {
            mBinding.assignmentOverviewForthMarkTv.text = "-"
        }

        mBinding.assignmentOverviewFifthMarkTv.text = mAssignment.totalGrade.toString()
        mBinding.assignmentOverviewSixthMarkTv.text = mAssignment.passGrade.toString()

        if (mAssignment.grade != null) {
            mBinding.assignmentOverviewSeventhMarkTv.text = mAssignment.grade.toString()
        } else {
            mBinding.assignmentOverviewSeventhMarkTv.text = "-"
        }

        setUserAssignmentStatus()
    }

    private fun initInstructorMarks() {
        mBinding.assignmentOverviewFirstMarkImg.setImageResource(R.drawable.ic_paper_gray)
        mBinding.assignmentOverviewSecondMarkImg.setImageResource(R.drawable.ic_more_circle)
        mBinding.assignmentOverviewThirdMarkImg.setImageResource(R.drawable.ic_done)
        mBinding.assignmentOverviewForthMarkImg.setImageResource(R.drawable.ic_failed)
        mBinding.assignmentOverviewFifthMarkImg.setImageResource(R.drawable.ic_star_circle)
        mBinding.assignmentOverviewSixthMarkImg.setImageResource(R.drawable.ic_done)
        mBinding.assignmentOverviewSeventhMarkImg.setImageResource(R.drawable.ic_chart)
        mBinding.assignmentOverviewEighthMarkImg.setImageResource(R.drawable.ic_more_circle)

        mBinding.assignmentOverviewFirstMarkKeyTv.text = getString(R.string.submissions)
        mBinding.assignmentOverviewSecondMarkKeyTv.text = getString(R.string.pending)
        mBinding.assignmentOverviewThirdMarkKeyTv.text = getString(R.string.passed)
        mBinding.assignmentOverviewForthMarkKeyTv.text = getString(R.string.failed)
        mBinding.assignmentOverviewFifthMarkKeyTv.text = getString(R.string.total_grade)
        mBinding.assignmentOverviewSixthMarkKeyTv.text = getString(R.string.pass_grade)
        mBinding.assignmentOverviewSeventhMarkKeyTv.text = getString(R.string.average_grade)
        mBinding.assignmentOverviewEighthMarkKeyTv.text = getString(R.string.status)

        mBinding.assignmentOverviewFirstMarkTv.text = mAssignment.submissionsCount.toString()
        mBinding.assignmentOverviewSecondMarkTv.text = mAssignment.pendingCount.toString()
        mBinding.assignmentOverviewThirdMarkTv.text = mAssignment.passedCount.toString()
        mBinding.assignmentOverviewForthMarkTv.text = mAssignment.failedCount.toString()
        mBinding.assignmentOverviewFifthMarkTv.text = mAssignment.totalGrade.toString()
        mBinding.assignmentOverviewSixthMarkTv.text = mAssignment.passGrade.toString()
        mBinding.assignmentOverviewSeventhMarkTv.text = mAssignment.averageGrade.toString()
        setUserAssignmentStatus()
    }

    private fun setUserAssignmentStatus() {
        var text = ""
        var textColor = R.color.red

        when (mAssignment.userStatus) {
            Assignment.UserStatus.NOT_PASSED.value -> {
                text = requireContext().getString(R.string.failed)
            }

            Assignment.UserStatus.PASSED.value -> {
                text = requireContext().getString(R.string.passed)
                textColor = R.color.green
            }

            Assignment.UserStatus.PENDING.value -> {
                text = requireContext().getString(R.string.pending)
                textColor = R.color.orange
            }

            Assignment.UserStatus.NOT_SUBMITTED.value -> {
                text = requireContext().getString(R.string.not_submitted)
            }
        }

        mBinding.assignmentOverviewEighthMarkTv.text = text
        mBinding.assignmentOverviewEighthMarkTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                textColor
            )
        )
    }

    private fun setAssignmentStatus() {
        val textRes: Int
        val textColor: Int

        when (mAssignment.userStatus) {
            Assignment.Status.ACTIVE.value -> {
                textRes = R.string.active
                textColor = R.color.green
            }

            else -> {
                textRes = R.string.disabled
                textColor = R.color.red
            }
        }

        mBinding.assignmentOverviewEighthMarkTv.setText(textRes)
        mBinding.assignmentOverviewEighthMarkTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                textColor
            )
        )
    }

    private fun initToolbar() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.assignment_overview)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putBoolean(App.INSTRUCTOR_TYPE, mIsInstructorType)

        val frag = if (mIsInstructorType) {
            bundle.putParcelableArrayList(App.ITEMS, mAssignmentStudents)
            AssignmentSubmissionsTabFrag()
        } else {
            bundle.putParcelable(App.ITEM, mAssignment)
            AssignmentConversationFrag()
        }

        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        if (mIsInstructorType) {
            val assignment = mAssignmentStudents[position]
            goToAssignmentConversation(assignment)
        } else {
            val attachment =
                (mBinding.assignmentOverviewRv.adapter as AttachmentRvAdapter).items[position]
            downloadAttachment(attachment)
        }
    }

    private fun goToAssignmentConversation(assignment: Assignment) {
        val bundle = Bundle()
        bundle.putParcelable(App.ITEM, assignment)
        bundle.putBoolean(App.INSTRUCTOR_TYPE, mIsInstructorType)

        val frag = AssignmentConversationFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    private fun downloadAttachment(attachment: Attachment) {
        val bundle = Bundle()

        bundle.putString(App.URL, attachment.url)
        bundle.putString(App.DIR, App.Companion.Directory.DOWNLOADS.value())
        bundle.putBoolean(App.TO_DOWNLOADS, true)

        val loadingDialog = ProgressiveLoadingDialog()
        loadingDialog.setOnFileSavedListener(object : ItemCallback<String> {
            override fun onItem(filePath: String, vararg args: Any) {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.success),
                    getString(R.string.file_saved_in_your_downloads),
                    ToastMaker.Type.SUCCESS
                )
            }
        })
        loadingDialog.arguments = bundle
        loadingDialog.show(childFragmentManager, null)
    }

    fun onStudentsReceived(assignmentStudents: List<Assignment>) {
        mAssignmentStudents = assignmentStudents as ArrayList<Assignment>

        mLoadingDialog.dismiss()

        if (assignmentStudents.isNotEmpty()) {
            val students = ArrayList<User>()
            for (item in assignmentStudents) {
                item.student?.let {
                    students.add(it)
                }
            }

            val rv = mBinding.assignmentOverviewRv

            mBinding.assignmentOverviewRvHeaderTv.text = getString(R.string.latest_submissions)
            rv.adapter = StudentRvAdapter(students, true)
            rv.addOnItemTouchListener(ItemClickListener(rv, this))
        }
    }

    fun onAssignmentReceived(assignment: Assignment) {
        mLoadingDialog.dismiss()
        mAssignment = assignment
        initUI()
    }
}