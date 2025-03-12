package com.online.course.manager

import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.CalendarContract
import android.provider.MediaStore
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.model.AppConfig
import com.online.course.model.BaseResponse
import java.io.*
import java.net.URL
import java.text.CharacterIterator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator
import java.util.*
import kotlin.math.abs


object Utils {

    val VIDEO_FORMATS = arrayOf("MP4", "MOV", "WMV", "AVI", "AVCHD", "MKV", "MPEG-2", "WEBM")

    @JvmStatic
    fun changeDpToPx(context: Context, `val`: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            `val`,
            context.resources.displayMetrics
        )
    }

    fun pickImg(fragment: Fragment, requestCode: Int) {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(fragment.requireContext().packageManager) != null) {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    fun pickFile(fragment: Fragment, requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        if (intent.resolveActivity(fragment.requireContext().packageManager) != null) {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    fun openCaller(context: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun getTimeWithNoSpace(millis: Long): String {
        var timeMs = millis
        val prefix = if (timeMs < 0) "-" else ""
        timeMs = abs(timeMs)
        val totalSeconds: Long = (timeMs + 500) / 1000
        val secs = totalSeconds % 60
        val mins = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        val formatter = Formatter()
        return if (hours > 0) formatter.format("%s%d:%02d:%02d", prefix, hours, mins, secs)
            .toString() else formatter.format("%s%02d:%02d", prefix, mins, secs).toString()
    }

    fun getRealPathFromURI(context: Context, uri: Uri?): String {
        val cursor =
            context.contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val path = cursor.getString(idx)
        cursor.close()
        return path
    }

    @JvmStatic
    fun convertToTimeZone(
        initialTimeInSeconds: Long,
        origin: TimeZone,
        target: TimeZone
    ): Long {
        return initialTimeInSeconds * 1000 + target.rawOffset - origin.getOffset(
            initialTimeInSeconds * 1000
        )
    }

    fun doesDatabaseExist(
        context: Context,
        dbName: String?
    ): Boolean {
        return context.getDatabasePath(dbName).exists()
    }

    val uTCInSeconds: Int
        get() = (System.currentTimeMillis() / 1000).toInt()

    fun getCurrentDateTime(
        timeInSeconds: Int,
        timeZone: String?
    ): Array<String> {
        val utc = convertToTimeZone(
            timeInSeconds.toLong(),
            TimeZone.getTimeZone(timeZone),
            TimeZone.getDefault()
        )
        val date = Date(utc)
        val simpleDateFormat =
            SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        return simpleDateFormat.format(date).split(" ".toRegex()).toTypedArray()
    }

    fun getCurrentDateTime(
        dateTime: String?,
        timeZone: String?,
        fromFormt: String = "yyyy-MM-dd HH:mm:ss",
        toFormat: String = "yyyy/MM/dd HH:mm:ss"
    ): Array<String>? {
        val format =
            SimpleDateFormat(fromFormt, Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone(timeZone)
        val df =
            SimpleDateFormat(toFormat, Locale.getDefault())
        df.timeZone = TimeZone.getDefault()
        try {
            return df.format(format.parse(dateTime)).split(" ".toRegex()).toTypedArray()
        } catch (e: ParseException) {
//            if (BuildVars.LOGS_ENABLED) {
//                Log.e(
//                    Utils::class.java.simpleName,
//                    "getDateTime -> ParseException -> " + e.message
//                )
//            }
        }
        return null
    }

    fun formatTime(time: String): String {
        return if (time.length > 5) {
            time.substring(0, 5)
        } else time
    }

    fun isVisible(windowManager: WindowManager, view: View?): Boolean {
        if (view == null) {
            return false
        }
        if (!view.isShown) {
            return false
        }
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val actualPosition = Rect()
        view.getGlobalVisibleRect(actualPosition)
        val screen =
            Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        return actualPosition.intersect(screen)
    }

    fun getScreenWidth(windowManager: WindowManager): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    fun getWindowInsets(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.rootWindowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
        } else {
//            window.insetsController?.
        }
    }

    val currentDate: String
        get() = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            .format(Date())

    val currentDateTime: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())

    val currentDateTimeInUTC: String
        get() {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            val sdf =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(calendar.time)
        }

    @JvmStatic
    fun getDateTimeFromUTC(dateTime: String): Array<String>? {
        var dateTimeStr = ""
        for (dt in dateTime) {
            if (dt.isDigit() || dt == ':' || dt == '-')
                dateTimeStr += dt
        }

        val format =
            SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.getDefault())
        val df =
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        df.timeZone = TimeZone.getDefault()
        try {
            return df.format(format.parse(dateTimeStr)).split(" ".toRegex()).toTypedArray()
        } catch (e: ParseException) {
//            if (BuildVars.LOGS_ENABLED) {
//                Log.e(
//                    Utils::class.java.simpleName,
//                    "getDateTime -> ParseException -> " + e.message
//                )
//            }
        }
        return null
    }

    fun getDateInUTC(dateTime: String?): String? {
        val format =
            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val df =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        df.timeZone = TimeZone.getTimeZone("UTC")
        try {
            return df.format(format.parse(dateTime))
        } catch (e: ParseException) {
//            if (BuildVars.LOGS_ENABLED) {
//                Log.e(
//                    Utils::class.java.simpleName,
//                    "getDateTime -> ParseException -> " + e.message
//                )
//            }
        }
        return null
    }

    fun saveImageToAppDir(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): String? {
        // save bitmap to cache directory
        try {
            val cachePath = File(context.cacheDir, "loc")
            if (!cachePath.exists()) {
                cachePath.mkdirs() // don't forget to make the directory
            }

            val absoluteFileName = cachePath.path + File.separator + fileName + ".png"

            val file = File(absoluteFileName)
            if (file.exists())
                file.delete()

            val stream = FileOutputStream(file) // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            return absoluteFileName
        } catch (e: IOException) {
            return null
        }
    }

    fun saveFile(
        context: Context,
        directory: String,
        fileName: String,
        byteStream: InputStream
    ): String? {
        // save bitmap to cache directory
        try {
            val filesDir = File(context.filesDir, directory)
            if (!filesDir.exists()) {
                filesDir.mkdirs() // don't forget to make the directory
            }

            val absoluteFileName = filesDir.path + File.separator + fileName

            val file = File(absoluteFileName)
            if (file.exists())
                file.delete()

            byteStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            return absoluteFileName
        } catch (e: IOException) {
            return null
        }
    }

    fun getScreenShot(v: View): Bitmap {
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        return bitmap
    }

    fun getScreenShotFromHalfOfScreen(v: View): Bitmap {
        val bitmap = Bitmap.createBitmap(v.width, v.height / 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        return bitmap
    }

    fun standandTime(timeInSeconds: Int): String {
        var time = timeInSeconds
        val hour: Int = time / 3600
        var s = ""

        if (hour > 0) {
            time -= hour * 3600
            s += "$hour:"
        }

        val minutes: Int = time / 60
        val seconds: Int = time % 60

        var preSec = ""

        if (seconds < 10)
            preSec = "0"

        s += "$minutes:$preSec$seconds"
        return s
    }

    fun formatTime(timeInSeconds: Int): Array<Int> {
        var time = timeInSeconds
        val days: Int = time / (3600 * 24)

        if (days > 0) {
            time -= days * (3600 * 24)
        }

        val hours = time / 3600
        if (hours > 0) {
            time -= hours * 3600
        }

        val minutes: Int = time / 60
        val seconds: Int = time % 60

        return arrayOf(days, hours, minutes, seconds)
    }

    fun getDuration(context: Context, mins: Int): String {
        val formatTime = formatTime(mins * 60)
        val hours = formatTime[1]
        val minutes = formatTime[2]

        val courseDuration = if (hours > 0) {
            if (minutes > 0) {
                if (minutes < 10) {
                    "${hours}:0${minutes} ${context.getString(R.string.hours)}"
                } else {
                    "${hours}:${minutes} ${context.getString(R.string.hours)}"
                }
            } else {
                "$hours ${context.getString(R.string.hour)}"
            }
        } else {
            "$minutes ${context.getString(R.string.minutes)}"
        }

        return courseDuration
    }

    fun changeToIntInCaseOfZeroDecimal(value: String): String {
        val valueArr = value.split('.')
        return if (valueArr.size == 2 && valueArr[1].startsWith('0'))
            valueArr[0]
        else
            value
    }

//    fun mapWithDuplicateKeys(params: List<KeyValuePair>): MutableMap<String, String> {
//        val paramMap: MutableMap<String, String> = HashMap()
//        var paramValue: String
//        for (paramPair in params) {
//            if (paramPair.key.isNotEmpty()) {
//                try {
//                    if (paramMap.containsKey(paramPair.key)) {
//                        // Add the duplicate key and new value onto the previous value
//                        // so (key, value) will now look like (key, value&key=value2)
//                        // which is a hack to work with Retrofit's QueryMap
//                        paramValue = paramMap[paramPair.key]!!
//                        paramValue += "&" + paramPair.key + "=" + URLEncoder.encode(
//                            paramPair.value, "UTF-8"
//                        )
//                    } else {
//                        // This is the first value, so directly map it
//                        paramValue = URLEncoder.encode(paramPair.value, "UTF-8")
//                    }
//                    paramMap[paramPair.key] = paramValue
//                } catch (e: UnsupportedEncodingException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//        return paramMap
//    }

    fun subtractDateTimeFromNowInSec(
        dateTime: String,
        dateTimeAssumesToBeBigger: Boolean = false
    ): Int {
        val dateTimeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        if (dateTimeAssumesToBeBigger) {
            return ((dateTimeFormat.parse(dateTime).time - Date().time) / 1000).toInt()
        }
        return ((Date().time - dateTimeFormat.parse(dateTime).time) / 1000).toInt()
    }

//    fun convert24FormatTo12(context: Context, time: String): String {
//        var t = time
//        val split = time.split(":")
//        val hourNumber = split[0].toInt()
//        t = if (hourNumber > 12)
//            hourNumber.toString() + ":" + split[1] + context.getString(R.string.pm)
//        else
//            split[0] + ":" + split[1] + context.getString(R.string.am)
//        return t
//    }

    fun isViewOverlapping(firstView: View, secondView: View): Boolean {
        val firstPosition = IntArray(2)
        val secondPosition = IntArray(2)

        firstView.getLocationOnScreen(firstPosition)
        secondView.getLocationOnScreen(secondPosition)

        // Rect constructor parameters: left, top, right, bottom

        // Rect constructor parameters: left, top, right, bottom
        val rectFirstView = Rect(
            firstPosition[0],
            firstPosition[1],
            firstPosition[0] + firstView.measuredWidth,
            firstPosition[1] + firstView.measuredHeight
        )
        val rectSecondView = Rect(
            secondPosition[0],
            secondPosition[1],
            secondPosition[0] + secondView.measuredWidth,
            secondPosition[1] + secondView.measuredHeight
        )

        return rectFirstView.intersect(rectSecondView)
    }

    fun bitmapFromVectorDrawable(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): Bitmap {
        val drawable = AppCompatResources.getDrawable(context, vectorDrawableResourceId)
        //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            drawable = (DrawableCompat.wrap(drawable)).mutate();
//        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas);

        return bitmap
    }

    fun changeLocale(context: Context, language: String) {
        val resources = context.resources
        val config = resources.configuration
        val locale = Locale(language)
        Locale.setDefault(locale)
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.createConfigurationContext(config)

        resources.updateConfiguration(config, resources.displayMetrics)
    }

//    fun createBlurBitmap(containerView: View): Bitmap? {
//        val bitmap = captureView(containerView)
//        if (bitmap != null) {
//            ImageHelper.blurBitmapWithRenderscript(
//                RenderScript.create(containerView.context),
//                bitmap
//            )
//        }
//        return bitmap
//    }

    private fun captureView(view: View): Bitmap? {
        //Create a Bitmap with the same dimensions as the View
        val image = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_4444
        ) //reduce quality
        //Draw the view inside the Bitmap
        val canvas = Canvas(image)
        view.draw(canvas)

        //Make it frosty
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val filter: ColorFilter = LightingColorFilter(-0x1, 0x00222222) // lighten
        //ColorFilter filter =
        //   new LightingColorFilter(0xFF7F7F7F, 0x00000000); // darken
        paint.colorFilter = filter
        canvas.drawBitmap(image, 0f, 0f, paint)
        return image
    }

    fun humanReadableByteCountSI(bytes: Long): String {
        var bt = bytes
        if (-1000 < bt && bt < 1000) {
            return "$bt B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bt <= -999950 || bt >= 999950) {
            bt /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", bt / 1000.0, ci.current(), Locale.getDefault())
    }

    fun extractFileNameFromUrl(name: String): String {
        return name.substring(name.lastIndexOf("/") + 1, name.length)
    }

    fun extractFileExtensionFromString(file: String): String {
        return file.substring(file.lastIndexOf(".") + 1, file.length);
    }

    fun copyFile(
        context: Context,
        src: File,
        dir: String,
        name: String
    ) {
        try {
            val filesDir = File(context.filesDir, dir)
            if (!filesDir.exists())
                filesDir.mkdirs() // don't forget to make the directory

            val inStream = FileInputStream(src)
            val outStream =
                FileOutputStream(filesDir.absolutePath + File.separator + name)
            val inChannel = inStream.channel
            val outChannel = outStream.channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
            inStream.close()
            outStream.close()
        } catch (e: IOException) {
//            if (BuildVars.LOGS_ENABLED) {
//                Log.e("copyFile", "ex:$e")
//            }
        }
    }

    fun copyFileToDownloads(
        context: Context,
        src: File,
        descFileName: String
    ) {
        try {

            val inStream = FileInputStream(src)

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, descFileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "*/*")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri =
                        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        val openOutputStream = resolver.openOutputStream(uri)
                        openOutputStream?.let { copy(inStream, it) }
                        openOutputStream?.close()
                    }
                } else {
                    val target = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        descFileName
                    )
                    val inChannel = inStream.channel
                    val outStream = FileOutputStream(target)
                    val outChannel = outStream.channel
                    inChannel.transferTo(0, inChannel.size(), outChannel)
                    outStream.close()
                }
                inStream.close()
            } catch (ex: Exception) {
                if (BuildVars.LOGS_ENABLED) {
                    Log.e("Utils", "saveImageToDownload -> $ex")
                }
            }


        } catch (e: IOException) {
//            if (BuildVars.LOGS_ENABLED) {
//                Log.e("copyFile", "ex:$e")
//            }
        }
    }

    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    fun getDeviceDetails(): String? {
        var deviceDetails = ""

        if (Build.MODEL != null) {
            deviceDetails += """
                MODEL: ${Build.MODEL}
                """.trimIndent()
        }

        if (Build.ID != null) {
            deviceDetails += """
                ID: ${Build.ID}
                """.trimIndent()
        }
        if (Build.MANUFACTURER != null) {
            deviceDetails += """
                Manufacture: ${Build.MANUFACTURER}
                """.trimIndent()
        }
        if (Build.BRAND != null) {
            deviceDetails += """
                brand: ${Build.BRAND}
                """.trimIndent()
        }
        if (Build.TYPE != null) {
            deviceDetails += """
                type: ${Build.TYPE}
                """.trimIndent()
        }
        if (Build.USER != null) {
            deviceDetails += """
                user: ${Build.USER}
                """.trimIndent()
        }

        deviceDetails += """
            BASE: ${Build.VERSION_CODES.BASE}
            """.trimIndent()

        if (Build.VERSION.INCREMENTAL != null) {
            deviceDetails += """
                INCREMENTAL ${Build.VERSION.INCREMENTAL}
                """.trimIndent()
        }
        if (Build.BOARD != null) {
            deviceDetails += """
                BOARD: ${Build.BOARD}
                """.trimIndent()
        }
        if (Build.HOST != null) {
            deviceDetails += """
                HOST ${Build.HOST}
                """.trimIndent()
        }
        if (Build.FINGERPRINT != null) {
            deviceDetails += """
                FINGERPRINT: ${Build.FINGERPRINT}
                """.trimIndent()
        }
        if (Build.VERSION.RELEASE != null) {
            deviceDetails += """
                Version Code: ${Build.VERSION.RELEASE}
                """.trimIndent()
        }
        return deviceDetails
    }

    fun getAppVersionName(context: Context): String? {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun showKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun closeKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun getFormattedDuration(context: Context, duration: Int): String {
        return if (duration >= 60) {
            val dur = (duration / 60).toString() + ":" + duration % 60
            "$dur ${context.getString(R.string.hours)}"
        } else {
            "$duration ${context.getString(R.string.minutes)}"
        }
    }

    fun getDateFromTimestamp(timestamp: Long, format: String = "dd MMM yyyy"): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        return sdf.format(date)
    }

    fun getSubtractedTimeInSeconds(end: String, start: String): Long {
        val df = SimpleDateFormat("hh:mma", Locale.ENGLISH)
        val date1 = df.parse(start)!!
        val date2 = df.parse(end)!!
        return (date2.time - date1.time) / 1000
    }

    fun getDateTimeFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("dd MMM yyyy | HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        return sdf.format(date)
    }

    fun getDateTimeWithDayFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("EEE dd MMM yyyy | HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        return sdf.format(date)
    }

    fun getDateWithDayFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        return sdf.format(date)
    }

    fun encodeTo64(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun copyToClipbaord(context: Context, label: String, text: String) {
        val clipboard = ContextCompat.getSystemService(
            context,
            ClipboardManager::class.java
        )
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
    }

    fun openLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun openEmail(context: Context, title: String, recipient: String, subject: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        try {
            context.startActivity(Intent.createChooser(intent, title))
        } catch (ex: ActivityNotFoundException) {
        }
    }

    fun addToCalendar(context: Context, title: String, begin: Long, end: Long) {
        val calIntent = Intent(Intent.ACTION_INSERT)
        calIntent.data = CalendarContract.Events.CONTENT_URI
        calIntent.putExtra(CalendarContract.Events.TITLE, title)
        calIntent.putExtra(
            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            begin
        )
        if (end > 0) {
            calIntent.putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                end
            )
        }
        context.startActivity(calIntent)
    }

    fun addToCalendar(
        context: Context,
        title: String,
        desc: String?,
        durationInMins: Int,
        date: Long
    ) {
        val calIntent = Intent(Intent.ACTION_INSERT)
        calIntent.data = CalendarContract.Events.CONTENT_URI
        calIntent.putExtra(CalendarContract.Events.TITLE, title)
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, desc)
        calIntent.putExtra(CalendarContract.Events.DURATION, "${durationInMins}M")
        calIntent.putExtra(CalendarContract.Events.DTSTART, date)
        calIntent.putExtra(CalendarContract.Events.DTEND, date)
        calIntent.putExtra(CalendarContract.Events.ALL_DAY, true)
        context.startActivity(calIntent)
    }


    fun shareLink(context: Context, title: String, url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, url)
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun shareFile(context: Context, title: String, uri: Uri) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "*/*"
        sharingIntent.putExtra(
            Intent.EXTRA_STREAM,
            uri
        )
        context.startActivity(Intent.createChooser(sharingIntent, title))
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val mimeType: String?
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            context.contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri.toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase()
            )
        }
        return mimeType
    }

    fun setViewAndChildrenEnabled(
        view: View,
        enabled: Boolean
    ) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            val viewGroup = view
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                if (child is Toolbar) {
                    setViewAndChildrenEnabled(child, true)
                } else {
                    setViewAndChildrenEnabled(child, enabled)
                }
            }
        }
    }

    fun saveFileToDownloads(context: Context, url: String, fileName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "*/*")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    URL(url).openStream().use { input ->
                        resolver.openOutputStream(uri).use { output ->
                            input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                        }
                    }
                }
            } else {
                val target = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                URL(url).openStream().use { input ->
                    FileOutputStream(target).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        } catch (ex: Exception) {
        }
    }

    fun saveFileToDownloads(context: Context, inputStream: InputStream, fileName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "*/*")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    inputStream.use { input ->
                        resolver.openOutputStream(uri).use { output ->
                            input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                        }
                    }
                }
            } else {
                val target = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )
                inputStream.use { input ->
                    FileOutputStream(target).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("Utils", "saveImageToDownload -> $ex")
        }
    }


    fun getBaseUrlAndHostFromUrl(url: String): Array<String>? {
        return try {
            val urlObject = URL(url)
            val baseUrl = urlObject.protocol + "://" + urlObject.host + "/"
            var path = urlObject.path
            if (path.startsWith("/"))
                path = path.removeRange(0, 0)
            arrayOf(baseUrl, path)
        } catch (ex: Exception) {
            null
        }
    }

    fun getTextAsHtml(text: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }
    }

    fun getFormattedPriceWithCurrency(formattedPrice: String): String {
        var priceWithCurrency = ""
        val currency = App.appConfig.currency.sign

        App.appConfig.currencyPosition

        when (App.appConfig.currencyPosition) {
            AppConfig.CurrencyPosition.RIGHT.value -> {
                priceWithCurrency = "$formattedPrice$currency"
            }

            AppConfig.CurrencyPosition.RIGHT_WITH_SPACE.value -> {
                priceWithCurrency = "$formattedPrice $currency"
            }

            AppConfig.CurrencyPosition.LEFT.value -> {
                priceWithCurrency = "$currency$formattedPrice"
            }

            AppConfig.CurrencyPosition.LEFT_WITH_SPACE.value -> {
                priceWithCurrency = "$currency $formattedPrice"
            }
        }

        return priceWithCurrency
    }

    fun formatPrice(context: Context, price: Double, showFree: Boolean = true): String {
        return if (price > 0.0 || !showFree) {
            val formattedPrice = if ((price % 1) == 0.0)
                price.toInt().toString()
            else
                String.format("%.2f", price)
            getFormattedPriceWithCurrency(formattedPrice)
        } else {
            context.getString(R.string.free)
        }
    }

    fun isYoutubeUrl(url: String): Boolean {
        val pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+"
        return url.isNotEmpty() && url.matches(Regex(pattern))
    }

    fun isVimeoUrl(url: String): Boolean {
        val pattern = "^(http(s)?:\\/\\/)?((w){3}.)?vimeo?(\\.com)?\\/.+"
        return url.isNotEmpty() && url.matches(Regex(pattern))
    }

    fun viewFile(context: Context, file: File) {
        try {
            val fileUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )

            val mime = context.contentResolver.getType(fileUri)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val res = BaseResponse()
            res.isSuccessful = false
            res.message = context.getString(R.string.cannot_open_this_file)

            ToastMaker.show(context, res)
        }
    }

    //    fun Double.format()
    fun Boolean.toInt() = if (this) 1 else 0
    fun Int.toBoolean() = this != 0


    fun TextView.isEllipsized() = layout.text.toString() != text.toString()
}