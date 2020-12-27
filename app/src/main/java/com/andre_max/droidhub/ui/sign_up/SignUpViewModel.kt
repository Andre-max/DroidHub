package com.andre_max.droidhub.ui.sign_up

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andre_max.droidhub.R
import com.andre_max.droidhub.utils.FirebaseUtils
import com.andre_max.droidhub.utils.User
import timber.log.Timber

class SignUpViewModel : ViewModel() {

    val username = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val shouldNavigate = MutableLiveData<Int>()

    private var _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var _showPassword = MutableLiveData(false)
    val showPassword: LiveData<Boolean> = _showPassword

    fun verifyInput() {
        _isLoading.value = true

        val inputMap = mapOf(
            "Username" to username.value,
            "Email" to email.value,
            "Password" to password.value
        )
        for (entry in inputMap) {
            if (entry.value.isNullOrEmpty()) return
        }

        createAccount(username.value!!, email.value!!, password.value!!)
    }

    fun showOrHidePassword() {
        // Reverses current state of showPassword
        _showPassword.value = _showPassword.value != true
    }

    fun navigate(@IdRes navigationId: Int) {
        shouldNavigate.value = navigationId
    }

    private fun createAccount(username: String, email: String, password: String) {
        FirebaseUtils.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = User(username, it.user?.email ?: email, password, it?.user?.uid ?: "")
                FirebaseUtils.fireStore.collection("users").document(user.uid).set(user)
                    .addOnSuccessListener {
                        Timber.d("Success: User created as $user")
                        navigate(R.id.homeFragment)
                    }
            }
            .addOnFailureListener {
                Timber.e(it)
            }
            .addOnCompleteListener {
                _isLoading.value = false
            }
    }
}