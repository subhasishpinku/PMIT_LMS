package com.online.course.ui

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationBarView
import com.online.course.R
import com.online.course.databinding.ActivityMainBinding
import com.online.course.manager.*
import com.online.course.manager.adapter.SlideMenuRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.ApiService
import com.online.course.model.*
import com.online.course.model.view.PaymentRedirection
import com.online.course.model.view.ViewShape
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.LogoutPresenterImpl
import com.online.course.ui.frag.*
import com.online.course.ui.widget.AppDialog
import com.online.course.ui.widget.CustomDuoDrawerLayout.LOCK_MODE_LOCKED_CLOSED
import com.online.course.ui.widget.CustomDuoDrawerLayout.LOCK_MODE_UNLOCKED
import com.online.course.ui.widget.LoadingDialog
import com.online.course.ui.widget.UserRegistrationDialog
import com.robinhood.ticker.TickerUtils
import java.util.*

class MainActivity : BaseActivity(), View.OnClickListener, OnItemClickListener,
    NavigationBarView.OnItemSelectedListener, DrawerLayout.DrawerListener,
    SelectionDialog.ItemSelection<Language> {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mRequestToLoginContract: ActivityResultLauncher<Intent>
    private lateinit var mPaymentStatusContract: ActivityResultLauncher<Intent>
    private lateinit var mPresenter: Presenter.LogoutPresenter

    private var mCurrentBackStackCount = 1
    private var mStatusBarColor = 0
    private var mClickCallback: ItemCallback<Any>? = null
    private var mRefreshListener: OnRefreshListener? = null
    private var mTopShape: ViewShape? = null
    private var mBottomShape: ViewShape? = null
    private var mCurrentFrag: Fragment? = null
    private var mDrawerSlideListener: DrawerSlideListener? = null

    interface OnRefreshListener {
        fun refresh()
    }

    interface DrawerSlideListener {
        fun onDrawerSlide(corner: Float)
    }

    companion object {
        private const val MANUAL_OPEN = -1
    }

    fun setOnDrawerSlideListener(listener: DrawerSlideListener?) {
        mDrawerSlideListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        init()
    }

    private fun init() {
        App.currentActivity = this
        mPresenter = LogoutPresenterImpl()

        setApiServiceActivity()
        initNavDrawer()
        initContracts()
        initToolbar()
//        initBars()
        initUserInfo()
        initHome()
        initClickListeners()
        initNeedToRegister()
        mCurrentBackStackCount = supportFragmentManager.backStackEntryCount
    }

    private fun setApiServiceActivity() {
        ApiService.activity = this
    }

    private fun initToolbar() {
        mBinding.mainToolbarEndCircleView.setCharacterLists(TickerUtils.provideNumberList())
        mBinding.mainToolbarEndCircleView.typeface =
            ResourcesCompat.getFont(this, R.font.regular)
    }

    private fun initHome() {
        transact(HomeFrag(), false, false)
    }


    private fun initContracts() {
        mRequestToLoginContract =
            registerForActivityResult(ResultContracts.ActivityResultResponse()) { intent ->
                if (intent != null && intent.getBooleanExtra(
                        App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP,
                        false
                    )
                ) {
                    showRegisterDialog(intent)
                } else {
                    refreshCurrentFrag()
                }

                initUserInfo()

                if (App.isLoggedIn()) {
                    mBinding.slideLogInOutBtn.text = getString(R.string.logout)
                }
            }

        mPaymentStatusContract =
            registerForActivityResult(ResultContracts.ActivityResultResponse()) { intent ->
                if (intent != null) {
                    val redirection =
                        intent.getParcelableExtra<PaymentRedirection>(App.REDIRECTION)!!

                    runOnPaymentSuccess(redirection)
                }
            }
    }

    private fun runOnPaymentSuccess(redirection: PaymentRedirection) {
        supportFragmentManager.popBackStackImmediate(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        if (redirection.isNavDrawer) {
            onClick(null, redirection.position, MANUAL_OPEN)
        } else {
            mBinding.bottomNav.findViewById<View>(redirection.position).performClick()
        }

        val loadingDialog = LoadingDialog.instance
        loadingDialog.show(supportFragmentManager, null)
        Handler(Looper.getMainLooper()).postDelayed({ loadingDialog.dismiss() }, 2500)
    }

    override fun onStart() {
        super.onStart()
        if (PaymentStatusActivity.isSuccessful) {
            PaymentStatusActivity.isSuccessful = false
            runOnPaymentSuccess(PaymentStatusActivity.paymentRedirection)
        }
    }

    fun goToLoginPage(listener: OnRefreshListener? = null) {
        mRefreshListener = listener

        val intent = Intent(this, SignInActivity::class.java)
        intent.putExtra(App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, true)
        mRequestToLoginContract.launch(intent)
    }

    fun goToPaymentStatusPage(paymentStatus: Boolean, paymentRedirection: PaymentRedirection) {
        val intent = Intent(this, PaymentStatusActivity::class.java)
        intent.putExtra(App.PAYMENT_STATUS, paymentStatus)
        intent.putExtra(App.REDIRECTION, paymentRedirection)
        mPaymentStatusContract.launch(intent)
    }

//    private fun initBars() {
//        val statusBarHeight = App.statusBarHeight(this)
//        val navBarHeight = App.navBarHeight(this)

//        val margin16 = resources.getDimension(R.dimen.margin_16).roundToInt()

//        val params = mBinding.bottomNavKeysContainer.layoutParams as ViewGroup.LayoutParams
//        params.height = navBarHeight
//        mBinding.bottomNavKeysContainer.requestLayout()

//        val lngParams = mBinding.slideLngContainer.layoutParams as RelativeLayout.LayoutParams
//        lngParams.bottomMargin = navBarHeight + margin16
//        mBinding.slideLngContainer.requestLayout()

//        mBinding.slideMenuContainer.setPadding(
//            0, statusBarHeight + margin16, 0, 0
//        )

//        val toolbarParams = mBinding.mainToolbar.layoutParams as ConstraintLayout.LayoutParams
//        toolbarParams.topMargin = statusBarHeight
//        mBinding.mainToolbar.requestLayout()

//        mNavBarColor = window.navigationBarColor
//        mBinding.bottomNavKeysContainer.setBackgroundColor(mNavBarColor)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.setDecorFitsSystemWindows(false)
//        } else {
//            @Suppress("DEPRECATION")
//            window.decorView.systemUiVisibility = (
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//        }

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
//    }

    private fun initNavDrawer() {
        mBinding.drawerLayout.setDrawerListener(this)

        val items = ArrayList<MenuItem>()
        items.add(MenuItem(R.drawable.ic_home, getString(R.string.home)))
        items.add(MenuItem(R.drawable.ic_dashboard, getString(R.string.dashboard)))
        items.add(MenuItem(R.drawable.ic_classes, getString(R.string.classes)))
        items.add(MenuItem(R.drawable.ic_meetings, getString(R.string.meetings)))
        items.add(MenuItem(R.drawable.ic_assignment, getString(R.string.assignments)))
        items.add(MenuItem(R.drawable.ic_quizzes, getString(R.string.quizzes)))
        items.add(MenuItem(R.drawable.ic_certificates, getString(R.string.certificates)))
        items.add(MenuItem(R.drawable.ic_favorites, getString(R.string.favorites)))
        items.add(MenuItem(R.drawable.ic_comments, getString(R.string.comments)))
        items.add(MenuItem(R.drawable.ic_financial, getString(R.string.financial)))
        items.add(MenuItem(R.drawable.ic_subscription, getString(R.string.subscription)))
        items.add(MenuItem(R.drawable.ic_mail, getString(R.string.support)))


        val adapter = SlideMenuRvAdapter(items)
        mBinding.slideMenuRv.adapter = adapter
        mBinding.slideMenuRv.addOnItemTouchListener(ItemClickListener(mBinding.slideMenuRv, this))
    }

    private fun initNeedToRegister() {
        if (intent != null) {
            if (intent.getBooleanExtra(App.SHOULD_REGISTER, false)) {
                showRegisterDialog(intent)
            }
        }
    }

    private fun showRegisterDialog(intent: Intent) {
        val dialog = UserRegistrationDialog()

        val bundle = Bundle()
        bundle.putParcelable(App.USER, intent.getParcelableExtra<ThirdPartyLogin>(App.USER))
        bundle.putInt(App.USER_ID, intent.getIntExtra(App.USER_ID, -1))

        dialog.isCancelable = false
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, null)
    }

    private fun initClickListeners() {
        mBinding.mainHomeContainer.setOnClickListener(this)
        mBinding.bottomNav.setOnItemSelectedListener(this)
        mBinding.mainToolbarStartImg.setOnClickListener(this)
        mBinding.mainToolbarEndImg.setOnClickListener(this)
        mBinding.slideUserSettingsBtn.setOnClickListener(this)
        mBinding.slideLngContainer.setOnClickListener(this)
        mBinding.slideLogInOutBtn.setOnClickListener(this)
    }

    fun initUserInfo() {
        val user = App.loggedInUser

        mBinding.slideCountryImg.setImageResource(
            BuildVars.LNG_FLAG[BaseActivity.language!!.name.uppercase(
                Locale.ENGLISH
            )]!!
        )

        if (user != null) {
            mBinding.slideLogInOutBtn.text = getString(R.string.logout)

            if (!user.avatar.isNullOrEmpty())
                Glide.with(this).load(user.avatar).into(mBinding.slideUserImg)

            mBinding.slideUserNameTv.text = user.name
        } else {
            mBinding.slideLogInOutBtn.text = getString(R.string.login)
            mBinding.slideUserNameTv.text = getString(R.string.app_name)
        }

        if (mCurrentFrag is HomeFrag) {
            (mCurrentFrag as HomeFrag).initUserInfo()
        }
    }

    fun openDrawer() {
        if (!mBinding.drawerLayout.isDrawerOpen) {
            mBinding.mainContainer.post {
                mBinding.drawerLayout.openDrawer()
            }
        }
    }

    fun showHome() {
        mBinding.mainHomeTv.visibility = View.VISIBLE
    }

    fun uncheckAllItems() {
        val menu = mBinding.bottomNav.menu
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }

    fun setOnFilterButtonClickListener(callback: ItemCallback<Any>) {
        mClickCallback = callback
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.mainHomeContainer -> {
                if (!mBinding.mainHomeTv.isVisible) {
                    mBinding.mainHomeTv.visibility = View.VISIBLE
                    transact(HomeFrag(), false, false)
                }
            }
            R.id.mainToolbarStartImg, R.id.mainToolbarEndImg -> {
                when (v.tag as ToolbarOptions.Icon) {
                    ToolbarOptions.Icon.BACK -> {
                        onBackPressed()
                    }

                    ToolbarOptions.Icon.NAV -> {
                        openDrawer()
                    }

                    ToolbarOptions.Icon.CART -> {
                        transact(CartFrag())
                    }

                    ToolbarOptions.Icon.FILTER -> {
                        mClickCallback?.onItem(Any())
                    }
                }
            }

            R.id.slideUserSettingsBtn -> {
                if (!App.isLoggedIn()) {
                    goToLoginPage(null)
                    return
                }

                if (!mBinding.drawerLayout.isDrawerOpen) {
                    return
                }

                mBinding.drawerLayout.closeDrawer()
                Handler(Looper.getMainLooper()).postDelayed({
                    transact(SettingsFrag())
                }, 300)
            }

            R.id.slideLngContainer -> {
                if (!mBinding.drawerLayout.isDrawerOpen) {
                    return
                }

                val bundle = Bundle()
                bundle.putSerializable(App.SELECTION_TYPE, SelectionDialog.Selection.Language)

                val instance = SelectionDialog.getInstance<Language>()
                instance.setOnItemSelected(this)
                instance.arguments = bundle
                instance.show(supportFragmentManager, null)
            }

            R.id.slideLogInOutBtn -> {
                if (!mBinding.drawerLayout.isDrawerOpen) {
                    return
                }

                val btnText = mBinding.slideLogInOutBtn.text.toString()
                if (btnText == getString(R.string.login)) {
                    goToLoginPage(null)
                } else {
                    showLogoutConfirmation()
                }
            }
        }
    }

    private fun showLogoutConfirmation() {
        val dialog = AppDialog.instance
        val bdl = Bundle()
        bdl.putString(App.TITLE, getString(R.string.logout))
        bdl.putString(App.TEXT, getString(R.string.logout_desc))
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {
                override fun onCancel() {
                    dialog.dismiss()
                }

                override fun onOk() {
                    mBinding.drawerLayout.closeDrawer()
                    Handler(Looper.getMainLooper()).postDelayed({
                        logout()
                    }, 300)
                }

            })
        dialog.arguments = bdl
        dialog.show(supportFragmentManager, null)
    }

    private fun logout() {
        mPresenter.logout()
        App.logout(this)
    }

    override fun onItemSelected(language: Language) {
        if (BaseActivity.language!!.code != language.code) {
            mBinding.slideLngTv.text = language.name

            val prefManager = PrefManager(this)
            prefManager.language = language
            BaseActivity.language = language

            language.img?.let { mBinding.slideCountryImg.setImageResource(it) }

            ApiService.createApiServiceWithLocale(prefManager.language!!.code)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun onBottomBarItemSelected(item: android.view.MenuItem?, itemId: Int?): Boolean {
        val fragment: Fragment

        when (item?.itemId ?: itemId!!) {
            R.id.categories_page -> {
                fragment = CategoriesFrag()
            }
            R.id.providers_page -> {
                fragment = ProvidersFrag()
            }
            R.id.blog_page -> {
                fragment = BlogsFrag()
            }
            R.id.my_classes_page -> {
                fragment = MyClassesTabFrag()
            }

            else -> return false
        }

        val addToBackstack =
            mBinding.mainHomeTv.isVisible && supportFragmentManager.backStackEntryCount == 0

        mBinding.mainHomeTv.visibility = View.INVISIBLE
        transact(fragment, false, addToBackstack)

        return true
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        return onBottomBarItemSelected(item, null)
    }

    fun setStatusBarColor(color: Int) {
        mStatusBarColor = ContextCompat.getColor(this, color)
        window.statusBarColor = mStatusBarColor
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        val startColor = mStatusBarColor
        val endColor = ContextCompat.getColor(this, R.color.dark_green)

        window.statusBarColor = ColorUtils.blendARGB(startColor, endColor, slideOffset)

        val corner = Utils.changeDpToPx(this, slideOffset * 20)
        mDrawerSlideListener?.onDrawerSlide(corner)

        setTopCorner(corner)
        setBottomCorner(corner)
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    private fun setTopCorner(corner: Float) {
        val topShape = GradientDrawable()
        topShape.shape = GradientDrawable.RECTANGLE
        topShape.cornerRadii = floatArrayOf(corner, corner, corner, corner, 0f, 0f, 0f, 0f)
        topShape.setColor(ContextCompat.getColor(this, R.color.pageBg))

        if (mBinding.mainToolbar.isVisible) {
            mBinding.mainToolbar.background = topShape
        } else {
            mBinding.mainContainer.background = topShape
            var currentCorners = 0f
            if (mTopShape != null) {
                currentCorners = mTopShape!!.currentCorners
            }

            mTopShape?.viewBg?.cornerRadii = floatArrayOf(
                corner,
                corner,
                corner,
                corner,
                currentCorners,
                currentCorners,
                currentCorners,
                currentCorners
            )
            mTopShape?.view?.background = mTopShape?.viewBg
        }
    }

    private fun setBottomCorner(corner: Float) {
        val bottomShape = GradientDrawable()
        bottomShape.shape = GradientDrawable.RECTANGLE
        bottomShape.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, corner, corner, corner, corner)
        bottomShape.orientation = GradientDrawable.Orientation.BR_TL

        val navStartColor = ContextCompat.getColor(this, R.color.bottom_nav_start_color)
        val navEndColor = ContextCompat.getColor(this, R.color.bottom_nav_end_color)
        bottomShape.colors = intArrayOf(navStartColor, navEndColor)

        if (mBinding.bottomNav.isVisible) {
            mBinding.bottomNav.background = bottomShape
        } else {
            bottomShape.setColor(ContextCompat.getColor(this, R.color.pageBg))
            if (mBottomShape == null) {
                mBinding.mainContainer.background = bottomShape
            } else {
                mBinding.mainContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.transparent
                    )
                )
                val bg = mBottomShape?.viewBg as GradientDrawable
                bg.cornerRadii =
                    floatArrayOf(corner, corner, corner, corner, corner, corner, corner, corner)

                mBottomShape?.view?.background = bg
//                mCurrentFrag?.view?.background = bg
            }
        }
    }

    override fun onBackPressed() {
        mCurrentBackStackCount = supportFragmentManager.backStackEntryCount

        if (App.currentFrag != null && App.currentFrag is QuizFrag) {
            (App.currentFrag!! as QuizFrag).onBackPressed()
        } else if (mBinding.mainHomeTv.isVisible && mCurrentBackStackCount == 0) {
            App.showExitDialog(this)

        } else if (mCurrentBackStackCount != supportFragmentManager.backStackEntryCount) {
            if (supportFragmentManager.backStackEntryCount <= 1) {
                super.onBackPressed()
            } else {
                supportFragmentManager.popBackStack()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        if (!App.isLoggedIn() && position != SlideMenuItem.HOME.value()) {
            goToLoginPage(null)
            return
        }

        if (mBinding.drawerLayout.isDrawerOpen) {
            mBinding.drawerLayout.closeDrawer()
        } else if (id != MANUAL_OPEN) {
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (position == SlideMenuItem.CLASSES.value() && mBinding.bottomNav.isVisible) {
                mBinding.bottomNav.findViewById<View>(R.id.my_classes_page).performClick()
                mBinding.mainHomeTv.visibility = View.INVISIBLE
                return@postDelayed
            }

            when (position) {

                SlideMenuItem.HOME.value() -> {
                    if (!mBinding.mainHomeTv.isVisible) {
                        if (mBinding.bottomNav.isVisible) {
                            super.onBackPressed()
                        } else {
                            transact(HomeFrag(), false)
                        }
                    }
                }

                SlideMenuItem.DASHBOARD.value() -> {
                    transact(DashboardFrag())
                }

                SlideMenuItem.MEETINGS.value() -> {
                    transact(MeetingsTabFrag())
                }

                SlideMenuItem.ASSIGNMENTS.value() -> {
                    transact(AssignmentsTabFrag())
                }

                SlideMenuItem.CLASSES.value() -> {
                    transact(MyClassesTabFrag())
                }

                SlideMenuItem.QUIZZES.value() -> {
                    transact(QuizzesTabFrag())
                }

                SlideMenuItem.CERTIFICATES.value() -> {
                    transact(CertificatesTabFrag())
                }

                SlideMenuItem.FAVORITES.value() -> {
                    transact(FavoritesFrag())
                }

                SlideMenuItem.COMMENTS.value() -> {
                    transact(CommentsFrag())
                }

                SlideMenuItem.FINANCIAL.value() -> {
                    transact(FinancialFrag())
                }

                SlideMenuItem.SUBSCRIPTION.value() -> {
                    if (App.loggedInUser!!.isUser()) {
                        transact(SubscriptionFrag())
                    } else {
                        transact(SubscriptionsTabFrag())
                    }
                }

                SlideMenuItem.SUPPORT.value() -> {
                    transact(SupportTabFrag())
                }
            }
        }, 300)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun onUserRegistered() {
        initUserInfo()
        if (mCurrentFrag is HomeFrag) {
            (mCurrentFrag as HomeFrag).initUserInfo()
        }
    }

    fun refreshCurrentFrag() {
        mRefreshListener?.refresh()
    }

    fun transact(
        frag: Fragment,
        removeBottomBar: Boolean = true,
        addToBackstack: Boolean = true
    ) {
        mTopShape = null
        mBottomShape = null

        setContainer(removeBottomBar)

        var transaction =
            supportFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                R.anim.enter_from_bottom,
//                R.anim.exit_to_top,
//                R.anim.enter_from_top,
//                R.anim.exit_to_bottom)
                .replace(R.id.mainContainer, frag)

        if (addToBackstack) {
            transaction = transaction.addToBackStack(null)
        }

        transaction.commit()

        mCurrentFrag = frag
    }

    internal fun setContainer(removeBottomBar: Boolean) {
        mBinding.mainContainer.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.pageBg
            )
        )

        if (removeBottomBar) {
            if (mBinding.bottomNav.isVisible) {
                mBinding.bottomNav.visibility = View.GONE
                mBinding.mainHomeContainer.visibility = View.GONE
                mBinding.mainHomeTv.visibility = View.GONE
                mBinding.mainHomeIcon.visibility = View.GONE
            }

        } else {
            if (!mBinding.bottomNav.isVisible) {
                mBinding.bottomNav.visibility = View.VISIBLE
                mBinding.mainHomeContainer.visibility = View.VISIBLE
                mBinding.mainHomeIcon.visibility = View.VISIBLE
            }
        }
    }

    fun showToolbar(toolbarOptions: ToolbarOptions, id: Int) {
        showToolbar(toolbarOptions, getString(id))
    }

    fun showToolbar(toolbarOptions: ToolbarOptions, title: String) {
        mBottomShape = null
        mBinding.mainToolbarTitleTv.text = title

        if (toolbarOptions.startIcon != null) {
            if (toolbarOptions.startIcon!!.icon == ToolbarOptions.Icon.NAV.icon) {
                setDrawerLock(false)
            } else {
                setDrawerLock(true)
            }

            mBinding.mainToolbarStartImg.tag = toolbarOptions.startIcon
            mBinding.mainToolbarStartImg.setImageResource(toolbarOptions.startIcon!!.icon)
            mBinding.mainToolbarStartImg.visibility = View.VISIBLE
        } else {
            mBinding.drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
            mBinding.mainToolbarStartImg.visibility = View.GONE
        }

        if (toolbarOptions.endIcon != null) {
            mBinding.mainToolbarEndImg.tag = toolbarOptions.endIcon
            mBinding.mainToolbarEndImg.setImageResource(toolbarOptions.endIcon!!.icon)
            mBinding.mainToolbarEndImg.visibility = View.VISIBLE

            if (toolbarOptions.endIcon == ToolbarOptions.Icon.CART && App.quickInfo != null &&
                App.quickInfo!!.cartItemsCount > 0
            ) {
                mBinding.mainToolbarEndCircleView.text = App.quickInfo!!.cartItemsCount.toString()
                mBinding.mainToolbarEndCircleView.visibility = View.VISIBLE
            } else {
                mBinding.mainToolbarEndCircleView.visibility = View.GONE
            }

        } else {
            mBinding.mainToolbarEndCircleView.visibility = View.GONE
            mBinding.mainToolbarEndImg.visibility = View.GONE
        }

        if (!mBinding.mainToolbar.isVisible) {
            setStatusBarColor(R.color.default_status_bar_color)
            mBinding.mainToolbar.visibility = View.VISIBLE
        }
    }

    fun hideToolbar(topShape: ViewShape? = null, bottomShape: ViewShape? = null) {
        mTopShape = topShape
        mBottomShape = bottomShape
        setDrawerLock(true)

        if (mBinding.mainToolbar.isVisible) {
            setStatusBarColor(R.color.primary_status_bar_color)
            mBinding.mainToolbar.visibility = View.GONE
        }
    }

    fun setDrawerLock(lock: Boolean) {
        if (lock) {
            mBinding.drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        } else {
            mBinding.drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
        }
    }

    fun updateCart() {
        val quickInfo = App.quickInfo
        if (quickInfo != null) {
            quickInfo.cartItemsCount += 1
            if (mBinding.mainToolbarEndImg.tag == ToolbarOptions.Icon.CART) {
                mBinding.mainToolbarEndCircleView.text = quickInfo.cartItemsCount.toString()
                mBinding.mainToolbarEndCircleView.visibility = View.VISIBLE
            }
        }
    }

    enum class SlideMenuItem(private val type: Int) {
        HOME(0),
        DASHBOARD(1),
        CLASSES(2),
        MEETINGS(3),
        ASSIGNMENTS(4),
        QUIZZES(5),
        CERTIFICATES(6),
        FAVORITES(7),
        COMMENTS(8),
        FINANCIAL(9),
        SUBSCRIPTION(10),
        SUPPORT(11);

        fun value(): Int {
            return type
        }
    }
}