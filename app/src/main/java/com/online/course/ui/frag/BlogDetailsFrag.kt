package com.online.course.ui.frag

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.FragBlogDetailsBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.adapter.CommentRvAdapter
import com.online.course.model.Blog
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.CommentDialog
import java.io.File
import java.lang.NullPointerException
import java.util.*


class BlogDetailsFrag : Fragment(), View.OnClickListener {

    private lateinit var mBinding: FragBlogDetailsBinding
    private lateinit var mBlog: Blog
    private var mTextToSpeech: TextToSpeech? = null
    private var mTextToSpeechFile: File? = null
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragBlogDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.blog_post)

        mBlog = requireArguments().getParcelable(App.BLOG)!!


        mBinding.blogDetailsCommentBtn.setOnClickListener(this)
        mBinding.blogDetailsPlayBtn.setOnClickListener(this)

        initTTSService()
        initBlogDetails()
        initComments(mBlog)
    }

    private fun initBlogDetails() {
        mBinding.blogDetailsTitleTv.text = mBlog.title
        if (mBlog.image != null) {
            Glide.with(requireContext()).load(mBlog.image).into(mBinding.blogDetailsImg)
        }
        mBinding.blogDetailsDateTv.text = Utils.getDateFromTimestamp(mBlog.createdAt)
        mBinding.blogDetailsDateTv.append((" ${getString(R.string._in)} "))

        val cat = SpannableString(mBlog.category)
        cat.setSpan(UnderlineSpan(), 0, mBlog.category.length, 0)
        mBinding.blogDetailsDateTv.append(cat)

        val author = mBlog.author!!
        mBinding.blogDetailsAuthorNameTv.text = author.name
        if (author.avatar != null) {
            Glide.with(requireContext()).load(author.avatar).into(mBinding.blogDetailsAuthorImg)
        }
        mBinding.blogDetailsContentTv.text = Utils.getTextAsHtml(mBlog.content)
    }


    private fun initTTSService() {
        mTextToSpeech = TextToSpeech(
            requireContext()
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                try {
                    Locale.ENGLISH
                    val locale = Locale(mBlog.locale)
                    if (mTextToSpeech!!.isLanguageAvailable(locale) != TextToSpeech.LANG_NOT_SUPPORTED) {
                        mTextToSpeech!!.language = locale
                        mBinding.blogDetailsPlayBtn.visibility = View.VISIBLE
                    }
                } catch (ex: NullPointerException) {
                }
            }
        }
    }

    private fun initComments(blog: Blog) {
        mBinding.blogDetailsRvContainer.rvProgressBar.visibility = View.GONE

        if (blog.comments.isNotEmpty()) {
            val rv = mBinding.blogDetailsRvContainer.rv
            rv.isNestedScrollingEnabled = true
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = CommentRvAdapter(blog.comments, childFragmentManager, false)
        } else {
            mBinding.blogDetailsCommentTv.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.blogDetailsPlayBtn -> {
                playTTS()
            }

            R.id.blogDetailsCommentBtn -> {
                comment()
            }
        }
    }

    private fun playTTS() {
        if (mTextToSpeechFile == null) {
            mBinding.blogDetailsPlayBtn.setImageResource(0)
            mBinding.blogDetailsPlayBtnProgressBar.visibility = View.VISIBLE

            var name = UUID.randomUUID().toString()
            if (name.length > 10) {
                name = name.substring(0, 10)
            }

            createFile(name)

        } else if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
                mBinding.blogDetailsPlayBtn.setImageResource(R.drawable.ic_play_white)
            } else {
                mMediaPlayer!!.start()
                mBinding.blogDetailsPlayBtn.setImageResource(R.drawable.ic_pause_white)
            }
        }
    }

    private fun createFile(fileName: String) {
        mTextToSpeechFile = File(requireContext().cacheDir, "${fileName}.wav")
        mTextToSpeech!!.setOnUtteranceProgressListener(object :
            UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post {
                    startTTSMedia()
                }
            }

            override fun onError(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post {
                    if (context == null) return@post
                    mBinding.blogDetailsPlayBtnProgressBar.visibility = View.GONE
                    mBinding.blogDetailsPlayBtn.setImageResource(R.drawable.ic_play_white)
                    ToastMaker.show(
                        requireContext(),
                        getString(R.string.error),
                        getString(R.string.error_read_audio),
                        ToastMaker.Type.ERROR
                    )
                }
            }

            override fun onStart(utteranceId: String?) {
            }
        })

        mTextToSpeech!!.synthesizeToFile(
            Utils.getTextAsHtml(mBlog.content),
            null,
            mTextToSpeechFile,
            fileName
        )
    }

    private fun startTTSMedia() {
        mBinding.blogDetailsPlayBtnProgressBar.visibility = View.GONE
        mBinding.blogDetailsPlayBtn.setImageResource(R.drawable.ic_pause_white)
        mMediaPlayer =
            MediaPlayer.create(
                requireContext(),
                Uri.fromFile(mTextToSpeechFile)
            )
        mMediaPlayer!!.setOnCompletionListener {
            mBinding.blogDetailsPlayBtn.setImageResource(R.drawable.ic_play_white)
        }
        mMediaPlayer!!.start()
    }

    private fun comment() {
        if (!App.isLoggedIn()) {
            (activity as MainActivity).goToLoginPage(null)
            return
        }

        val bundle = Bundle()
        bundle.putSerializable(App.SELECTION_TYPE, CommentDialog.Type.COMMENT_BLOG)
        bundle.putInt(App.ID, mBlog.id)
        bundle.putString(App.ITEM, App.Companion.ItemType.BLOG.value)

        val dialog = CommentDialog()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }
}