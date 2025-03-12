package com.online.course.ui.widget

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.online.course.R
import com.online.course.databinding.DialogProvidersFiltersBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toInt
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.Category
import com.online.course.model.Count
import com.online.course.model.Data
import com.online.course.model.KeyValuePair
import com.online.course.presenterImpl.ProvidersFiltersPresenterImpl

class ProvidersFiltersDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogProvidersFiltersBinding
    private val mSelectedCatsMap: MutableMap<Int, Chip> = HashMap()
    private var mCallback: ItemCallback<ArrayList<KeyValuePair>>? = null
    private var mSelectedList: ArrayList<KeyValuePair>? = null

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogProvidersFiltersBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun setCallback(callback: ItemCallback<ArrayList<KeyValuePair>>) {
        mCallback = callback
    }

    private fun init() {
        val presenter = ProvidersFiltersPresenterImpl(this)
        presenter.getCategories()

        mBinding.providersFiltersBestSellersRd.tag = "top_sale"
        mBinding.providersFiltersBestRatedRd.tag = "top_rate"

        mBinding.providersFiltersCancelBtn.setOnClickListener(this)
        mBinding.providersFiltersApplyBtn.setOnClickListener(this)

        val list = requireArguments().getParcelableArrayList<KeyValuePair>(App.SELECTED)
        if (list != null) {
            mSelectedList = list

            var item = getItem("available_for_meetings")

            if (item != null) {
                mBinding.providersFiltersAvailableForMeetingsSwitch.isChecked = true
            }

            item = getItem("free_meetings")
            if (item != null) {
                mBinding.providersFiltersFreeMeetingsSwitch.isChecked = true
            }

            item = getItem("discount")
            if (item != null) {
                mBinding.providersFiltersMeetingDiscountsSwitch.isChecked = true
            }

            item = getItem("downloadable")
            if (item != null) {
                mBinding.providersFiltersDownloadableContentSwitch.isChecked = true
            }

            item = getItem("sort")
            if (item != null) {
                when (item) {
                    "top_rate" -> mBinding.providersFiltersBestRatedRd.isChecked = true
                    "top_sale" -> mBinding.providersFiltersBestSellersRd.isChecked = true
                }
            }
        }
    }


    fun getItem(key: String): String? {
        for (item in mSelectedList!!) {
            if (item.key == key)
                return item.value
        }

        return null
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.providersFiltersCancelBtn -> {
                dismiss()
            }

            R.id.providersFiltersApplyBtn -> {
                val keyValPairs = ArrayList<KeyValuePair>()

                if (mBinding.providersFiltersAvailableForMeetingsSwitch.isChecked) {
                    keyValPairs.add(
                        KeyValuePair(
                            "available_for_meetings",
                            mBinding.providersFiltersAvailableForMeetingsSwitch.isChecked.toInt()
                                .toString()
                        )
                    )
                }

                if (mBinding.providersFiltersFreeMeetingsSwitch.isChecked) {
                    keyValPairs.add(
                        KeyValuePair(
                            "free_meetings",
                            mBinding.providersFiltersFreeMeetingsSwitch.isChecked.toInt()
                                .toString()
                        )
                    )
                }

                if (mBinding.providersFiltersMeetingDiscountsSwitch.isChecked) {
                    keyValPairs.add(
                        KeyValuePair(
                            "discount",
                            mBinding.providersFiltersMeetingDiscountsSwitch.isChecked.toInt()
                                .toString()
                        )
                    )
                }

                if (mBinding.providersFiltersDownloadableContentSwitch.isChecked) {
                    keyValPairs.add(
                        KeyValuePair(
                            "downloadable",
                            mBinding.providersFiltersDownloadableContentSwitch.isChecked.toInt()
                                .toString()
                        )
                    )
                }

                if (mBinding.courseOptionsSortRg.checkedRadioButtonId != R.id.courseOptionsAllRd) {
                    val rd =
                        mBinding.root.findViewById<RadioButton>(mBinding.courseOptionsSortRg.checkedRadioButtonId)
                    if (rd.tag != null) {
                        keyValPairs.add(KeyValuePair("sort", rd.tag.toString()))
                    }
                }

                if (mSelectedCatsMap.isNotEmpty()) {
                    for (key in mSelectedCatsMap.keys) {
                        keyValPairs.add(KeyValuePair("categories", key.toString()))
                    }
                }

                mCallback?.onItem(keyValPairs)
                dismiss()
            }
        }
    }

    fun onCategoriesRecevied(data: Data<Count<Category>>) {
        val cats = data.data!!.items

        for (category in cats) {
            val horizontalPadding = Utils.changeDpToPx(requireContext(), 25f).toInt()
            val verticalPadding = Utils.changeDpToPx(requireContext(), 15f).toInt()
            val chip = Chip(context)

            val drawable = ChipDrawable.createFromAttributes(
                requireContext(), null, 0,
                R.style.Widget_MaterialComponents_Chip_Choice
            )

            drawable.setPadding(
                horizontalPadding,
                verticalPadding,
                horizontalPadding,
                verticalPadding
            )

            drawable.shapeAppearanceModel =
                ShapeAppearanceModel.builder()
                    .setAllCorners(CornerFamily.ROUNDED, Utils.changeDpToPx(requireContext(), 10f))
                    .build()

            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )

            val bgColors = intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.accent),
                ContextCompat.getColor(requireContext(), R.color.white)
            )

            drawable.chipBackgroundColor = ColorStateList(states, bgColors)
            drawable.chipStrokeColor =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accent
                    )
                )
            drawable.chipStrokeWidth = Utils.changeDpToPx(requireContext(), 1f)
            drawable.shapeAppearanceModel.withCornerSize(10f)
            drawable.setTextSize(resources.getDimension(R.dimen.textsize_14d))

            val colors = intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.white),
                ContextCompat.getColor(requireContext(), R.color.accent)
            )

            chip.setChipDrawable(drawable)
            chip.text = category.title
            chip.gravity = Gravity.CENTER
            chip.typeface = ResourcesCompat.getFont(requireContext(), R.font.regular)
            chip.setTextColor(ColorStateList(states, colors))
            chip.tag = category.id
            chip.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    mSelectedCatsMap[category.id] = chip
                } else {
                    mSelectedCatsMap.remove(category.id)
                }
            }

            if (mSelectedList != null) {
                for (c in mSelectedList!!) {
                    if (c.key == "categories" && c.value.toInt() == category.id) {
                        chip.isChecked = true
                        break
                    }
                }
            }
            mBinding.providersFiltersCategoryChipGroup.addView(chip)
        }

        mBinding.providersFiltersCategoryProgressBar.visibility = View.GONE
    }
}