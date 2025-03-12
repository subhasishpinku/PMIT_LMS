package com.online.course.ui.frag

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.online.course.R
import com.online.course.databinding.FragQuizReviewBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.adapter.QuizQuestionAnswerRvAdapter
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.QuizReviewPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.AppDialog
import java.lang.NumberFormatException

class QuizReviewFrag : Fragment(), View.OnClickListener {
    private lateinit var mBinding: FragQuizReviewBinding
    private lateinit var mResult: QuizResult
    private lateinit var mQuiz: Quiz
    private lateinit var mQuestions: List<QuizQuestion>
    private lateinit var mCurrentQuestion: QuizQuestion
    private lateinit var mUserQuizAnswerMap: HashMap<Int, QuizQuestion>
    private lateinit var mInstructorQuizReviewMap: HashMap<Int, QuizAnswerItem>
    private lateinit var mPresenter: Presenter.QuizReviewPresenter

    private var mIsInstructorReview = false
    private var mQuestionProgress = 1

    private val mGradeTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val counterEdtx = mBinding.quizReviewGradeCounter.counterEdtx
            try {
                val writtenGrade = counterEdtx.text.toString().toDouble()
                if (writtenGrade > mCurrentQuestion.grade) {
                    counterEdtx.setText(mCurrentQuestion.grade.toString())
                }
            } catch (ex: NumberFormatException) {
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragQuizReviewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        (activity as MainActivity).hideToolbar()
//        App.initHeader(mBinding.quizReviewStatusbar)

        mResult = requireArguments().getParcelable(App.RESULT)!!
        mIsInstructorReview = requireArguments().getBoolean(App.INSTRUCTOR_TYPE, false)

        mQuiz = mResult.quiz
        mQuestions = mQuiz.questions
        mCurrentQuestion = mQuestions[0]

        mUserQuizAnswerMap = HashMap()
        for (question in mResult.quizReview!!) {
            mUserQuizAnswerMap[question.id] = question
        }

        if (mIsInstructorReview) {
            mPresenter = QuizReviewPresenterImpl(this)
            mInstructorQuizReviewMap = HashMap()

            mBinding.quizReviewHeaderDescTv.text =
                ("${getString(R.string.this_quiz_includes)} ${getQuestionsForReviewCount()} ${getString(
                    R.string.questions_for_review
                )}")
            mBinding.quizReviewGradeCounter.counterEdtx.addTextChangedListener(mGradeTextWatcher)
            mBinding.quizReviewGradeCounter.counterMinus.setOnClickListener(this)
            mBinding.quizReviewGradeCounter.counterPlus.setOnClickListener(this)
        } else {
            mBinding.quizReviewHeaderDescTv.text = getString(R.string.compare_with_correct_answers)
        }

        mBinding.quizReviewHeaderTitleTv.text = mQuiz.title
        mBinding.quizReviewHeaderLinearProgressBar.max = mQuiz.questionCount

        changeBtnVisibility(
            mBinding.quizReviewPreviousBtn,
            mBinding.quizReviewNextFinishBtn,
            false
        )
        updateQuestionProgress()

        checkForFinish()

        loadQuestion(mQuestions[0])

