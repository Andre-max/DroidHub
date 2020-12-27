package com.andre_max.droidhub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.andre_max.droidhub.ui.home.HomeFragment
import com.andre_max.droidhub.utils.FirebaseUtils
import com.andre_max.droidhub.utils.getFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    var intentUri: Uri? = null
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        navController = findNavController(R.id.nav_host_fragment)
        checkIntent()

        if (FirebaseUtils.firebaseAuth.currentUser == null)
            navController.navigate(R.id.signUpFragment)
    }

    private fun checkIntent() {
        if (FirebaseUtils.firebaseAuth.currentUser == null) return
        intentUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)?.also {
            Timber.d("Uri in checkIntent() is $intentUri")
            navController.navigate(R.id.uploadFragment)
        }
    }

    @ExperimentalCoroutinesApi
    override fun onBackPressed() {
        when (navController.currentDestination?.id) {

            // Get the home fragment and check if the subFolders have been opened and close if needed
            R.id.homeFragment -> {
                val homeFragment = getFragment(HomeFragment::class.java)
                val homeViewModel = homeFragment?.viewModel

                if (homeViewModel?.listOfRemoteFiles?.value != null) {
                    homeFragment.binding.storagePath.text = "Root"
                    homeViewModel.resetListOfRemoteFiles()
                } else {
                    finish()
                }
            }

            // Means the user is not signed up or in; so get them outta here
            R.id.signUpFragment -> finish()

            // In other fragments, allow super.onBackPressed() in order to trigger navController.popBackStack()
            else -> super.onBackPressed()
        }
    }

}