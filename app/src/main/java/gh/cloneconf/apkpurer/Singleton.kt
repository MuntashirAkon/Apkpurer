package gh.cloneconf.apkpurer

import android.content.Context
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object Singleton {

    const val USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36"

    lateinit var okhttp : OkHttpClient

    val gson by lazy { Gson() }

    fun init( c : Context){
        okhttp = OkHttpClient().newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .retryOnConnectionFailure(false)
            .connectTimeout(30, TimeUnit.SECONDS)
            .cache(Cache(c.filesDir, 3000))
            .build()
    }


}