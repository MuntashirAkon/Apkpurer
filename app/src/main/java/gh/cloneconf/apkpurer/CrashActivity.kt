package gh.cloneconf.apkpurer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import gh.cloneconf.apkpurer.databinding.ActivityCrashBinding

class CrashActivity : Activity() {

    private lateinit var binds : ActivityCrashBinding
    private lateinit var msg : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        msg = intent.getStringExtra("msg")!!

        binds = ActivityCrashBinding.inflate(layoutInflater).apply {
            setContentView(root)

            msgTv.text = msg

            sendBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this

                intent.putExtra(Intent.EXTRA_EMAIL, "cloneconf@gmail.com")
                intent.putExtra(Intent.EXTRA_SUBJECT, "Apkpurer crash: ")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }else{
                    Toast.makeText(this@CrashActivity, "Error :/", Toast.LENGTH_SHORT).show()
                }
            }


            restartBtn.setOnClickListener {
                startActivity(Intent(this@CrashActivity, MainActivity::class.java))
                finish()
            }
        }



    }

}