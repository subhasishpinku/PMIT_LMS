package com.online.course.ui.frag

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.course.R
import com.online.course.databinding.FragCertificateDetailsBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.adapter.StudentRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CertificateDetailsPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.ProgressiveLoadingDialog
import java.io.File
import java.lang.Exception

class CertificateDetailsFrag : NetworkObserverFragment(), View.OnClickListener {

    private lateinit var mBinding: FragCertificateDetailsBinding
    private lateinit var mCertStudents: ArrayList<QuizResult>
    private lateinit var mStoragePermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mPresenter: Presenter.CertificateDetailsPresenter

    private var mResult: QuizResult? = null
    private var mQuiz: Quiz? = null
    private var mCompletionCert: CompletionCert? = null
    private var mSavedCertPath: String? = null


    companion object {
        const val CERT_DOWNLOAD_URL = "${BuildVars.BASE_URL}panel/quizzes/results/{}/show"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStoragePermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                ) {
                    onClick(mBinding.certificateDetailsDownloadBtn)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragCertificateDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.certificate_overview)

        when (val parcelable: Any = requireArguments().getParcelable(App.CERTIFICATE)!!) {
            is QuizResult -> {
                mResult = parcelable
                mQuiz = mResult!!.quiz

                mPresenter = CertificateDetailsPresenterImpl(this)

                mBinding.certificateDetailsImg.setImageResource(R.drawable.cert_default)

                initResultCertMarks()

                if (!App.loggedInUser!!.isUser()) {
                    mPresenter.getStudents()
                }

                mBinding.certificateDetailsTitleTv.text = mQuiz!!.title
                mBinding.certificateDetailsDescTv.text = mQuiz!!.course.title
            }
            is Quiz -> {
                mQuiz = parcelable
                mBinding.certificateDetailsBtnsContainer.visibility = View.GONE

                initQuizCertMarks()

                loadCertImg(mQuiz!!.course.img)

                initCertInfo()

                mBinding.certificateDetailsTitleTv.text = mQuiz!!.title
                mBinding.certificateDetailsDescTv.text = mQuiz!!.course.title
            }

            is CompletionCert -> {
                mCompletionCert = parcelable
                initCertInfo()
                initCompletionCertInfo(mCompletionCert!!)
                mBinding.certificateDetailsImg.setImageResource(R.drawable.cert_default)
            }

            else -> {
                return
            }
        }

