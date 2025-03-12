package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.FragProfileAboutBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.Utils.toBoolean
import com.online.course.manager.Utils.toInt
import com.online.course.model.Follow
import com.online.course.model.UserProfile
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ProfileAboutPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState
import com.online.course.ui.widget.NewMessageDialog

class ProfileAboutFrag : Fragment(), EmptyState, MainActivity.OnRefreshListener {

    private lateinit var mBinding: FragProfileAboutBinding
    private lateinit var mUserProfile: UserProfile
    private lateinit var mPresenter: Presenter.ProfileAboutPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragProfileAboutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = ProfileAboutPresenterImpl(this)
        mUserProfile = requireArguments().getParcelable(App.USER)!!

        if (mUserProfile.bio != null) {
            mBinding.profileAboutBioTv.text = Utils.getTextAsHtml(mUserProfile.bio!!)
        }

        if (mUserProfile.offline.toBoolean() && !mUserProfile.offlineMessage.isNullOrEmpty()) {
            mBinding.profileAboutOfflineHeader.root.visibility = View.VISIBLE

            var headerTitle = if (mUserProfile.isOrganizaton()) {
                getString(R.string.organization)
            } else {
                getString(R.string.instructor)
            }

            headerTitle += " ${getString(R.string.is_not_available)}"

            mBinding.profileAboutOfflineHeader.HeaderInfoTitleTv.text = headerTitle
            mBinding.profileAboutOfflineHeader.HeaderInfoDescTv.text = mUserProfile.offlineMessage
        }

        if (mUserProfile.experiences.isNotEmpty()) {
            val builder = StringBuilder()

            for (experience in mUserProfile.experiences) {
                builder.append("- ${experience}\n")
            }

            mBinding.profileAboutExperiencesValueTv.text = builder.toString()
        } else {
            mBinding.profileAboutExperiencesKeyTv.visibility = View.GONE
            mBinding.profileAboutExperiencesValueTv.visibility = View.GONE
        }

        if (mUserProfile.educations.isNotEmpty()) {
            val builder = StringBuilder()

            for (education in mUserProfile.educations) {
                builder.append("- ${education}\n")
            }

            mBinding.profileAboutEducationsValueTv.text = builder.toString()
        } else {
            mBinding.profileAboutEducationsKeyTv.visibility = View.GONE
            mBinding.profileAboutEducationsValueTv.visibility = View.GONE
        }

        if (mUserProfile.bio.isNullOrEmpty() && mUserProfile.experiences.isEmpty() &&
            mUserProfile.educations.isEmpty()
        ) {
            showEmptyState()
        }
    }

    fun onFollowed(follow: Follow) {
        if (context == null) return

        mUserProfile.userIsFollower = follow.status.toBoolean()
        (parentFragment as ProfileFrag).updateFollowBtn(follow.status)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_biography, R.string.no_biography, R.string.no_biography_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.profileAboutEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return this
    }

    override fun refresh() {
        hideEmptyState()
        mUserProfile = App.loggedInUser!!
        init()
    }

    fun onSendMessage() {
        if (!App.isLoggedIn()) {
            (activity as MainActivity).goToLoginPage()
            return
        }

        val bundle = Bundle()
        bundle.putInt(App.USER_ID, mUserProfile.id)

        val dialog = NewMessageDialog()
        dialog.arguments = bundle

        dialog.show(childFragmentManager, null)
    }

    fun onFollowUnfollow() {
        if (!App.isLoggedIn()) {
            (activity as MainActivity).goToLoginPage()
            return
        }

        val follow = Follow()

        if (mUserProfile.userIsFollower) {
            follow.status = false.toInt()
        } else {
            follow.status = true.toInt()
        }

        mPresenter.follow(mUserProfile.id, follow)
    }

}