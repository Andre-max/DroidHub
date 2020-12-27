package com.andre_max.droidhub.utils

import com.andre_max.droidhub.models.RemoteFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import timber.log.Timber

object FirebaseUtils {

    val firebaseStorage = FirebaseStorage.getInstance()
    val fireStore = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    fun getFileTypeCollection(remoteFile: RemoteFile): CollectionReference {
        return fireStore
            .collection("Root")
            .document(firebaseAuth.uid ?: "")
            .collection(remoteFile.getFileTypeFromFirebaseContentType())
    }
}