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
import com.example.emrtdapplication.LDS1.BAC
import com.example.emrtdapplication.LDS1.DG1
import com.example.emrtdapplication.LDS1.DG10
import com.example.emrtdapplication.LDS1.DG11
import com.example.emrtdapplication.LDS1.DG12
import com.example.emrtdapplication.LDS1.DG13
import com.example.emrtdapplication.LDS1.DG14
import com.example.emrtdapplication.LDS1.DG15
import com.example.emrtdapplication.LDS1.DG16
import com.example.emrtdapplication.LDS1.DG2
import com.example.emrtdapplication.LDS1.DG3
import com.example.emrtdapplication.LDS1.DG4
import com.example.emrtdapplication.LDS1.DG5
import com.example.emrtdapplication.LDS1.DG6
import com.example.emrtdapplication.LDS1.DG7
import com.example.emrtdapplication.LDS1.DG8
import com.example.emrtdapplication.LDS1.DG9
import com.example.emrtdapplication.LDS1.EfCom
import com.example.emrtdapplication.LDS1.EfSod
import com.example.emrtdapplication.common.AttributeInfo
import com.example.emrtdapplication.common.CardAccess
import com.example.emrtdapplication.common.CardSecurity
import com.example.emrtdapplication.common.Directory
import com.example.emrtdapplication.common.PACE
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.CONNECT_SUCCESS
import com.example.emrtdapplication.utils.INIT_SUCCESS
import com.example.emrtdapplication.utils.Logger
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.NfcRespondCodeSW1
import com.example.emrtdapplication.utils.NfcRespondCodeSW2
import com.example.emrtdapplication.utils.SELECT_APPLICATION_SUCCESS
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.UNABLE_TO_SELECT_APPLICATION

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
    private var apduControl = APDUControl()
    private var bac = BAC(apduControl)
    private var ca = CardAccess(apduControl)
    private var cs = CardSecurity(apduControl)
    private var ai = AttributeInfo(apduControl)
    private var dir = Directory(apduControl)
    private var idPaceOid : ByteArray? = null
    private var pace = PACE(apduControl)
    private var useCAN = false
    private var mrz : String? = null
    private var efCOM : EfCom = EfCom(apduControl)
    private var efSod: EfSod = EfSod(apduControl)
    private var DG1 : DG1 = DG1(apduControl)
    private var DG2 : DG2 = DG2(apduControl)
    private var DG3 : DG3 = DG3(apduControl)
    private var DG4 : DG4 = DG4(apduControl)
    private var DG5 : DG5 = DG5(apduControl)
    private var DG6 : DG6 = DG6(apduControl)
    private var DG7 : DG7 = DG7(apduControl)
    private var DG8 : DG8 = DG8(apduControl)
    private var DG9 : DG9 = DG9(apduControl)
    private var DG10 : DG10 = DG10(apduControl)
    private var DG11 : DG11 = DG11(apduControl)
    private var DG12 : DG12 = DG12(apduControl)
    private var DG13 : DG13 = DG13(apduControl)
    private var DG14 : DG14 = DG14(apduControl)
    private var DG15 : DG15 = DG15(apduControl)
    private var DG16 : DG16 = DG16(apduControl)

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
        if (apduControl.init(tag) != INIT_SUCCESS) {
            return
        }
        if (apduControl.connectToNFC() != CONNECT_SUCCESS) {
            return
        }
        readeMRTDParams()
        apduControl.closeNFC()
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
        /*if (cs.read() != SUCCESS) {
            log("Unable to read Card Security")
        }*/
        /*if (ai.read() != SUCCESS) {
            log("Unable to read AttributeInfo")
            return
        } else {
            log("Successfully read AI")
        }*/
        /*if (dir.read() != SUCCESS) {
            log("Unable to read Directory")
        }*/
        /*if (ca.read() != SUCCESS) {
            log("Unable to read Card Access")
        }
        idPaceOid = ca.getPACEInfo().getPaceOid()*/
        if (selectEMRTDApplication() != SUCCESS) {
            log("Unable to select LDS1")
            return
        }
        if (bac.init(mrz) != SUCCESS) {
            log("Unable to initialize BAC")
            return
        }
        if (bac.bacProtocol() != SUCCESS) {
            log("Unable to do BAC protocol successfully")
            return
        }
        readLDS1Files()
        //pace.init(mrz, useCAN, idPaceOid, ca.getPACEInfo().getParameterID())
        //pace.paceProtocol()
        log("Finished Reading")
    }

    private fun readLDS1Files() {
        if (efCOM.read() != SUCCESS) {
            log("Unable to read EF COM")
        }
        DG1.read()
        DG2.read()
        DG3.read()
        DG4.read()
        DG5.read()
        DG6.read()
        DG7.read()
        DG8.read()
        DG9.read()
        DG10.read()
        DG11.read()
        DG12.read()
        DG13.read()
        DG14.read()
        DG15.read()
        DG16.read()
        if (efSod.read() != SUCCESS) {
            log("Unable to read EF SOD")
        }
    }

    /**
     * Selects the LDS1 EMRTD Application which is mandatory for EMRTD
     * @return Success(0) or Failure(-1)
     */
    private fun selectEMRTDApplication() : Int {
        val info = apduControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_DF, NfcP2Byte.SELECT_FILE, byteArrayOf(0xA0.toByte(), 0, 0, 2, 0x47, 0x10, 1)))
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