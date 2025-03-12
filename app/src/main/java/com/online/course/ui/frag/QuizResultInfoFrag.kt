package com.online.course.ui.frag

import android.animation.Animator
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.online.course.R
import com.online.course.databinding.FragQuizResultBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Data
import com.online.course.model.Quiz
import com.online.course.model.QuizResult
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.QuizResultInfoPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.AppDialog
import com.online.course.ui.widget.LoadingDialog

class QuizResultInfoFrag : NetworkObserverFragment(), View.OnClickListener {

    private lateinit var mBinding: FragQuizResultBinding
    private lateinit var mPresenter: Presenter.QuizResultInfoPresenter
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mResult: QuizResult
    private lateinit var mQuiz: Quiz

    private var mIsInstructorReview = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragQuizResultBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        val result = requireArguments().getParcelable<QuizResult>(App.RESULT)
        val quiz = requireArguments().getParcelable<Quiz>(App.QUIZ)
        val quizId = requireArguments().getInt(App.ID)

        if (result != null) {
            (activity as MainActivity).showToolbar(toolbarOptions, R.string.quiz_result)

            mResult = result
            mIsInstructorReview = requireArguments().getBoolean(App.INSTRUCTOR_TYPE, false)
            if (mIsInstructorReview) {
                initReviewQuiz(mResult)
            } else {
                mPresenter = QuizResultInfoPresenterImpl(this)
                initStudentResult(mResult)
            }
        } else if (quiz != null) {
            (activity as MainActivity).showToolbar(toolbarOptions, R.string.quiz_info)
            mQuiz = quiz
            initQuizInfo(mQuiz)
        } else {
            mLoadingDialog = LoadingDialog.instance
            mLoadingDialog.show(childFragmentManager, null)

            mPresenter = QuizResultInfoPresenterImpl(this)
            mPresenter.getQuizResult(quizId)
        }

