package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.RvBinding
import com.online.course.databinding.TabBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.CourseCommon2RvAdapter
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Course
import com.online.course.model.Meeting
import com.online.course.model.Meetings
import com.online.course.model.Quiz
import com.online.course.presenter.Presenter
import com.online.course.ui.MainActivity
import java.util.ArrayList

class CourseLearningQuizzesFrag : Fragment(), OnItemClickListener {
    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!
        val offlineMode = requireArguments().getBoolean(App.OFFLINE)
        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = CourseCommon2RvAdapter(course.quizzes)
        if (!offlineMode) {
            mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val quiz = (mBinding.rv.adapter as CourseCommon2RvAdapter).items[position] as Quiz

        val frag: Fragment
        val bundle = Bundle()

        frag = if (quiz.authStatus == Quiz.NOT_PARTICIPATED) {
            bundle.putParcelable(App.QUIZ, quiz)
            QuizOverviewFrag()
        } else {
            bundle.putInt(App.ID, quiz.id)
            QuizResultInfoFrag()
        }

        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }
}