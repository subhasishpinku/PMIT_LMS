package com.online.course.manager

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

class PermissionManager(
    private val context: Context,
    private val permissions: Array<String>,
    private val resultManager: ActivityResultLauncher<Array<String>>
) {

    fun isGranted(): Boolean {
        for (perm in permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                    context,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        return true
    }


    fun request() {
        resultManager.launch(permissions)
    }

}