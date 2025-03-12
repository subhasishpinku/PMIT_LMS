package com.online.course.ui.widget

import android.os.Bundle
import android.text.TextUtils
import android.util.LayoutDirection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import com.online.course.R
import com.online.course.databinding.DialogCourseOptionsBinding
import com.online.course.manager.App
import com.online.course.manager.Utils.toInt
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.KeyValuePair
import java.util.*
import kotlin.collections.ArrayList

class ClassesOptionsDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogCourseOptionsBinding
    private var mCallback: ItemCallback<List<KeyValuePair>>? = null

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogCourseOptionsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun setCallback(callback: ItemCallback<List<KeyValuePair>>) {
        mCallback = callback
    }

    private fun init() {
        mBinding.courseOptionsNewsetRd.tag = "newest"
        mBinding.courseOptionsHighestPriceRd.tag = "expensive"
        mBinding.courseOptionsLowestPriceRd.tag = "inexpensive"
        mBinding.courseOptionsBestSellersRd.tag = "bestsellers"
        mBinding.courseOptionsBestRatedRd.tag = "best_rates"

        initChbx()

        mBinding.courseOptionsCancelBtn.setOnClickListener(this)
        mBinding.courseOptionsApplyBtn.setOnClickListener(this)

        val selectedList = requireArguments().getParcelableArrayList<KeyValuePair>(App.SELECTED)
        if (selectedList != null) {
            if (isItemSelected(selectedList, "upcoming") != null) {
                mBinding.courseOptionsUpcomingClassesSwitch.isChecked = true
            }

            if (isItemSelected(selectedList, "free") != null) {
                mBinding.courseOptionsFreeClassesSwitch.isChecked = true
            }

            if (isItemSelected(selectedList, "discount") != null) {
                mBinding.courseOptionsDiscountedClassesSwitch.isChecked = true
            }

            if (isItemSelected(selectedList, "downloadable") != null) {
                mBinding.courseOptionsDownloadableContentSwitch.isChecked = true
            }

            val sort = isItemSelected(selectedList, "sort")

            if (sort != null) {
                when (sort.value) {
                    "newest" -> mBinding.courseOptionsNewsetRd.isChecked = true
                    "expensive" -> mBinding.courseOptionsHighestPriceRd.isChecked = true
                    "inexpensive" -> mBinding.courseOptionsLowestPriceRd.isChecked = true
                    "bestsellers" -> mBinding.courseOptionsBestSellersRd.isChecked = true
                    "best_rates" -> mBinding.courseOptionsBestRatedRd.isChecked = true
                }
            }

            setListItemSelection(selectedList)
        }
    }

    private fun initChbx() {
        setCheckBoxDir(mBinding.courseOptionsLiveClassChbx)
        setCheckBoxDir(mBinding.courseOptionsCourseChbx)
        setCheckBoxDir(mBinding.courseOptionsTextLessonChbx)
        setCheckBoxDir(mBinding.courseOptionsShowOnlySubscribeChbx)
        setCheckBoxDir(mBinding.courseOptionsShowOnlyCertificateIncludedChbx)
        setCheckBoxDir(mBinding.courseOptionsShowOnlyCoursesWithQuizChbx)
        setCheckBoxDir(mBinding.courseOptionsShowOnlyFeaturedCoursesChbx)
    }

    private fun setCheckBoxDir(checkBox: CheckBox) {
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.LTR) {
            checkBox.layoutDirection = View.LAYOUT_DIRECTION_RTL
            checkBox.textDirection = View.TEXT_DIRECTION_LTR
        } else {
            checkBox.layoutDirection = View.LAYOUT_DIRECTION_LTR
            checkBox.textDirection = View.TEXT_DIRECTION_RTL
        }
    }

    private fun isItemSelected(
        selected: ArrayList<KeyValuePair>?,
        key: String
    ): KeyValuePair? {
        if (selected != null) {
            for (select in selected) {
                if (key == select.key) {
                    return select
                }
            }
        }

        return null
    }

    private fun setListItemSelection(
        selected: ArrayList<KeyValuePair>?
    ) {
        if (selected != null) {
            for (select in selected) {
                if (select.key == "type") {
                    when (select.value) {
                        "webinar" -> mBinding.courseOptionsLiveClassChbx.isChecked = true
                        "course" -> mBinding.courseOptionsCourseChbx.isChecked = true
                        "text_lesson" -> mBinding.courseOptionsTextLessonChbx.isChecked = true
                    }

                } else if (select.key == "moreOptions") {
                    when (select.value) {
                        "subscribe" -> mBinding.courseOptionsShowOnlySubscribeChbx.isChecked = true
                        "certificate_included" -> mBinding.courseOptionsShowOnlyCertificateIncludedChbx.isChecked =
                            true
                        "with_quiz" -> mBinding.courseOptionsShowOnlyCoursesWithQuizChbx.isChecked =
                            true
                        "featured" -> mBinding.courseOptionsShowOnlyFeaturedCoursesChbx.isChecked =
                            true
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.courseOptionsCancelBtn -> {
                dismiss()
            }

            R.id.courseOptionsApplyBtn -> {
                val queryParams = ArrayList<KeyValuePair>()

                if (mBinding.courseOptionsLiveClassChbx.isChecked) {
                    queryParams.add(KeyValuePair("type", "webinar"))
                }

                if (mBinding.courseOptionsCourseChbx.isChecked) {
                    queryParams.add(KeyValuePair("type", "course"))
                }

                if (mBinding.courseOptionsTextLessonChbx.isChecked) {
                    queryParams.add(KeyValuePair("type", "text_lesson"))
                }


                if (mBinding.courseOptionsShowOnlySubscribeChbx.isChecked) {
                    queryParams.add(KeyValuePair("moreOptions", "subscribe"))
                }

                if (mBinding.courseOptionsShowOnlyCertificateIncludedChbx.isChecked) {
                    queryParams.add(KeyValuePair("moreOptions", "certificate_included"))
                }

                if (mBinding.courseOptionsShowOnlyCoursesWithQuizChbx.isChecked) {
                    queryParams.add(KeyValuePair("moreOptions", "with_quiz"))
                }

                if (mBinding.courseOptionsShowOnlyFeaturedCoursesChbx.isChecked) {
                    queryParams.add(KeyValuePair("moreOptions", "featured"))
                }


                if (mBinding.courseOptionsUpcomingClassesSwitch.isChecked) {
                    queryParams.add(
                        KeyValuePair(
                            "upcoming",
                            mBinding.courseOptionsUpcomingClassesSwitch.isChecked.toInt().toString()
                        )
                    )
                }

                if (mBinding.courseOptionsFreeClassesSwitch.isChecked) {
                    queryParams.add(
                        KeyValuePair(
                            "free",
                            mBinding.courseOptionsFreeClassesSwitch.isChecked.toInt().toString()
                        )
                    )
                }

                if (mBinding.courseOptionsDiscountedClassesSwitch.isChecked) {
                    queryParams.add(
                        KeyValuePair(
                            "discount",
                            mBinding.courseOptionsDiscountedClassesSwitch.isChecked.toInt()
                                .toString()
                        )
                    )
                }

                if (mBinding.courseOptionsDownloadableContentSwitch.isChecked) {
                    queryParams.add(
                        KeyValuePair(
                            "downloadable",
                            mBinding.courseOptionsDownloadableContentSwitch.isChecked.toInt()
                                .toString()
                        )
                    )
                }

                if (mBinding.courseOptionsSortRg.checkedRadioButtonId != R.id.courseOptionsAllRd) {
                    queryParams.add(
                        KeyValuePair(
                            "sort",
                            mBinding.root.findViewById<RadioButton>(mBinding.courseOptionsSortRg.checkedRadioButtonId).tag.toString()
                        )
                    )
                }


                mCallback?.onItem(queryParams)
                dismiss()
            }
        }
    }
}