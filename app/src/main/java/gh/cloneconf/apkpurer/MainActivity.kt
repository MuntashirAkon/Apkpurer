package gh.cloneconf.apkpurer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import gh.cloneconf.apkpurer.ui.AppFragment
import gh.cloneconf.apkpurer.ui.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        goTo(SearchFragment())

    }


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