package com.online.course.ui.frag

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.online.course.R
import com.online.course.databinding.FragDashboardBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.adapter.NoticeBoardSliderAdapter
import com.online.course.manager.adapter.CardStatisticsRvAdapter
import com.online.course.manager.adapter.DashboardGeneralSliderAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.view.CommonItem
import com.online.course.model.MenuItem
import com.online.course.model.QuickInfo
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.CustomXAxisRenderer
import com.online.course.ui.widget.LoadingDialog

class DashboardFrag : Fragment(), ItemCallback<QuickInfo>, View.OnClickListener {

    private lateinit var mBinding: FragDashboardBinding
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragDashboardBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val user = App.loggedInUser!!
        val userTxt = "${getString(R.string.hi)} ${user.name} \uD83D\uDC4B"
        mBinding.dashbaordHeaderNameTv.text = userTxt

        mBinding.dashboardHeaderNavBtn.setOnClickListener(this)
        mBinding.dashboardHeaderCartBtn.setOnClickListener(this)
        mBinding.dashboardHeaderNotificatonBtn.setOnClickListener(this)

        (activity as MainActivity).hideToolbar(
            App.getShapeFromColor(
                mBinding.dashboardHeaderContainer,
                R.color.accent, 20f
            ),
            App.getShapeFromColor(
                mBinding.dashboardContainer,
                R.color.pageBg
            )
        )

        (activity as MainActivity).setDrawerLock(false)

        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

//        App.initHeader(mBinding.dashboardStatusBar)
        initChart()

        val presenter = CommonApiPresenterImpl.getInstance()
        presenter.getQuickInfo(this)
    }

    private fun initChart() {
        val dashboardCubicChart = mBinding.dashboardCubicChart

        dashboardCubicChart.setXAxisRenderer(
            CustomXAxisRenderer(
                dashboardCubicChart.viewPortHandler,
                dashboardCubicChart.xAxis,
                dashboardCubicChart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )

        dashboardCubicChart.setViewPortOffsets(0f, 0f, 0f, 0f)
        dashboardCubicChart.description.isEnabled = false

        val xAxis = dashboardCubicChart.xAxis
        xAxis.typeface = ResourcesCompat.getFont(requireContext(), R.font.regular)
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.gray81)
        xAxis.textSize = 14f
        xAxis.setLabelCount(14, true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawGridLines(true)
        xAxis.setDrawAxisLine(false)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.setDrawGridLinesBehindData(true)
        xAxis.gridColor = ContextCompat.getColor(requireContext(), R.color.lightGray2)
        xAxis.setGridDashedLine(DashPathEffect(floatArrayOf(15f, 15f), 0f))
//        val xMax = financialCubicChart.data.getDataSetByIndex(0).xMax
//        val xMin: Float = 0f
//        xAxis.spaceMax = 0.2f
//        xAxis.spaceMax = 0.2f

        val axisLeft = dashboardCubicChart.axisLeft
        axisLeft.axisMinimum = 0f
        axisLeft.setDrawGridLines(false)
        axisLeft.setDrawAxisLine(false)
        axisLeft.setDrawZeroLine(false)
//        yAxis.draw
        axisLeft.isEnabled = false

        val axisRight = dashboardCubicChart.axisRight
        axisRight.axisMaximum = 0f
        axisRight.setDrawGridLines(false)
        axisRight.setDrawAxisLine(false)
        axisRight.setDrawZeroLine(false)
//        yAxis.draw
        axisRight.isEnabled = false

        dashboardCubicChart.setTouchEnabled(true)
        dashboardCubicChart.setPinchZoom(false)
        dashboardCubicChart.isDoubleTapToZoomEnabled = false
        dashboardCubicChart.setScaleEnabled(false)
        dashboardCubicChart.legend.isEnabled = false
        dashboardCubicChart.invalidate()
    }

    override fun onItem(info: QuickInfo, vararg args: Any) {
        if (context == null) return

        App.quickInfo = info
        mLoadingDialog.dismiss()

        if (info.unreadNotifs.count > 0) {
            mBinding.dashboardHeaderNotificatonCircleView.text = info.unreadNotifs.count.toString()
            mBinding.dashboardHeaderNotificatonCircleView.visibility = View.VISIBLE
            mBinding.dashbaordHeaderDescTv.text =
                ("${getString(R.string.you_have_new)} ${info.unreadNotifs.count} ${getString(R.string.events)}")
        } else {
            mBinding.dashbaordHeaderDescTv.text = getString(R.string.you_have_no_new_events)
        }

        if (info.cartItemsCount > 0) {
            mBinding.dashboardHeaderCartCircleView.text = info.cartItemsCount.toString()
            mBinding.dashboardHeaderCartCircleView.visibility = View.VISIBLE
        }

        if (App.loggedInUser!!.isUser()) {
            addStudentTopRvData(info)
        } else {
            addInstructorTopRvData(info)
        }

        addGeneralSlider(info)
        addChartData(info)
        addNoticeBoard(info)
    }

    override fun onFailed() {
        mLoadingDialog.dismiss()
    }

    private fun addChartData(info: QuickInfo) {
        if (App.loggedInUser!!.isUser()) {
            mBinding.dashboardMonthSalesTv.text = getString(R.string.learning_statistics)
        } else {
            mBinding.dashboardMonthSalesTv.text = getString(R.string.month_sales)
        }

        val data = info.monthlyChart.data
        val chart = mBinding.dashboardCubicChart

        if (data.isNotEmpty()) {
            val layoutParams = chart.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = Utils.changeDpToPx(requireContext(), 300f).toInt()
            chart.requestLayout()
        }

        val values = ArrayList<Entry>()
        val dateLabels = ArrayList<String>()

        values.add(Entry(0f, data[data.size - 1].toFloat()))
        dateLabels.add("")

        for ((index, item) in data.withIndex()) {
            val entry = Entry((index + 1).toFloat(), item.toFloat())
            values.add(entry)

            dateLabels.add((index + 1).toString())
        }

        values.add(Entry(13f, data[0].toFloat()))
        dateLabels.add("")

        val xAxis = chart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return dateLabels[value.toInt()]
            }
        }

        val set = LineDataSet(values, "")

        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        set.fillDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.round_view_gradient_accent_bottom_corner20
        )
        set.setDrawFilled(true)
        set.setDrawCircles(false)
