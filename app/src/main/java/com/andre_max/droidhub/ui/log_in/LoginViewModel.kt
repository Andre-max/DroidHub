package com.andre_max.droidhub.ui.log_in

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andre_max.droidhub.R
import com.andre_max.droidhub.utils.FirebaseUtils
import timber.log.Timber

class LoginViewModel : ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val shouldNavigate = MutableLiveData<Int>()

    private var _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun verifyInput() {
        _isLoading.value = true

        val inputMap = mapOf("Email" to email.value, "Password" to password.value)
        for (entry in inputMap) {
            if (entry.value.isNullOrEmpty()) return
        }

        loginAccount(email.value!!, password.value!!)
    }

    fun navigate(@IdRes navigationId: Int) {
        shouldNavigate.value = navigationId
    }

    private fun loginAccount(email: String, password: String) {
        FirebaseUtils.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Timber.d("Success: Sign in was successful")
                navigate(R.id.homeFragment)
            }
            .addOnFailureListener {
                Timber.e(it)
            }
            .addOnCompleteListener {
                _isLoading.value = false
            }
    }
}