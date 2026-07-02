package com.example.emrtdapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.emrtdapplication.constants.ANDROID_LOG_INFO_TAG
import com.example.emrtdapplication.constants.MASTER_LIST_PAHT
import com.example.emrtdapplication.constants.MESSAGE_STRING
import com.example.emrtdapplication.utils.MasterList
import kotlin.concurrent.thread

/** Starting activity of the app. User decides which type of document the user wants to read.
 * Currently only supports eMRTD documents (e.g. passports)
 * */
class ApplicationEMRTD : AppCompatActivity() {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thread {
            if (!MasterList.isDecoded) {
                val directory = resources.assets.list(MASTER_LIST_PAHT)
                if (directory != null) {
                    val filename = directory[0]
                    val readFile = resources.assets.open("$MASTER_LIST_PAHT/$filename")
                    try {
                        MasterList.decodeMasterList(readFile.readBytes())
                    } catch (e: IllegalArgumentException) {
                        Log.i(ANDROID_LOG_INFO_TAG, MESSAGE_STRING + e.message)
                    }
                    readFile.close()
                }
            }
        }
        setContentView(R.layout.entry_view)
        findViewById<Button>(R.id.exit).setOnClickListener{
            this.finishAffinity()
        }
        findViewById<Button>(R.id.scan).setOnClickListener {
            startActivity(Intent(this, ManualInput().javaClass))
        }
    }
}