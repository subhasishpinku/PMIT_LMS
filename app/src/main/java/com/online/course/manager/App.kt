package com.online.course.manager

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.contextaware.ContextAware
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.manager.db.AppDb
import com.online.course.manager.net.ApiService
import com.online.course.model.*
import com.online.course.model.view.ViewShape
import com.online.course.ui.MainActivity
import com.online.course.ui.SignInActivity
import com.online.course.ui.widget.AppDialog

class App : Application() {

    companion object {
        const val PAYMENT_STATUS = "paymentStatus"
        const val IS_CANCELABLE = "is_cancelable"
        const val IS_LOGIN = "isLogin"
        const val SELECTION_TYPE = "selectionType"
        const val REQUEDT_TO_LOGIN_FROM_INSIDE_APP = "login_request"
        const val TITLE = "title"
        const val TEXT = "text"
        const val IMG = "img"
        const val BUTTON = "button"
        const val SHOULD_REGISTER = "shouldRegister"
        const val USER_ID = "userId"
        const val SIGN_UP = "signUp"
        const val KEY = "key"
        const val COURSES = "courses"
        const val BADGES = "badges"
        const val COURSE = "course"
        const val USERS = "users"
        const val USER = "user"
        const val CATEGORY = "category"
        const val FILTERS = "filters"
        const val SELECTED = "selected"
        const val BLOG = "blog"
        const val COMMENT = "comment"
        const val ID = "id"
        const val MEETINGS = "meetings"
        const val CERTIFICATE = "certificate"
        const val MEETING = "meeting"
        const val URL = "url"
        const val DIR = "dir"
        const val NOTIF = "notif"
        const val TICKET = "ticket"
        const val POSITION = "position"
        const val RESULT = "result"
        const val REDIRECTION = "redirection"
        const val QUIZ = "quiz"
        const val INSTRUCTOR_TYPE = "instructorType"
        const val AMOUNT = "amount"
        const val PAYOUT_ACCOUT = "payoutAccount"
        const val FINANCIAL = "financial"
        const val ITEM = "item"
        const val ITEMS = "items"
        const val ORDER = "order"
        const val USE_GRID = "useGrid"
        const val REQUEST_LANDSCAPE = "requestLandscape"
        const val CHAPTER_POSITION = "chapterPosition"
        const val CHAPTER_ITEM_POSITION = "chapterItemPosition"
        const val EMPTY_STATE = "emptyState"
        const val OFFLINE = "offline"
        const val ATTACHMENT_POSITION = "attachmentPosition"
        const val TO_DOWNLOADS = "toDownloads"
        const val FILE_NAME_FROM_HEADER = "fileNameFromHeader"
        const val DEFAULT_FILE_NAME = "defaultFileName"
        const val NESTED_ENABLED = "nestedEnabled"
        const val TYPE = "type"

        lateinit var appConfig: AppConfig
        lateinit var currentActivity: AppCompatActivity
        var currentFrag: Fragment? = null
        var loggedInUser: UserProfile? = null
        var quickInfo: QuickInfo? = null

        enum class RegistrationProvider(private val type: Int) {
            GOOGLE(1), FACEBOOK(2);

            fun value(): Int {
                return type
            }
        }

        enum class Registration(private val type: String) {
            EMAIL("email"), MOBILE("mobile");

            fun value(): String {
                return type
            }
        }

        enum class Directory(private val dirName: String) {
            TICKETS_ATTACHMENT("TicketAttachments"),
            FORUMS_ATTACHMENT("ForumsAttachments"),
            CERTIFICATE("Cert"),
            DOWNLOADS("Downloads");

            fun value(): String {
                return dirName
            }
        }

        enum class ItemType(val value: String) {
            WEBINAR("webinar"),
            BUNDLE("bundle"),
            BLOG("blog");
        }

        fun showExitDialog(activity: AppCompatActivity) {
            val bundle = Bundle()
            bundle.putString(App.TITLE, activity.getString(R.string.exit))
            bundle.putString(App.TEXT, activity.getString(R.string.exit_desc))

            val appDialog = AppDialog.instance
            appDialog.arguments = bundle
            appDialog.setOnDialogBtnsClickedListener(
                AppDialog.DialogType.YES_CANCEL,
                object : AppDialog.OnDialogCreated {
                    override fun onCancel() {
                    }

                    override fun onOk() {
                        activity.finish()
                    }

                })
            appDialog.show(activity.supportFragmentManager, null)
        }

        fun initHeader(statusBar: View) {
            val statusBarHeight = statusBarHeight(statusBar.context)
            statusBar.post {
                if (statusBar.height != statusBarHeight) {
                    val params = statusBar.layoutParams as ViewGroup.LayoutParams
                    params.height = statusBarHeight
                    statusBar.requestLayout()
                }
            }
        }

        fun statusBarHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun navBarHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun isLoggedIn(): Boolean {
            return loggedInUser != null
        }

        fun saveToLocal(
            json: String,
            context: Context,
            type: AppDb.DataType,
            keySuffix: String? = null
        ) {
            Thread {
                val db = AppDb(context)
                db.saveData(type, json, keySuffix)
                db.close()
            }.start()
        }

        fun getShapeFromColor(view: View, color: Int, currentCorners: Float = 0f): ViewShape {
            val topShape = ViewShape()
            topShape.view = view

            val gradientDrawable = GradientDrawable()
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            gradientDrawable.setColor(ContextCompat.getColor(view.context, color))

            topShape.viewBg = gradientDrawable
            topShape.currentCorners = Utils.changeDpToPx(view.context, currentCorners)
            return topShape
        }

        fun logout(activity: MainActivity) {
            Thread {
                val appDb = AppDb(activity)
                appDb.deleteAllData()
                appDb.close()
            }.start()

            loggedInUser = null
            quickInfo = null
            ApiService.createApiService()

            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}