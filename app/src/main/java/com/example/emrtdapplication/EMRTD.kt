package com.example.emrtdapplication

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

class EMRTD : NfcAdapter.ReaderCallback, Activity() {
    private lateinit var nfcAdapter : NfcAdapter
    private var bac = BAC()
    private var ca = CardAccess()
    private var cs = CardSecurity()
    private var ai = AttributeInfo()
    private var dir = Directory()
    private var id_PACE_OID : ByteArray? = null
    private var pace = PACE()
    private var useCAN = false
    private var MRZ : String? = null
    private var efcom : EfCom = EfCom()

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emrtd_view)
        useCAN = intent.getBooleanExtra("UseCAN", useCAN)
        log("UseCAN is $useCAN")
        MRZ = intent.getStringExtra("MRZ")
        log("MRZ is $MRZ")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!nfcAdapter.isEnabled) {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.VISIBLE
        } else {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.GONE
        }
        findViewById<Button>(R.id.nfcCancel).setOnClickListener {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.GONE
        }
        findViewById<Button>(R.id.enableNFC).setOnClickListener {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
        }
    }

    @Override
    override fun onResume() {
        super.onResume()
        if (!nfcAdapter.isEnabled) {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.VISIBLE
        } else {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.GONE
        }
        val options = Bundle()
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

    @Override
    override fun onTagDiscovered(tag: Tag?) {
        if (APDUControl.init(tag) != APDUControlConstants.INIT_SUCCESS) {
            return
        }
        if (APDUControl.connectToNFC() != APDUControlConstants.CONNECT_SUCCESS) {
            return
        }
        readeMRTDParams()
        APDUControl.closeNFC()
        Logger.log(ApplicationConstants.TAG, ApplicationConstants.ENABLE_LOGGING, "End of Tag discovered")
    }

    private fun readeMRTDParams() {
        Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING, "Reading eMRTD Params...")
        /*val mrz = MRZ(TestValues.MRZ)
        mrz.extractMRZInformation()
        Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING, "Extracted MRZ: ${mrz.getMRZInfoString()}")
        MRZ = mrz.getMRZInfoString()*/
        //Logger.log(TAG, "Reading Parameters")
        //cs.read()
        //ai.read()
        //dir.read()
        //ca.read()
        //id_PACE_OID = ca.getPACEInfo().getPACEOID()
        selecteMRTDApplication()
        bac.init(MRZ)
        bac.bacProtocol()
        Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING, "Reading EFCOM...")
        efcom.read()
        Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING, "Read EFCOM")
        //pace.init(null, TestValues.CAN, id_PACE_OID)
        //pace.PACE_Protoccol()
    }

    private fun selecteMRTDApplication() : Int {
        val info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_DF, NfcP2Byte.SELECT_FILE, true, 7, ZERO_SHORT, byteArrayOf(0xA0.toByte(), 0, 0, 2, 0x47, 0x10, 1)))
        return if (info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK) {
            Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING,
                SELECT_APPLICATION_SUCCESS,
                "Selected LDS1. Contents are: ", info)
        } else {
            Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING,
                UNALBE_TO_SELECT_APPLICATION,
                "Could not select LDS1. Error Code: ", info)
        }

    }

    private fun log(msg : String) {
        Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING, msg)
    }
}