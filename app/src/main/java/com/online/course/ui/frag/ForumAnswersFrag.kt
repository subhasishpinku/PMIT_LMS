package com.online.course.ui.frag

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.FragForumAnswersBinding
import com.online.course.manager.App
import com.online.course.manager.PermissionManager
import com.online.course.manager.adapter.BaseArrayAdapter
import com.online.course.manager.adapter.ForumAnswersRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.ForumItem
import com.online.course.model.ForumItemAnswer
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ForumAnswersPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState
import com.online.course.ui.widget.ForumReplyDialog

class ForumAnswersFrag : NetworkObserverFragment(), EmptyState, View.OnClickListener, ItemCallback<Any> {

    private lateinit var mBinding: FragForumAnswersBinding
    private lateinit var mForumItem: ForumItem
    private lateinit var mPresenter: Presenter.ForumAnswersPresenter
    private lateinit var mStoragePermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mPermissionManager: PermissionManager

    companion object {
        val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStoragePermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (mPermissionManager.isGranted()
                ) {
                    val adapter = mBinding.forumAnswersReplyRv.adapter as ForumAnswersRvAdapter
                    adapter.downloadFile(requireContext(), adapter.items[0].attachment!!)
                }
            }
        mPermissionManager =
            PermissionManager(requireContext(), PERMISSIONS, mStoragePermissionResultLauncher)
    }

    fun hasPermission(): Boolean {
        return mPermissionManager.isGranted()
    }

    fun requestPermission() {
        return mPermissionManager.request()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragForumAnswersBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mForumItem = requireArguments().getParcelable(App.ITEM)!!
        mPresenter = ForumAnswersPresenterImpl(this)
        initUI()
        getAnswers()
    }

    private fun initUI() {
        mBinding.forumAnswersReplyBtn.setOnClickListener(this)
        setToolbar()
    }

    private fun setToolbar() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, mForumItem.title)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.forumAnswersEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_comments, R.string.no_answers, R.string.no_answers_desc)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(App.ITEM, mForumItem)

        val dialog = ForumReplyDialog()
        dialog.setOnReplySavedListener(this)
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, null)
    }

    fun onAnswersReceived(answers: List<ForumItemAnswer>) {
        mBinding.forumAnswersReplyRvProgrssBar.visibility = View.GONE

        val question = ForumItemAnswer()
        question.title = mForumItem.title
        question.description = mForumItem.description
        question.user = mForumItem.user
        question.createdAt = mForumItem.createdAt
        question.attachment = null

        val answersList = answers.toMutableList()
        answersList.add(0, question)

        if (answers.isEmpty()) {
            showEmptyState()
        } else {
            mBinding.forumAnswersReplyRv.adapter =
                ForumAnswersRvAdapter(answersList, this, this)
        }
    }

    override fun onItem(item: Any, vararg args: Any) {
        getAnswers()
    }

    private fun getAnswers() {
        mBinding.forumAnswersReplyRvProgrssBar.visibility = View.VISIBLE

        val adapter = mBinding.forumAnswersReplyRv.adapter

        if (adapter != null) {
            val baseArrayAdapter = adapter as BaseArrayAdapter<*, *>
            val size = baseArrayAdapter.items.size
            baseArrayAdapter.items.clear()
            baseArrayAdapter.notifyItemRangeRemoved(0, size)
        }

        mPresenter.getForumQuestionAnswers(mForumItem.id)
    }
}