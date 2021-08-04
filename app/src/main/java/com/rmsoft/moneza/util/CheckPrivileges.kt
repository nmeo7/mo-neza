package com.rmsoft.moneza.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class CheckPrivileges (private var context: Context, private var activity: Activity) {

    private fun runtimeAskPrivileges (permission: String) : Boolean
    {
        val myVersion = Build.VERSION.SDK_INT

        if (myVersion > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    101
                )
                return true
            }
        }
        return false
    }

    fun requestAllPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            ),
            101
        )
    }

    fun requestCallPhonePermission () : Boolean
    {
        return runtimeAskPrivileges (Manifest.permission.CALL_PHONE)
    }

    fun requestCameraPermission () : Boolean
    {
        return runtimeAskPrivileges (Manifest.permission.CAMERA)
    }
}