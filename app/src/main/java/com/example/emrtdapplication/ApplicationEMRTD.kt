package com.example.emrtdapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/** Starting activity of the app. User decides which type of document the user wants to read.
 * Currently only supports eMRTD documents (e.g. passports)
 * */
class ApplicationEMRTD : AppCompatActivity() {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry_view)
        findViewById<Button>(R.id.exit).setOnClickListener{
            this.finishAffinity()
        }
        findViewById<Button>(R.id.scan).setOnClickListener {
            startActivity(Intent(this, ManualInput().javaClass))
        }
    }
}