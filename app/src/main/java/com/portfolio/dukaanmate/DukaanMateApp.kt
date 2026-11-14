package com.portfolio.dukaanmate

import android.app.Application
import com.portfolio.dukaanmate.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DukaanMateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DukaanMateApp)
            modules(appModule)
        }
    }
}
