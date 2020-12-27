package com.andre_max.droidhub.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.View
import com.andre_max.droidhub.models.RemoteFile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

object LocalStorageUtils {

    @ExperimentalCoroutinesApi
    @Suppress("DEPRECATION")
    fun downloadRemoteFile(view: View, remoteFile: RemoteFile) {
        GlobalScope.launch {
            FirebaseStorageUtils.getStorageUrlFromRemoteFile(remoteFile)
                .collect {
                    Timber.d("Uri collected in downloadRemoteFile() as $it")

                    val request = DownloadManager.Request(it).apply {
                        setTitle(remoteFile.displayName)
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDescription("The file is downloading...")
                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, remoteFile.displayName
                        )
                        allowScanningByMediaScanner()
                    }

                    val downloadManager =
                        view.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                    PermissionUtils.checkPermission(view.context) { isGranted ->
                        Timber.d("Permission lambda invoked with isGranted as $isGranted")
                        if (isGranted) {
                            downloadManager.enqueue(request)
                            view.showLongSnackbar("Download has started")
                        }
                        else
                            view.showLongSnackbar("Permission is required to download file.")
                    }
                }
        }
    }

    fun getLocalFileNameFromUri(localFileUri: Uri?): String {
        val file = File(localFileUri?.path ?: return "")
        Timber.d("file.name is ${file.name}")
        Timber.d("file.nameWithoutExtension is ${file.nameWithoutExtension}")
        Timber.d("file.length is ${file.length()}")

        return "File selected:  ${file.name}"
    }

}