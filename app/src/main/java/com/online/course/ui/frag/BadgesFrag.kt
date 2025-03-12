package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.RvBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.UserBadgeGridAdapter
import com.online.course.model.UserBadge
import com.online.course.ui.frag.abs.EmptyState

class BadgesFrag : Fragment(), EmptyState {

    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.isNestedScrollingEnabled = true

        val badges = requireArguments().getParcelableArrayList<UserBadge>(App.BADGES)!!
        if (badges.isNotEmpty()) {
            val adapter = UserBadgeGridAdapter(badges)
            mBinding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
            mBinding.rv.adapter = adapter
        } else {
            showEmptyState()
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_badges, R.string.no_badges, R.string.no_badges_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}