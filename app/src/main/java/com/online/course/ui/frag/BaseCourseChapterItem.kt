package com.online.course.ui.frag

import android.Manifest
import android.animation.LayoutTransition
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.webkit.URLUtil
import android.widget.Button
import android.widget.CompoundButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.FragCourseChapterItemBinding
import com.online.course.manager.*
import com.online.course.manager.Utils.toBoolean
import com.online.course.manager.Utils.toInt
import com.online.course.manager.adapter.CourseCommonRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.OnDownloadProgressListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.manager.player.PlayerHelper
import com.online.course.manager.player.FileVideoPlayerHelper
import com.online.course.manager.player.VimeoVideoPlayerHelper
import com.online.course.manager.player.YoutubeVideoPlayerHelper
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CourseChapterItemPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.SplashScreenActivity
import com.online.course.ui.VideoPlayerActivity
import com.online.course.ui.WebViewActivity
import com.online.course.ui.widget.AppDialog
import com.online.course.ui.widget.LoadingDialog
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.rajat.pdfviewer.PdfViewerActivity
import java.io.File
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.*
import kotlin.math.roundToInt

abstract class BaseCourseChapterItem : NetworkObserverFragment(), View.OnClickListener,
    PlayerHelper.Listener, CompoundButton.OnCheckedChangeListener, OnItemClickListener,
    OnDownloadProgressListener {

    protected lateinit var mBinding: FragCourseChapterItemBinding
    protected lateinit var mCourse: Course
    protected lateinit var mPresenter: Presenter.CourseChapterItemPresenter
    private lateinit var mStoragePermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mYouTubePlayerView: View
    private lateinit var mLoadingDialog: LoadingDialog

    protected var mOfflineMode = false
    protected var mSessionItem: ChapterSessionItem? = null
    protected var mFileItem: ChapterFileItem? = null
    protected var mTextItem: ChapterTextItem? = null
    private var mVideoHelper: PlayerHelper.Player? = null
    private var mActivityFullScreenStarted = false
    private var mTextToSpeech: TextToSpeech? = null
    private var mTextToSpeechFile: File? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mTTSFiles: HashMap<Int, File>? = null
    protected var mTextLessons: List<ChapterTextItem>? = null
    private var mLocalFilePath: String? = null


    abstract fun canViewItem(): Boolean

    protected abstract fun showAccessDenied()

    protected abstract fun loadTextLesson(next: Boolean?)

    protected abstract fun getTransactionFrag(): BaseCourseChapterItem


    protected fun hideLoadingDialog() {
        mLoadingDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStoragePermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                ) {
                    downloadFile()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragCourseChapterItemBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!

        initUI()

        if (!mOfflineMode) {
            mPresenter = CourseChapterItemPresenterImpl(this)
        }
    }

    private fun initUI() {
        initToolbar()

        mBinding.courseChapterItemStartBtn.setOnClickListener(this)
        mBinding.courseChapterItemEndBtn.setOnClickListener(this)
        mBinding.courseChapterItemPlayBtn.setOnClickListener(this)

        mBinding.courseChapterItemCourseTv.text = mCourse.title

        if (!mOfflineMode) {
            mLoadingDialog = LoadingDialog.instance
            mLoadingDialog.show(childFragmentManager, null)
        }
    }

    private fun initToolbar() {
        if (activity is MainActivity) {
            val toolbarOptions = ToolbarOptions()
            toolbarOptions.startIcon = ToolbarOptions.Icon.BACK
            (activity as MainActivity).showToolbar(toolbarOptions, mCourse.title)
        } else if (activity is SplashScreenActivity) {
            (activity as SplashScreenActivity).showToolbar(mCourse.title)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mActivityFullScreenStarted) {
            mActivityFullScreenStarted = false

            val playerState = PlayerHelper.playerState!!

            if (mVideoHelper != null) {

                if (playerState.currentPosition != mVideoHelper!!.getCurrentPosition()) {
                    mVideoHelper!!.seekTo(playerState.currentPosition)
                    if (mVideoHelper is FileVideoPlayerHelper) {
                        (mVideoHelper as FileVideoPlayerHelper).player.playWhenReady = false
                    }
                }

                if (playerState.isPlaying != mVideoHelper!!.isPlaying()) {
                    mVideoHelper?.playPauseVideo()
                }

                if (mVideoHelper is FileVideoPlayerHelper && !mVideoHelper!!.isPlaying()) {
                    (mVideoHelper as FileVideoPlayerHelper).setVideoPosition()
                }
            }

        } else {
            mVideoHelper?.restorePlayerState()
        }
    }

    override fun onStop() {
        super.onStop()
        mVideoHelper?.savePlayerState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeAllFiles()
        mVideoHelper?.release()
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mTextToSpeech?.stop()
        mTextToSpeech?.shutdown()
    }

    private fun removeAllFiles() {
        if (mTTSFiles != null && mTTSFiles!!.isNotEmpty()) {
            Thread {
                for (f in mTTSFiles!!.keys) {
                    mTTSFiles!![f]!!.delete()
                }
            }.start()
        }
    }

    protected fun initSessionItem() {
        initSessionMarks()

        if (!canViewItem()) {
            hideItems()
            return
        }

        if (mSessionItem!!.isConducted()) {
            mBinding.courseChapterItemEndBtn.isEnabled = false
            val containerTv = "${getString(R.string.session_conducted)} ${
                Utils.getDateTimeFromTimestamp(
                    mSessionItem!!.date
                )
            }"
            mBinding.courseChapterItemBtnContainerTv.text = containerTv
        } else {
            val containerTv = "${getString(R.string.session_start_at)} ${
                Utils.getDateTimeFromTimestamp(
                    mSessionItem!!.date
                )
            }"
            mBinding.courseChapterItemBtnContainerTv.text = containerTv
        }


        mBinding.courseChapterItemStartBtn.text = getString(R.string.join)
        mBinding.courseChapterItemEndBtn.text = getString(R.string.add_to_calendar)

        val joinLink = getJoinLink()

        if (!URLUtil.isValidUrl(joinLink)) {
            mBinding.courseChapterItemStartBtn.isEnabled = false
        }
        mBinding.courseChapterItemBtnContainerTv.visibility = View.VISIBLE
        mBinding.courseChapterItemStartBtn.isEnabled = mSessionItem!!.canJoin

        showReadSwitch(mSessionItem!!.authHasRead)
    }

    private fun hideItems() {
        mBinding.courseChapterBtnsContainer.visibility = View.GONE
        mBinding.courseChapterItemReadSwitch.visibility = View.GONE
        showAccessDenied()
    }


    private fun initSessionMarks() {
        val status = if (mSessionItem!!.isConducted()) {
            getString(R.string.conducted)
        } else {
            getString(R.string.not_conducted)
        }

        mBinding.courseChapterItemTitleTv.text = mSessionItem!!.title
        mBinding.courseChapterItemDescTv.text = mSessionItem!!.description

        mBinding.courseChapterItemFirstMarkImg.setImageResource(R.drawable.ic_video_gray)
        mBinding.courseChapterItemFirstMarkKeyTv.text = getString(R.string.type)
        mBinding.courseChapterItemFirstMarkValueTv.text = getString(R.string.webinar)

        mBinding.courseChapterItemSecondMarkImg.setImageResource(R.drawable.ic_more_circle)
        mBinding.courseChapterItemSecondMarkKeyTv.text = getString(R.string.status)
        mBinding.courseChapterItemSecondMarkValueTv.text = status

        mBinding.courseChapterItemThirdMarkImg.setImageResource(R.drawable.ic_calendar)
        mBinding.courseChapterItemThirdMarkKeyTv.text = getString(R.string.start_date)
        mBinding.courseChapterItemThirdMarkValueTv.text =
            Utils.getDateFromTimestamp(mSessionItem!!.date)

        mBinding.courseChapterItemForthMarkImg.setImageResource(R.drawable.ic_time)
        mBinding.courseChapterItemForthMarkKeyTv.text = getString(R.string.duration)
        mBinding.courseChapterItemForthMarkValueTv.text =
            Utils.getDuration(requireContext(), mSessionItem!!.duration)
    }

    protected fun initFileItem() {
        initFileMarks()

        if (!canViewItem()) {
            hideItems()
            return
        }

        if ((mFileItem!!.downloadable.toBoolean() && mFileItem!!.storage == ChapterFileItem.Storage.UPLOAD.value)
            || ((mFileItem!!.storage == ChapterFileItem.Storage.EXTERNAL_LINK.value ||
                    mFileItem!!.storage == ChapterFileItem.Storage.S3.value) &&
                    mFileItem!!.fileType.uppercase() != ChapterFileItem.VIDEO_FILE_TYPE) && !mOfflineMode
        ) {
            if (ApiService.downloadingRequestsResponses.containsKey(mFileItem!!.id)) {
                val progressResponseBody = ApiService.downloadingRequestsResponses[mFileItem!!.id]
                progressResponseBody?.setProgressListener(this)
                mBinding.courseChapterItemBtnContainerTv.visibility = View.VISIBLE
                mBinding.courseChapterItemDownloadProgressBar.visibility = View.VISIBLE
                mBinding.courseChapterItemStartBtn.text =
                    getString(R.string.saving_file_into_your_downloads)
            } else {
                mBinding.courseChapterItemStartBtn.text = getString(R.string.download)
            }
            mBinding.courseChapterItemEndBtn.visibility = View.GONE
        } else {
            mBinding.courseChapterBtnsContainer.visibility = View.GONE
        }

        if ((mFileItem!!.storage == ChapterFileItem.Storage.UPLOAD.value && mFileItem!!.fileType.uppercase(
                Locale.ENGLISH
            ) in Utils.VIDEO_FORMATS) || (mFileItem!!.storage == ChapterFileItem.Storage.EXTERNAL_LINK.value
                    || mFileItem!!.storage == ChapterFileItem.Storage.S3.value && mFileItem!!.fileType.uppercase(
                Locale.ENGLISH
            ) == ChapterFileItem.VIDEO_FILE_TYPE)
        ) {
            initVideoPlayerFromLocalFile()
        } else if (!mOfflineMode && (mFileItem!!.storage == ChapterFileItem.Storage.YOUTUBE.value ||
                    mFileItem!!.storage == ChapterFileItem.Storage.VIMEO.value)
        ) {

            initOnlineVideoConf()

        } else if (!mFileItem!!.downloadable.toBoolean() && mFileItem!!.fileType.uppercase(
                Locale.ENGLISH
            ) == ChapterFileItem.PDF_FILE_TYPE &&
            (mFileItem!!.storage == ChapterFileItem.Storage.S3.value ||
                    mFileItem!!.storage == ChapterFileItem.Storage.UPLOAD.value)
        ) {
            if (mOfflineMode && getFileFromLocalDir() == null) {
                mBinding.courseChapterBtnsContainer.visibility = View.GONE
                return
            }

            mBinding.courseChapterItemStartBtn.text = getString(R.string.read)
            mBinding.courseChapterItemEndBtn.visibility = View.GONE
            mBinding.courseChapterBtnsContainer.visibility = View.VISIBLE

        } else if (!mOfflineMode && mFileItem!!.storage != ChapterFileItem.Storage.EXTERNAL_LINK.value &&
            mFileItem!!.storage != ChapterFileItem.Storage.S3.value
        ) {
            if (mFileItem!!.interactiveType != null) {
                mBinding.courseChapterItemStartBtn.text = getString(R.string.play)
            } else {
                mBinding.courseChapterItemStartBtn.text = getString(R.string.view)
            }

            mBinding.courseChapterItemEndBtn.visibility = View.GONE
            mBinding.courseChapterBtnsContainer.visibility = View.VISIBLE
        }

        showReadSwitch(mFileItem!!.authHasRead)
    }

    private fun initVideoPlayerFromLocalFile() {
        val videoFromLocalDir = getFileFromLocalDir()

        if ((!mOfflineMode && URLUtil.isValidUrl(mFileItem!!.file)) || videoFromLocalDir != null) {
            mBinding.courseChapterItemPlayerView.visibility = View.VISIBLE

            mVideoHelper = FileVideoPlayerHelper(requireContext())
            mVideoHelper!!.setOnCallbackListener(this)
            mBinding.courseChapterItemPlayerView.player =
                (mVideoHelper as FileVideoPlayerHelper).player
            if (mOfflineMode) {
                mLocalFilePath = videoFromLocalDir!!.absolutePath
                (mVideoHelper as FileVideoPlayerHelper).initPlayer(videoFromLocalDir)
            } else {
                mVideoHelper!!.initPlayer(mFileItem!!.file, null)
            }

            mBinding.courseChapterItemPlayerView.videoSurfaceView?.setOnClickListener {
                showVideoInFullscreen(false)
            }

            initVideoController()
        }
    }

    private fun initFileMarks() {
        mBinding.courseChapterItemTitleTv.text = mFileItem!!.title
        mBinding.courseChapterItemDescTv.text = mFileItem!!.description

        mBinding.courseChapterItemFirstMarkImg.setImageResource(R.drawable.ic_paper_gray)
        mBinding.courseChapterItemFirstMarkKeyTv.text = getString(R.string.type)
        if (mFileItem!!.interactiveType != null) {
            mBinding.courseChapterItemFirstMarkValueTv.text = getTypeFromInteractive()
        } else {
            mBinding.courseChapterItemFirstMarkValueTv.text =
                mFileItem!!.fileType.uppercase(Locale.ENGLISH)
        }

        mBinding.courseChapterItemSecondMarkImg.setImageResource(R.drawable.ic_download_gray)
        mBinding.courseChapterItemSecondMarkKeyTv.text = getString(R.string.volume)
        mBinding.courseChapterItemSecondMarkValueTv.text = mFileItem!!.volume

        mBinding.courseChapterItemThirdMarkImg.setImageResource(R.drawable.ic_calendar)
        mBinding.courseChapterItemThirdMarkKeyTv.text = getString(R.string.publish_date)
        mBinding.courseChapterItemThirdMarkValueTv.text =
            Utils.getDateFromTimestamp(mFileItem!!.createdAt)

        mBinding.courseChapterItemForthMarkImg.setImageResource(R.drawable.ic_download_gray)
        mBinding.courseChapterItemForthMarkKeyTv.text = getString(R.string.downloadable)
        mBinding.courseChapterItemForthMarkValueTv.text =
            if (mFileItem!!.downloadable.toBoolean()) getString(R.string.yes) else getString(R.string.no)
    }

    private fun initOnlineVideoConf() {
        val controllerParam =
            mBinding.courseChapterItemPlayerControllerView.root.layoutParams as ConstraintLayout.LayoutParams

        if (Utils.isYoutubeUrl(mFileItem!!.file)) {
            controllerParam.topToBottom = R.id.course_chapter_item_youtube_player_view
            initYoutubePlayer()
            initVideoController()
        } else if (Utils.isVimeoUrl(mFileItem!!.file)) {
            controllerParam.topToBottom = R.id.course_chapter_item_vimeo_player_view
            initVimeoPlayer()
            initVideoController()
        }
    }

    private fun getTypeFromInteractive(): String {
        val split = mFileItem!!.interactiveType!!.split('_')
        val typeBuilder = StringBuilder()

        for (i in split.indices) {
            if (i > 0) {
                val s = split[i]
                if (s.length > 1) {
                    typeBuilder.append(s[0].uppercase(Locale.ENGLISH))
                        .append(s.substring(1, s.length))
                } else {
                    typeBuilder.append(s)
                }
            } else {
                typeBuilder.append(split[0])
            }
        }

        return typeBuilder.toString()
    }

    private fun initVimeoPlayer() {
        val vimeoPlayerView = mBinding.courseChapterItemVimeoPlayerView
        mVideoHelper = VimeoVideoPlayerHelper(mBinding.courseChapterItemVimeoPlayerView)
        try {
            mVideoHelper!!.initPlayer(Utils.extractFileNameFromUrl(mFileItem!!.file), null)
            mVideoHelper!!.setOnCallbackListener(this@BaseCourseChapterItem)
            vimeoPlayerView.visibility = View.VISIBLE
        } catch (ex: NumberFormatException) {
        }
    }

    private fun initYoutubePlayer() {
        val youTubePlayerView = mBinding.courseChapterItemYoutubePlayerView

        val listener: YouTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                mYouTubePlayerView =
                    youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_youtube_player)

                if (mVideoHelper == null) {
                    mVideoHelper = YoutubeVideoPlayerHelper(youTubePlayerView, youTubePlayer)
                    youTubePlayer.addListener((mVideoHelper as YoutubeVideoPlayerHelper))
                    mVideoHelper!!.setOnCallbackListener(this@BaseCourseChapterItem)
                }

                (mVideoHelper as YoutubeVideoPlayerHelper).hideYouTubeShareBtn()
                mVideoHelper!!.initPlayer(
                    Utils.extractFileNameFromUrl(mFileItem!!.file),
                    0
                )
            }
        }

        // disable web ui
        val options = IFramePlayerOptions.Builder().controls(0).build()
        youTubePlayerView.initialize(listener, options)
        youTubePlayerView.visibility = View.VISIBLE
        youTubePlayerView.setOnClickListener(this)
    }

    private fun getFileFromLocalDir(): File? {
        if (mFileItem!!.storage == ChapterFileItem.Storage.UPLOAD.value) {
            val fileName = "${mFileItem!!.id}.${mFileItem!!.fileType}"

            val videoFile = File(
                requireContext().filesDir,
                "${App.Companion.Directory.DOWNLOADS.value()}${File.separator}$fileName"
            )

            if (videoFile.exists()) {
                return videoFile
            }
        }

        return null
    }

    protected fun showReadSwitch(authHasRead: Boolean) {
        if (mCourse.hasUserBought && !mOfflineMode) {
            mBinding.courseChapterItemReadSwitch.visibility = View.VISIBLE
            mBinding.courseChapterItemReadSwitch.isChecked = authHasRead
            mBinding.courseChapterItemReadSwitch.setOnCheckedChangeListener(this)
        }
    }

    protected fun initTextItem() {
        initTextLessonMarks()

        if (!canViewItem()) {
            loadTextLessonMarks()
            hideItems()
            return
        }

        setLayoutTransition()

        mBinding.courseChapterItemStartBtn.text = getString(R.string.previous_lesson)
        mBinding.courseChapterItemEndBtn.text = getString(R.string.next_lesson)

        val green = ContextCompat.getColor(requireContext(), R.color.accent)
        val white = ContextCompat.getColor(requireContext(), R.color.white)

        mBinding.courseChapterItemStartBtn.setTextColor(green)
        mBinding.courseChapterItemStartBtn.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.white)
        mBinding.courseChapterItemStartBtnContainer.strokeWidth =
            Utils.changeDpToPx(requireContext(), 1f).toInt()
        mBinding.courseChapterItemStartBtnContainer.strokeColor =
            ContextCompat.getColor(requireContext(), R.color.accent)

        mBinding.courseChapterItemEndBtn.setTextColor(white)
        mBinding.courseChapterItemEndBtn.strokeWidth = 0
        mBinding.courseChapterItemEndBtn.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.accent)

        loadTextLesson(null)
    }

    private fun setLayoutTransition() {
        mBinding.courseChapterItemContainer.layoutTransition = LayoutTransition()
    }

    private fun initTextLessonMarks() {
        mBinding.courseChapterItemFirstMarkImg.setImageResource(R.drawable.ic_paper_gray)
        mBinding.courseChapterItemFirstMarkKeyTv.text = getString(R.string.type)
        mBinding.courseChapterItemFirstMarkValueTv.text = getString(R.string.text)

        mBinding.courseChapterItemSecondMarkImg.setImageResource(R.drawable.ic_download_gray)
        mBinding.courseChapterItemSecondMarkKeyTv.text = getString(R.string.attachments)

        mBinding.courseChapterItemThirdMarkImg.setImageResource(R.drawable.ic_calendar)
        mBinding.courseChapterItemThirdMarkKeyTv.text = getString(R.string.publish_date)

        mBinding.courseChapterItemForthMarkImg.setImageResource(R.drawable.ic_time)
        mBinding.courseChapterItemForthMarkKeyTv.text = getString(R.string.study_time)
    }


    protected fun loadTextLessonMarks() {
        mBinding.courseChapterItemTitleTv.text = mTextItem!!.title
        mBinding.courseChapterItemSecondMarkValueTv.text = mTextItem!!.attachments.size.toString()
        mBinding.courseChapterItemThirdMarkValueTv.text =
            Utils.getDateFromTimestamp(mTextItem!!.createdAt)
        mBinding.courseChapterItemForthMarkValueTv.text =
            Utils.getDuration(requireContext(), mTextItem!!.studyTime)
    }

    protected fun setBtnMargin(margin: Int) {
        var params =
            mBinding.courseChapterItemEndBtn.layoutParams as ConstraintLayout.LayoutParams
        params.marginStart = margin
        mBinding.courseChapterItemEndBtn.requestLayout()

        params =
            mBinding.courseChapterItemStartBtnContainer.layoutParams as ConstraintLayout.LayoutParams
        params.marginEnd = margin
        mBinding.courseChapterItemStartBtnContainer.requestLayout()
    }

    protected fun initTTS() {
        mBinding.courseChapterItemPlayBtn.setImageResource(R.drawable.ic_play_white)
        mMediaPlayer?.stop()
        mTextToSpeechFile = null

        if (mTextToSpeech == null) {
            mTextToSpeech = TextToSpeech(
                requireContext()
            ) { status ->
                if (status != TextToSpeech.ERROR) {
                    mTTSFiles = HashMap()
                    initTTSService()
                } else {
                    checkForHideContainer()
                    mTextToSpeech = null
                }
            }
        } else {
            initTTSService()
        }
    }

    private fun checkForHideContainer() {
        if (!mBinding.courseChapterItemStartBtn.isVisible && !mBinding.courseChapterItemEndBtn.isVisible
            && !mBinding.courseChapterItemPlayBtn.isVisible
        ) {
            mBinding.courseChapterBtnsContainer.visibility = View.GONE
        }
    }

    private fun initTTSService() {
        val locale = Locale(mTextItem!!.locale)
        if (mTextToSpeech!!.isLanguageAvailable(locale) != TextToSpeech.LANG_NOT_SUPPORTED) {
            mBinding.courseChapterItemPlayBtn.visibility = View.VISIBLE
            mTextToSpeech!!.language = locale

        } else {
            mBinding.courseChapterItemPlayBtn.visibility = View.GONE
        }
    }

    protected fun showBuyAlert() {
        ToastMaker.show(
            requireContext(),
            getString(R.string.error),
            getString(R.string.you_have_to_buy_this_course),
            ToastMaker.Type.ERROR
        )
    }

    protected fun loadRelatedFiles() {
        if (mTextItem!!.attachments.isEmpty()) {
            mBinding.courseChapterItemRelatedFilesTv.visibility = View.GONE
            mBinding.courseChapterItemRelatedRv.visibility = View.GONE
        } else {
            var adapter = mBinding.courseChapterItemRelatedRv.adapter
            if (adapter == null) {
                mBinding.courseChapterItemRelatedRv.adapter =
                    CourseCommonRvAdapter(ArrayList(mTextItem!!.attachments), true)
                mBinding.courseChapterItemRelatedRv.addOnItemTouchListener(
                    ItemClickListener(
                        mBinding.courseChapterItemRelatedRv,
                        this
                    )
                )
            } else {
                adapter = adapter as CourseCommonRvAdapter
                val size = adapter.itemCount
                adapter.items.clear()
                adapter.items.addAll(ArrayList(mTextItem!!.attachments))
                if (size > adapter.itemCount) {
                    adapter.notifyItemRangeRemoved(adapter.itemCount - 1, size - adapter.itemCount)
                }
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            }

            mBinding.courseChapterItemRelatedFilesTv.visibility = View.VISIBLE
            mBinding.courseChapterItemRelatedRv.visibility = View.VISIBLE
        }
    }

    private fun initVideoController() {
        mBinding.courseChapterItemPlayerControllerView.playerPlayPauseBtn.setOnClickListener(this)
        mBinding.courseChapterItemPlayerControllerView.playerControllerMuteBtn.setOnClickListener(
            this
        )
        mBinding.courseChapterItemPlayerControllerView.playerControllerFullscreenBtn.setOnClickListener(
            this
        )

        mBinding.courseChapterItemPlayerControllerView.root.visibility = View.VISIBLE
    }

    fun getJoinLink(): String {
        return when {
            mSessionItem!!.zoomStartLink != null -> {
                mSessionItem!!.zoomStartLink!!
            }

            mSessionItem!!.joinLink != null -> {
                mSessionItem!!.joinLink!!
            }

            mSessionItem!!.link != null -> {
                mSessionItem!!.link!!
            }
            else -> {
                ""
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.course_chapter_item_start_btn, R.id.course_chapter_item_end_btn -> {
                when ((v as Button).text.toString()) {

                    //For session page
                    getString(R.string.join) -> {
                        if (mSessionItem!!.sessionApi == ChapterSessionItem.SessionApiType.AGORA.value) {
                            val intent = Intent(requireContext(), WebViewActivity::class.java)
                            intent.putExtra(App.URL, mSessionItem!!.joinLink)
                            startActivity(intent)
                        } else {
                            Utils.openLink(requireContext(), getJoinLink())
                        }
                    }

                    getString(R.string.add_to_calendar) -> {
                        Utils.addToCalendar(
                            requireContext(),
                            mSessionItem!!.title,
                            mSessionItem!!.description,
                            mSessionItem!!.duration,
                            mSessionItem!!.date * 1000
                        )
                    }


                    // For File page
                    getString(R.string.download) -> {
                        downloadFile()
                    }

                    getString(R.string.saving_file_into_your_downloads) -> {
                        cancelDownload()
                    }

                    //For Text Lesson page
                    getString(R.string.previous_lesson) -> {
                        loadTextLesson(false)
                    }

                    getString(R.string.next_lesson) -> {
                        loadTextLesson(true)
                    }

                    getString(R.string.play), getString(R.string.view) -> {
                        val intent = Intent(requireContext(), WebViewActivity::class.java)
                        if (mFileItem!!.interactiveFilePath != null) {
                            intent.putExtra(App.URL, mFileItem!!.interactiveFilePath)
                        } else {
                            intent.putExtra(App.URL, mFileItem!!.file)
                        }
                        startActivity(intent)
                    }

                    getString(R.string.read) -> {
                        viewPDFFile()
                    }
                }
            }

            R.id.course_chapter_item_play_btn -> {
                playTTS()
            }

            R.id.player_play_pause_btn -> {
                mVideoHelper?.playPauseVideo()
            }

            R.id.player_controller_mute_btn -> {
                mVideoHelper?.muteUnmuteVideo()
            }

            R.id.player_controller_fullscreen_btn -> {
                showVideoInFullscreen(true)
            }

            R.id.course_chapter_item_youtube_player_view -> {
            }
        }
    }

    private fun viewPDFFile() {
        if (mOfflineMode) {
            val file = getFileFromLocalDir()
            if (file != null) {
                startActivity(
                    PdfViewerActivity.launchPdfFromPath(
                        requireContext(),
                        file.path,
                        mFileItem!!.title,
                        "",
                        enableDownload = false
                    )
                )
            }

        } else {
            startActivity(
                PdfViewerActivity.launchPdfFromUrl(
                    requireContext(),
                    mFileItem!!.file,
                    mFileItem!!.title,
                    requireContext().filesDir.absolutePath,
                    enableDownload = false
                )
            )
        }
    }

    private fun playTTS() {
        if (mTextToSpeechFile == null) {
            mTextToSpeechFile = getAudioFile(mTextItem!!.id)
            if (mTextToSpeechFile != null) {
                startTTSMedia()
                return
            }

            mBinding.courseChapterItemPlayBtn.setImageResource(0)
            mBinding.courseChapterItemPlayBtnProgressBar.visibility = View.VISIBLE

            var name = UUID.randomUUID().toString()
            if (name.length > 10) {
                name = name.substring(0, 10)
            }

            mTextToSpeechFile = File.createTempFile(name, ".wav")
            mTextToSpeech!!.setOnUtteranceProgressListener(object :
                UtteranceProgressListener() {
                override fun onDone(utteranceId: String?) {
                    Handler(Looper.getMainLooper()).post {
                        addFileToAudioMap(mTextItem!!.id)
                        startTTSMedia()
                    }
                }

                override fun onError(utteranceId: String?) {
                    Handler(Looper.getMainLooper()).post {
                        mBinding.courseChapterItemPlayBtnProgressBar.visibility = View.GONE
                        mBinding.courseChapterItemPlayBtn.setImageResource(R.drawable.ic_play_white)
                    }
                }

                override fun onStart(utteranceId: String?) {
                }
            })

            mTextToSpeech!!.synthesizeToFile(
                Utils.getTextAsHtml(mTextItem!!.content),
                null,
                mTextToSpeechFile,
                name
            )
        } else if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
                mBinding.courseChapterItemPlayBtn.setImageResource(R.drawable.ic_play_white)
            } else {
                mMediaPlayer!!.start()
                mBinding.courseChapterItemPlayBtn.setImageResource(R.drawable.ic_pause_white)
            }
        }
    }

    private fun cancelDownload() {
        val dialog = AppDialog()
        val bundle = Bundle()
        bundle.putString(App.TITLE, getString(R.string.cancel_download))
        bundle.putString(App.TEXT, getString(R.string.cancel_download_desc))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {
                override fun onCancel() {
                }

                override fun onOk() {
                    mPresenter.cancelDownload(mFileItem!!.id)
                    onAttachmentDownloadedFinished(mFileItem!!.id)
                }

            })
        dialog.show(childFragmentManager, null)
    }

    private fun addFileToAudioMap(id: Int) {
        if (!mTTSFiles!!.containsKey(id)) {
            mTTSFiles!![id] = mTextToSpeechFile!!
        }
    }

    private fun getAudioFile(id: Int): File? {
        if (mTTSFiles!!.containsKey(id)) {
            return mTTSFiles!![id]
        }

        return null
    }

    private fun startTTSMedia() {
        mBinding.courseChapterItemPlayBtnProgressBar.visibility = View.GONE
        mBinding.courseChapterItemPlayBtn.setImageResource(R.drawable.ic_pause_white)
        mMediaPlayer?.reset()
        mMediaPlayer =
            MediaPlayer.create(
                requireContext(),
                Uri.fromFile(mTextToSpeechFile)
            )
        mMediaPlayer!!.setOnCompletionListener {
            mBinding.courseChapterItemPlayBtn.setImageResource(R.drawable.ic_play_white)
        }
        mMediaPlayer!!.start()
    }

    private fun downloadFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mBinding.courseChapterItemBtnContainerTv.visibility = View.VISIBLE
            mBinding.courseChapterItemDownloadProgressBar.visibility = View.VISIBLE
            mBinding.courseChapterItemStartBtn.text =
                getString(R.string.saving_file_into_your_downloads)
            mBinding.courseChapterItemBtnContainerTv.text = "0"
            mPresenter.downloadFile(mFileItem!!, this)
        } else {
            mStoragePermissionResultLauncher.launch(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private fun initVideoPlayerView(width: Int, height: Int) {
        mBinding.courseChapterItemPlayerView.post {
            mBinding.courseChapterItemPlayerView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val layoutParams = mBinding.courseChapterItemPlayerView.layoutParams

                    var changed = false

                    if (height < layoutParams.height) {
                        changed = true
                        layoutParams.height = height
                    }

                    if (width < layoutParams.width) {
                        changed = true
                        layoutParams.width = width
                    }

                    if (changed) {
                        mBinding.courseChapterItemPlayerView.requestLayout()
                    }

                    outline.setRoundRect(
                        Rect(0, 0, width, height),
                        Utils.changeDpToPx(requireContext(), 20f)
                    )
                }
            }

            mBinding.courseChapterItemPlayerView.clipToOutline = true
        }
    }

    override fun onMute(mute: Boolean) {
        val player =
            mBinding.courseChapterItemPlayerControllerView.playerControllerMuteBtn

        if (mute) {
            player.setImageResource(
                R.drawable.ic_mute
            )
        } else {
            player.setImageResource(
                R.drawable.ic_sound
            )
        }
    }

    override fun onVideoPaused() {
        mBinding.courseChapterItemPlayerControllerView.playerPlayPauseBtn
            .setImageResource(R.drawable.ic_play_circle)
    }

    override fun onVideoPlayed() {
        mBinding.courseChapterItemPlayerControllerView.playerPlayPauseBtn
            .setImageResource(R.drawable.ic_pause_circle)
    }

    override fun timeToString(millis: Long): String? {
        return Utils.getTimeWithNoSpace(millis)
    }

    override fun onUpdateCurrentPosition(currentPosition: String, videoDuration: String) {
        var text = currentPosition
        if (videoDuration.isNotEmpty()) {
            text += " / $videoDuration"
        }
        mBinding.courseChapterItemPlayerControllerView.playerControllerTv.text = text
    }

    override fun onFinished() {
        mBinding.courseChapterItemPlayerControllerView.playerPlayPauseBtn.setImageResource(R.drawable.ic_play_circle)
    }

    override fun changeVideoBg(transparent: Boolean) {
    }

    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        initVideoPlayerView(width, height)
    }

    private fun showVideoInFullscreen(requestLanscape: Boolean) {
        mActivityFullScreenStarted = true

        val playerState = PlayerState()
        if (mVideoHelper != null) {
            playerState.currentPosition = mVideoHelper!!.getCurrentPosition()
            playerState.isPlaying = mVideoHelper!!.isPlaying()
            playerState.playerType = mVideoHelper!!.getPlayerType()
        } else {
            playerState.playerType = PlayerHelper.Type.YOUTUBE
        }

        if (mOfflineMode) {
            playerState.path = mLocalFilePath!!
            playerState.isLocalFile = true
        } else {
            playerState.path = mFileItem!!.file
        }

        PlayerHelper.playerState = playerState
        val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
        intent.putExtra(App.REQUEST_LANDSCAPE, requestLanscape)
        startActivity(intent)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val chapterItemMark = ChapterItemMark()
        chapterItemMark.courseId = mCourse.id
        chapterItemMark.status = isChecked.toInt()

        when {
            mSessionItem != null -> {
                chapterItemMark.itemId = mSessionItem!!.id
                chapterItemMark.item = ChapterItemMark.Item.SESSION.value
            }
            mFileItem != null -> {
                chapterItemMark.itemId = mFileItem!!.id
                chapterItemMark.item = ChapterItemMark.Item.FILE.value
            }
            mTextItem != null -> {
                chapterItemMark.itemId = mTextItem!!.id
                chapterItemMark.item = ChapterItemMark.Item.TEXT_LESSON.value
            }
            else -> {
                return
            }
        }

        mBinding.courseChapterItemReadSwitch.setOnCheckedChangeListener(null)
        mPresenter.changeItemStatus(chapterItemMark)
    }

    fun onItemStatusChanged(
        res: BaseResponse,
        chapterItemMark: ChapterItemMark
    ) {
        if (context == null) return

        if (!res.isSuccessful) {
            mBinding.courseChapterItemReadSwitch.isChecked = !chapterItemMark.status.toBoolean()

            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                res.message,
                ToastMaker.Type.ERROR
            )
        }

        mBinding.courseChapterItemReadSwitch.setOnCheckedChangeListener(this)
    }


    override fun onClick(view: View?, position: Int, id: Int) {
        val item =
            (mBinding.courseChapterItemRelatedRv.adapter as CourseCommonRvAdapter).items[position]!!

        val bundle = Bundle()
        bundle.putParcelable(App.COURSE, mCourse)

        if (item is ChapterFileItem) {
            if (item.accessibility == ChapterFileItem.Accessibility.PAID.value && !mCourse.hasUserBought) {
                showBuyAlert()
                return
            }

            bundle.putParcelable(App.ITEM, item)
        }

        val chapterFrag = getTransactionFrag()
        chapterFrag.arguments = bundle
        if (activity is MainActivity) {
            (activity as MainActivity).transact(chapterFrag)
        } else if (activity is SplashScreenActivity) {
            (activity as SplashScreenActivity).transact(chapterFrag)
        }
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun onAttachmentDownloadedFinished(fileId: Int) {
        ApiService.downloadingRequestsResponses.remove(fileId)

        if (context == null) return

        mBinding.courseChapterItemStartBtn.text = getString(R.string.download)
        mBinding.courseChapterItemDownloadProgressBar.progress = 0
        mBinding.courseChapterItemDownloadProgressBar.visibility = View.GONE
        mBinding.courseChapterItemBtnContainerTv.visibility = View.GONE
        mBinding.courseChapterItemBtnContainerTv.text = ""
    }

    override fun onAttachmentDownloadedError() {
        if (context == null) return

        mBinding.courseChapterItemStartBtn.text = getString(R.string.download)
    }

    override fun onAttachmentDownloadUpdate(percent: Float, id: Int?) {
        Handler(Looper.getMainLooper()).post {
            if (percent < 100) {
                mBinding.courseChapterItemBtnContainerTv.text = ("$percent %")
                mBinding.courseChapterItemDownloadProgressBar.progress = percent.roundToInt()
            } else {
                onAttachmentDownloadedFinished(id!!)
            }
        }
    }
}