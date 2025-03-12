package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.FragFinancialPayoutBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.adapter.CommonRvAdapter
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.PayoutAccount
import com.online.course.model.PayoutRes
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.FinancialPayoutPresenterImpl
import com.online.course.ui.frag.abs.EmptyState
import com.online.course.ui.widget.PayoutRequestDialog

class FinancialPayoutFrag : NetworkObserverFragment(), View.OnClickListener, EmptyState {

    private lateinit var mBinding: FragFinancialPayoutBinding
    private lateinit var mPresenter: Presenter.FinancialPayoutPresenter

    private lateinit var mPayoutAccount: PayoutAccount

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragFinancialPayoutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = FinancialPayoutPresenterImpl(this)
        mPresenter.getPayouts()
    }

    fun onPayoutsReceived(res: PayoutRes) {
        mPayoutAccount = res.payoutAccount
        mBinding.financialPayoutRvProgressBar.visibility = View.GONE
        if (res.payouts.isNotEmpty()) {
            mBinding.financialPayoutRv.adapter = CommonRvAdapter(res.payouts)
        } else {
            showEmptyState()
        }

        mBinding.financialPayoutAmountTv.text =
            Utils.formatPrice(requireContext(), res.payoutAccount.amonut, false)
        mBinding.financialPayoutRequestBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(App.PAYOUT_ACCOUT, mPayoutAccount)

        val dialog = PayoutRequestDialog()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_payout, R.string.no_payout, R.string.no_payout_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.financialPayoutEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}