package com.andre_max.droidhub.ui.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andre_max.droidhub.utils.CustomResult
import com.andre_max.droidhub.utils.FirebaseStorageUtils
import com.andre_max.droidhub.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class UploadViewModel : ViewModel() {
    var uploadFileUri = MutableLiveData<Uri>(null)
    private var firestoreId: UUID = UUID.randomUUID()

    val fileNameInput = MutableLiveData("")
    val snackbarMessage = MutableLiveData<String>(null)

    private var _status = MutableLiveData(Status.Inactive)
    val status: LiveData<Status> = _status

    private var _startFilePickerIntent = MutableLiveData(false)
    val startFilePickerIntent: LiveData<Boolean> = _startFilePickerIntent

    fun startFilePickerIntent() {
        _startFilePickerIntent.value = true
    }

    fun resetFilePickerIntentToDefault() {
        _startFilePickerIntent.value = false
    }

    @ExperimentalCoroutinesApi
    fun uploadFileToFirebaseStorage(context: Context) {
        _status.value = Status.Loading

        viewModelScope.launch {
            FirebaseStorageUtils.uploadFileToFirebaseStorage(
                context, firestoreId, uploadFileUri.value, fileNameInput.value
            ).collect {
                when (it) {
                    is CustomResult.Error -> {
                        Timber.e(it.throwable)
                        _status.value = Status.Inactive
                        snackbarMessage.value =
                            "Error occurred during upload session. Try again later"
                    }
                    is CustomResult.Success -> {
                        snackbarMessage.value = it.data
                        _status.value = Status.Completed
                    }
                    is CustomResult.Failure -> {
                        snackbarMessage.value = it.data
                        _status.value = Status.Inactive
                    }
                }
            }
        }
    }


}