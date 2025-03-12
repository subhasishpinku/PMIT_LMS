package com.online.course.ui.frag

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.FragMeetingDetailsBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toBoolean
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.Meeting
import com.online.course.model.ToolbarOptions
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.MeetingDetailsMoreDialog
import com.online.course.ui.widget.MeetingJoinDetailsDialog
import java.lang.StringBuilder

class MeetingDetailsFrag : Fragment(), View.OnClickListener {

    private lateinit var mBinding: FragMeetingDetailsBinding
    private lateinit var mMeeting: Meeting
    private var mIsForInstructor = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragMeetingDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.meeting_details)

        mMeeting = requireArguments().getParcelable(App.MEETING)!!
        mIsForInstructor = requireArguments().getBoolean(App.INSTRUCTOR_TYPE, false)

        mBinding.meetingDetailsCallBtn.setOnClickListener(this)
        mBinding.meetingDetailsEmailBtn.setOnClickListener(this)
        mBinding.meetingDetailsMoreBtn.setOnClickListener(this)

        if (!mMeeting.user.avatar.isNullOrEmpty()) {
            Glide.with(requireContext()).load(mMeeting.user.avatar)
                .into(mBinding.meetingDetailsUserImg)
        }

        if (mMeeting.inPerson.toBoolean()) {
            mBinding.meetingDetailsCondutionTypeTv.text = getString(R.string.in_persion)
        } else {
            mBinding.meetingDetailsCondutionTypeTv.text = getString(R.string.online)
        }

        if (mMeeting.studentCount > 0) {
            mBinding.meetingDetailsMeetingTypeTv.text =
                StringBuilder().append(getString(R.string.group)).append(" (${mMeeting.studentCount})")
        } else {
            mBinding.meetingDetailsMeetingTypeTv.text = getString(R.string.individual)
        }

        mBinding.meetingDetailsUserNameTv.text = mMeeting.user.name
        mBinding.meetingDetailsUserTypeTv.text = mMeeting.user.roleName
        mBinding.meetingDetailsStartDateTv.text = Utils.getDateFromTimestamp(mMeeting.date)
        mBinding.meetingDetailsStartTimeTv.text = mMeeting.time.start
        mBinding.meetingDetailsEndTimeTv.text = mMeeting.time.end
        mBinding.meetingDetailsAmountTv.text = Utils.formatPrice(requireContext(), mMeeting.amount)
        mBinding.meetingDetailsStatusTv.text = mMeeting.status

        if (mMeeting.description.isNotEmpty()) {
            mBinding.meetingDetailsDescriptionTv.text = mMeeting.description
        }else{
            mBinding.meetingDetailsDescriptionLayout.visibility = View.GONE
        }

        if (mMeeting.user.address.isNotEmpty()) {
            mBinding.meetingDetailsAddressTv.text = mMeeting.user.address
        }else{
            mBinding.meetingDetailsAddressLayout.visibility = View.GONE
        }

        mBinding.meetingDetailsDurationTv.text = Utils.getDuration(
            requireContext(), Utils.getSubtractedTimeInSeconds(
                mMeeting.time.end,
                mMeeting.time.start
            ).toInt()
        )

        if (mMeeting.status == Meeting.FINISHED || mMeeting.status == Meeting.CANCELED) {

            if (mMeeting.status == Meeting.CANCELED) {
                val red = ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
                mBinding.meetingDetailsStatusTv.setTextColor(red)
                mBinding.meetingDetailsOuterCircleImg.setImageResource(R.drawable.circle_red)
                mBinding.meetingDetailsMainBtn.text = getString(R.string.canceled)

            } else {
                val green = ContextCompat.getColor(
                    requireContext(),
                    R.color.accent
                )
                mBinding.meetingDetailsStatusTv.setTextColor(green)
                mBinding.meetingDetailsOuterCircleImg.setImageResource(R.drawable.circle_accent)
                mBinding.meetingDetailsMainBtn.text = getString(R.string.finished)
            }

            mBinding.meetingDetailsMoreBtn.visibility = View.GONE
            mBinding.meetingDetailsEmailBtn.visibility = View.GONE
            mBinding.meetingDetailsCallBtn.visibility = View.GONE

            Handler(Looper.getMainLooper()).postDelayed({
                mBinding.meetingDetailsMainBtn.isEnabled = false
            }, 140)
        } else {
            if (mMeeting.user.mobile.isNullOrEmpty() && mMeeting.user.email.isNullOrEmpty()) {
                mBinding.meetingDetailsEmailBtn.visibility = View.GONE
                mBinding.meetingDetailsCallBtn.visibility = View.GONE
            } else if (mMeeting.user.email.isNullOrEmpty()) {
                val params =
                    mBinding.meetingDetailsCallBtn.layoutParams as ConstraintLayout.LayoutParams
                params.marginEnd = 0
                mBinding.meetingDetailsCallBtn.requestLayout()

                mBinding.meetingDetailsEmailBtn.visibility = View.GONE
            } else if (mMeeting.user.mobile.isNullOrEmpty()) {
                val params =
                    mBinding.meetingDetailsEmailBtn.layoutParams as ConstraintLayout.LayoutParams
                params.marginStart = 0
                mBinding.meetingDetailsEmailBtn.requestLayout()

                val mark1Params =
                    mBinding.meetingDetailsStartDateImg.layoutParams as ConstraintLayout.LayoutParams
                mark1Params.topToBottom = R.id.meetingDetailsEmailBtn
                mBinding.meetingDetailsStartDateImg.requestLayout()

                val mark2Params =
                    mBinding.meetingDetailsDurationImg.layoutParams as ConstraintLayout.LayoutParams
                mark2Params.topToBottom = R.id.meetingDetailsEmailBtn
                mBinding.meetingDetailsDurationImg.requestLayout()

                mBinding.meetingDetailsCallBtn.visibility = View.GONE
            }

            if (mIsForInstructor) {
                mBinding.meetingDetailsMainBtn.setOnClickListener(this)

                if (mMeeting.link.isNullOrEmpty()) {
                    mBinding.meetingDetailsMainBtn.text = getString(R.string.create_join_info)
                } else {
                    mBinding.meetingDetailsMainBtn.text = getString(R.string.join_meeting)
                }
            } else {
                if (mMeeting.link.isNullOrEmpty()) {
                    mBinding.meetingDetailsMainBtn.text = getString(R.string.start_meeting)
                    mBinding.meetingDetailsMainBtn.isEnabled = false
                } else {
                    mBinding.meetingDetailsMainBtn.text = getString(R.string.join_meeting)
                    mBinding.meetingDetailsMainBtn.setOnClickListener(this)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.meetingDetailsMainBtn -> {
                if (mBinding.meetingDetailsMainBtn.text == getString(R.string.join_meeting)) {
                    Utils.openLink(requireContext(), mMeeting.link!!)
                }
            }

            R.id.meetingDetailsEmailBtn -> {
                Utils.openEmail(
                    requireContext(), getString(R.string.sending_email),
                    mMeeting.user.email!!, getString(R.string.meeting)
                )
            }

            R.id.meetingDetailsCallBtn -> {
                Utils.openCaller(requireContext(), mMeeting.user.mobile!!)
            }

            R.id.meetingDetailsMoreBtn -> {
                val bundle = Bundle()
                bundle.putParcelable(App.MEETING, mMeeting)

                val dialog: NetworkObserverBottomSheetDialog = if (mIsForInstructor) {
                    MeetingJoinDetailsDialog()
                } else {
                    MeetingDetailsMoreDialog()
                }

                dialog.arguments = bundle
                dialog.show(childFragmentManager, null)
            }
        }
    }
}