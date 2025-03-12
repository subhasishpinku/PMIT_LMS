package com.online.course.ui.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.online.course.databinding.DialogNotifBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.Notif
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.NotifDialogPresenterImpl

class NotifDialog : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var mBinding: DialogNotifBinding
    private lateinit var mPresenter: Presenter.NotifDialogPresenter
    private var mStatusChangedCallback: ItemCallback<Int>? = null
    private var mItemPosition = 0

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
        mBinding = DialogNotifBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notif = requireArguments().getParcelable<Notif>(App.NOTIF)!!

        if (notif.status == Notif.Type.UNREAD.value()) {
            val presenter = NotifDialogPresenterImpl(this)
            presenter.setStatusToSeen(notif.id)
        }

        mBinding.notifTitleTv.text = notif.title
        mBinding.notifDescTV.text = Utils.getTextAsHtml(notif.message)
        mBinding.notifDateTimeTv.text = Utils.getDateTimeFromTimestamp(notif.createdAt)

        mBinding.notifCloseBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        dismiss()
    }

    fun setOnStatusChangeListener(callback: ItemCallback<Int>, position: Int) {
        mItemPosition = position
        mStatusChangedCallback = callback
    }

    fun onNotifSatusChange() {
        mStatusChangedCallback?.onItem(mItemPosition)
    }
}