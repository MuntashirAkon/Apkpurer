package gh.cloneconf.apkpurer

import android.content.Context
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object Singleton {

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