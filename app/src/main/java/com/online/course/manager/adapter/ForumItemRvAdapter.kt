package com.online.course.manager.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemForumBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.ForumItem
import com.online.course.model.ForumItemAnswer
import com.online.course.presenter.Presenter
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.ForumAnswersFrag
import com.online.course.ui.widget.ForumOptionsDialog

class ForumItemRvAdapter(
    forumItems: List<ForumItem>,
    val frag: Fragment,
    val callback: ItemCallback<Any>
) :
    BaseArrayAdapter<ForumItem, ForumItemRvAdapter.ViewHolder>(forumItems) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemForumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val item = items[position]
        val context = viewholder.itemView.context
        val binding = viewholder.binding

        viewholder.init(item)

        val user = item.user
        if (!user.avatar.isNullOrEmpty()) {
            Glide.with(context).load(user.avatar).into(binding.itemForumUserImg)
        }

        binding.itemForumUserNameTv.text = user.name
        binding.itemForumDateTimeTv.text = Utils.getDateTimeFromTimestamp(item.createdAt)
        binding.itemForumQuestionTitleTv.text = item.title
        binding.itemForumQuestionDescTv.text = item.description

        if (item.answersCount > 0) {
            binding.itemForumQuestionAnswerUserImg1.visibility = View.VISIBLE
            binding.itemForumAnswersTv.visibility = View.VISIBLE
            binding.itemForumAnswersKeyTv.visibility = View.VISIBLE
            binding.itemForumSeperator.visibility = View.VISIBLE
            binding.itemForumLastActivityTv.visibility = View.VISIBLE
            binding.itemForumLastActivityKeyTv.visibility = View.VISIBLE

            binding.itemForumAnswersTv.text = item.answersCount.toString()
            binding.itemForumLastActivityTv.text = Utils.getDateTimeFromTimestamp(item.lastActivity)

            val activeUsers = item.activeUsers
            if (activeUsers.isNotEmpty()) {
                Glide.with(context).load(activeUsers[0])
                    .into(binding.itemForumQuestionAnswerUserImg1)
                if (activeUsers.size > 1) {
                    Glide.with(context).load(activeUsers[1])
                        .into(binding.itemForumQuestionAnswerUserImg2)
                    binding.itemForumQuestionAnswerUserImg2.visibility = View.VISIBLE
                }

                if (activeUsers.size > 2) {
                    Glide.with(context).load(activeUsers[2])
                        .into(binding.itemForumQuestionAnswerUserImg3)
                    binding.itemForumQuestionAnswerUserImg3.visibility = View.VISIBLE
                }
            }
        }
    }


    inner class ViewHolder(val binding: ItemForumBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        fun init(item: ForumItem) {
            binding.itemForumCard.setOnClickListener(this)
            val loggedInUser = App.loggedInUser!!
            if (item.isPinned) {
                binding.itemForumPinImg.visibility = View.VISIBLE
            }

            if (loggedInUser.isInstructor() || loggedInUser.isOrganizaton() || item.can.update) {
                binding.itemForumMoreBtn.visibility = View.VISIBLE
                binding.itemForumMoreBtn.setOnClickListener(this)
            }
        }

        override fun onClick(v: View?) {
            val item = items[bindingAdapterPosition]

            when (v?.id) {
                R.id.item_forum_more_btn -> {
                    val bundle = Bundle()
                    bundle.putParcelable(App.ITEM, item)

                    val dialog = ForumOptionsDialog()
                    dialog.setOnSuccessListener(callback)
                    dialog.arguments = bundle
                    dialog.show(frag.childFragmentManager, null)
                }

                R.id.item_forum_card -> {
                    val bundle = Bundle()
                    bundle.putParcelable(App.ITEM, item)

                    val forumAnswersFrag = ForumAnswersFrag()
                    forumAnswersFrag.arguments = bundle
                    (frag.activity as MainActivity).transact(forumAnswersFrag)
                }
            }
        }

    }
}