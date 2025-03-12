package com.online.course.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.online.course.R
import com.online.course.databinding.ActivityPaymentStatusBinding
import com.online.course.manager.App
import com.online.course.manager.Utils.toBoolean
import com.online.course.model.view.PaymentRedirection


class PaymentStatusActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityPaymentStatusBinding

    companion object {
        var isSuccessful = false
        lateinit var paymentRedirection: PaymentRedirection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPaymentStatusBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initBars()

        val uri = intent.data
        if (uri != null) {
            val status = uri.getQueryParameter("status")
            if (!status.isNullOrBlank()) {
                isSuccessful = status.toInt().toBoolean()
            }
        }

//        mIsSuccessful = intent.getBooleanExtra(App.PAYMENT_STATUS, false)
//        mPaymentRedirection = intent.getParcelableExtra(App.REDIRECTION)!!

        mBinding.paymentStatusBackBtn.setOnClickListener(this)

        if (isSuccessful) {
            mBinding.paymentStatusBackBtn.text = paymentRedirection.buttonTitle
        } else {
            mBinding.paymentStatusImg.setImageResource(R.drawable.ic_bill_failure)
            mBinding.paymentStatusConatiner.setBackgroundResource(R.drawable.gradient_payment_status_failure)
            mBinding.paymentStatusTitleTv.text = getString(R.string.failed_payment)
            mBinding.paymentStatusDescTv.text = getString(R.string.failed_payment_desc)
            mBinding.paymentStatusBackBtn.text = getString(R.string.back)
        }
    }

    private fun initBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.systemBars())
            window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onBackPressed() {
        onBackClicked()
        super.onBackPressed()
    }

    private fun onBackClicked() {
        if (isSuccessful) {
            val intent = Intent()
            intent.putExtra(App.REDIRECTION, paymentRedirection)
            setResult(Activity.RESULT_OK, intent)
        }
    }

    override fun onClick(v: View?) {
        onBackPressed()
    }
}