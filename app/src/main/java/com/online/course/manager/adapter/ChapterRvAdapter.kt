package com.online.course.manager.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemChapterBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.ToastMaker
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.model.*
import com.online.course.model.view.ContentItem
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.*

class ChapterRvAdapter(
    items: List<ContentItem>,
    private val course: Course,
    private val activity: MainActivity
) :
    BaseArrayAdapter<ContentItem, ChapterRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        viewHolder.binding.itemChapterTitleTv.text = item.title
        viewHolder.binding.itemChapterRv.adapter =
            ChapterItemRvAdapter(item.chapterItems, activity.windowManager)
    }

    inner class ViewHolder(val binding: ItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root), OnItemClickListener {
        init {
            binding.itemChapterRv.addOnItemTouchListener(
                ItemClickListener(
                    binding.itemChapterRv,
                    this
                )
            )
        }

        override fun onClick(view: View?, position: Int, id: Int) {
            val item = items[bindingAdapterPosition].chapterItems[position]
            val nextFrag: Fragment
            val bundle = Bundle()

            if (!course.hasUserBought && !item.can.view) {
                showBuyAlert()
                return
            }

            when {
                item.isQuiz() -> {
                    if (course.hasUserBought) {
                        if (item.authStatus == Quiz.NOT_PARTICIPATED) {
                            bundle.putSerializable(App.TYPE, QuizzesFrag.Type.NOT_PARTICIPATED)
                        } else {
                            bundle.putSerializable(App.TYPE, QuizzesFrag.Type.MY_RESULT)
                        }
                    } else {
                        bundle.putSerializable(App.TYPE, QuizzesFrag.Type.NOT_PARTICIPATED)
                    }

                    nextFrag = QuizzesTabFrag()
                }
                item.isCert() -> {
                    nextFrag = CertificatesTabFrag()
                }
                item.isAssignment() -> {
                    nextFrag = AssignmentsTabFrag()
                }
                else -> {
                    bundle.putParcelable(App.COURSE, course)
                    bundle.putParcelable(App.ITEM, item)
                    nextFrag = CourseChapterItemFrag()
                }
            }

            nextFrag.arguments = bundle
            activity.transact(nextFrag)
        }

        private fun showBuyAlert() {
            ToastMaker.show(
                itemView.context,
                itemView.context.getString(R.string.error),
                itemView.context.getString(R.string.you_have_to_buy_this_course),
                ToastMaker.Type.ERROR
            )
        }

        override fun onLongClick(view: View?, position: Int, id: Int) {
        }
    }
}