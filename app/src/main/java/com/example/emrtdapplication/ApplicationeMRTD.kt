package com.example.emrtdapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ApplicationeMRTD : AppCompatActivity() {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry_view)
        findViewById<Button>(R.id.exit).setOnClickListener{
            Logger.log(ApplicationConstants.TAG, ApplicationConstants.ENABLE_LOGGING, "Exit button clicked. Exiting...")
            this.finishAffinity()
        }
        findViewById<Button>(R.id.scan).setOnClickListener {
            Logger.log(ApplicationConstants.TAG, ApplicationConstants.ENABLE_LOGGING, "Clicked Scan button.")
            startActivity(Intent(this, ManualInput().javaClass))
        }
    }
}