package gh.cloneconf.apkpurer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.databinding.FragmentCrashBinding

class CrashFragment : Fragment() {

    companion object {
        fun newInstance(msg : String) =
            CrashFragment().apply {
                arguments = Bundle().apply {
                    putString("msg", msg)
                }
        }
    }


    private lateinit var binds : FragmentCrashBinding
    lateinit var msg : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msg = requireArguments().getString("msg")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binds = FragmentCrashBinding.inflate(layoutInflater)
        return binds.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binds.apply {

            msgTv.text = msg

            (requireActivity() as MainActivity).apply {
                setTitle(R.string.crash)

            }.back(false)

            msgTv.text = msg

            restartBtn.setOnClickListener {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }

            sendBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this

                intent.putExtra(Intent.EXTRA_EMAIL, "cloneconf@gmail.com")
                intent.putExtra(Intent.EXTRA_SUBJECT, "Apkpurer crash: ")
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }else{
                    Toast.makeText(requireContext(), "Error :/", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





}