        mBinding.quizResultBackReviewBtn.setOnClickListener(this)
        mBinding.quizResultReviewRetryBtn.setOnClickListener(this)
    }

    private fun initReviewQuiz(result: QuizResult) {
        setHeaderResult(result, true)
        hideLastTwoItems()

        val quiz = result.quiz

        mBinding.quizResultCourseTitleTv.text = quiz.title
        mBinding.quizResultCourseDescTv.text = quiz.course.title
        mBinding.quizResultTv.text = getString(R.string.review_the_result)
        mBinding.quizResultDescTv.text = getString(R.string.review_the_result_desc)
        mBinding.quizResultReviewRetryBtn.visibility = View.GONE

        if (result.status == QuizResult.Result.WAITING.value) {
            mBinding.quizResultBackReviewBtn.text = getString(R.string.review_quiz)
        } else {
            mBinding.quizBtnsContainer.visibility = View.GONE
        }

        initFirstTwoItems(quiz)

        mBinding.quizResultThirdItemImg.setImageResource(R.drawable.ic_profile)
        mBinding.quizResultThirdItemKeyTv.text = getString(R.string.student)
        mBinding.quizResultThirdItemValueTv.text = result.user!!.name

        mBinding.quizResultForthItemImg.setImageResource(R.drawable.ic_plus2)
        mBinding.quizResultForthItemKeyTv.text = getString(R.string.attempts)
        mBinding.quizResultForthItemValueTv.text =
            ("${result.quiz.attempt - result.countTryAgain}/${quiz.attempt}")

        mBinding.quizResultFifthItemImg.setImageResource(R.drawable.ic_calendar)
        mBinding.quizResultFifthItemKeyTv.text = getString(R.string.submit_date)
        mBinding.quizResultFifthItemValueTv.text = Utils.getDateFromTimestamp(result.createdAt)

        setItemToStatus(
            result.status!!,
            mBinding.quizResultSixthItemImg,
            mBinding.quizResultSixthItemKeyTv,
            mBinding.quizResultSixthItemValueTv
        )
    }

    fun initStudentResult(result: QuizResult) {
        if (this::mLoadingDialog.isInitialized && mLoadingDialog.isVisible) {
            mLoadingDialog.dismiss()
        }

        mResult = result

        setHeaderResult(result, false)
        hideLastFourItems()

        initFirstTwoItems(result.quiz)

        mBinding.quizResultBackReviewBtn.text = getString(R.string.back_to_quizzes)

        mBinding.quizResultThirdItemImg.setImageResource(R.drawable.ic_profile)
        mBinding.quizResultThirdItemKeyTv.text = getString(R.string.your_grade)
        mBinding.quizResultThirdItemValueTv.text = result.userGrade.toString()

        when (result.status) {
            QuizResult.Result.FAILED.value -> {
                mBinding.quizResultTv.text = getString(R.string.you_failed_the_quiz)
                mBinding.quizResultDescTv.text = getString(R.string.you_failed_the_quiz_desc)

                mBinding.quizResultForthItemImg.setImageResource(R.drawable.ic_plus2)
                mBinding.quizResultForthItemKeyTv.text = getString(R.string.remained_attempts)
                mBinding.quizResultForthItemValueTv.text =
                    ("${(result.countTryAgain)}/${result.quiz.attempt}")

                if (result.authCanTryAgain) {
                    mBinding.quizResultReviewRetryBtn.text = getString(R.string.retry)
                } else {
                    mBinding.quizResultReviewRetryBtn.text = getString(R.string.review_answers)
                }
            }

            QuizResult.Result.WAITING.value -> {
                mBinding.quizResultTv.text = getString(R.string.wait_for_final_result)
                mBinding.quizResultDescTv.text = getString(R.string.wait_for_final_result_desc)

                setItemToStatus(
                    result.status!!,
                    mBinding.quizResultForthItemImg,
                    mBinding.quizResultForthItemKeyTv,
                    mBinding.quizResultForthItemValueTv
                )
                mBinding.quizResultReviewRetryBtn.visibility = View.GONE
            }

            QuizResult.Result.PASSED.value -> {
                mBinding.quizResultTv.text = getString(R.string.you_passed_the_quiz)
                mBinding.quizResultDescTv.text = getString(R.string.you_passed_the_quiz_desc)

                setItemToStatus(
                    result.status!!,
                    mBinding.quizResultForthItemImg,
                    mBinding.quizResultForthItemKeyTv,
                    mBinding.quizResultForthItemValueTv
                )

                playConfettiAnim()
                mBinding.quizResultReviewRetryBtn.text = getString(R.string.review_answers)
            }
        }
    }

    private fun hideLastFourItems() {
        mBinding.quizResultFifthItemImg.visibility = View.GONE
        mBinding.quizResultFifthItemKeyTv.visibility = View.GONE
        mBinding.quizResultFifthItemValueTv.visibility = View.GONE

        mBinding.quizResultSixthItemImg.visibility = View.GONE
        mBinding.quizResultSixthItemKeyTv.visibility = View.GONE
        mBinding.quizResultSixthItemValueTv.visibility = View.GONE

        hideLastTwoItems()
    }

    private fun hideLastTwoItems() {
        mBinding.quizResultSeventhItemImg.visibility = View.GONE
        mBinding.quizResultSeventhItemKeyTv.visibility = View.GONE
        mBinding.quizResultSeventhItemValueTv.visibility = View.GONE

        mBinding.quizResultEighthItemImg.visibility = View.GONE
        mBinding.quizResultEighthItemKeyTv.visibility = View.GONE
        mBinding.quizResultEighthItemValueTv.visibility = View.GONE
    }

    private fun setHeaderResult(result: QuizResult, review: Boolean) {
        mBinding.quizResultGradeValueTv.visibility = View.VISIBLE
        mBinding.quizResultGradeValueTv.text = result.userGrade.toString()

        if (review) {
            mBinding.quizResultGradeKeyTv.text = getString(R.string.student_grade)
        } else {
            mBinding.quizResultGradeKeyTv.text = getString(R.string.your_grade)
        }

        when (result.status) {
            QuizResult.Result.FAILED.value -> {
                mBinding.quizResultCircleImg.setImageResource(R.drawable.circle_red_stroke)
                mBinding.quizResultIconImg.setBackgroundResource(R.drawable.circle_red)
                mBinding.quizResultIconImg.setImageResource(R.drawable.ic_x_white)
            }

            QuizResult.Result.WAITING.value -> {
                mBinding.quizResultGradeKeyTv.visibility = View.GONE
                mBinding.quizResultGradeValueTv.text = getString(R.string.wait_for_final_result)
                mBinding.quizResultGradeValueTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                mBinding.quizResultCircleImg.setImageResource(R.drawable.circle_orange_stroke)
                mBinding.quizResultIconImg.setBackgroundResource(R.drawable.circle_orange)
                mBinding.quizResultIconImg.setImageResource(R.drawable.ic_more_white)
            }
        }
    }

    private fun setItemToStatus(
        status: String,
        imageView: ImageView,
        key: TextView,
        value: TextView
    ) {
        imageView.setImageResource(R.drawable.ic_more_circle)
        key.text = getString(R.string.status)
        value.text = status

        val color: Int

        when (status) {
            QuizResult.Result.PASSED.value -> {
                color = R.color.accent
            }

            QuizResult.Result.FAILED.value -> {
                color = R.color.red
            }

            else -> {
                color = R.color.orange
            }
        }

        value.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
    }

    private fun playConfettiAnim() {
        mBinding.quizOverviewLottie.visibility = View.VISIBLE
        mBinding.quizOverviewLottie.playAnimation()
        mBinding.quizOverviewLottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mBinding.quizOverviewLottie.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }

    private fun initFirstTwoItems(quiz: Quiz) {
        mBinding.quizResultFirstItemImg.setImageResource(R.drawable.ic_star_circle)
        mBinding.quizResultFirstItemKeyTv.text = getString(R.string.total_mark)
        mBinding.quizResultFirstItemValueTv.text = quiz.totalMark.toString()

        mBinding.quizResultSecondItemImg.setImageResource(R.drawable.ic_done)
        mBinding.quizResultSecondItemKeyTv.text = getString(R.string.pass_mark)
        mBinding.quizResultSecondItemValueTv.text = quiz.passMark.toString()

    }

    private fun initQuizInfo(quiz: Quiz) {
        mBinding.quizResultGradeValueTv.visibility = View.VISIBLE
        mBinding.quizResultTv.visibility = View.GONE

        mBinding.quizResultCourseTitleTv.text = quiz.title

        mBinding.quizResultBackReviewBtn.text = getString(R.string.back_to_quizzes)
        mBinding.quizResultReviewRetryBtn.visibility = View.GONE

        mBinding.quizResultGradeValueTv.text = quiz.averageGrade.toString()
        mBinding.quizResultGradeKeyTv.text = getString(R.string.average_grade)

        initFirstTwoItems(quiz)

        mBinding.quizResultIconImg.setImageResource(R.drawable.ic_statistics)

        mBinding.quizResultThirdItemImg.setImageResource(R.drawable.ic_questions)
        mBinding.quizResultThirdItemKeyTv.text = getString(R.string.questions)
        mBinding.quizResultThirdItemValueTv.text = quiz.questionCount.toString()

        mBinding.quizResultForthItemImg.setImageResource(R.drawable.ic_time)
        mBinding.quizResultForthItemKeyTv.text = getString(R.string.time)
        mBinding.quizResultForthItemValueTv.text = Utils.getDuration(requireContext(), quiz.time)

        mBinding.quizResultFifthItemImg.setImageResource(R.drawable.ic_profile)
        mBinding.quizResultFifthItemKeyTv.text = getString(R.string.students)
        mBinding.quizResultFifthItemValueTv.text = quiz.participatedCount.toString()

        mBinding.quizResultSixthItemImg.setImageResource(R.drawable.ic_user)
        mBinding.quizResultSixthItemKeyTv.text = getString(R.string.passsed_students)
        mBinding.quizResultSixthItemValueTv.text = (quiz.successRate.toString() + "%")

        mBinding.quizResultSeventhItemImg.setImageResource(R.drawable.ic_calendar)
        mBinding.quizResultSeventhItemKeyTv.text = getString(R.string.date_created)
        mBinding.quizResultSeventhItemValueTv.text = Utils.getDateFromTimestamp(quiz.createdAt)

        mBinding.quizResultEighthItemImg.setImageResource(R.drawable.ic_more_circle)
        mBinding.quizResultEighthItemKeyTv.text = getString(R.string.status)
        mBinding.quizResultEighthItemValueTv.text = quiz.status
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.quizResultBackReviewBtn -> {
                val btnText = mBinding.quizResultBackReviewBtn.text.toString()

                if (btnText == getString(R.string.back_to_quizzes)) {
                    activity?.onBackPressed()
                } else if (btnText == getString(R.string.review_quiz)) {
                    goToReviewPage()
                }
            }

            R.id.quizResultReviewRetryBtn -> {
                val btnText = mBinding.quizResultReviewRetryBtn.text.toString()
                if (btnText == getString(R.string.review_answers)) {
                    goToReviewPage()
                } else {
                    showRetryConfirmation()
                }
            }
        }
    }

    private fun goToReviewPage() {
        val bundle = Bundle()
        bundle.putParcelable(App.RESULT, mResult)
        bundle.putBoolean(App.INSTRUCTOR_TYPE, mIsInstructorReview)

        val frag = QuizReviewFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    private fun showRetryConfirmation() {
        val dialog = AppDialog.instance
        val bundle = Bundle()
        bundle.putString(App.TITLE, getString(R.string.retry))
        bundle.putString(App.TEXT, getString(R.string.retry_quiz_desc))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                }

                override fun onOk() {
                    mLoadingDialog = LoadingDialog.instance
                    mLoadingDialog.show(childFragmentManager, null)

                    mPresenter.startQuiz(mResult.quiz.id)
                }
            })

        dialog.show(childFragmentManager, null)
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

    fun onQuizStartBegin(data: Data<QuizResult>) {
        val bundle = Bundle()
        bundle.putParcelable(App.RESULT, data.data!!)

        val frag = QuizFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag, addToBackstack = false)
    }
}