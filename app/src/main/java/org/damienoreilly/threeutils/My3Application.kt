package org.damienoreilly.threeutils

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.damienoreilly.threeutils.di.appModule
import org.damienoreilly.threeutils.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class My3Application : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        startKoin {
            androidLogger()
            androidContext(this@My3Application)
            androidFileProperties()
            modules(appModule, networkModule)
        }

    }

}
