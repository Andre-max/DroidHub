package com.andre_max.droidhub.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import com.andre_max.droidhub.models.RemoteFile
import com.andre_max.droidhub.utils.FirebaseUtils.firebaseAuth
import com.andre_max.droidhub.utils.FirebaseUtils.firebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.*


object FirebaseStorageUtils {

    @ExperimentalCoroutinesApi
    fun getStorageUrlFromRemoteFile(remoteFile: RemoteFile): Flow<Uri> = channelFlow {
        firebaseStorage.getReference(remoteFile.storagePath).downloadUrl
            .addOnSuccessListener {
                launch {
                    Timber.d("Sending uri in downloadRemoteFile() as $it")
                    send(it)
                    close()
                }
            }

        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun uploadFileToFirebaseStorage(
        context: Context,
        firestoreId: UUID,
        uploadFileUri: Uri?,
        fileNameInput: String?
    ) =
        channelFlow {
            firebaseStorage.getReference("${firebaseAuth.uid}/$firestoreId")
                .putFile(uploadFileUri ?: run {
                    send(CustomResult.Failure("Select a file to upload"))
                    close()
                    return@channelFlow
                })
                .addOnSuccessListener {
                    val metadata = it?.metadata ?: return@addOnSuccessListener
                    val remoteStoragePath = metadata.path
                    val remoteContentType = metadata.contentType ?: ""
                    val remoteSizeBytes = metadata.sizeBytes
                    val remoteTimeCreated = metadata.creationTimeMillis

                    Timber.d("remoteStoragePath is $remoteStoragePath, remoteContentType is $remoteContentType, remoteSizeBytes is $remoteSizeBytes and remoteTimeCreated is $remoteTimeCreated")

                    // Already checked if uploadFileUri is null in the putFile() function above
                    val extension =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            File(uploadFileUri?.toString()!!).extension
                        else {
                            val contentResolver: ContentResolver = context.contentResolver
                            val mime = MimeTypeMap.getSingleton()
                            mime.getExtensionFromMimeType(contentResolver.getType(uploadFileUri!!))
                        }

                    val remoteFile = RemoteFile(
                        displayName = fileNameInput ?: firestoreId.toString(),
                        storagePath = remoteStoragePath,
                        firestoreId = firestoreId.toString(),
                        sizeInBytes = remoteSizeBytes,
                        timeStamp = remoteTimeCreated,
                        contentType = remoteContentType,
                        extension = extension ?: ""
                    )

                    FireStoreUtils.addFileToFirebaseFireStore(
                        remoteFile
                    ) { isSuccess ->
                        launch {
                            send(
                                if (isSuccess)
                                    CustomResult.Success("File saved in database")
                                else
                                    CustomResult.Failure("Error occurred while adding file to database.")
                            )

                            close()
                        }
                    }
                }
                .addOnFailureListener {
                    Timber.e(it)
                    launch {
                        send(CustomResult.Error(it))
                        close()
                    }
                }

            awaitClose()
        }

    fun deleteRemoteFileFromFirebaseStorage(remoteFile: RemoteFile, onSuccess: () -> Unit) {
        firebaseStorage.getReference(remoteFile.storagePath)
            .delete()
            .addOnSuccessListener {
                Timber.d("deleteCustomFile in FirebaseStorage succeeded with deletedCustomFile as $remoteFile")
                onSuccess()
            }
            .addOnFailureListener {
                Timber.e(it)
            }
    }

}