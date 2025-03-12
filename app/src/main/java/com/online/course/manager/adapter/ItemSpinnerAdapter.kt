package com.online.course.manager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.online.course.R


class ItemSpinnerAdapter(
    context: Context,
    val items: List<String>
) : ArrayAdapter<String>(context, R.layout.item_text_view) {

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return getSpinnerView(convertView, position, parent)
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return getSpinnerView(convertView, position, parent)
    }

    override fun getItem(position: Int): String {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    private fun getSpinnerView(
        convertView: View?,
        position: Int,
        parent: ViewGroup
    ): View {
        var rootView = convertView
        val viewHolder: ViewHolder
        if (rootView == null || rootView.tag == null) {
            rootView =
                LayoutInflater.from(context).inflate(R.layout.item_text_view, parent, false)
            viewHolder = ViewHolder()
            viewHolder.mTextTv = rootView.findViewById(R.id.tv)
            rootView.tag = viewHolder
        } else {
            viewHolder =
                rootView.tag as ViewHolder
        }
        viewHolder.mTextTv.text = getItem(position)
        return rootView!!
    }

    private inner class ViewHolder {
        lateinit var mTextTv: TextView
    }
}