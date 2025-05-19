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

/**
 * Constants for the EMRTD class
 */
const val EMRTD_TAG = "EMRTD"
const val EMRTD_ENABLE_LOGGING = true
/**
 * Main class for reading EMRTD. Selects the application to read (LDS1 EMRTD application, Travel Records application
 * Visa Records application and Additional Biometrics application) and reads available files.
 */
//TODO: Write functions to select LDS2 applications (Travel Records, Visa Records and Additional Biometrics)
//TODO: Make loading screen to indicate reading in progress or finished
//TODO: Make class for each application? Ask the user which application to read? Or read everything at once?
class EMRTD : NfcAdapter.ReaderCallback, Activity() {
    //TODO: Get rid of all warnings, implement PACE, Refactor code, Testing
    private lateinit var nfcAdapter : NfcAdapter
    private var bac = BAC()
    private var ca = CardAccess()
    private var cs = CardSecurity()
    private var ai = AttributeInfo()
    private var dir = Directory()
    private var idPaceOid : ByteArray? = null
    private var pace = PACE()
    private var useCAN = false
    private var mrz : String? = null
    private var efCOM : EfCom = EfCom()

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emrtd_view)
        useCAN = intent.getBooleanExtra("UseCAN", useCAN)
        log("UseCAN is $useCAN")
        mrz = intent.getStringExtra("MRZ")
        log("MRZ is $mrz")
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
        if (APDUControl.init(tag) != INIT_SUCCESS) {
            return
        }
        if (APDUControl.connectToNFC() != CONNECT_SUCCESS) {
            return
        }
        readeMRTDParams()
        APDUControl.closeNFC()
        log("End of Tag discovered")
    }

    /**
     * Reading EMRTD Parameters from all available files prior to application selection(EF.DIR, EF.ATR/INFO)
     * @return Success(0) if everything was read without error or Failure(-1) if something went wrong
     */
    //TODO: Return value to indicate overall success or failure
    //TODO: Refactor code
    private fun readeMRTDParams() {
        log("Reading EMRTD Params...")
        /*val mrz = MRZ(TestValues.MRZ)
        mrz.extractMRZInformation()
        Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING, "Extracted MRZ: ${mrz.getMRZInfoString()}")
        MRZ = mrz.getMRZInfoString()*/
        //Logger.log(TAG, "Reading Parameters")
        log("Reading Card Security...")
        cs.read()
        log("Reading AttrInfo...")
        ai.read()
        log("Reading Directory...")
        dir.read()
        log("Reading Card Access...")
        ca.read()
        idPaceOid = ca.getPACEInfo().getPACEOid()
        selectEMRTDApplication()
        //bac.init(mrz)
        //bac.bacProtocol()
        //log("Reading EF.COM...")
        //efCOM.read()
        pace.init(mrz, null, idPaceOid)
        pace.paceProtocol()
    }

    /**
     * Selects the LDS1 EMRTD Application which is mandatory for EMRTD
     * @return Success(0) or Failure(-1)
     */
    private fun selectEMRTDApplication() : Int {
        val info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_DF, NfcP2Byte.SELECT_FILE, true, 7, ZERO_SHORT, byteArrayOf(0xA0.toByte(), 0, 0, 2, 0x47, 0x10, 1)))
        return if (info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK) {
            log(SELECT_APPLICATION_SUCCESS, "Selected LDS1. Contents are: ", info)
        } else {
            log(UNABLE_TO_SELECT_APPLICATION, "Could not select LDS1. Error Code: ", info)
        }

    }

    /**
     * Logs the message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg : String) {
        Logger.log(EMRTD_TAG, EMRTD_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(EMRTD_TAG, EMRTD_ENABLE_LOGGING, error, msg, b)
    }
}