        mBinding.quizReviewPreviousBtn.setOnClickListener(this)
        mBinding.quizReviewNextFinishBtn.setOnClickListener(this)
        mBinding.quizReviewHeaderBackBtn.setOnClickListener(this)
    }

    private fun updateQuestionProgress() {
        mBinding.quizHeaderQuestionCountTv.text = ("${mQuestionProgress}/${mQuiz.questionCount}")
        mBinding.quizReviewHeaderLinearProgressBar.progress = mQuestionProgress
    }

    private fun checkForFinish() {
        if (mQuestionProgress == mQuiz.questionCount) {
            if (mIsInstructorReview) {
                mBinding.quizReviewNextFinishBtn.text = getString(R.string.finish)
                mBinding.quizReviewNextFinishBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            } else {
                changeBtnVisibility(
                    mBinding.quizReviewNextFinishBtn,
                    mBinding.quizReviewPreviousBtn,
                    false
                )
            }
        }
    }

    private fun changeBtnVisibility(
        btn: MaterialButton,
        otherBtn: MaterialButton,
        visible: Boolean
    ) {
        val margin16 = Utils.changeDpToPx(requireContext(), 16f).toInt()
        val margin10 = Utils.changeDpToPx(requireContext(), 10f).toInt()

        val layoutParams = otherBtn.layoutParams as ConstraintLayout.LayoutParams

        if (visible) {
            if (otherBtn.tag == "left") {
                layoutParams.marginStart = margin10
            } else {
                layoutParams.marginEnd = margin10
            }

            btn.visibility = View.VISIBLE

        } else {
            if (!otherBtn.isVisible) {
                mBinding.quizReviewBtnsContainer.visibility = View.GONE
                return
            }

            layoutParams.marginStart = margin16
            layoutParams.marginEnd = margin16

            btn.visibility = View.GONE
        }

        otherBtn.requestLayout()
    }

    private fun showPreviousQuestion() {
        if (mIsInstructorReview) {
            if (!addToInstructorReview()) {
                return
            }
        }

        mQuestionProgress -= 1
        loadQuestion(mQuestions[mQuestionProgress - 1])
        if (mQuestionProgress == 1) {
            changeBtnVisibility(
                mBinding.quizReviewPreviousBtn,
                mBinding.quizReviewNextFinishBtn,
                false
            )
        } else if (mQuestionProgress + 1 == mQuestions.size) {
            if (mIsInstructorReview) {
                mBinding.quizReviewNextFinishBtn.text = getString(R.string.next)
                mBinding.quizReviewNextFinishBtn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accent
                    )
                )
            } else {
                changeBtnVisibility(
                    mBinding.quizReviewNextFinishBtn,
                    mBinding.quizReviewPreviousBtn,
                    true
                )
            }
        }
        updateQuestionProgress()
    }

    private fun showNextQuestion() {
        if (mIsInstructorReview) {
            if (!addToInstructorReview()) {
                return
            }

        } else if (mQuestionProgress + 1 == mQuestions.size) {
            changeBtnVisibility(
                mBinding.quizReviewNextFinishBtn,
                mBinding.quizReviewPreviousBtn,
                false
            )
        }

        if (mQuestionProgress == 1) {
            changeBtnVisibility(
                mBinding.quizReviewPreviousBtn,
                mBinding.quizReviewNextFinishBtn,
                true
            )
        }


        mQuestionProgress += 1
        loadQuestion(mQuestions[mQuestionProgress - 1])
        updateQuestionProgress()

        if (mIsInstructorReview) {
            checkForFinish()
        }
    }

    private fun addToInstructorReview(): Boolean {
        if (mCurrentQuestion.type == QuizQuestion.Type.DESCRIPTIVE.value) {
            val answerItem = QuizAnswerItem()

            val correctAnswer = mBinding.quizReviewUserCorrectAnswerEdtx.text.toString()

            if (correctAnswer.isNotEmpty()) {
                answerItem.answer = correctAnswer
            }

            answerItem.questionId = mCurrentQuestion.id
            try {
                answerItem.grade =
                    mBinding.quizReviewGradeCounter.counterEdtx.text.toString().toDouble()
            } catch (ex: NumberFormatException) {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.error),
                    getString(R.string.enter_valid_number),
                    ToastMaker.Type.ERROR
                )
                return false
            }

            mInstructorQuizReviewMap[mCurrentQuestion.id] = answerItem
        }

        return true
    }

    private fun loadQuestion(question: QuizQuestion) {
        mBinding.quizReviewQuestionTv.text = question.title
        mBinding.quizReviewGradeTv.text = ("${getString(R.string.grade)}: ${question.grade}")
        mCurrentQuestion = question

        if (mCurrentQuestion.type == QuizQuestion.Type.MULTIPLE.value) {
            mBinding.quizReviewDescriptiveAnswerContainer.visibility = View.GONE
            mBinding.quizReviewAnswersRv.visibility = View.VISIBLE

            if (mUserQuizAnswerMap.containsKey(mCurrentQuestion.id) && mUserQuizAnswerMap[mCurrentQuestion.id]!!.userAnswer != null) {
                val userAnswer = mUserQuizAnswerMap[mCurrentQuestion.id]!!.userAnswer
                for (answer in mCurrentQuestion.answers!!) {
                    if (answer.id == userAnswer!!.answer.toInt()) {
                        answer.isUserAnswer = true
                    }
                }
            }

            mBinding.quizReviewAnswersRv.adapter =
                QuizQuestionAnswerRvAdapter(
                    mCurrentQuestion.answers!!.toMutableList(),
                    true,
                    mIsInstructorReview
                )

        } else {
            mBinding.quizReviewAnswersRv.visibility = View.GONE
            mBinding.quizReviewDescriptiveAnswerContainer.visibility = View.VISIBLE

            val quizReviewGradeCounter = mBinding.quizReviewGradeCounter

            if (mUserQuizAnswerMap.containsKey(mCurrentQuestion.id) && mUserQuizAnswerMap[mCurrentQuestion.id]!!.userAnswer != null) {
                mBinding.quizReviewUserAnswerTv.text =
                    mUserQuizAnswerMap[mCurrentQuestion.id]!!.userAnswer!!.answer
            }

            if (mIsInstructorReview) {
                mBinding.quizReviewUserAnswerTitleTv.text = getString(R.string.student_answer)
                mBinding.quizReviewUserGradeTitleTv.text = getString(R.string.student_grade)

                mBinding.quizReviewUserCorrectAnswerTv.visibility = View.GONE
                mBinding.quizReviewUserCorrectAnswerEdtx.visibility = View.VISIBLE

                if (mCurrentQuestion.descriptiveCorrectAnswer != null) {
                    mBinding.quizReviewUserCorrectAnswerEdtx.setText(mCurrentQuestion.descriptiveCorrectAnswer!!)
                }

                if (mInstructorQuizReviewMap.containsKey(mCurrentQuestion.id)) {
                    quizReviewGradeCounter.counterEdtx.setText(
                        mInstructorQuizReviewMap[mCurrentQuestion.id]!!.grade.toString()
                    )
                } else {
                    quizReviewGradeCounter.counterEdtx.setText(mCurrentQuestion.grade.toString())
                }

            } else {
                mBinding.quizReviewUserAnswerTitleTv.text = getString(R.string.your_answer)
                mBinding.quizReviewUserGradeTitleTv.text = getString(R.string.your_grade)

                if (mCurrentQuestion.descriptiveCorrectAnswer == null) {
                    mBinding.quizReviewUserCorrectAnswerTitleKeyTv.visibility = View.GONE
                    mBinding.quizReviewUserCorrectAnswerTv.visibility = View.GONE
                } else {
                    mBinding.quizReviewUserCorrectAnswerTv.text =
                        mCurrentQuestion.descriptiveCorrectAnswer
                }

                quizReviewGradeCounter.root.visibility = View.GONE
                if (mCurrentQuestion.userAnswer != null) {
                    mBinding.quizReviewDescriptiveGradeTv.text =
                        mCurrentQuestion.userAnswer!!.grade.toString()
                }
            }

        }
    }

    private fun getQuestionsForReviewCount(): Int {
        var counter = 0
        for (question in mQuestions) {
            if (question.type == QuizQuestion.Type.DESCRIPTIVE.value) {
                counter++
            }
        }

        return counter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.quizReviewPreviousBtn -> {
                showPreviousQuestion()
            }

            R.id.quizReviewNextFinishBtn -> {
                if (mBinding.quizReviewNextFinishBtn.text == getString(R.string.finish)) {
                    if (!addToInstructorReview()) {
                        return
                    }
                    showStoreReviewResultConfirmation()
                } else {
                    showNextQuestion()
                }
            }

            R.id.quizReviewHeaderBackBtn -> {
                activity?.supportFragmentManager?.popBackStack()
            }

            R.id.counterMinus -> {
                val counterEdtx = mBinding.quizReviewGradeCounter.counterEdtx
                val grade = counterEdtx.text.toString().toDouble()
                if (grade - 0.5 < 0) {
                    counterEdtx.setText(0.0.toString())
                } else {
                    counterEdtx.setText((grade - 0.5).toString())
                }
            }

            R.id.counterPlus -> {
                val counterEdtx = mBinding.quizReviewGradeCounter.counterEdtx
                val grade = counterEdtx.text.toString().toDouble()
                if (grade + 0.5 > mCurrentQuestion.grade) {
                    counterEdtx.setText(mCurrentQuestion.grade.toString())
                } else {
                    counterEdtx.setText((grade + 0.5).toString())
                }
            }
        }
    }

    private fun showStoreReviewResultConfirmation() {
        val dialog = AppDialog.instance
        val bundle = Bundle()
        bundle.putString(App.TITLE, getString(R.string.finish))
        bundle.putString(App.TEXT, getString(R.string.finish_quiz_desc))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                }

                override fun onOk() {
                    mPresenter.storeReviewResult(
                        mResult.id,
                        mInstructorQuizReviewMap.values.toList()
                    )
                }
            })
        dialog.show(childFragmentManager, null)
    }

    fun onResultStored(response: BaseResponse) {
        if (context == null) return

        if (response.isSuccessful) {
            activity?.onBackPressed()
            activity?.onBackPressed()

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