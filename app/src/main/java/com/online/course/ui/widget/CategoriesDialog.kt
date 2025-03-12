package com.online.course.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.online.course.R
import com.online.course.databinding.DialogBlogCategoriesBinding
import com.online.course.manager.adapter.BlogCategoriesRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.Category
import com.online.course.model.KeyValuePair
import com.online.course.presenterImpl.BlogCategoriesPresenterImpl
import com.online.course.presenterImpl.CategoriesDialogPresenterImpl
import com.online.course.presenterImpl.CategoriesPresenterImpl
import com.online.course.presenterImpl.CategoryPresenterImpl

class CategoriesDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener,
    OnItemClickListener {

    private lateinit var mBinding: DialogBlogCategoriesBinding
    private var mCallback: ItemCallback<List<KeyValuePair>>? = null

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogBlogCategoriesBinding.inflate(inflater, container, false)
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
        mBinding.blogsCategoriesTv.text = getString(R.string.categories)
        mBinding.blogsCategoriesCancelBtn.setOnClickListener(this)
        val presenter = CategoriesDialogPresenterImpl(this)
        presenter.getCategories()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.blogs_categories_cancel_btn -> {
                dismiss()
            }
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val category =
            (mBinding.blogsCategoriesRv.adapter as BlogCategoriesRvAdapter).items[position]

        val keyVals = ArrayList<KeyValuePair>()
        keyVals.add(KeyValuePair("cat", category.id.toString()))

        mCallback?.onItem(keyVals)
        dismiss()
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun onCategoriesRecevied(items: List<Category>) {
        val cats = ArrayList<Category>()
        for (item in items) {
            if (item.subCategories.isNullOrEmpty()) {
                cats.add(item)
            } else {
                cats.addAll(item.subCategories)
            }
        }

        mBinding.blogsCategoriesRvProgressBar.visibility = View.GONE
        mBinding.blogsCategoriesRv.adapter = BlogCategoriesRvAdapter(cats)
        mBinding.blogsCategoriesRv.addOnItemTouchListener(
            ItemClickListener(
                mBinding.blogsCategoriesRv,
                this
            )
        )
    }
}