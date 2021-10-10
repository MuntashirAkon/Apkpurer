package gh.cloneconf.apkpurer

import android.app.Application
import gh.cloneconf.apkpurer.api.Apkpurer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Apkpurer.init(applicationContext)
    }
}