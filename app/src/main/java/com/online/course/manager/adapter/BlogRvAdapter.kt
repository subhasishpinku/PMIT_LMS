package com.online.course.manager.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.course.databinding.ItemBlogBinding
import com.online.course.manager.Utils
import com.online.course.model.Blog

class BlogRvAdapter(blogs: List<Blog>) :
    BaseArrayAdapter<Blog, BlogRvAdapter.ViewHolder>(blogs) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBlogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val blog = items[position]
        val context = viewholder.itemView.context

        if (blog.image != null) {
            Glide.with(context).load(blog.image).addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    viewholder.binding.itemBlogImgOverlay.visibility = View.VISIBLE
                    return false
                }

            }).into(viewholder.binding.itemBlogImg)
        }

        viewholder.binding.itemBlogTitleTv.text = blog.title
        viewholder.binding.itemBlogDescTv.text = Utils.getTextAsHtml(blog.description)
        viewholder.binding.itemBlogDateTv.text = Utils.getDateFromTimestamp(blog.createdAt)
        viewholder.binding.itemBlogCommentCountTv.text = blog.commentCount.toString()

        val author = blog.author!!
        viewholder.binding.itemBlogAuthorNameTv.text = author.name
        if (author.avatar != null) {
            Glide.with(context).load(author.avatar).into(viewholder.binding.itemBlogAuthorImg)
        }
    }

    class ViewHolder(val binding: ItemBlogBinding) :
        RecyclerView.ViewHolder(binding.root)
}