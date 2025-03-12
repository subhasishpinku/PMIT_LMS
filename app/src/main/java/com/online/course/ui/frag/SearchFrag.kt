package com.online.course.ui.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.online.course.R
import com.online.course.databinding.FragSearchBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.ClassListGridRvAdapter
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Course
import com.online.course.model.Data
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SearchPresenterImpl
import com.online.course.ui.MainActivity

class SearchFrag : NetworkObserverFragment(), View.OnClickListener{

    private lateinit var mBinding: FragSearchBinding
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mPresenter : Presenter.SearchPresenter

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) {
                if (s.isNotEmpty()) {
                    if (mBottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                } else if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.search)

        mPresenter = SearchPresenterImpl(this)
        mPresenter.getBestRatedCourses()

        mBottomSheetBehavior = BottomSheetBehavior.from(mBinding.searchBtnContainer)
        mBottomSheetBehavior.isHideable = true
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        mBinding.searchEdtx.addTextChangedListener(mTextWatcher)
        mBinding.searchBtn.setOnClickListener(this)
        setKeyboardEnterListener()
    }

    private fun setKeyboardEnterListener() {
        mBinding.searchEdtx.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(
                v: View?,
                keyCode: Int,
                event: KeyEvent
            ): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // Perform action on key press
                    onClick(mBinding.searchEdtx)
                    return true
                }
                return false
            }
        })

    }

    override fun onClick(v: View?) {
        val s = mBinding.searchEdtx.text.toString()
        if (s.isEmpty()) return

        val bundle = Bundle()
        bundle.putString(App.KEY, s)

        val resultFrag = SearchResultFrag()
        resultFrag.arguments = bundle

        (activity as MainActivity).transact(resultFrag)
    }

    fun onBestRatedCoursesRecevied(data: Data<List<Course>>) {
        mBinding.searchRvProgressBar.visibility = View.GONE
        mBinding.searchRv.adapter = ClassListGridRvAdapter(data.data!!, activity as MainActivity)
    }
}