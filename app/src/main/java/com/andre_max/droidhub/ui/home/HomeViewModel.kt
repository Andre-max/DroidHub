package com.andre_max.droidhub.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andre_max.droidhub.models.RemoteFile
import com.andre_max.droidhub.utils.FileTypes
import com.andre_max.droidhub.utils.FireStoreUtils
import com.andre_max.droidhub.utils.FirebaseStorageUtils
import com.andre_max.droidhub.utils.FirebaseUtils
import timber.log.Timber

class HomeViewModel : ViewModel() {

    val shouldNavigate = MutableLiveData(false)

    private val _listOfRemoteFiles = MutableLiveData<MutableList<RemoteFile>>(null)
    val listOfRemoteFiles: LiveData<MutableList<RemoteFile>> = _listOfRemoteFiles

    fun shouldNavigate() {
        shouldNavigate.value = true
    }

    fun resetListOfRemoteFiles() {
        _listOfRemoteFiles.value = null
    }

    fun getRemoteFilesFromFileType(fileType: FileTypes) {
        FirebaseUtils.fireStore
            .collection("Root")
            .document(FirebaseUtils.firebaseAuth.uid ?: "")
            .collection(fileType.toString())
            .get()
            .addOnSuccessListener {
                _listOfRemoteFiles.value = it.toObjects(RemoteFile::class.java)
                Timber.d("_listOfRemoteFiles.value is ${_listOfRemoteFiles.value} in addOnSuccessListener")
            }
            .addOnFailureListener {
                Timber.e(it)
            }
    }

    fun deleteRemoteFile(remoteFile: RemoteFile) {
        FirebaseStorageUtils.deleteRemoteFileFromFirebaseStorage(remoteFile) {
            FireStoreUtils.deleteCustomFromFireStore(remoteFile)
        }
    }


}
