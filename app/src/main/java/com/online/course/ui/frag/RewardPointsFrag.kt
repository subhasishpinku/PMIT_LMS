package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.FragRewardPointsBinding
import com.online.course.manager.adapter.CommonRvAdapter
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Points
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.RewardPointsPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState

class RewardPointsFrag : NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: FragRewardPointsBinding
    private lateinit var mPresenter: Presenter.RewardPointsPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragRewardPointsBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.reward_points)

        mPresenter = RewardPointsPresenterImpl(this)
        mPresenter.getPoints()
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_sales, R.string.no_points, R.string.no_points_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rewardPointsHistoryEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onPointsReceived(points: Points) {
        mBinding.rewardPointsHistoryRvProgressBar.visibility = View.GONE
        mBinding.rewardPointsTotalCountTv.text = points.totalPoints.toString()
        mBinding.rewardPointsSpentCountTv.text = points.spentPoints.toString()
        mBinding.rewardPointsRemainedCountTv.text = points.availablePoints.toString()

        if (points.rewards.isNotEmpty()) {
            val adapter = CommonRvAdapter(points.rewards)
            mBinding.rewardPointsHistoryRv.adapter = adapter
        } else {
            showEmptyState()
        }
    }

}