package com.online.course.manager.adapter

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemFavoriteBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.Favorite
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.CourseDetailsFrag
import com.online.course.ui.frag.FavoritesFrag
import com.online.course.ui.widget.AppDialog

class FavoritesRvAdapter(favorites: MutableList<Favorite>, private val frag: FavoritesFrag) :
    BaseArrayAdapter<Favorite, FavoritesRvAdapter.ViewHolder>(favorites) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val favorite = items[position]
        val context = viewholder.itemView.context
        val course = favorite.course

        if (course.discount > 0) {
            viewholder.binding.itemFavoriteCourseDiscountPctTv.text =
                (course.discount.toString() + "%" + context.getString(R.string.off))

            viewholder.binding.itemFavoriteCoursePriceTv.text =
                Utils.formatPrice(context, course.priceWithDiscount)

            viewholder.binding.itemFavoriteCoursePriceWithDiscountTv.text =
                Utils.formatPrice(context, course.price)

            viewholder.binding.itemFavoriteCoursePriceWithDiscountTv.paintFlags =
                Paint.STRIKE_THRU_TEXT_FLAG

        } else {
            viewholder.binding.itemFavoriteCoursePriceTv.text =
                Utils.formatPrice(context, course.price)
        }

        if (course.img != null) {
            Glide.with(viewholder.itemView.context).load(course.img)
                .into(viewholder.binding.itemFavoriteCourseImg)
        }
        viewholder.binding.itemFavoriteCourseTitleTv.text = course.title
        viewholder.binding.itemFavoriteCourseRatingBar.rating = course.rating
        viewholder.binding.itemFavoriteCourseDateTv.text =
            Utils.getDateFromTimestamp(course.createdAt)
    }

    inner class ViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {

        init {
            binding.itemFavoriteCourseContainer.setOnLongClickListener(this)
            binding.itemFavoriteCourseContainer.setOnClickListener(this)
            binding.itemFavoriteRemoveContainer.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.item_favorite_remove_container -> {
                    val dialog = AppDialog.instance
                    val bundle = Bundle()
                    val context = itemView.context

                    bundle.putString(App.TITLE, context.getString(R.string.delete))
                    bundle.putString(App.TEXT, context.getString(R.string.delete_from_fav))
                    dialog.arguments = bundle
                    dialog.setOnDialogBtnsClickedListener(
                        AppDialog.DialogType.YES_CANCEL,
                        object : AppDialog.OnDialogCreated {

                            override fun onCancel() {
                                dialog.dismiss()
                            }

                            override fun onOk() {
                                dialog.dismiss()
                                frag.removeItem(
                                    items[bindingAdapterPosition].course,
                                    bindingAdapterPosition
                                )
                            }
                        })

                    dialog.show(frag.childFragmentManager, null)
                }

                R.id.item_favorite_course_container -> {
                    val bundle = Bundle()
                    bundle.putParcelable(App.COURSE, items[bindingAdapterPosition].course)

                    val detailsFrag = CourseDetailsFrag()
                    detailsFrag.arguments = bundle
                    (frag.activity as MainActivity).transact(detailsFrag)
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {
            binding.itemFavoriteSwipeLayout.open()
            return true
        }
    }
}