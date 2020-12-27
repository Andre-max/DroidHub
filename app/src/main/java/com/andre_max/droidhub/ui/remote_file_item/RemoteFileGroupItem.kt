package com.andre_max.droidhub.ui.remote_file_item

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.text.format.Formatter.formatShortFileSize
import android.view.View
import com.andre_max.droidhub.R
import com.andre_max.droidhub.databinding.RemoteFileLayoutBinding
import com.andre_max.droidhub.models.RemoteFile
import com.andre_max.droidhub.utils.FileTypes
import com.andre_max.droidhub.utils.FirebaseStorageUtils
import com.andre_max.droidhub.utils.getFileTypeFromFirebaseContentType
import com.bumptech.glide.Glide
import com.xwray.groupie.viewbinding.BindableItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

class RemoteFileGroupItem(
    val remoteFile: RemoteFile,
    private val remoteFileClickLambda: (RemoteFile) -> Unit,
    private val remoteFileLongClickLambda: (RemoteFileGroupItem, View) -> Boolean
) : BindableItem<RemoteFileLayoutBinding>() {
    var index = -1

    @SuppressLint("SetTextI18n")
    @ExperimentalCoroutinesApi
    override fun bind(binding: RemoteFileLayoutBinding, position: Int) {
        index = position

        val fileType = FileTypes.valueOf(remoteFile.getFileTypeFromFirebaseContentType())
        binding.remoteFileName.text = "${remoteFile.displayName}.${remoteFile.extension}"
        binding.sizeAndDateText.text = binding.getSizeAndDate(remoteFile)
        binding.remoteFilePreviewImage.setImageResource(fileType.getDrawable())

        loadImageBasedOnFileType(binding, fileType)

        binding.root.setOnClickListener {
            remoteFileClickLambda(remoteFile)
        }
        binding.root.setOnLongClickListener {
            remoteFileLongClickLambda(this, binding.root)
        }
    }

    override fun getLayout(): Int = R.layout.remote_file_layout
    override fun initializeViewBinding(view: View) =
        RemoteFileLayoutBinding.bind(view)

    override fun bind(
        binding: RemoteFileLayoutBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.bind(binding, position, payloads)
        else {
            remoteFile.displayName = payloads[0] as String
            binding.remoteFileName.text = "${remoteFile.displayName}.${remoteFile.extension}"
        }
    }

    @ExperimentalCoroutinesApi
    private fun loadImageBasedOnFileType(binding: RemoteFileLayoutBinding, fileType: FileTypes) {
        if (fileType == FileTypes.Images || fileType == FileTypes.Video) {
            GlobalScope.launch(Dispatchers.IO) {
                FirebaseStorageUtils.getStorageUrlFromRemoteFile(remoteFile)
                    .collect { remoteUri ->
                        if (fileType == FileTypes.Images)
                            binding.glideImageInMainThread(remoteUri)
                        else {
                            val mmR = MediaMetadataRetriever()
                            mmR.setDataSource(remoteUri.toString(), mapOf())
                            binding.glideImageInMainThread(mmR.frameAtTime)
                            mmR.release()
                        }
                    }
            }
        }
    }

    private suspend fun RemoteFileLayoutBinding.glideImageInMainThread(any: Any?) {
        withContext(Dispatchers.Main) {
            Glide.with(root)
                .load(any ?: return@withContext)
                .centerCrop()
                .into(remoteFilePreviewImage)
        }
    }

    private fun RemoteFileLayoutBinding.getSizeAndDate(remoteFile: RemoteFile): String {
        val displaySize = formatShortFileSize(root.context, remoteFile.sizeInBytes)
        val displayDate = getDisplayDateFromRemoteFile(remoteFile)

        return "$displaySize, $displayDate"
    }

    private fun getDisplayDateFromRemoteFile(remoteFile: RemoteFile) =
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(remoteFile.timeStamp)
}