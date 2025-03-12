package com.online.course.manager.adapter

import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemGatewayGridBinding
import com.online.course.manager.Utils
import com.online.course.model.PaymentChannel
import com.online.course.ui.frag.ChargeAccountPaymentFrag

class PaymentGatewayGridAdapter(
    items: MutableList<PaymentChannel>,
    private val selectionItemCallback: Selection,
    private val rv: RecyclerView,
    private val acccountCharge: Double
) :
    BaseArrayAdapter<PaymentChannel, PaymentGatewayGridAdapter.ViewHolder>(items) {

    private var mSelectedPosition: Int? = null

    interface Selection {
        fun onItemSelected(paymentChannel: PaymentChannel, position: Int)
        fun onItemDeselected()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGatewayGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun getSelectedItem(): PaymentChannel? {
        return if (mSelectedPosition != null) items[mSelectedPosition!!] else null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gateway = items[position]
        holder.binding.gatewayGridTitleTv.text = gateway.title
        if (gateway.image != null) {
            Glide.with(holder.itemView.context).load(gateway.image)
                .into(holder.binding.gatewayGridIconImg)
        }

        if (gateway.id == ChargeAccountPaymentFrag.PaymentMethod.ACCOUNT_CHARGE.type) {
            holder.binding.gatewayGridAmountTv.text =
                Utils.formatPrice(holder.itemView.context, acccountCharge, false)
            holder.binding.gatewayGridAmountTv.visibility = View.VISIBLE
        } else {
            holder.binding.gatewayGridAmountTv.visibility = View.GONE
        }

        val gatewayContainer = holder.binding.gatewayGridContainer
        val params = gatewayContainer.layoutParams as GridLayoutManager.LayoutParams

        val margin = if (position == itemCount - 1) {
            50f
        } else {
            7f
        }
        params.bottomMargin = Utils.changeDpToPx(holder.itemView.context, margin).toInt()
        gatewayContainer.requestLayout()

        if (mSelectedPosition == position) {
            val transition = holder.binding.gatewayGridContainer.background as TransitionDrawable
            transition.startTransition(300)
        } else {
            holder.binding.gatewayGridContainer.background = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.bg_transition_white_to_green
            ) as TransitionDrawable
        }
    }

    inner class ViewHolder(val binding: ItemGatewayGridBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        init {
            itemView.background = ContextCompat.getDrawable(
                itemView.context,
                R.drawable.bg_transition_white_to_green
            ) as TransitionDrawable

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            val gateway = items[position]

            if (mSelectedPosition != null) {
                if (mSelectedPosition == position) {
                    return
                }

                try {
                    val viewHolder =
                        rv.findViewHolderForAdapterPosition(mSelectedPosition!!) as ViewHolder
                    (viewHolder.binding.gatewayGridContainer.background as TransitionDrawable).reverseTransition(
                        300
                    )
                } catch (ex: Exception) {
                }
            }

            val transition = binding.gatewayGridContainer.background as TransitionDrawable
            transition.startTransition(300)

            mSelectedPosition = position

            selectionItemCallback.onItemSelected(gateway, position)
        }

        fun deselect(binding: ItemGatewayGridBinding) {
            mSelectedPosition = null
            (binding.gatewayGridContainer.background as TransitionDrawable).reverseTransition(300)
        }
    }
}