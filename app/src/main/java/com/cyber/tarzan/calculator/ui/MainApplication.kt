package com.cyber.tarzan.calculator.ui

import android.app.Application
import android.util.Log
import com.cyber.tarzan.calculator.appOpenManger.AppOpenManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Log.d("testtag", "onCreate: created")
        AppOpenManager(this)
    }
}