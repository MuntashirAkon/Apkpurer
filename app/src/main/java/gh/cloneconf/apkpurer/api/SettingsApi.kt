package gh.cloneconf.apkpurer.api

import android.content.Context
import android.content.SharedPreferences

class SettingsApi(val context : Context) {

    private fun preference(): SharedPreferences {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }


    var liteMode
    get() = preference().getBoolean("lite", false)
    set(value) = preference().edit()
        .putBoolean("lite", value)
        .apply()

}