package com.andre_max.droidhub.utils

import com.andre_max.droidhub.models.RemoteFile
import com.andre_max.droidhub.utils.FirebaseUtils.fireStore
import com.andre_max.droidhub.utils.FirebaseUtils.firebaseAuth
import timber.log.Timber

object FireStoreUtils {

    fun addFileToFirebaseFireStore(
        remoteFile: RemoteFile,
        completeLambda: (Boolean) -> Unit
    ) {
        FirebaseUtils.getFileTypeCollection(remoteFile)
            .document(remoteFile.firestoreId)
            .set(remoteFile)
            .addOnCompleteListener {
                completeLambda.invoke(it.isSuccessful)
            }
    }

    fun renameRemoteFile(oldRemoteFile: RemoteFile, newDisplayName: String) {
        if (newDisplayName.isNotEmpty()) {
            val newRemoteFile = oldRemoteFile.copy(displayName = newDisplayName)

            FirebaseUtils.getFileTypeCollection(oldRemoteFile)
                .document(oldRemoteFile.firestoreId)
                .set(newRemoteFile)
                .addOnSuccessListener {
                    Timber.d("renameFile succeeded with newCustomFile as $newRemoteFile")
                }
                .addOnFailureListener {
                    Timber.e(it)
                }
        }
    }

    fun deleteCustomFromFireStore(remoteFile: RemoteFile) {
        FirebaseUtils.getFileTypeCollection(remoteFile)
            .document(remoteFile.firestoreId)
            .delete()
            .addOnSuccessListener {
                Timber.d("deleteCustomFile in FireStore succeeded with deletedCustomFile as $remoteFile")
            }
            .addOnFailureListener {
                Timber.e(it)
            }
    }

}