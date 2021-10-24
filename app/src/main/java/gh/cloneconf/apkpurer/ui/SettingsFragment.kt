package gh.cloneconf.apkpurer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    val settings by lazy {
        (requireActivity() as MainActivity).settings
    }

    private lateinit var binds : FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binds = FragmentSettingsBinding.inflate(inflater)
        return binds.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).apply {
            title = getString(R.string.settings)
            back(true)
        }


        binds.liteSwitch.isChecked = settings.liteMode

        binds.liteSwitch.setOnCheckedChangeListener { compoundButton, b ->
            settings.liteMode = b
        }

    }
}