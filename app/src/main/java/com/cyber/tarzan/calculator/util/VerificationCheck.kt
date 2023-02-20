package com.cyber.tarzan.calculator.util

import android.content.Context
import com.cyber.tarzan.calculator.BuildConfig

object VerificationCheck {
    fun playStoreAppVerification(context: Context): Boolean {
        // A list with valid installers package name
        val validInstallers: List<String> =
            ArrayList(listOf("com.android.vending", "com.google.android.feedback"))

        // The package name of the app that has installed your app
        val installChecker = context.packageManager.getInstallerPackageName(context.packageName)

        // true if your app has been downloaded from Play Store
        return BuildConfig.DEBUG || installChecker != null && validInstallers.contains(installChecker)
    }

}