package gh.cloneconf.apkpurer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import gh.cloneconf.apkpurer.api.SettingsApi
import gh.cloneconf.apkpurer.ui.SearchFragment
import gh.cloneconf.apkpurer.ui.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val settings by lazy { SettingsApi(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        goTo(SearchFragment())
    }


    /**
     * Menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.settings -> {
                goTo(SettingsFragment())
                true
            }
            else -> false
        }
    }

    fun settings(b:Boolean) {
        try {
            toolbar.menu.findItem(R.id.settings).isVisible = b
        }catch (e:Exception){}
    }




    /**
     * Navigation system.
     */

    fun goTo(fragment : Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.last() is SearchFragment)
            finish()
        else
            supportFragmentManager.popBackStack()
    }

    fun back(b : Boolean){
        supportActionBar?.setDisplayHomeAsUpEnabled(b)
        supportActionBar?.setDisplayHomeAsUpEnabled(b)
    }
}