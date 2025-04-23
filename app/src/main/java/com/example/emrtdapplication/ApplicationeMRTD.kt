package com.example.emrtdapplication

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ApplicationeMRTD : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var nfcAdapter: NfcAdapter
    private var emrtd : EMRTD = EMRTD()


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    @Override
    override fun onTagDiscovered(tag: Tag?) {
        emrtd.discoveredTag(tag)
        Logger.log(ApplicationConstants.TAG, ApplicationConstants.ENABLE_LOGGING, "End of Tag discovered")
    }

    @Override
    override fun onResume() {
        super.onResume()
        if (!nfcAdapter.isEnabled) {
            Toast.makeText(this, "NFC has to be enabled", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
        }
        val options = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or
            NfcAdapter.FLAG_READER_NFC_B or
            NfcAdapter.FLAG_READER_NFC_F or
            NfcAdapter.FLAG_READER_NFC_V or
            NfcAdapter.FLAG_READER_NFC_BARCODE or
            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, options)
    }

    @Override
    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this)
    }
}