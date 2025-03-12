package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.TabBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.model.Meeting
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.ui.MainActivity
import java.util.ArrayList

class QuizzesTabFrag : Fragment() {

    private lateinit var mBinding: TabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.quizzes)

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val myResultsFrag = QuizzesFrag()
        val notParticipatedFrag = QuizzesFrag()

        var bundle = Bundle()
        bundle.putSerializable(App.SELECTION_TYPE, QuizzesFrag.Type.MY_RESULT)
        myResultsFrag.arguments = bundle

        bundle = Bundle()
        bundle.putSerializable(App.SELECTION_TYPE, QuizzesFrag.Type.NOT_PARTICIPATED)
        notParticipatedFrag.arguments = bundle

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(myResultsFrag, getString(R.string.my_results))
        adapter.add(notParticipatedFrag, getString(R.string.not_participated))

        if (App.loggedInUser!!.isInstructor() || App.loggedInUser!!.isOrganizaton()) {
            val studentsResultsFrag = QuizzesFrag()
            val listFrag = QuizzesFrag()

            bundle = Bundle()
            bundle.putSerializable(App.SELECTION_TYPE, QuizzesFrag.Type.STUDENT_RESULT)
            studentsResultsFrag.arguments = bundle

            bundle = Bundle()
            bundle.putSerializable(App.SELECTION_TYPE, QuizzesFrag.Type.LIST)
            listFrag.arguments = bundle

            adapter.add(studentsResultsFrag, getString(R.string.student_results))
            adapter.add(listFrag, getString(R.string._list))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        val serializable = arguments?.getSerializable(App.TYPE)
        if (serializable != null){
            val type = serializable as QuizzesFrag.Type
            if (type == QuizzesFrag.Type.MY_RESULT) {
                viewPager.currentItem = 0
            }else if (type == QuizzesFrag.Type.NOT_PARTICIPATED) {
                viewPager.currentItem = 1
            }
        }
    }

}