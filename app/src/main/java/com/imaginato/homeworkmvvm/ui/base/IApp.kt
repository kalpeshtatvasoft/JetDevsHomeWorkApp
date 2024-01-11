package com.imaginato.homeworkmvvm.ui.base

import android.app.Application
import com.imaginato.homeworkmvvm.di.apiModules
import com.imaginato.homeworkmvvm.di.commonModule
import com.imaginato.homeworkmvvm.di.databaseModule
import com.imaginato.homeworkmvvm.di.netModules
import com.imaginato.homeworkmvvm.di.repositoryModules
import com.imaginato.homeworkmvvm.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class IApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@IApp)
            modules(
                commonModule, databaseModule, netModules, apiModules, repositoryModules, viewModelModules
            )
        }
    }
}