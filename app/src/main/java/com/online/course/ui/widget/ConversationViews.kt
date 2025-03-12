package com.online.course.ui.widget

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.Conversation
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ConversationViews {

    class UserItem(private val conversation: Conversation) :
        Item<GroupieViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.item_conversation_user
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val userChatDateTimeTv =
                viewHolder.itemView.findViewById<TextView>(R.id.userChatDateTimeTv)
            val userChatTv = viewHolder.itemView.findViewById<TextView>(R.id.userChatTv)

            userChatTv.text = conversation.message
            userChatDateTimeTv.text = Utils.getDateTimeFromTimestamp(conversation.createdAt)
        }
    }

    class SystemUserItem(val conversation: Conversation) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.item_conversation_support_user
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val userChatDateTimeTv =
                viewHolder.itemView.findViewById<TextView>(R.id.userSupportChatDateTimeTv)
            val userChatTv = viewHolder.itemView.findViewById<TextView>(R.id.userSupportChatTv)
            val userChatImg =
                viewHolder.itemView.findViewById<CircleImageView>(R.id.userSupportChatImg)

            if (conversation.supporter!!.avatar != null) {
                Glide.with(viewHolder.itemView.context)
                    .load(conversation.supporter!!.avatar)
                    .into(userChatImg)
            }

            userChatTv.text = conversation.message
            userChatDateTimeTv.text = Utils.getDateTimeFromTimestamp(conversation.createdAt)
        }
    }


    class AttachmentItem<T>(
        private val frag: T,
        private val conversation: Conversation,
        private val systemUserAttachment: Boolean,
    ) : Item<GroupieViewHolder>(), View.OnClickListener where T : FileSize, T : Fragment {

        override fun getLayout(): Int {
            return R.layout.item_conversation_attachment
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val attachmentFileSizeProgressBar =
                viewHolder.itemView.findViewById<ProgressBar>(R.id.attachmentFileSizeProgressBar)

            val attachmentFileSizeTv =
                viewHolder.itemView.findViewById<TextView>(R.id.attachmentFileSizeTv)

            if (conversation.fileSize == null) {
                frag.getFileSize(conversation, object : ItemCallback<Long> {
                    override fun onItem(size: Long, vararg args: Any) {
                        Handler(Looper.getMainLooper()).post {
                            attachmentFileSizeProgressBar.visibility = View.GONE
                            conversation.fileSize = size
                            attachmentFileSizeTv.text =
                                Utils.humanReadableByteCountSI(
                                    conversation.fileSize!!
                                )
                        }
                    }
                })
            } else {
                attachmentFileSizeTv.text = Utils.humanReadableByteCountSI(
                    conversation.fileSize!!
                )
            }
        }

        override fun createViewHolder(itemView: View): GroupieViewHolder {
            val attachmentContainer = itemView.findViewById<View>(R.id.attachmentContainer)

            if (systemUserAttachment) {
                val layoutParams = attachmentContainer.layoutParams as FrameLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                itemView.requestLayout()
            }

            if (conversation.fileTitle.isNotEmpty()) {
                val titleTv = itemView.findViewById<TextView>(R.id.attachmentTitleTv)
                titleTv.text = conversation.fileTitle
            }

            attachmentContainer.setOnClickListener(this)
            return super.createViewHolder(itemView)
        }

        override fun onClick(v: View?) {
            val fileUrl =
                if (conversation.attachment != null) conversation.attachment else conversation.filePath

            val context = frag.requireContext()

            val path =
                App.Companion.Directory.TICKETS_ATTACHMENT.value() + File.separator + Utils.extractFileNameFromUrl(
                    fileUrl!!
                )
            val attachment = File(context.filesDir, path)
            if (attachment.exists()) {
                Utils.viewFile(context, attachment)
            } else {
                val bundle = Bundle()
                bundle.putString(App.URL, fileUrl)
                bundle.putString(App.DIR, App.Companion.Directory.TICKETS_ATTACHMENT.value())

                val loadingDialog = ProgressiveLoadingDialog()
                loadingDialog.setOnFileSavedListener(object : ItemCallback<String> {
                    override fun onItem(filePath: String, vararg args: Any) {
                        Utils.viewFile(context, File(filePath))
                    }
                })
                loadingDialog.arguments = bundle
                loadingDialog.show(frag.childFragmentManager, null)
            }
        }

    }

    interface FileSize {
        fun getFileSize(conversation: Conversation, callback: ItemCallback<Long>)
    }

}