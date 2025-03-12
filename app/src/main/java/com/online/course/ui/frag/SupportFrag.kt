package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.RvBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.BaseArrayAdapter
import com.online.course.manager.adapter.CommonRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Data
import com.online.course.model.Ticket
import com.online.course.model.Conversation
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SupportPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState
import com.online.course.ui.widget.NewTicketDialog
import java.io.Serializable

class SupportFrag : NetworkObserverFragment(), OnItemClickListener, View.OnClickListener,
    EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mType: Type
    private lateinit var mPresenter: Presenter.SupportPresenter

    enum class Type : Serializable {
        TICKETS,
        CLASS_SUPPORT,
        MY_CLASS_SUPPORT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        mType = requireArguments().getSerializable(App.SELECTION_TYPE) as Type

        mBinding.rvAddBtn.setOnClickListener(this)

        mPresenter = SupportPresenterImpl(this)
        fetchItems()
    }

    private fun fetchItems() {
        val adapter = mBinding.rv.adapter

        adapter?.let {
            val size = (it as BaseArrayAdapter<*, *>).items.size
            it.items.clear()
            it.notifyItemRangeRemoved(0, size)
        }

        mBinding.rvProgressBar.visibility = View.VISIBLE
        when (mType) {
            Type.TICKETS -> {
                mBinding.rvAddBtn.visibility = View.VISIBLE
                mPresenter.getTickets()
            }
            Type.CLASS_SUPPORT -> {
                mBinding.rvAddBtn.visibility = View.VISIBLE
                mPresenter.getClassSupport()
            }
            Type.MY_CLASS_SUPPORT -> {
                mPresenter.getMyClassSupport()
            }
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val item = (mBinding.rv.adapter as CommonRvAdapter).items[position] as Ticket

        val bundle = Bundle()
        bundle.putParcelable(App.TICKET, item)

        val frag = TicketConversationFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }


    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()

        when (mType) {
            Type.TICKETS -> {
                bundle.putSerializable(App.SELECTION_TYPE, NewTicketDialog.Type.PLATFORM_SUPPORT)
            }
            Type.CLASS_SUPPORT -> {
                bundle.putSerializable(App.SELECTION_TYPE, NewTicketDialog.Type.COURSE_SUPPORT)
            }
        }

        val newTicketBinding = NewTicketDialog()
        newTicketBinding.arguments = bundle
        newTicketBinding.setOnTicketAdded(object : ItemCallback<Ticket> {
            override fun onItem(ticket: Ticket, vararg args: Any) {
                hideEmptyState()
                fetchItems()
            }
        })
        newTicketBinding.show(childFragmentManager, null)
    }

    fun showEmptyState() {
        when (mType) {
            Type.TICKETS -> {
                showEmptyState(
                    R.drawable.no_comments,
                    R.string.no_tickets,
                    R.string.no_tickets_desc
                )
            }
            Type.CLASS_SUPPORT -> {
                showEmptyState(
                    R.drawable.no_comments,
                    R.string.no_courses,
                    R.string.purchase_no_courses
                )
            }
            Type.MY_CLASS_SUPPORT -> {
                showEmptyState(
                    R.drawable.no_comments,
                    R.string.no_courses,
                    R.string.no_courses_class
                )
            }
        }
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onSupportsReceived(data: Data<List<Ticket>>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        mBinding.rv.adapter = CommonRvAdapter(data.data!!)
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))

        if (data.data!!.isEmpty()) {
            showEmptyState()
        }
    }
}