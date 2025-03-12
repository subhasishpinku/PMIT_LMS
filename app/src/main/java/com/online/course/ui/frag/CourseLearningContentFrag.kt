package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.RvBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.ChaptersContentAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.*
import com.online.course.model.view.ChapterView
import com.online.course.presenterImpl.CommonApiPresenterImpl
import java.util.ArrayList

class CourseLearningContentFrag : Fragment(), ItemCallback<List<Chapter>> {
    private lateinit var mBinding: RvBinding
    private lateinit var mCourse: Course

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
        mCourse = requireArguments().getParcelable(App.COURSE)!!

        val presenter = CommonApiPresenterImpl.getInstance()
        presenter.getCourseContent(mCourse.id, this)
    }

    override fun onItem(chapters: List<Chapter>, vararg args: Any) {
        if (context == null) return

        val items = ArrayList<ChapterView>()

        for (chapter in chapters) {
            items.add(
                ChapterView(
                    chapter.title,
                    "${chapter.items.size} ${getString(R.string.lessons)}",
                    chapter.items
                )
            )
        }

        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = ChaptersContentAdapter(
            items, mCourse, requireActivity(), false
        )
    }
}