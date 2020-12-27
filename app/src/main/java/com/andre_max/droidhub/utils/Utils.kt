package com.andre_max.droidhub.utils

import com.andre_max.droidhub.R
import com.andre_max.droidhub.models.RemoteFile

enum class Status {
    Completed, Loading, Inactive
}

sealed class CustomResult {
    data class Success(val data: String) : CustomResult()
    data class Failure(val data: String) : CustomResult()
    data class Error(val throwable: Throwable) : CustomResult()
}

enum class FileTypes {
    Video, Images, Audio, Documents, APK;

    fun getDrawable(): Int {
        return when (this) {
            Video -> R.drawable.ic_video_library
            Images -> R.drawable.ic_image
            Audio -> R.drawable.ic_music_note
            Documents -> R.drawable.ic_document
            APK -> R.drawable.ic_adb
        }
    }
}

//val audio = "audio/x-wav"
//val image = "image/jpeg"
//val video = "video/mp4"
//val apk = "application/vnd.android.package-archive"
//val pdf = "application/pdf"

fun RemoteFile.getFileTypeFromFirebaseContentType(): String =
    when (contentType.substringBefore("/")) {
        "audio" -> FileTypes.Audio
        "image" -> FileTypes.Images
        "video" -> FileTypes.Video
        else -> {
            if (extension == "apk")
                FileTypes.APK
            else
                FileTypes.Documents
        }
    }.toString()

data class User(var username: String, var email: String, var password: String, var uid: String) {
    constructor() : this("", "", "", "")
}
//
//fun <T> LiveData<T>.debounce(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
//    val source = this
//    val handler = Handler(Looper.getMainLooper())
//
//    val runnable = Runnable {
//        mld.value = source.value
//    }
//
//    mld.addSource(source) {
//        handler.removeCallbacks(runnable)
//        handler.postDelayed(runnable, duration)
//    }
//}
