package com.example.emrtdapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Constants for class ApplicationEMRTD
 */
const val A_TAG = "Application"
const val A_ENABLE_LOGGING = true

/** Starting activity of the app. User decides which type of document the user wants to read.
 * Currently only supports EMRTD documents (e.g. passports)
 * */
class ApplicationEMRTD : AppCompatActivity() {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry_view)
        findViewById<Button>(R.id.exit).setOnClickListener{
            log("Exit button clicked. Exiting...")
            this.finishAffinity()
        }
        findViewById<Button>(R.id.scan).setOnClickListener {
            log("Clicked Scan button.")
            startActivity(Intent(this, ManualInput().javaClass))
        }
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(A_TAG, A_ENABLE_LOGGING, msg)
    }
}