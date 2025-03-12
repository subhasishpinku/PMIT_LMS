package com.online.course.ui.frag

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.online.course.R
import com.online.course.databinding.FragProfileBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toBoolean
import com.online.course.manager.Utils.toInt
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.model.view.EmptyStateData
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ProfilePresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.LoadingDialog
import com.online.course.ui.widget.NewMessageDialog
import java.util.ArrayList
import kotlin.math.roundToInt

class ProfileFrag : NetworkObserverFragment(), TabLayout.OnTabSelectedListener,
    View.OnClickListener {

    private lateinit var mBinding: FragProfileBinding
    private lateinit var mPresenter: Presenter.ProfilePresenter
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var mUserProfile: UserProfile

    companion object {
        const val ABOUT_POSITION = 0
        const val RESEVRE_MEETING_POSITION = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val user = requireArguments().getParcelable<User>(App.USER)!!

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(
            toolbarOptions,
            "${user.name} ${getString(R.string.profile)}"
        )

        initBaseUI(user)
        initBottomSheet()

        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        mPresenter = ProfilePresenterImpl(this)
        mPresenter.getUserProfile(user.id)
    }

    private fun initBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBinding.profileBtnsContainer)
        mBottomSheetBehavior.isHideable = true
        mBottomSheetBehavior.isDraggable = false
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun initBaseUI(user: User) {
        mBinding.profileStartBtn.setOnClickListener(this)
        mBinding.profileEndBtn.setOnClickListener(this)

        if (user.avatar != null) {
            Glide.with(requireContext()).load(user.avatar).into(mBinding.profileUserImg)
        }

        mBinding.profileUserNameTv.text = user.name
        mBinding.profileUserRating.rating = user.rating
    }

    private fun initAllUI(userProfile: UserProfile) {
        initBaseUI(userProfile)

        mBinding.profileStudentsTv.text = userProfile.studentsCount.toString()
        mBinding.profileFollowersTv.text = userProfile.followers.size.toString()
        mBinding.profileClassesTv.text = userProfile.coursesCount.toString()

        if (userProfile.verified.toBoolean()) {
            mBinding.profileUserVerifiedImg.visibility = View.VISIBLE
        }
    }

    fun onUserProfileReceived(data: Data<UserProfile>) {
        mUserProfile = data.data!!
        initBaseUI(mUserProfile)
        initAllUI(mUserProfile)

        val tabLayout = mBinding.profileTabLayout
        val viewPager = mBinding.profileViewPager

        val aboutFrag = ProfileAboutFrag()
        var bundle = Bundle()
        bundle.putParcelable(App.USER, mUserProfile)
        aboutFrag.arguments = bundle

        var emptyStateData =
            EmptyStateData(R.drawable.no_course, R.string.no_courses, R.string.no_courses_desc)
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)

        val classesFrag = ClassesFrag()
        bundle = Bundle()
        bundle.putParcelableArrayList(App.COURSES, mUserProfile.courses as ArrayList<Course>)
        bundle.putBoolean(App.USE_GRID, true)
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)
        bundle.putBoolean(App.NESTED_ENABLED, true)
        classesFrag.arguments = bundle

        val badgesFrag = BadgesFrag()
        bundle = Bundle()
        bundle.putParcelableArrayList(App.BADGES, mUserProfile.badges as ArrayList<UserBadge>)
        badgesFrag.arguments = bundle

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(aboutFrag, getString(R.string.about))
        adapter.add(classesFrag, getString(R.string.classes))
        adapter.add(badgesFrag, getString(R.string.badges))

        bundle = Bundle()
        bundle.putParcelable(App.MEETING, mUserProfile.meetingReserve)
        bundle.putInt(App.USER_ID, mUserProfile.id)

        val meetingsFrag = ReserveMeetingFrag()
        meetingsFrag.arguments = bundle
        adapter.add(meetingsFrag, getString(R.string.meeting))

        if (mUserProfile.isOrganizaton()) {
            bundle = Bundle()
            bundle.putBoolean(App.NESTED_ENABLED, true)
            bundle.putParcelableArrayList(
                App.USERS,
                mUserProfile.organizationTeachers as ArrayList<User>
            )

            emptyStateData = EmptyStateData(
                R.drawable.no_instructor,
                R.string.no_instructor,
                R.string.no_instructor_desc
            )
            bundle.putParcelable(App.EMPTY_STATE, emptyStateData)

            val organizationTeachersFrag = UsersOrganizationsFrag()
            organizationTeachersFrag.arguments = bundle
            adapter.add(organizationTeachersFrag, getString(R.string.instructors))
        }

        viewPager.adapter = adapter
        tabLayout.addOnTabSelectedListener(this)
        tabLayout.setupWithViewPager(viewPager)

        mLoadingDialog.dismiss()
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val tabPosition = mBinding.profileTabLayout.selectedTabPosition
        Log.d("ProfileFrag", "onTabSelected: tabPosition:$tabPosition")

        if (tabPosition == ABOUT_POSITION || tabPosition == RESEVRE_MEETING_POSITION) {
            if (tabPosition == ABOUT_POSITION) {
                initAboutTab()
            } else {
                initReserveTab()
            }
        } else {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    private fun initAboutTab() {
        mBinding.profilePriceKeyTv.visibility = View.GONE
        mBinding.profilePriceValueTv.visibility = View.GONE
        mBinding.profilePriceWithDiscountValueTv.visibility = View.GONE

        if (mUserProfile.userIsFollower) {
            mBinding.profileStartBtn.text = getString(R.string.unfollow)
        } else {
            mBinding.profileStartBtn.text = getString(R.string.follow)
        }

        mBinding.profileStartBtn.visibility = View.VISIBLE

        mBinding.profileEndBtn.text = getString(R.string.send_message)

        if (!mUserProfile.publicMessage.toBoolean()) {
            mBinding.profileEndBtn.visibility = View.GONE
            val params = mBinding.profileStartBtn.layoutParams as ConstraintLayout.LayoutParams
            params.marginEnd = Utils.changeDpToPx(requireContext(), 16f).toInt()
            mBinding.profileStartBtn.requestLayout()
        }

        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initReserveTab() {
        val mMeetingReserve = mUserProfile.meetingReserve

        if (mMeetingReserve == null || mMeetingReserve.disabled.toBoolean() || mMeetingReserve.timings.size == 0) {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }

        mBinding.profilePriceKeyTv.visibility = View.VISIBLE
        mBinding.profilePriceValueTv.visibility = View.VISIBLE

        mBinding.profileStartBtn.visibility = View.GONE
        mBinding.profileEndBtn.visibility = View.VISIBLE
        mBinding.profileEndBtn.text = getString(R.string.reserve_a_meeting)

        val params = mBinding.profileEndBtn.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = resources.getDimension(R.dimen.margin_16).roundToInt()
        mBinding.profileEndBtn.requestLayout()

        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        if (mMeetingReserve.price != mMeetingReserve.priceWithDiscount && mMeetingReserve.discount > 0) {
            mBinding.profilePriceValueTv.text =
                Utils.formatPrice(requireContext(), mMeetingReserve.priceWithDiscount)
            mBinding.profilePriceWithDiscountValueTv.visibility = View.VISIBLE
            mBinding.profilePriceWithDiscountValueTv.text =
                Utils.formatPrice(requireContext(), mMeetingReserve.price)
            mBinding.profilePriceWithDiscountValueTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            mBinding.profilePriceValueTv.text =
                Utils.formatPrice(requireContext(), mMeetingReserve.price)
        }

    }

    override fun onClick(v: View?) {
        val adapter = mBinding.profileViewPager.adapter as ViewPagerAdapter

        when (v?.id) {
            R.id.profileStartBtn -> {
                (adapter.getItem(ABOUT_POSITION) as ProfileAboutFrag).onFollowUnfollow()
            }

            R.id.profileEndBtn -> {
                if (mBinding.profileTabLayout.selectedTabPosition == ABOUT_POSITION) {
                    (adapter.getItem(ABOUT_POSITION) as ProfileAboutFrag).onSendMessage()
                } else {
                    (adapter.getItem(RESEVRE_MEETING_POSITION) as ReserveMeetingFrag).onMeetingReserve()
                }
            }
        }
    }

    fun updateFollowBtn(follow: Int) {
        if (follow.toBoolean()) {
            mBinding.profileStartBtn.text = getString(R.string.unfollow)
        } else {
            mBinding.profileStartBtn.text = getString(R.string.follow)
        }
    }

}