//        set.setCircleColor(ContextCompat.getColor(requireContext(), R.color.orange_accent_light))
//        set.setDrawCircleHole(true)
//        set.circleRadius = 4f
//        set.setCircleColor(Color.BLACK)
        set.lineWidth = 1.8f
        set.color = ContextCompat.getColor(requireContext(), R.color.chartLightGreen)
        set.setDrawHighlightIndicators(false)

        val lineData = LineData(set)

        lineData.setValueTypeface(
            ResourcesCompat.getFont(
                requireContext(),
                R.font.regular
            )
        )
        lineData.setValueTextSize(9f)
        lineData.setDrawValues(false)

        val mv = CustomMarkerView(requireContext())
        chart.marker = mv

        // set data
        chart.data = lineData
//        chart.onChartGestureListener = this
        chart.invalidate()
    }

    private fun addNoticeBoard(info: QuickInfo) {
        if (info.unreadNoticeboards.isNotEmpty()) {
            mBinding.dashboardNoticesViewPager.adapter =
                NoticeBoardSliderAdapter(info.unreadNoticeboards)
            mBinding.dashboardNoticesIndicator.setViewPager2(mBinding.dashboardNoticesViewPager)

        } else {
            mBinding.dashboardNoticesTv.visibility = View.GONE
            mBinding.dashboardNoticesViewPager.visibility = View.GONE
            mBinding.dashboardNoticesIndicator.visibility = View.GONE
        }
    }


    private fun addGeneralSlider(info: QuickInfo) {
        val menuItems = ArrayList<MenuItem>()

        var menuItem = MenuItem()
        menuItem.title = Utils.formatPrice(requireContext(), info.balance, false)

        menuItem.desc = getString(R.string.account_balance)
        menuItem.type = DashboardGeneralSliderAdapter.Type.BALANCE.value
        menuItems.add(menuItem)

        if (info.badges?.nextBadge != null && !App.loggedInUser!!.isUser()) {
            menuItem = MenuItem()
            menuItem.title = getString(R.string.top_seller)
            menuItem.desc = getString(R.string.next_badge)
            menuItem.type = DashboardGeneralSliderAdapter.Type.BADGE.value
            menuItem.progress = info.badges!!.percent.toInt()
            menuItems.add(menuItem)
        }

        menuItem = MenuItem()
        menuItem.title = info.availablePoints.toString()
        menuItem.desc = getString(R.string.reward_points)
        menuItem.type = DashboardGeneralSliderAdapter.Type.POINTS.value
        menuItem.isClickable = true
        menuItems.add(menuItem)

        mBinding.dashboardStatisticsViewPager.adapter =
            DashboardGeneralSliderAdapter(menuItems, requireActivity() as MainActivity)
        mBinding.dashboardStatisticsIndicator.setViewPager2(mBinding.dashboardStatisticsViewPager)
    }

    private fun addInstructorTopRvData(info: QuickInfo) {
        val items = ArrayList<CommonItem>()
        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.upcomingLiveSessions.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.pending_live_sessions)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_video_green
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_green
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.supportsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.support_messages)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_email_red
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_red
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.monthlySalesCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.monthly_sales_count)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_wallet_blue
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_blue
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.commentsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.comments)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_comments_light_green
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_green2
            }
        })
        mBinding.dashboardStatisticsRv.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        mBinding.dashboardStatisticsRv.adapter = CardStatisticsRvAdapter(items)
    }

    private fun addStudentTopRvData(info: QuickInfo) {
        val items = ArrayList<CommonItem>()
        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.upcomingLiveSessions.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.upcoming_live_sessions)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_video_green
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_green
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.supportsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.support_messages)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_email_red
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_red
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.reserveMeetingsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.meetings)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_wallet_blue
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_blue
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return info.commentsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.comments)
            }

            override fun imgResource(): Int? {
                return R.drawable.ic_comments_light_green
            }

            override fun imgBgResource(): Int? {
                return R.drawable.circle_light_green2
            }
        })

        mBinding.dashboardStatisticsRv.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        mBinding.dashboardStatisticsRv.adapter = CardStatisticsRvAdapter(items)
    }

    class CustomMarkerView(context: Context) :
        MarkerView(context, R.layout.chart_marker) {

        private var mOffset: MPPointF? = null

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        override fun refreshContent(
            e: Entry,
            highlight: Highlight?
        ) {
            val chartMarkerTv = rootView.findViewById<TextView>(R.id.chartMarkerTv)
            if (App.loggedInUser!!.isUser()) {
                chartMarkerTv.text = e.y.toString()
            } else {
                chartMarkerTv.text =
                    (App.appConfig.currency.sign + e.y.toString()) // set the entry-value as the display text
            }
            super.refreshContent(e, highlight)
        }


        override fun draw(canvas: Canvas?, posX: Float, posY: Float) {
            super.draw(canvas, posX, posY)
            getOffsetForDrawingAtPoint(posX, posY);
        }

        override fun getOffset(): MPPointF {
            if (mOffset == null) {
//                var height = (-height).toFloat() - 20
//
//                val point = chart.getValuesByTouchPoint(
//                    (-(width / 2)).toFloat(),
//                    height,
//                    YAxis.AxisDependency.LEFT
//                )
//
//                while (chart.viewPortHandler.isInBounds(point.x.toFloat(), point.y.toFloat())) {
//                    height -= 10
//                }

                // center the marker horizontally and vertically
                mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat() - 20)
            }

            return mOffset as MPPointF
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dashboardHeaderNavBtn -> {
                (activity as MainActivity).openDrawer()
            }

            R.id.dashboardHeaderNotificatonBtn -> {
                (activity as MainActivity).transact(NotifsFrag())
            }

            R.id.dashboardHeaderCartBtn -> {
                (activity as MainActivity).transact(CartFrag())
            }
        }
    }

}