package com.online.course.ui.frag

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.FragHomeBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.HomeClassRvAdapter
import com.online.course.manager.adapter.FeaturedSliderAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.model.QuickInfo
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.presenterImpl.HomePresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.CustomScrollView
import kotlin.math.abs


class HomeFrag : NetworkObserverFragment(), ViewTreeObserver.OnScrollChangedListener,
    View.OnClickListener {

    private lateinit var mBinding: FragHomeBinding
    private lateinit var mPresenter: Presenter.HomePresenter
    private var mScrollY = 0
    private var mCurrentTime = System.currentTimeMillis()
    private var lock = true

    companion object {
        private const val OFFSET = 0
        private const val LIMIT = 10
        private const val TAG = "HomeFrag"
    }

    private val mQuickInfoCallback = object : ItemCallback<QuickInfo> {
        override fun onItem(info: QuickInfo, vararg args: Any) {
            if (context == null) return

            App.quickInfo = info
            if (info.unreadNotifs.count > 0) {
                mBinding.homeHeaderNotificatonCircleView.text = info.unreadNotifs.count.toString()
                mBinding.homeHeaderNotificatonCircleView.visibility = View.VISIBLE
            }

            if (info.cartItemsCount > 0) {
                mBinding.homeHeaderCartCircleView.text = info.cartItemsCount.toString()
                mBinding.homeHeaderCartCircleView.visibility = View.VISIBLE
            }
        }
    }

    private val mViewAllBtnListener = View.OnClickListener { v ->
        var map: HashMap<String, String>? = null
        var bundles: ArrayList<Course>? = null

        val title: String

        when (v?.id) {
            R.id.homeViewAllNewestClassesBtn -> {
                title = mBinding.homeNewestClassesTv.text.toString()
                map = mBinding.homeNewestClasseRv.tag as HashMap<String, String>
            }

            R.id.homeViewAllBestRatedBtn -> {
                title = mBinding.homeBestRatedTv.text.toString()
                map = mBinding.homeBestRatedRv.tag as HashMap<String, String>
            }

            R.id.homeViewAllBestSellersBtn -> {
                title = mBinding.homeBestSellersTv.text.toString()
                map = mBinding.homeBestSellersRv.tag as HashMap<String, String>
            }

            R.id.homeViewAllDiscountedClassesBtn -> {
                title = mBinding.homeDiscountedClassesTv.text.toString()
                map = mBinding.homeDiscountedClassesRv.tag as HashMap<String, String>
            }

            R.id.homeViewAllFreeClassesBtn -> {
                title = mBinding.homeFreeClassesTv.text.toString()
                map = mBinding.homeFreeClassesRv.tag as HashMap<String, String>
            }

            R.id.homeViewAllNewestBundleClassesBtn -> {
                title = mBinding.homeNewestBundleClassesTv.text.toString()
                bundles = ((mBinding.homeNewestBundleClasseRv.adapter) as HomeClassRvAdapter).items
                        as ArrayList<Course>
            }

            else -> {
                return@OnClickListener
            }
        }

        val bundle = Bundle()
        bundle.putString(App.TITLE, title)
        if (bundles != null) {
            bundle.putParcelableArrayList(App.COURSES, bundles)
        }else{
            bundle.putSerializable(App.FILTERS, map)
        }

        val frag = ClassesFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        (activity as MainActivity).hideToolbar(
            App.getShapeFromColor(
                mBinding.homeHeaderContainer,
                R.color.accent,
                20f
            )
        )
        (activity as MainActivity).setContainer(false)
        (activity as MainActivity).showHome()
        (activity as MainActivity).uncheckAllItems()
        (activity as MainActivity).setDrawerLock(false)

//        App.initHeader(mBinding.homeStatusBar)

        mBinding.homeSearchLayout.setOnClickListener(this)
        mBinding.homeViewAllNewestClassesBtn.setOnClickListener(mViewAllBtnListener)
        mBinding.homeViewAllBestRatedBtn.setOnClickListener(mViewAllBtnListener)
        mBinding.homeViewAllBestSellersBtn.setOnClickListener(mViewAllBtnListener)
        mBinding.homeViewAllDiscountedClassesBtn.setOnClickListener(mViewAllBtnListener)
        mBinding.homeViewAllFreeClassesBtn.setOnClickListener(mViewAllBtnListener)
        mBinding.homeViewAllNewestBundleClassesBtn.setOnClickListener(mViewAllBtnListener)
        mBinding.homeHeaderNavBtn.setOnClickListener(this)
        mBinding.homeHeaderNotificatonBtn.setOnClickListener(this)
        mBinding.homeHeaderCartBtn.setOnClickListener(this)
        mBinding.homeRewardCoursesViewBtn.setOnClickListener(this)

        setScrollListener()

        mPresenter = HomePresenterImpl(this)
        mPresenter.getFeaturedCourses()

        initCourseLists()
        initUserInfo()
    }

    fun initUserInfo() {
        if (App.isLoggedIn()) {
            val name = (getString(R.string.hi) + " " + App.loggedInUser!!.name + "\uD83D\uDC4B")
            mBinding.homeHeaderNameTv.text = name

            val presenter = CommonApiPresenterImpl.getInstance()
            presenter.getQuickInfo(mQuickInfoCallback)
        }
    }

    private fun initCourseLists() {
        var map = HashMap<String, String>()

        mPresenter.getBundles()

        map["sort"] = "newest"
        map["offset"] = OFFSET.toString()
        map["limit"] = LIMIT.toString()
        mBinding.homeNewestClasseRv.tag = map
        mPresenter.getNewestCourses(map)

        map = HashMap()
        map["sort"] = "best_rates"
        map["offset"] = OFFSET.toString()
        map["limit"] = LIMIT.toString()
        mBinding.homeBestRatedRv.tag = map
        mPresenter.getBestRatedCourses(map)

        map = HashMap()
        map["sort"] = "bestsellers"
        map["offset"] = OFFSET.toString()
        map["limit"] = LIMIT.toString()
        mBinding.homeBestSellersRv.tag = map
        mPresenter.getBestSellingCourses(map)

        map = HashMap()
        map["discount"] = "1"
        map["offset"] = OFFSET.toString()
        map["limit"] = LIMIT.toString()
        mBinding.homeDiscountedClassesRv.tag = map
        mPresenter.getDiscountedCourses(map)

        map = HashMap()
        map["free"] = "1"
        map["offset"] = OFFSET.toString()
        map["limit"] = LIMIT.toString()
        mBinding.homeFreeClassesRv.tag = map
        mPresenter.getFreeCourses(map)
    }


//    @SuppressLint("ClickableViewAccessibility")
//    private fun setEditTextClickListener() {
//        mBinding.homeSearchEdtx.setOnTouchListener(OnTouchListener { v, event ->
//            val DRAWABLE_LEFT = 0
//            val DRAWABLE_RIGHT = 2
//            if (event.action == MotionEvent.ACTION_UP) {
//                if ((mBinding.homeSearchEdtx.compoundDrawables[DRAWABLE_RIGHT] != null && event.rawX >= mBinding.homeSearchEdtx.right - mBinding.homeSearchEdtx.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) ||
//                    (mBinding.homeSearchEdtx.compoundDrawables[DRAWABLE_LEFT] != null && event.rawX <= (mBinding.homeSearchEdtx.compoundDrawables[DRAWABLE_LEFT].bounds.width()))
//                ) {
//                    search()
//                    return@OnTouchListener true
//                }
//            }
//            v?.performClick()
//            false
//        })
//
//        mBinding.homeSearchEdtx.setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(
//                v: View?,
//                keyCode: Int,
//                event: KeyEvent
//            ): Boolean {
//                // If the event is a key-down event on the "enter" button
//                if (event.action == KeyEvent.ACTION_DOWN &&
//                    keyCode == KeyEvent.KEYCODE_ENTER
//                ) {
//                    // Perform action on key press
//                    search()
//                    return true
//                }
//                return false
//            }
//        })
//    }


    override fun onDestroy() {
        super.onDestroy()
        removeScrollListener()
    }

    private fun setScrollListener() {
        mBinding.homeScrollView.setOnScrollChangeListener(object :
            CustomScrollView.ScrollStatusChangeListener {
            override fun onScrollChanged(type: CustomScrollView.ScrollType) {
                if (type == CustomScrollView.ScrollType.DOWN) {
                    if (mBinding.homeSearchLayout.isVisible) {
                        mBinding.homeSearchLayout.visibility = View.GONE
                    }
                } else {
                    if (!mBinding.homeSearchLayout.isVisible) {
                        mBinding.homeSearchLayout.visibility = View.VISIBLE
                    }
                }
            }
        })

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mBinding.homeScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//
//                Log.d(TAG, "setScrollListener: oldScrollY:$oldScrollY")
//                Log.d(TAG, "setScrollListener: scrollY:$scrollY")
//
//                if (scrollY > 0) {
//                    if (scrollY > oldScrollY) {
//                        if (mBinding.homeSearchLayout.isVisible) {
//                            mBinding.homeSearchLayout.visibility = View.GONE
//                        }
//                    } else {
//                        if (!mBinding.homeSearchLayout.isVisible) {
//                            mBinding.homeSearchLayout.visibility = View.VISIBLE
//                        }
//                    }
//                }
//            }
//        } else {
//            mBinding.homeScrollView.viewTreeObserver.addOnScrollChangedListener(this)
//        }
    }

    private fun removeScrollListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mBinding.homeScrollView.viewTreeObserver.removeOnScrollChangedListener(this)
        }
    }

    override fun onScrollChanged() {
        val scrollY = mBinding.homeScrollView.scrollY

        var difference = 100
        if (lock) {
            difference = 300
        }

        if (System.currentTimeMillis() - mCurrentTime >= difference && abs(mScrollY - scrollY) > 10) {
            mCurrentTime = System.currentTimeMillis()
            lock = false

            if (scrollY > 0) {
                if (mScrollY < scrollY) {
                    if (mBinding.homeSearchLayout.visibility == View.VISIBLE) {
                        lock = true
                        mBinding.homeSearchLayout.post {
                            mBinding.homeSearchLayout.visibility = View.GONE
                        }
                    }

                } else if (mScrollY > scrollY) {
                    if (mBinding.homeSearchLayout.visibility != View.VISIBLE) {
                        lock = true
                        mBinding.homeSearchLayout.post {
                            mBinding.homeSearchLayout.visibility = View.VISIBLE
                        }
                    }
                }

                mScrollY = scrollY
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.homeSearchLayout -> {
                (activity as MainActivity).transact(SearchFrag())
            }

            R.id.homeHeaderNavBtn -> {
                (activity as MainActivity).openDrawer()
            }

            R.id.homeHeaderNotificatonBtn -> {
                (activity as MainActivity).transact(NotifsFrag())
            }

            R.id.homeHeaderCartBtn -> {
                (activity as MainActivity).transact(CartFrag())
            }

            R.id.homeRewardCoursesViewBtn -> {
                (activity as MainActivity).transact(RewardCoursesFrag())
            }
        }
    }

    fun onFeaturedCoursesReceived(items: List<Course>) {
        mBinding.homeFeaturedViewPagerProgressBar.visibility = View.GONE
        mBinding.homeFeaturedViewPager.adapter =
            FeaturedSliderAdapter(items, activity as MainActivity)
        mBinding.homeFeaturedIndicator.setViewPager2(mBinding.homeFeaturedViewPager)
    }

    fun onNewestCoursesRecevied(data: Data<List<Course>>) {
        addToRv(
            data.data!!,
            mBinding.homeNewestClassesProgressBar,
            mBinding.homeNewestClasseRv
        )
    }

    fun onBestRatedCoursesRecevied(data: Data<List<Course>>) {
        addToRv(data.data!!, mBinding.homeBestRatedProgressBar, mBinding.homeBestRatedRv)
    }

    fun onBestSellersCoursesRecevied(data: Data<List<Course>>) {
        addToRv(data.data!!, mBinding.homeBestSellersProgressBar, mBinding.homeBestSellersRv)
    }

    fun onFreeCoursesRecevied(data: Data<List<Course>>) {
        if (data.data!!.isEmpty()) {
            mBinding.homeViewAllFreeClassesBtn.visibility = View.GONE
            mBinding.homeFreeClassesRv.visibility = View.GONE
            mBinding.homeFreeClassesProgressBar.visibility = View.GONE
            mBinding.homeFreeClassesRv.visibility = View.GONE
        } else {
            addToRv(data.data!!, mBinding.homeFreeClassesProgressBar, mBinding.homeFreeClassesRv)
        }
    }

    fun onDiscountedCoursesRecevied(data: Data<List<Course>>) {
        if (data.data!!.isEmpty()) {
            mBinding.homeViewAllDiscountedClassesBtn.visibility = View.GONE
            mBinding.homeDiscountedClassesRv.visibility = View.GONE
            mBinding.homeDiscountedClassesProgressBar.visibility = View.GONE
            mBinding.homeDiscountedClassesTv.visibility = View.GONE
        } else {
            addToRv(
                data.data!!,
                mBinding.homeDiscountedClassesProgressBar,
                mBinding.homeDiscountedClassesRv
            )
        }

    }

    private fun addToRv(
        data: List<Course>,
        progressBar: ProgressBar,
        rv: RecyclerView
    ) {
        progressBar.visibility = View.GONE
        rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        rv.adapter = HomeClassRvAdapter(data, activity as MainActivity)
    }

    fun onBundleCoursesReceived(courses: List<Course>) {
        addToRv(
            courses,
            mBinding.homeNewestBundleClassesProgressBar,
            mBinding.homeNewestBundleClasseRv
        )
    }
}
