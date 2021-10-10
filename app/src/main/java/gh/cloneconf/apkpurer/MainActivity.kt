package gh.cloneconf.apkpurer

import android.content.ClipboardManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gh.cloneconf.apkpurer.api.SettingsApi
import gh.cloneconf.apkpurer.databinding.ActivityMainBinding
import gh.cloneconf.apkpurer.ui.CrashFragment
import gh.cloneconf.apkpurer.ui.SearchFragment

class MainActivity : AppCompatActivity() {

    val settings by lazy { SettingsApi(this) }

    lateinit var binds : ActivityMainBinding

    val clipboardManager by lazy { getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binds = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setSupportActionBar(toolbar)
        }

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            supportFragmentManager.fragments.forEach { _ ->
                supportFragmentManager.popBackStack()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fContainer, CrashFragment.newInstance(e.stackTraceToString()))
                .commit()
        }

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fContainer, SearchFragment())
                .commit()
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun back(b : Boolean){
        supportActionBar?.setDisplayHomeAsUpEnabled(b)
        supportActionBar?.setDisplayHomeAsUpEnabled(b)
    }

}