        mBinding.certificateDetailsDownloadBtn.setOnClickListener(this)
        mBinding.certificateDetailsShareBtn.setOnClickListener(this)
    }


    private fun initCertInfo() {
        mBinding.certificateDetailsShareTv.text = getString(R.string.certificate_overview)
        mBinding.certificateDetailsShareDescTv.text =
            getString(R.string.certificate_overview_desc)
    }

    private fun loadCertImg(img: String?) {
        if (img != null) {
            Glide.with(requireContext()).load(img)
                .addListener(object : RequestListener<Drawable> {
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
                        mBinding.certificateDetailsImgOverlay.visibility = View.VISIBLE
                        return false
                    }

                }).into(mBinding.certificateDetailsImg)
        }
    }

    private fun initCompletionCertInfo(completionCert: CompletionCert) {
        mBinding.certificateDetailsTitleTv.text = completionCert.course.title
        mBinding.certificateDetailsDescTv.text =
            Utils.getTextAsHtml(completionCert.course.description)

        mBinding.certificateDetailsGradeImg.visibility = View.GONE
        mBinding.certificateDetailsGradeKeyTv.visibility = View.GONE
        mBinding.certificateDetailsGradeTv.visibility = View.GONE

        mBinding.certificateDetailsPassGradeImg.visibility = View.GONE
        mBinding.certificateDetailsPassGradeKeyTv.visibility = View.GONE
        mBinding.certificateDetailsPassGradeTv.visibility = View.GONE

        mBinding.certificateDetailsTakenDateImg.setImageResource(R.drawable.ic_calendar)
        mBinding.certificateDetailsTakenDateKeyTv.text = getString(R.string.taken_date)
        mBinding.certificateDetailsTakenDateTv.text =
            Utils.getDateFromTimestamp(completionCert.date)

        mBinding.certificateDetailsCertIdImg.setImageResource(R.drawable.ic_more_circle)
        mBinding.certificateDetailsCertIdKeyTv.text = getString(R.string.cert_id)
        mBinding.certificateDetailsCertIdTv.text = completionCert.id.toString()
    }

    private fun initQuizCertMarks() {
        mBinding.certificateDetailsGradeKeyTv.text = getString(R.string.grade)
        mBinding.certificateDetailsGradeTv.text = mQuiz?.passMark.toString()

        mBinding.certificateDetailsPassGradeImg.setImageResource(R.drawable.ic_chart)
        mBinding.certificateDetailsPassGradeKeyTv.text = getString(R.string.average)
        mBinding.certificateDetailsPassGradeTv.text = mQuiz?.averageGrade.toString()

        mBinding.certificateDetailsTakenDateImg.setImageResource(R.drawable.ic_user)
        mBinding.certificateDetailsTakenDateKeyTv.text = getString(R.string.total_students)
        mBinding.certificateDetailsTakenDateTv.text = mQuiz?.participatedCount.toString()

        mBinding.certificateDetailsCertIdImg.setImageResource(R.drawable.ic_calendar)
        mBinding.certificateDetailsCertIdKeyTv.text = getString(R.string.date_created)
        mBinding.certificateDetailsCertIdTv.text =
            Utils.getDateFromTimestamp(mQuiz!!.course.createdAt)
    }

    private fun initResultCertMarks() {
        mBinding.certificateDetailsGradeKeyTv.text = getString(R.string.your_grade)
        mBinding.certificateDetailsGradeTv.text = mResult!!.userGrade.toString()

        mBinding.certificateDetailsPassGradeImg.setImageResource(R.drawable.ic_done)
        mBinding.certificateDetailsPassGradeKeyTv.text = getString(R.string.pass_grade)
        mBinding.certificateDetailsPassGradeTv.text = mQuiz!!.passMark.toString()

        mBinding.certificateDetailsTakenDateImg.setImageResource(R.drawable.ic_calendar)
        mBinding.certificateDetailsTakenDateKeyTv.text = getString(R.string.taken_date)
        mBinding.certificateDetailsTakenDateTv.text = Utils.getDateFromTimestamp(mQuiz!!.createdAt)

        mBinding.certificateDetailsCertIdImg.setImageResource(R.drawable.ic_more_circle)
        mBinding.certificateDetailsCertIdKeyTv.text = getString(R.string.cert_id)

        if (mResult!!.certificate != null) {
            mBinding.certificateDetailsCertIdTv.text = mResult!!.certificate!!.id.toString()
        } else {
            mBinding.certificateDetailsCertIdTv.text = "-"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.certificateDetailsDownloadBtn -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mStoragePermissionResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                } else {
                    if (mQuiz != null) {
                        downloadCert(
                            getDownloadUrlFromCert(),
                            certNeedToGetFileNameFromHeader(),
                            true
                        )
                    } else {
                        downloadCert(
                            mCompletionCert!!.link,
                            fileNameFromHeader = true,
                            toDownloads = true,
                            defaultFileName = "cert${mCompletionCert!!.id}"
                        )
                    }
                }
            }

            R.id.certificateDetailsShareBtn -> {
                if (mSavedCertPath != null) {
                    try {
                        Utils.shareFile(
                            requireContext(),
                            getString(R.string.share_certificate_with),
                            FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().packageName + ".provider",
                                File(mSavedCertPath!!)
                            )
                        )
                    } catch (ex: Exception) {
                    }
                } else {
                    if (mQuiz != null) {
                        downloadCert(
                            getDownloadUrlFromCert(),
                            certNeedToGetFileNameFromHeader(),
                            true
                        )
                    } else {
                        downloadCert(
                            mCompletionCert!!.link,
                            fileNameFromHeader = true,
                            toDownloads = false,
                            defaultFileName = "cert${mCompletionCert!!.id}"
                        )
                    }
                }
            }

            R.id.certificateDetailsLatestStdViewAllBtn -> {
                val bundle = Bundle()
                bundle.putParcelableArrayList(App.RESULT, mCertStudents)

                val frag = CertStudentsFrag()
                frag.arguments = bundle
                (activity as MainActivity).transact(frag)
            }
        }
    }

    private fun getDownloadUrlFromCert(): String {
        return CERT_DOWNLOAD_URL.replace("{}", mResult!!.id.toString())
    }

    private fun certNeedToGetFileNameFromHeader(): Boolean {
        return mResult!!.certificate == null
    }

    private fun downloadCert(
        downloadUrl: String,
        fileNameFromHeader: Boolean,
        toDownloads: Boolean,
        defaultFileName: String = "default",
        share: Boolean = true
    ) {
        val bundle = Bundle()

        bundle.putString(App.URL, downloadUrl)
        bundle.putBoolean(App.FILE_NAME_FROM_HEADER, fileNameFromHeader)
        bundle.putString(App.DIR, App.Companion.Directory.CERTIFICATE.value())
        bundle.putBoolean(App.TO_DOWNLOADS, toDownloads)
        bundle.putString(App.DEFAULT_FILE_NAME, defaultFileName)

        val loadingDialog = ProgressiveLoadingDialog()
        loadingDialog.setOnFileSavedListener(object : ItemCallback<String> {
            override fun onItem(filePath: String, vararg args: Any) {
                mSavedCertPath = filePath

                onFileSaved(mSavedCertPath!!)

                if (toDownloads) {
                    ToastMaker.show(
                        requireContext(),
                        getString(R.string.success),
                        getString(R.string.file_saved_in_your_downloads),
                        ToastMaker.Type.SUCCESS
                    )
                } else if (share) {
                    onClick(mBinding.certificateDetailsShareBtn)
                }
            }
        })
        loadingDialog.arguments = bundle
        loadingDialog.show(childFragmentManager, null)
    }

    fun onStudentsReceived(data: List<QuizResult>) {
        if (data.isNotEmpty()) {
            val students = ArrayList<User>()
            for (item in data) {
                val user = item.user

                val student = User()
                student.avatar = user!!.avatar
                student.name = user.name
                student.date = item.createdAt
                students.add(student)
            }

            mBinding.certificateDetailsLatestStdRv.adapter = StudentRvAdapter(students)
            mBinding.certificateDetailsLatestStdTv.visibility = View.VISIBLE
            mBinding.certificateDetailsLatestStdRv.visibility = View.VISIBLE
            if (data.size > 5) {
                mCertStudents = data as ArrayList<QuizResult>
                mBinding.certificateDetailsLatestStdViewAllBtn.setOnClickListener(this)
                mBinding.certificateDetailsLatestStdViewAllBtn.visibility = View.VISIBLE
            }
        }
    }

    fun onFileSaved(filePath: String) {
        Glide.with(requireContext()).load(File(filePath))
            .into(mBinding.certificateDetailsImg)
        mBinding.certificateDetailsImgOverlay.visibility = View.VISIBLE
    }
}