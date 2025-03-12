package com.online.course.ui.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.DialogAppBinding
import com.online.course.manager.App

class AppDialog : DialogFragment(), View.OnClickListener {
    private lateinit var mOnDialogCreated: OnDialogCreated
    private lateinit var mBinding: DialogAppBinding

    companion object {
        val instance get() = AppDialog()
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog!!.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog!!.window
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogAppBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val title = requireArguments().getString(App.TITLE)
        val desc = requireArguments().getString(App.TEXT)
        val img = requireArguments().getString(App.IMG, null)
        val button = requireArguments().getString(App.BUTTON, null)

        if (button != null) {
            mBinding.appDialogOkBtn.text = button
        }

        if (img != null) {
            Glide.with(requireContext()).load(img).into(mBinding.appDialogImg)
            mBinding.appDialogImg.visibility = View.VISIBLE
        }

        mBinding.appDialogTitleTv.text = title
        mBinding.appDialogdescTV.text = desc

        mBinding.appDialogLeftCancelBtn.setOnClickListener(this)
        mBinding.appDialogRightCancelBtn.setOnClickListener(this)
        mBinding.appDialogDeleteBtn.setOnClickListener(this)
        mBinding.appDialogOkBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (this::mOnDialogCreated.isInitialized) {
            when (v.id) {
                R.id.appDialogLeftCancelBtn, R.id.appDialogRightCancelBtn -> mOnDialogCreated.onCancel()
                R.id.appDialogDeleteBtn, R.id.appDialogOkBtn -> mOnDialogCreated.onOk()
            }

            dismiss()
        }
    }

    fun setOnDialogBtnsClickedListener(dialogType: DialogType, onDialogCreated: OnDialogCreated) {
        mOnDialogCreated = onDialogCreated

        when (dialogType) {
            DialogType.OK -> {
                mBinding.appDialogRightCancelBtn.visibility = View.GONE
                mBinding.appDialogRightCancelBtn.text = getString(R.string.ok)
            }

            DialogType.LOGIN -> {
                mBinding.appDialogRightCancelBtn.visibility = View.GONE
                mBinding.appDialogRightCancelBtn.text = getString(R.string.login)
                mBinding.appDialogImg.visibility = View.VISIBLE
            }

            DialogType.DELETE -> {
                mBinding.appDialogOkBtn.visibility = View.GONE
                mBinding.appDialogRightCancelBtn.visibility = View.GONE
                mBinding.appDialogLeftCancelBtn.visibility = View.VISIBLE
                mBinding.appDialogDeleteBtn.visibility = View.VISIBLE
            }
        }
    }

    interface OnDialogCreated {
        fun onCancel()
        fun onOk()
    }

    enum class DialogType {
        YES_CANCEL,
        OK,
        DELETE,
        LOGIN
    }
}