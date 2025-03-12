package com.online.course.ui.widget

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.LayoutDirection
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.online.course.R
import com.online.course.databinding.DialogCourseFiltersBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.CourseFilter
import com.online.course.model.CourseFilterOption
import com.online.course.model.KeyValuePair
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ClassesFiltersDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogCourseFiltersBinding
    private lateinit var mFilterOptions: MutableList<CourseFilterOption>
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
        mBinding = DialogCourseFiltersBinding.inflate(inflater, container, false)
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
        mBinding.courseFiltersCancelBtn.setOnClickListener(this)

        mFilterOptions = ArrayList()
        val filters = requireArguments().getParcelableArrayList<CourseFilter>(App.FILTERS)!!
        val selected = requireArguments().getParcelableArrayList<KeyValuePair>(App.SELECTED)

        for (filter in filters) {
            mFilterOptions.addAll(filter.options)
            mBinding.courseFiltersContainer.addView(getFilterTextView(filter))

            for (option in filter.options) {
                mBinding.courseFiltersContainer.addView(getOptionCheckBox(option, selected))
            }
        }

        mBinding.courseFiltersContainer.addView(getApplyButton())
    }

    private fun getApplyButton(): MaterialButton {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            resources.getDimension(R.dimen.btn_height).toInt()
        )
        params.gravity = Gravity.START
        params.topMargin = Utils.changeDpToPx(requireContext(), 16f).toInt()
        params.bottomMargin = Utils.changeDpToPx(requireContext(), 16f).toInt()

        val button = MaterialButton(requireContext())
        button.isAllCaps = false
        button.layoutParams = params
        button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent))
        button.cornerRadius = resources.getDimension(R.dimen.btn_corner_radius).toInt()
        button.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.regular)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        button.text = getString(R.string.filter_items)
        button.setOnClickListener(this)
        return button
    }

    private fun getFilterTextView(filter: CourseFilter): MaterialTextView {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.START
        params.topMargin = resources.getDimension(R.dimen.margin_16).roundToInt()

        val textView = MaterialTextView(requireContext())
        textView.layoutParams = params
        textView.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.bold)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color5))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
        textView.text = filter.title
        return textView
    }

    private fun getOptionCheckBox(
        option: CourseFilterOption,
        selected: java.util.ArrayList<KeyValuePair>?
    ): MaterialCheckBox {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val checkbox = MaterialCheckBox(requireContext())
        checkbox.layoutParams = layoutParams
        checkbox.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.regular)
        checkbox.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color5))
        checkbox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
        checkbox.text = option.title
        checkbox.id = option.id
        checkbox.isUseMaterialThemeColors = false
        checkbox.setButtonDrawable(R.drawable.chbx_button_bg)
        checkbox.gravity = Gravity.START
        val padding = resources.getDimension(R.dimen.margin_8).roundToInt()
        checkbox.setPadding(padding, 0, padding, 0)
        if (isItemSelected(selected, option)) {
            checkbox.isChecked = true
        }

        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.LTR) {
            checkbox.layoutDirection = View.LAYOUT_DIRECTION_RTL
            checkbox.textDirection = View.TEXT_DIRECTION_LTR
        } else {
            checkbox.layoutDirection = View.LAYOUT_DIRECTION_LTR
            checkbox.textDirection = View.TEXT_DIRECTION_RTL
        }
        return checkbox
    }

    private fun isItemSelected(
        selected: ArrayList<KeyValuePair>?,
        option: CourseFilterOption
    ): Boolean {
        if (selected != null) {
            for (select in selected) {
                if (option.id == select.value.toInt()) {
                    return true
                }
            }
        }

        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.courseFiltersCancelBtn -> {
                dismiss()
            }

            else -> {
                val keyValPairs = ArrayList<KeyValuePair>()

                if (this::mFilterOptions.isInitialized) {
                    for (option in mFilterOptions) {
                        val chbx = mBinding.root.findViewById<CheckBox>(option.id)
                        if (chbx.isChecked) {
                            keyValPairs.add(KeyValuePair("filter_option", option.id.toString()))
                        }
                    }
                }

                mCallback?.onItem(keyValPairs)
                dismiss()
            }
        }
    }
}