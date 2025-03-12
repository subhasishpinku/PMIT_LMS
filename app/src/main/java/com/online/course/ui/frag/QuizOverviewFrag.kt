package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.FragQuizOverviewBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.QuizOverviewPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.AppDialog
import com.online.course.ui.widget.LoadingDialog

class QuizOverviewFrag : NetworkObserverFragment(), View.OnClickListener {

    private lateinit var mBinding: FragQuizOverviewBinding
    private lateinit var mQuiz: Quiz
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mPresenter: Presenter.QuizOverviewPresenter

    companion object {
        private const val DISABLED = "disabled"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragQuizOverviewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.quiz_information)

        mQuiz = requireArguments().getParcelable(App.QUIZ)!!
        mPresenter = QuizOverviewPresenterImpl(this)
        if (!mQuiz.course.hasUserBought) {
            mBinding.quizOverviewStartBtn.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.gray81)
            mBinding.quizOverviewStartBtn.tag = DISABLED
        }

        mBinding.quizOverviewTitleTv.text = mQuiz.title
        mBinding.quizOverviewDescTv.text = mQuiz.course.title
        mBinding.quizOverviewTotalMarkTv.text = mQuiz.totalMark.toString()
        mBinding.quizOverviewPassMarkTv.text = mQuiz.passMark.toString()
        mBinding.quizOverviewAttemptsTv.text = mQuiz.attempt.toString()

        mBinding.quizOverviewTimeTv.text = Utils.getDuration(requireContext(), mQuiz.time)
        mBinding.quizOverviewStartBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (mBinding.quizOverviewStartBtn.tag == DISABLED) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                getString(R.string.you_have_to_buy_this_course),
                ToastMaker.Type.ERROR
            )

            return
        }

        val dialog = AppDialog()
        val bundle = Bundle()
        bundle.putString(App.TITLE, getString(R.string.quiz))
        bundle.putString(App.TEXT, getString(R.string.start_quiz_desc))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                }

                override fun onOk() {
                    mLoadingDialog = LoadingDialog.instance
                    mLoadingDialog.show(childFragmentManager, null)
                    mPresenter.startQuiz(mQuiz.id)
                }
            })

        dialog.show(childFragmentManager, null)
    }

    fun onQuizStartBegin(data: Data<QuizResult>) {
        mLoadingDialog.dismiss()
        val bundle = Bundle()
        bundle.putParcelable(App.RESULT, data.data!!)

        val frag = QuizFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag, addToBackstack = false)
    }

    fun cannotStartQuiz(data: Data<QuizResult>?) {
        mLoadingDialog.dismiss()
        if (data != null) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }

}