package com.online.course.ui.frag

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.online.course.R
import com.online.course.databinding.FragSettingsBinding
import com.online.course.manager.*
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.manager.db.AppDb
import com.online.course.model.BaseResponse
import com.online.course.model.ProfilePhoto
import com.online.course.model.ToolbarOptions
import com.online.course.model.UserProfile
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SettingsPresenterImpl
import com.online.course.ui.MainActivity
import kotlin.math.roundToInt

class SettingsFrag : Fragment(), View.OnClickListener, ViewPager.OnPageChangeListener,
    MainActivity.DrawerSlideListener {

    private lateinit var mBinding: FragSettingsBinding
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<String>
    private lateinit var mPresenter: Presenter.SettingsPresenter
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    interface SaveCallback {
        fun onSaveClicked()
        fun initTab()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                ) {
                    mActivityResultLauncher.launch("image/*")
                }
            }

        mActivityResultLauncher =
            registerForActivityResult(ResultContracts.SelectFile()) { uri ->
                if (uri != null) {
                    mBinding.settingsUserImgProgressBar.visibility = View.VISIBLE

                    Glide.with(requireContext()).load(uri).into(mBinding.meetingDetailsUserImg)
//                    val imageStream = requireContext().contentResolver.openInputStream(uri)
//                    val selectedImage = BitmapFactory.decodeStream(imageStream)
//                    mEncodedImg = Utils.encodeTo64(selectedImage)

//                    val profilePhoto = ProfilePhoto()
//                    profilePhoto.profileImage = mEncodedImg!!
                    mPresenter.uploadPhoto(UriToPath.getPath(requireContext(), uri))
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSettingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroyView() {
        (activity as MainActivity).setOnDrawerSlideListener(null)
        super.onDestroyView()
    }

    fun changeSaveBtnVisibility(visible: Boolean) {
        if (visible) {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun changeSaveBtnEnable(enable: Boolean) {
        mBinding.settingsSaveBtn.isEnabled = enable
    }

    private fun init() {
        mPresenter = SettingsPresenterImpl(this)

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.settings)

        initBottomSheet()
        initTabs()
        initUserInfo()

        mBinding.settingsAddOrRemovePhotoFab.setOnClickListener(this)
        mBinding.settingsSaveBtn.setOnClickListener(this)
        (activity as MainActivity).setOnDrawerSlideListener(this)
    }

    private fun initBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBinding.settingsSaveContainer)
        mBottomSheetBehavior.isDraggable = false
        mBottomSheetBehavior.isHideable = true
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initUserInfo() {
        val selectFinancialTab = arguments != null && requireArguments().getBoolean(App.FINANCIAL)
        if (selectFinancialTab) {
            val pager = mBinding.settingsUserViewPager
            pager.setCurrentItem(2, true)
        }

        val user = App.loggedInUser!!
        if (user.avatar != null) {
            Glide.with(requireContext()).load(user.avatar).into(mBinding.meetingDetailsUserImg)
        }
        mBinding.settingsUserNameTv.text = user.name
        mBinding.settingsUserTypeTv.text =
            if (user.userGroup == null) user.roleName else user.userGroup!!.name
    }

    private fun initTabs() {
        val tabLayout = mBinding.ssettingsUserTabLayout
        val viewPager = mBinding.settingsUserViewPager

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(SettingsGeneralFrag(), getString(R.string.general))
        adapter.add(SettingsSecurityFrag(), getString(R.string.security))
        adapter.add(SettingsFinancialFrag(), getString(R.string.financial))
        adapter.add(SettingsLocalizationFrag(), getString(R.string.localization))

        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(this)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settingsAddOrRemovePhotoFab -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        mPermissionResultLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    } else {
                        mPermissionResultLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )
                    }

                } else {
                    mActivityResultLauncher.launch("image/*")
                }
            }

            R.id.settingsSaveBtn -> {
                val viewPager = mBinding.settingsUserViewPager
                val viewPagerAdapter = viewPager.adapter as ViewPagerAdapter
                val saveCallback = viewPagerAdapter.getItem(viewPager.currentItem) as SaveCallback
                saveCallback.onSaveClicked()
            }
        }
    }

    fun onProfileSaved(response: BaseResponse) {
        if (context == null) return

        (activity as MainActivity).initUserInfo()
        mBinding.settingsUserImgProgressBar.visibility = View.GONE
        if (!response.isSuccessful) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.success),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        mRunnable?.let { mHandler?.removeCallbacks(it) }

        val viewPager = mBinding.settingsUserViewPager
        val viewPagerAdapter = viewPager.adapter as ViewPagerAdapter
        val saveCallback = viewPagerAdapter.getItem(position) as SaveCallback
        saveCallback.initTab()
    }

    override fun onDrawerSlide(corner: Float) {
        if (context == null) return

        val topCorner = Utils.changeDpToPx(requireContext(), 20f)

        val corners =
            floatArrayOf(topCorner, topCorner, topCorner, topCorner, corner, corner, corner, corner)
        val bg = mBinding.settingsSaveContainer.background as GradientDrawable
        bg.cornerRadii = corners
    }


}