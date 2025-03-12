package com.online.course.ui.widget

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.online.course.R
import com.online.course.databinding.TwoItemsSelectionBinding

class TwoItemSelectionHelper(
    binding: TwoItemsSelectionBinding,
    private val firstItem: Item,
    private val secondItem: Item,
    private val itemSelected: ItemSelected
) :
    View.OnClickListener {

    private var mSelectedBtn: View? = null

    class Item(var title: String, var enabled: Boolean = true)

    interface ItemSelected {
        fun onItemSelected(item: Item)
    }

    init {
        binding.selectionFirstItemBtn.text = firstItem.title
        binding.selectionSecondItemBtn.text = secondItem.title

        binding.selectionFirstItemBtn.setOnClickListener(this)
        binding.selectionSecondItemBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v == mSelectedBtn) {
            return
        }

        when (v.id) {
            R.id.selection_first_item_btn -> {
                itemSelected.onItemSelected(firstItem)
                if (!firstItem.enabled) {
                    return
                }
            }

            R.id.selection_second_item_btn -> {
                itemSelected.onItemSelected(secondItem)
                if (!secondItem.enabled) {
                    return
                }
            }
        }

        val context = v.context

        mSelectedBtn?.setBackgroundColor(
            ContextCompat.getColor(
                context,
                android.R.color.transparent
            )
        )
        if (mSelectedBtn != null) {
            (mSelectedBtn as TextView).setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.gull_gray
                )
            )
        }

        v.setBackgroundResource(R.drawable.two_tab_bg)
        (v as TextView).setTextColor(ContextCompat.getColor(context, R.color.white))

        mSelectedBtn = v
    }
}