package com.online.course.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.online.course.R
import com.online.course.databinding.ActivitySplashScreenBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.PrefManager
import com.online.course.manager.Utils
import com.online.course.manager.db.AppDb
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.RetryListener
import com.online.course.manager.net.observer.NetworkObserverActivity
import com.online.course.model.Language
import com.online.course.model.UserProfile
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.presenterImpl.SplashScreenPresenterImpl
import com.online.course.ui.frag.ErrorFrag


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : NetworkObserverActivity(), View.OnClickListener {

    private var mStartTime: Long = 0
    private var mShouldLogin = true
    private lateinit var mBinding: ActivitySplashScreenBinding
    private lateinit var mPresenter: Presenter.SplashScreenPresenter
    private var mSystemUIVisibility = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        changeLocale()

        mBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        App.currentActivity = this
        setStatusBarColor(R.color.accent)

        mSystemUIVisibility = window.decorView.systemUiVisibility

        hideEverything()

        mStartTime = System.currentTimeMillis()
        mBinding.splashToolbarBackImg.setOnClickListener(this)

        val rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        mBinding.splashScreenImgContainer.animation = rotateAnim

        mPresenter = SplashScreenPresenterImpl(this)
        mPresenter.getAppConfig()
    }

    private fun hideEverything() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.systemBars())
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

    fun showEverything() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            window.insetsController?.show(WindowInsets.Type.systemBars())
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        } else {
            window.decorView.systemUiVisibility = mSystemUIVisibility
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun goToNextPage() {
        if (System.currentTimeMillis() - mStartTime > 1000) {
            checkIfUserLoggedIn()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                checkIfUserLoggedIn()
            }, 1000 - System.currentTimeMillis() + mStartTime)
        }
    }

    private fun checkIfUserLoggedIn() {
        val appDb = AppDb(this)
        val token = appDb.getData(AppDb.DataType.TOKEN)

        if (token == null) {
            val prefManager = PrefManager(this)

            val activity =
                if (mShouldLogin && prefManager.isFirstTimeLaunch) OnboardingActivity::class.java else if (mShouldLogin && !prefManager.skipLogin)
                    SignInActivity::class.java else MainActivity::class.java

            startActivity(Intent(this, activity))
            finish()

        } else {
            val loggedInUserData = appDb.getData(AppDb.DataType.USER)
            val user = Gson().fromJson(loggedInUserData, UserProfile::class.java)
            val presenter = CommonApiPresenterImpl.getInstance()
            presenter.getUserInfo(user.id, object : ItemCallback<UserProfile> {
                override fun onItem(item: UserProfile, vararg args: Any) {
                    App.saveToLocal(
                        Gson().toJson(item),
                        this@SplashScreenActivity,
                        AppDb.DataType.USER
                    )
                    App.loggedInUser = item

                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    finish()

                    ApiService.createAuthorizedApiService(token)
                }
            })
        }

        appDb.close()
    }

    private fun changeLocale() {
        val prefManager = PrefManager(this)

        if (prefManager.language != null) {
            BaseActivity.language = prefManager.language!!
            Utils.changeLocale(this, prefManager.language!!.code)

            ApiService.createApiServiceWithLocale(prefManager.language!!.code)
        } else {
            val language = Language()
            language.name = BuildVars.DefaultLang.NAME
            language.code = BuildVars.DefaultLang.CODE
            language.isDefault = true
            prefManager.language = language
            BaseActivity.language = language
        }
    }

    fun onAppConfigReceived() {
        App.saveToLocal(
            Gson().toJson(App.appConfig),
            this,
            AppDb.DataType.APP_CONFIG
        )
        goToNextPage()
    }

    override fun onBackPressed() {
        if (ErrorFrag.isFragVisible) {
            App.showExitDialog(this)
        } else {
            super.onBackPressed()
        }
    }

    fun showNoNetFrag() {
        if (isDestroyed || isFinishing) return

        val errorFrag = ErrorFrag.getInstance()
        errorFrag.addOnRetryListener {
            hideEverything()
            mPresenter.getAppConfig()
        }

        val bundle = Bundle()
        bundle.putBoolean(App.COURSES, true)
        errorFrag.arguments = bundle

        try {
            transact(errorFrag)
            showEverything()
        } catch (ex: IllegalStateException) {
        }
    }

    fun transact(
        frag: Fragment,
        addToBackstack: Boolean = true
    ) {
        var transaction =
            supportFragmentManager.beginTransaction()
                .replace(R.id.splashScreenContainer, frag)

        if (addToBackstack) {
            transaction = transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    fun setStatusBarColor(color: Int) {
        val colorRes = ContextCompat.getColor(this, color)
        window.statusBarColor = colorRes
    }

    fun showToolbar(title: String) {
        setStatusBarColor(R.color.white)
        mBinding.splashContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.pageBg))
        mBinding.splashToolbarTitleTv.text = title
        if (!mBinding.splashToolbar.isVisible) {
            mBinding.splashToolbar.visibility = View.VISIBLE
        }
    }

    fun hideToolbar() {
        setStatusBarColor(R.color.accent)
        mBinding.splashContainer.setBackgroundResource(R.drawable.gradient_splash)
        if (mBinding.splashToolbar.isVisible) {
            mBinding.splashToolbar.visibility = View.GONE
        }
    }

    fun showToolbar(title: Int) {
        showToolbar(getString(title))
    }

    override fun onClick(v: View?) {
        onBackPressed()
    }

}