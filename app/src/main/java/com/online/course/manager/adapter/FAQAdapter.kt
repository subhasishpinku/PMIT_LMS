package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemQuestionAnswerBinding
import com.online.course.databinding.ItemQuestionTitleBinding
import com.online.course.manager.Utils
import com.online.course.model.view.ChapterView
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class FAQAdapter(
    groups: List<ExpandableGroup<*>>?
) : ExpandableRecyclerViewAdapter<FAQAdapter.TitleViewHolder, FAQAdapter.AnswerViewHolder>(
    groups
) {

    class TitleViewHolder(val binding: ItemQuestionTitleBinding) :
        GroupViewHolder(binding.root) {

        private fun initCard(expand: Boolean) {
            val params = binding.itemQuestionTitleCard.layoutParams as RecyclerView.LayoutParams
            params.topMargin = Utils.changeDpToPx(itemView.context, 8f).toInt()
            if (expand) {
                binding.itemQuestionTitleCard.setBackgroundResource(R.drawable.round_view_white_top_corner15)
            } else {
                binding.itemQuestionTitleCard.setBackgroundResource(R.drawable.round_view_white_corner15)
            }
            binding.itemQuestionTitleCard.requestLayout()
        }

        override fun expand() {
            initCard(true)
            binding.itemQuestionArrowImg.setImageResource(R.drawable.ic_arrow_top_gull_gray)
        }

        override fun collapse() {
            initCard(false)
            binding.itemQuestionArrowImg.setImageResource(R.drawable.ic_arrow_bottom_gull_gray)
        }
    }

    class AnswerViewHolder(val binding: ItemQuestionAnswerBinding) :
        ChildViewHolder(binding.root) {
        init {
            val params =
                binding.itemQuestionAnswerCard.layoutParams as RecyclerView.LayoutParams
            params.topMargin = 0
            params.bottomMargin = 0
            binding.itemQuestionAnswerCard.requestLayout()
        }
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): TitleViewHolder {
        return TitleViewHolder(
            ItemQuestionTitleBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): AnswerViewHolder {
        return AnswerViewHolder(
            ItemQuestionAnswerBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onBindChildViewHolder(
        holder: AnswerViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val context = holder.itemView.context
        val chapterView = group as ChapterView

        holder.binding.itemQuestionAnswerTv.text = chapterView.description

        val params = holder.binding.itemQuestionAnswerCard.layoutParams as RecyclerView.LayoutParams
        if (childIndex == group.itemCount - 1) {
            params.bottomMargin = Utils.changeDpToPx(context, 8f).toInt()
            holder.binding.itemQuestionAnswerCard.setBackgroundResource(R.drawable.round_view_white_bottom_corner15)
        } else {
            holder.binding.itemQuestionAnswerCard.setBackgroundResource(R.color.white)
            params.bottomMargin = 0
        }
        holder.binding.itemQuestionAnswerCard.requestLayout()
    }

    override fun onBindGroupViewHolder(
        holder: TitleViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val chapterView = group as ChapterView
        holder.binding.itemQuestionTitleTv.text = chapterView.title
    }
}