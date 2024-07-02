package com.yonder.weightly.utils.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import timber.log.Timber

fun NavController.safeNavigate(id: Int) {
    try {
        navigate(id)
    } catch (e: IllegalArgumentException) {
        Timber.e(e)
    }
}

fun Fragment.findNavControllerSafely(): NavController? {
    return if (isAdded) {
        findNavController()
    } else {
        null
    }
}

fun NavController.safeNavigate(direction: NavDirections) {
    try {
        navigate(direction)
    } catch (e: IllegalArgumentException) {
        Timber.e(e)
    }
}
fun NavController.safeNavigate(id: Int, bundle: Bundle) {
    try {
        navigate(id, bundle)
    } catch (e: IllegalArgumentException) {
        Timber.e(e)
    }
}

fun Fragment.safeNavigate(direction: NavDirections) {
    findNavControllerSafely()?.safeNavigate(direction)
}

fun Fragment.safeNavigate(id: Int) {
    findNavControllerSafely()?.safeNavigate(id)
}
