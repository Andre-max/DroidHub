package com.andre_max.droidhub.utils

import android.Manifest
import android.content.Context
import android.os.Build
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import timber.log.Timber

object PermissionUtils {
    private const val writeExternalPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    fun checkPermission(context: Context, permission: String = writeExternalPermission, lambda: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            lambda.invoke(true)

        Dexter.withContext(context)
            .withPermission(permission)
            .withListener(object : BasePermissionListener() {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    super.onPermissionGranted(p0)
                    Timber.d("Permission granted")
                    lambda.invoke(true)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    super.onPermissionDenied(p0)
                    Timber.d("Permission granted")
                    lambda.invoke(false)
                }
            })
            .check()
    }
}