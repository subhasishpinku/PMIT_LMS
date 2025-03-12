package com.online.course.ui.frag.abs

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.ui.MainActivity


interface EmptyState {

    fun emptyViewBinding(): EmptyStateBinding

    fun getVisibleFrag(): Fragment

    fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return null
    }

    fun showLoginState() {
        val view = emptyViewBinding()
        val visibleFrag = getVisibleFrag()

        view.root.visibility = View.VISIBLE
        view.emptyStateImg.setImageResource(R.drawable.learning)
        view.emptyStatetitleTv.text = visibleFrag.getString(R.string.please_login_error)
        view.emptyStateDescTV.text = visibleFrag.getString(R.string.please_login_error_desc)
        view.emptyStateLoginBtn.visibility = View.VISIBLE
        view.emptyStateLoginBtn.text = visibleFrag.getString(R.string.login)
        view.emptyStateLoginBtn.setOnClickListener {
            (visibleFrag.activity as MainActivity).goToLoginPage(getRefreshListener())
        }
    }

    fun showEmptyState(
        @DrawableRes img: Int,
        @StringRes titleRes: Int,
        @StringRes descRes: Int
    ) {
        val view = emptyViewBinding()
        val context = getVisibleFrag()

        view.root.visibility = View.VISIBLE
        view.emptyStateImg.setImageResource(img)
        view.emptyStatetitleTv.text = context.getString(titleRes)
        view.emptyStateDescTV.text = context.getString(descRes)
    }

    fun showEmptyState(
        @DrawableRes img: Int,
        titleRes: String,
        descRes: String
    ) {
        val view = emptyViewBinding()

        view.root.visibility = View.VISIBLE
        view.emptyStateImg.setImageResource(img)
        view.emptyStatetitleTv.text = titleRes
        view.emptyStateDescTV.text = descRes
    }

    fun hideEmptyState() {
        val emptyViewBinding = emptyViewBinding()
        emptyViewBinding.emptyStateLoginBtn.visibility = View.GONE
        emptyViewBinding.root.visibility = View.GONE
    }
}