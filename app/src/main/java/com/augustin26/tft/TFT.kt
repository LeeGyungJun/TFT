package com.augustin26.tft

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import timber.log.Timber

class TFT : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree()) //팀버 로그
        Logger.addLogAdapter(AndroidLogAdapter()) //로거 로그
    }
}