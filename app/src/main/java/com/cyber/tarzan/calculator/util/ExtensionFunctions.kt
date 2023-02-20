package com.cyber.tarzan.calculator.util

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

fun Fragment.getClassName(): String {
    return this.javaClass.simpleName
}

fun ViewModel.getClassName(): String {
    return this.javaClass.simpleName
}

fun Activity.getClassName(): String {
    return this.javaClass.simpleName
}

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}