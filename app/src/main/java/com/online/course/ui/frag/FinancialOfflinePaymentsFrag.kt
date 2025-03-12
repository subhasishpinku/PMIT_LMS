package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.RvBinding
import com.online.course.manager.adapter.OfflinePaymentRvAdapter
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.OfflinePayment
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.FinancialOfflinePaymentsPresenterImpl
import com.online.course.ui.frag.abs.EmptyState

class FinancialOfflinePaymentsFrag : NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.FinancialOfflinePaymentsPresenter

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
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        mPresenter = FinancialOfflinePaymentsPresenterImpl(this)
        mPresenter.getOfflinePayments()
    }

    fun onPaymentsReceived(payments: List<OfflinePayment>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (payments.isNotEmpty()) {
            val adapter = OfflinePaymentRvAdapter(payments)
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = adapter
        } else {
            showEmptyState()
        }
    }

    fun showEmptyState() {
        showEmptyState(
            R.drawable.no_offline_payments,
            R.string.no_offline_payments,
            R.string.no_offline_payments_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

}