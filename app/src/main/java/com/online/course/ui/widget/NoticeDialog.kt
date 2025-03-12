package com.online.course.ui.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.online.course.databinding.DialogNoticeBinding
import com.online.course.databinding.DialogNotifBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.Notice
import com.online.course.model.Notif
import com.online.course.presenterImpl.NotifDialogPresenterImpl

class NoticeDialog : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var mBinding: DialogNoticeBinding

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
    ): View {
        mBinding = DialogNoticeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val notice = requireArguments().getParcelable<Notice>(App.ITEM)!!
        mBinding.noticeTitleTv.text = notice.title
        mBinding.noticeDesc.text = Utils.getTextAsHtml(notice.message)
        mBinding.noticeCreatedAtTv.text = Utils.getDateTimeFromTimestamp(notice.createdAt)

        val creator = notice.creator

        mBinding.noticeUserNameTv.text = creator.name
        if (!creator.avatar.isNullOrEmpty()) {
            Glide.with(requireContext()).load(creator.avatar).into(mBinding.noticeUserImg)
        }

        mBinding.noticeCloseBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        dismiss()
    }
}