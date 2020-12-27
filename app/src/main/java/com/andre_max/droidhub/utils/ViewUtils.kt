package com.andre_max.droidhub.utils

import android.content.res.Resources
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.andre_max.droidhub.R
import com.google.android.material.snackbar.Snackbar

fun View.showLongSnackbar(message: String) =
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setBackgroundTint(resources.getResColor(R.color.default_purple))
        .setTextColor(resources.getResColor(android.R.color.white))
        .show()

fun Fragment.showLongSnackbar(message: String) =
    requireView().showLongSnackbar(message)

fun Resources.getResColor(@ColorRes colorRes: Int) =
    ResourcesCompat.getColor(this, colorRes, null)

@Suppress("UNCHECKED_CAST")
fun <F : Fragment> AppCompatActivity.getFragment(fragmentClass: Class<F>): F? {
    val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment

    navHostFragment.childFragmentManager.fragments.forEach {
        if (fragmentClass.isAssignableFrom(it.javaClass)) {
            return it as F
        }
    }

    return null
}