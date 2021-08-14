package gh.cloneconf.apkpurer.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    val settings by lazy {
        (requireActivity() as MainActivity).settings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).apply {
            title = getString(R.string.settings)
            back(true)
            settings(false)
        }


        liteSwitch.isChecked = settings.liteMode

        liteSwitch.setOnCheckedChangeListener { compoundButton, b ->
            settings.liteMode = b
        }

    }
}