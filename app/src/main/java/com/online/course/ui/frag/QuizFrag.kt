package com.online.course.ui.frag

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.FragQuizBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.adapter.QuizQuestionAnswerRvAdapter
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.QuizPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.AppDialog
import com.online.course.ui.widget.LoadingDialog
import com.robinhood.ticker.TickerUtils

class QuizFrag : Fragment(), View.OnClickListener {

    private lateinit var mBinding: FragQuizBinding
    private lateinit var mResult: QuizResult
    private lateinit var mQuiz: Quiz
    private lateinit var mQuestions: List<QuizQuestion>
    private lateinit var mCurrentQuestion: QuizQuestion
    private lateinit var mTimer: String
    private lateinit var mPresenter: Presenter.QuizPresenter
    private lateinit var mQuizAnswerMap: HashMap<Int, QuizAnswerItem>
    private lateinit var mLoadingDialog: LoadingDialog

    private var mCountDownTimer: CountDownTimer? = null
    private var mQuestionProgress = 1
    private var mTimerInSeconds = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragQuizBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.currentFrag = this
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        App.currentFrag = null
    }

    private fun init() {
        (activity as MainActivity).hideToolbar()

        mResult = requireArguments().getParcelable(App.RESULT)!!
        mPresenter = QuizPresenterImpl(this)

        App.initHeader(mBinding.quizStatusbar)
        mQuizAnswerMap = HashMap()

        mQuiz = mResult.quiz
        mQuestions = mQuiz.questions
        if (mQuestions.isEmpty()) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                getString(R.string.quiz_has_no_question),
                ToastMaker.Type.ERROR
            )
            activity?.supportFragmentManager?.popBackStack()
            return
        }
        mCurrentQuestion = mQuestions[0]
        mTimerInSeconds = mQuiz.time * 60

        mBinding.quizHeaderProgressIndicator.max = mTimerInSeconds
        mBinding.quizHeaderLinearProgressBar.max = mQuiz.questionCount
        mBinding.quizHeaderTitleTv.text = mQuiz.title
        mBinding.quizPreviousBtn.visibility = View.GONE
        updateQuestionProgress()

        initTimer()
        checkForFinish()

        loadQuestion(mCurrentQuestion)

        mBinding.quizPreviousBtn.setOnClickListener(this)
        mBinding.quizNextFinishBtn.setOnClickListener(this)
        mBinding.quizHeaderBackBtn.setOnClickListener(this)
    }

    private fun updateQuestionProgress() {
        mBinding.quizHeaderQuestionProgressTv.text = ("${mQuestionProgress}/${mQuiz.questionCount}")
        mBinding.quizHeaderLinearProgressBar.progress = mQuestionProgress
    }

    private fun initTimer() {
        if (mQuiz.time > 0) {
            mTimer = Utils.standandTime(mQuiz.time)
            mBinding.quizHeaderTickerView.setCharacterLists(TickerUtils.provideNumberList());
            mBinding.quizHeaderTickerView.typeface =
                ResourcesCompat.getFont(requireContext(), R.font.bold);
            mBinding.quizHeaderTickerView.text = mTimer
            startTimer()
        } else {
            mBinding.quizHeaderTickerView.visibility = View.GONE
            mBinding.quizHeaderProgressIndicator.visibility = View.GONE
        }
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer((mTimerInSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.quizHeaderProgressIndicator.progress =
                    mBinding.quizHeaderProgressIndicator.progress + 1

                mTimerInSeconds -= 1
                mTimer = Utils.standandTime(mTimerInSeconds)
                mBinding.quizHeaderTickerView.text = mTimer
            }

            override fun onFinish() {
                addAnswerToAnswerSheet()
                storeQuizResult()
            }
        }
        mCountDownTimer!!.start()
    }

    private fun checkForFinish() {
        if (mQuestionProgress == mQuestions.size) {
            mBinding.quizNextFinishBtn.text = getString(R.string.finish)
            mBinding.quizNextFinishBtn.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
        }
    }

    fun onBackPressed() {
//        if (mQuestionProgress > 1) {
//            mBinding.quizNextFinishBtn.text = getString(R.string.next)
//            mBinding.quizNextFinishBtn.backgroundTintList = ColorStateList.valueOf(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.green_accent
//                )
//            )
//            showPreviousQuestion()
//
//        } else {
        showExitQuizConfirmation()
//        }
    }

    private fun showExitQuizConfirmation() {
        val dialog = AppDialog.instance
        val bundle = Bundle()
        bundle.putString(App.TITLE, getString(R.string.finish))
        bundle.putString(App.TEXT, getString(R.string.finish_quiz_desc))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                }

                override fun onOk() {
                    addAnswerToAnswerSheet()
                    storeQuizResult()
                }
            })
        dialog.show(childFragmentManager, null)
    }

    private fun storeQuizResult() {
        mCountDownTimer?.cancel()
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        val answerList = mQuizAnswerMap.values.toList()

        val answer = QuizAnswer()
        answer.quizResultId = mResult.quizResultId
        if (answerList.isEmpty()) {
            answer.answers = null
        } else {
            answer.answers = answerList
        }

        mPresenter.storeResult(mQuiz.id, answer)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.quizPreviousBtn -> {
                showPreviousQuestion()
            }

            R.id.quizNextFinishBtn -> {
                if (mBinding.quizNextFinishBtn.text == getString(R.string.finish)) {
                    showExitQuizConfirmation()
                } else {
                    showNextQuestion()
                }
            }

            R.id.quizHeaderBackBtn -> {
                showExitQuizConfirmation()
            }
        }
    }

    private fun showPreviousQuestion() {
        addAnswerToAnswerSheet()
        mQuestionProgress -= 1
        loadQuestion(mQuestions[mQuestionProgress - 1])
        if (mQuestionProgress == 1) {
            mBinding.quizPreviousBtn.visibility = View.GONE
        } else if (mQuestionProgress == mQuestions.size - 1) {
            showNextBtn()
        }
        updateQuestionProgress()
    }

    private fun showNextBtn() {
        mBinding.quizNextFinishBtn.text = getString(R.string.next)
        mBinding.quizNextFinishBtn.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.accent
            )
        )
    }

    private fun showNextQuestion() {
        addAnswerToAnswerSheet()
        mQuestionProgress += 1
        if (mQuestionProgress > 1 && !mBinding.quizPreviousBtn.isVisible) {
            mBinding.quizPreviousBtn.visibility = View.VISIBLE
        }
        loadQuestion(mQuestions[mQuestionProgress - 1])
        checkForFinish()
        updateQuestionProgress()
    }

    private fun loadQuestion(question: QuizQuestion) {
        mBinding.quizQuestionTv.text = question.title
        mBinding.quizGradeTv.text = ("${getString(R.string.grade)}: ${question.grade}")
        mCurrentQuestion = question

        if (mCurrentQuestion.type == QuizQuestion.Type.MULTIPLE.value) {
            mBinding.quizAnswerEdtx.visibility = View.GONE
            mBinding.quizAnswersRv.visibility = View.VISIBLE

            mBinding.quizAnswersRv.adapter =
                QuizQuestionAnswerRvAdapter(mCurrentQuestion.answers!!.toMutableList())

            setPreviousAnswer()

        } else {
            mBinding.quizAnswersRv.visibility = View.GONE
            mBinding.quizAnswerEdtx.visibility = View.VISIBLE
            mBinding.quizAnswerEdtx.setText("")

            setPreviousAnswer()
        }
    }

    private fun setPreviousAnswer() {
        if (mQuizAnswerMap.containsKey(mCurrentQuestion.id)) {
            val userAnswer = mQuizAnswerMap[mCurrentQuestion.id]!!

            if (mCurrentQuestion.type == QuizQuestion.Type.MULTIPLE.value) {
                val items = (mBinding.quizAnswersRv.adapter as QuizQuestionAnswerRvAdapter).items
                for ((index, item) in items.withIndex()) {
                    if (item.id == userAnswer.answer.toInt()) {
                        mBinding.quizAnswersRv.post {
                            val viewHolder =
                                mBinding.quizAnswersRv.findViewHolderForAdapterPosition(index) as QuizQuestionAnswerRvAdapter.ViewHolder
                            viewHolder.itemClick(item)
                        }
                    }
                }

            } else {
                mBinding.quizAnswerEdtx.setText(userAnswer.answer)
            }
        }
    }

    private fun addAnswerToAnswerSheet() {
        if (mCurrentQuestion.type == QuizQuestion.Type.MULTIPLE.value) {
            val adapter = mBinding.quizAnswersRv.adapter as QuizQuestionAnswerRvAdapter
            if (adapter.getSelectedItem() != null) {
                val answer = QuizAnswerItem()
                answer.answer = adapter.getSelectedItem()!!.id.toString()
                answer.questionId = mCurrentQuestion.id
                mQuizAnswerMap[mCurrentQuestion.id] = answer
            } else {
                mQuizAnswerMap.remove(mCurrentQuestion.id)
            }

        } else {
            val answerTxt = mBinding.quizAnswerEdtx.text.toString()
            if (answerTxt.isNotEmpty()) {
                val answer = QuizAnswerItem()
                answer.answer = answerTxt
                answer.questionId = mCurrentQuestion.id
                mQuizAnswerMap[mCurrentQuestion.id] = answer
            } else {
                mQuizAnswerMap.remove(mCurrentQuestion.id)
            }
        }
    }

    fun onQuizResultSaved(data: Data<Data<QuizResult>>) {
        if (context == null) return

        mLoadingDialog.dismiss()

        if (data.isSuccessful) {
            val bundle = Bundle()
            bundle.putParcelable(App.RESULT, data.data!!.data)

            val frag = QuizResultInfoFrag()
            frag.arguments = bundle

            (activity as MainActivity).transact(frag, addToBackstack = false)

        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }

}