package com.online.course.manager.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemCategoryBinding
import com.online.course.manager.App
import com.online.course.model.Category
import com.online.course.model.view.CategoryView
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.CategoryFrag
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class CategoriesRvAdapter(
    groups: List<ExpandableGroup<*>>,
    private val activity: MainActivity
) :
    ExpandableRecyclerViewAdapter<CategoriesRvAdapter.CategoryViewHolder, CategoriesRvAdapter.SubCategoryViewHolder>(
        groups
    ) {

    companion object {
        private const val TAG = "CategoriesRvAdapter"
    }

    fun getRotationAnim(expand: Boolean): RotateAnimation {
        val to: Float
        val from: Float
        if (expand) {
            from = 0f
            to = 90f
        } else {
            from = 90f
            to = 0f
        }

        val rotation = RotateAnimation(
            from,
            to,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotation.fillAfter = true

        return rotation
    }


    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        GroupViewHolder(binding.root) {

        override fun expand() {
            val listPos = expandableList.getUnflattenedPosition(adapterPosition)
            val expanded = (groups[listPos.groupPos] as CategoryView).expanded

            if (listPos.type == ExpandableListPosition.GROUP) {
                val rotation = getRotationAnim(true)
                if (expanded) {
                    rotation.duration = 0
                } else {
                    rotation.duration = 400
                    (groups[listPos.groupPos] as CategoryView).expanded = true
                }
                binding.itemCategoryArrowImg.startAnimation(rotation)
            }

            binding.itemCategoryImgBottomDash.visibility = View.VISIBLE
            binding.itemCategoryDivider.visibility = View.INVISIBLE
        }

        override fun collapse() {
            val listPos = expandableList.getUnflattenedPosition(adapterPosition)
            val expanded = (groups[listPos.groupPos] as CategoryView).expanded

            if (listPos.type == ExpandableListPosition.GROUP && expanded) {
                (groups[listPos.groupPos] as CategoryView).expanded = false
                val rotation = getRotationAnim(false)
                rotation.duration = 400
                binding.itemCategoryArrowImg.startAnimation(rotation)
            }

            binding.itemCategoryImgBottomDash.visibility = View.INVISIBLE
            binding.itemCategoryDivider.visibility = View.VISIBLE
        }
    }

    class SubCategoryViewHolder(val binding: ItemCategoryBinding) : ChildViewHolder(binding.root) {
        init {
            binding.itemCategoryImg.visibility = View.INVISIBLE
            binding.itemCategoryCircleImg.visibility = View.VISIBLE

            binding.itemCategoryDivider.visibility = View.GONE
            binding.itemCategoryArrowImg.visibility = View.GONE
        }
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): SubCategoryViewHolder {
        return SubCategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onBindChildViewHolder(
        holder: SubCategoryViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val context = holder.itemView.context

        val category = (group as CategoryView).items[childIndex]

        holder.binding.itemCategoryTitleTv.text = category.title
        holder.binding.itemCategoryDescTv.text =
            ("${category.count} ${context.getString(R.string.courses)}")
        holder.itemView.setOnClickListener {
            showCategoryPage(category)
        }

        holder.binding.itemCategoryCircleImgTopDash.visibility = View.VISIBLE

        if (childIndex == group.itemCount - 1) {
            holder.binding.itemCategoryCircleImgBottomDash.visibility = View.INVISIBLE
        } else {
            holder.binding.itemCategoryCircleImgBottomDash.visibility = View.VISIBLE
        }

        holder.binding.itemCategoryImg.setImageResource(R.drawable.circle_gray_stroke)
    }

    override fun onGroupClick(flatPos: Int): Boolean {
        val expand = super.onGroupClick(flatPos)
        val groupPos = expandableList.getUnflattenedPosition(flatPos).groupPos
        val categoryView = groups[groupPos] as CategoryView
        if (categoryView.subCats.isEmpty()) {
            showCategoryPage(categoryView)
        }
        return expand
    }

    override fun onBindGroupViewHolder(
        holder: CategoryViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val context = holder.itemView.context

        val categoryView = group as CategoryView

        if (categoryView.subCats.isEmpty()) {
            holder.binding.itemCategoryArrowImg.visibility = View.GONE
        } else {
            holder.binding.itemCategoryArrowImg.visibility = View.VISIBLE
        }

//        if (categoryView.expanded) {
//            val rotationAnim = getRotationAnim(true)
//            rotationAnim.duration = 0
//            holder.binding.itemCategoryArrowImg.startAnimation(rotationAnim)
//        } else {
//            holder.binding.itemCategoryArrowImg.setImageResource(R.drawable.ic_arrow_right_gull_gray)
//        }

        holder.binding.itemCategoryTitleTv.text = categoryView.catTitle
        holder.binding.itemCategoryDescTv.text =
            ("${categoryView.count} ${context.getString(R.string.courses)}")

        if (categoryView.icon != null)
            Glide.with(context).load(categoryView.icon).into(holder.binding.itemCategoryImg)
    }

    private fun showCategoryPage(category: Category) {
        val bundle = Bundle()
        bundle.putParcelable(App.CATEGORY, category)

        val categoryFrag = CategoryFrag()
        categoryFrag.arguments = bundle

        activity.transact(categoryFrag)
    }

    private fun showCategoryPage(categoryView: CategoryView) {
        val category = Category()
        category.subCategories = categoryView.subCats
        category.title = categoryView.title
        category.count = categoryView.count
        category.icon = categoryView.icon
        category.id = categoryView.id
        showCategoryPage(category)
    }
}