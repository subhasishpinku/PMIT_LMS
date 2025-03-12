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
import com.online.course.model.*
import com.online.course.ui.MainActivity

class CertificatesTabFrag : Fragment() {

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

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.certificates)

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val adapter = ViewPagerAdapter(childFragmentManager)

        var bundle = Bundle()
        bundle.putSerializable(App.TYPE, Certificate.Type.QUIZ)

        val quizCertFrag = CertificatesFrag()
        quizCertFrag.arguments = bundle

        adapter.add(quizCertFrag, getString(R.string.quiz_certificates))

        bundle = Bundle()
        bundle.putSerializable(App.TYPE, Certificate.Type.COMPLTETION)

        val completionCertFrag = CertificatesFrag()
        completionCertFrag.arguments = bundle

        adapter.add(completionCertFrag, getString(R.string.completeion_certificates))

        if (App.loggedInUser!!.isInstructor() || App.loggedInUser!!.isOrganizaton()) {
            bundle = Bundle()
            bundle.putSerializable(App.TYPE, Certificate.Type.CLASS)

            val classCertFrag = CertificatesFrag()
            classCertFrag.arguments = bundle

            adapter.add(classCertFrag, getString(R.string.class_certificates))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}