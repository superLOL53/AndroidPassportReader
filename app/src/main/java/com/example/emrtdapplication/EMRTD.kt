package com.example.emrtdapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.emrtdapplication.lds1.BAC
import com.example.emrtdapplication.lds1.DG1
import com.example.emrtdapplication.lds1.DG10
import com.example.emrtdapplication.lds1.DG11
import com.example.emrtdapplication.lds1.DG12
import com.example.emrtdapplication.lds1.DG13
import com.example.emrtdapplication.lds1.DG14
import com.example.emrtdapplication.lds1.DG15
import com.example.emrtdapplication.lds1.DG16
import com.example.emrtdapplication.lds1.DG2
import com.example.emrtdapplication.lds1.DG3
import com.example.emrtdapplication.lds1.DG4
import com.example.emrtdapplication.lds1.DG5
import com.example.emrtdapplication.lds1.DG6
import com.example.emrtdapplication.lds1.DG7
import com.example.emrtdapplication.lds1.DG8
import com.example.emrtdapplication.lds1.DG9
import com.example.emrtdapplication.lds1.EfCom
import com.example.emrtdapplication.lds1.EfSod
import com.example.emrtdapplication.common.AttributeInfo
import com.example.emrtdapplication.common.CardAccess
import com.example.emrtdapplication.common.CardSecurity
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.common.Directory
import com.example.emrtdapplication.common.PACE
import com.example.emrtdapplication.databinding.Lds1Binding
import com.example.emrtdapplication.display.DisplayLDS1
import com.example.emrtdapplication.lds1.ChipAuthentication
import com.example.emrtdapplication.utils.ADDITIONAL_ENCRYPTION_LENGTH
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
import com.google.android.material.navigation.NavigationView
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.SecureRandom
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * Constants for the EMRTD class
 */
const val EMRTD_TAG = "EMRTD"
const val EMRTD_ENABLE_LOGGING = true
/**
 * Main class for reading EMRTD. Selects the application to read (LDS1 EMRTD application, Travel Records application
 * Visa Records application and Additional Biometrics application) and reads available files.
 *
 */
//TODO: Write functions to select LDS2 applications (Travel Records, Visa Records and Additional Biometrics)
//TODO: Make loading screen to indicate reading in progress or finished
//TODO: Make class for each application? Ask the user which application to read? Or read everything at once?
class EMRTD : NfcAdapter.ReaderCallback, AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //TODO: Get rid of all warnings, implement PACE, Refactor code, Testing
    private lateinit var lds1ViewLayout : LinearLayout
    private lateinit var lds1Binding: Lds1Binding
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
    private var dg1 : DG1 = DG1(apduControl)
    private var dg2 : DG2 = DG2(apduControl)
    private var dg3 : DG3 = DG3(apduControl)
    private var dg4 : DG4 = DG4(apduControl)
    private var dg5 : DG5 = DG5(apduControl)
    private var dg6 : DG6 = DG6(apduControl)
    private var dg7 : DG7 = DG7(apduControl)
    private var dg8 : DG8 = DG8(apduControl)
    private var dg9 : DG9 = DG9(apduControl)
    private var dg10 : DG10 = DG10(apduControl)
    private var dg11 : DG11 = DG11(apduControl)
    private var dg12 : DG12 = DG12(apduControl)
    private var dg13 : DG13 = DG13(apduControl)
    private var dg14 : DG14 = DG14(apduControl)
    private var dg15 : DG15 = DG15(apduControl)
    private var dg16 : DG16 = DG16(apduControl)
    private var efMap = mapOf(
        dg1.shortEFIdentifier to dg1,
        dg2.shortEFIdentifier to dg2,
        dg3.shortEFIdentifier to dg3,
        dg4.shortEFIdentifier to dg4,
        dg5.shortEFIdentifier to dg5,
        dg6.shortEFIdentifier to dg6,
        dg7.shortEFIdentifier to dg7,
        dg8.shortEFIdentifier to dg8,
        dg9.shortEFIdentifier to dg9,
        dg10.shortEFIdentifier to dg10,
        dg11.shortEFIdentifier to dg11,
        dg12.shortEFIdentifier to dg12,
        dg13.shortEFIdentifier to dg13,
        dg14.shortEFIdentifier to dg14,
        dg15.shortEFIdentifier to dg15,
        dg16.shortEFIdentifier to dg16,
    )
    private var certs : Array<X509Certificate>? = null


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prov = BouncyCastleProvider()
        Security.removeProvider("BC")
        Security.addProvider(prov)
        setContentView(R.layout.emrtd_view)
        useCAN = intent.getBooleanExtra("UseCAN", useCAN)
        log("UseCAN is $useCAN")
        mrz = intent.getStringExtra("MRZ")
        log("MRZ is $mrz")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val b = Bundle()
        b.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 10000)
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, b)
        if (!nfcAdapter.isEnabled) {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.VISIBLE
        } else {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.GONE
        }
        val cancel = findViewById<Button>(R.id.nfcCancel)
        cancel.setOnClickListener {
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
        runOnUiThread {
            findViewById<LinearLayout>(R.id.beforeRead).visibility = View.GONE
            findViewById<LinearLayout>(R.id.Reading).visibility = View.VISIBLE
        }
        if (apduControl.init(tag) != INIT_SUCCESS) {
            return
        }
        if (apduControl.connectToNFC() != CONNECT_SUCCESS) {
            return
        }
        readeMRTDParams()
        apduControl.closeNFC()
        log("End of Tag discovered")
        runOnUiThread {
            //val toolbar = findViewById<Toolbar>(R.id.toolbar)
            //setSupportActionBar(toolbar)
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
            val navigationView = findViewById<NavigationView>(R.id.nvView)

            navigationView.setNavigationItemSelectedListener(this)

            val drawerToggle = ActionBarDrawerToggle(this,drawerLayout,null,
                0,0)

            drawerLayout.addDrawerListener(drawerToggle)
            drawerToggle.syncState()
            findViewById<LinearLayout>(R.id.Reading).visibility = View.GONE
            drawerLayout.visibility = View.VISIBLE
            lds1Binding = DataBindingUtil.setContentView(this, R.layout.lds1)
            lds1Binding.dg1 = dg1
            lds1Binding.dg2 = dg2
            lds1Binding.dg11 = dg11
            try {
                val dg1layout = findViewById<LinearLayout>(R.id.dg1layout)
                lds1ViewLayout = dg1layout.parent as LinearLayout
            } catch (_ :Exception) {

            }
            for (ef in efMap) {
                if (!ef.value.isPresent) {
                    try {
                        when (ef.key) {
                            0x01.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg1layout))
                            0x02.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg2layout))
                            0x03.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg3layout))
                            0x04.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg4layout))
                            0x05.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg5layout))
                            0x06.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg6layout))
                            0x07.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg7layout))
                            0x08.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg8layout))
                            0x09.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg9layout))
                            0x0A.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg10layout))
                            0x0B.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg11layout))
                            0x0C.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg12layout))
                            0x0D.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg13layout))
                            0x0E.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg14layout))
                            0x0F.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg15layout))
                            0x10.toByte() -> lds1ViewLayout.removeView(findViewById<LinearLayout>(R.id.dg16layout))
                        }
                    } catch (e: Exception) {
                        log(e.toString())
                    }
                } else if (!ef.value.isRead) {
                    val unableReadView = TextView(this)
                    unableReadView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                    unableReadView.text = "Unable to read file from passport"
                    when (ef.key) {
                        0x01.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x02.toByte() -> findViewById<LinearLayout>(R.id.dg2layout).addView(unableReadView)
                        0x03.toByte() -> findViewById<LinearLayout>(R.id.dg3layout).addView(unableReadView)
                        0x04.toByte() -> findViewById<LinearLayout>(R.id.dg4layout).addView(unableReadView)
                        0x05.toByte() -> findViewById<LinearLayout>(R.id.dg5layout).addView(unableReadView)
                        0x06.toByte() -> findViewById<LinearLayout>(R.id.dg6layout).addView(unableReadView)
                        0x07.toByte() -> findViewById<LinearLayout>(R.id.dg7layout).addView(unableReadView)
                        0x08.toByte() -> findViewById<LinearLayout>(R.id.dg8layout).addView(unableReadView)
                        0x09.toByte() -> findViewById<LinearLayout>(R.id.dg9layout).addView(unableReadView)
                        0x0A.toByte() -> findViewById<LinearLayout>(R.id.dg10layout).addView(unableReadView)
                        0x0B.toByte() -> findViewById<LinearLayout>(R.id.dg11layout).addView(unableReadView)
                        0x0C.toByte() -> findViewById<LinearLayout>(R.id.dg12layout).addView(unableReadView)
                        0x0D.toByte() -> findViewById<LinearLayout>(R.id.dg13layout).addView(unableReadView)
                        0x0E.toByte() -> findViewById<LinearLayout>(R.id.dg14layout).addView(unableReadView)
                        0x0F.toByte() -> findViewById<LinearLayout>(R.id.dg15layout).addView(unableReadView)
                        0x10.toByte() -> findViewById<LinearLayout>(R.id.dg16layout).addView(unableReadView)
                    }
                } else {
                    when (ef.key) {
                        0x01.toByte() -> dg1.createViews(this, findViewById<TableLayout>(R.id.dg1table))
                        0x02.toByte() -> dg2.createViews(this, findViewById<LinearLayout>(R.id.dg2layout))
                        0x03.toByte() -> dg3.createViews(this, findViewById<LinearLayout>(R.id.dg3layout))
                        0x04.toByte() -> dg4.createViews(this, findViewById<TableLayout>(R.id.dg4layout))
                        0x05.toByte() -> dg5.createViews(this, findViewById<TableLayout>(R.id.dg5layout))
                        0x06.toByte() -> dg6.createViews(this, findViewById<TableLayout>(R.id.dg6layout))
                        0x07.toByte() -> dg7.createViews(this, findViewById<TableLayout>(R.id.dg7layout))
                        0x08.toByte() -> dg8.createViews(this, findViewById<TableLayout>(R.id.dg8layout))
                        0x09.toByte() -> dg9.createViews(this, findViewById<TableLayout>(R.id.dg9layout))
                        0x0A.toByte() -> dg10.createViews(this, findViewById<TableLayout>(R.id.dg10layout))
                        0x0B.toByte() -> dg11.createViews(this, findViewById(R.id.dg11table))
                        0x0C.toByte() -> dg12.createViews(this, findViewById<TableLayout>(R.id.dg12layout))
                        0x0D.toByte() -> dg13.createViews(this, findViewById<TableLayout>(R.id.dg13layout))
                        0x0E.toByte() -> dg14.createViews(this, findViewById<TableLayout>(R.id.dg14layout))
                        0x0F.toByte() -> dg15.createViews(this, findViewById<TableLayout>(R.id.dg15layout))
                        0x10.toByte() -> dg16.createViews(this, findViewById<TableLayout>(R.id.dg16layout))
                    }
                }
            }
        }
    }

    /**
     * Reading EMRTD Parameters from all available files prior to application selection(EF.DIR, EF.ATR/INFO)
     */
    private fun readeMRTDParams() {
        val progressText = findViewById<TextView>(R.id.progressBarText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        runOnUiThread {
            progressBar.progress = progressBar.min
            progressText.text = "Reading common files..."
        }
        log("Reading EMRTD Params...")
        if (cs.read() != SUCCESS) {
            log("Unable to read Card Security")
        }
        if (ai.read() != SUCCESS) {
            log("Unable to read AttributeInfo")
            return
        } else {
            log("Successfully read AI")
        }
        if (ai.extendedLengthInfoInFile) {
            apduControl.maxResponseLength = ai.maxAPDUReceiveBytes - ADDITIONAL_ENCRYPTION_LENGTH
            apduControl.maxCommandLength = ai.maxAPDUTransferBytes - ADDITIONAL_ENCRYPTION_LENGTH
        } else {
            apduControl.maxResponseLength = UByte.MAX_VALUE.toInt() - ADDITIONAL_ENCRYPTION_LENGTH
            apduControl.maxCommandLength = UByte.MAX_VALUE.toInt() - ADDITIONAL_ENCRYPTION_LENGTH
        }
        if (dir.read() != SUCCESS) {
            log("Unable to read Directory")
        }
        if (ca.read() != SUCCESS) {
            log("Unable to read Card Access")
        }
        val list = ca.paceInfos
        for (info in list) {
            if (info.parameterId != null) {
                idPaceOid = info.protocol
                break
            }
        }
        runOnUiThread {
            progressBar.incrementProgressBy(10)
            progressText.text = "Initialize secure messaging..."
        }
        pace.init(mrz, useCAN, idPaceOid, ca.paceInfos[0].parameterId!!)
        pace.paceProtocol()
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
        verifyEMRTD()
        runOnUiThread {
            progressBar.incrementProgressBy(5)
            progressText.text = "Passport verified"
        }
        log("Finished Reading")
    }

    private fun verifyEMRTD() {
        val progressText = findViewById<TextView>(R.id.progressBarText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        runOnUiThread {
            progressBar.incrementProgressBy(5)
            progressText.text = "Reading CSCAs..."
        }
        readCSCAs()
        runOnUiThread {
            progressBar.incrementProgressBy(5)
            progressText.text = "Perform passive authentication..."
        }
        efSod.checkHashes(efMap)
        efSod.passiveAuthentication(certs)
        runOnUiThread {
            progressBar.incrementProgressBy(5)
            progressText.text = "Authenticate chip..."
        }
        dg15.activeAuthentication(SecureRandom())
        if (dg14.isRead && dg14.isPresent && dg14.securityInfos != null) {
            var chipPublicKey : ChipAuthenticationPublicKeyInfo? = null
            var chipInfo : ChipAuthenticationInfo? = null
            for (si in dg14.securityInfos!!) {
                if (si.type == CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE) {
                    chipPublicKey = si as ChipAuthenticationPublicKeyInfo
                } else if (si.type == CHIP_AUTHENTICATION_TYPE) {
                    chipInfo = si as ChipAuthenticationInfo
                }
            }
            if (chipPublicKey != null && chipInfo != null) {
                val auth = ChipAuthentication(apduControl, null, ByteArray(0),
                    chipPublicKey.publicKeyInfo, chipAuthenticationInfo = chipInfo)
                auth.authenticate()
            }
        }
    }

    /**
     * Read all files that can be present in LDS1 application
     */
    @SuppressLint("NewApi")
    private fun readLDS1Files() {
        val progressText = findViewById<TextView>(R.id.progressBarText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        runOnUiThread {
            progressBar.incrementProgressBy(3)
            progressText.text = "Reading EF.COM file..."
        }
        if (efCOM.read() != SUCCESS) {
            log("Unable to read EF COM")
        }
        for (ef in efMap) {
            runOnUiThread {
                progressBar.incrementProgressBy(4)
                progressText.text = "Reading DG${ef.key} file..."
            }
            ef.value.read()
            ef.value.parse()
        }
        runOnUiThread {
            progressBar.incrementProgressBy(3)
            progressText.text = "Reading EF.SOD file..."
        }
        if (efSod.read() != SUCCESS) {
            log("Unable to read EF SOD")
        }
        efSod.parse()
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

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val fm = supportFragmentManager
        val trans = fm.beginTransaction()
        var frag : Fragment? = null
        when (p0.itemId) {
            R.id.nav_lds1 -> frag = DisplayLDS1()
        }
        if (frag == null) {
            return false
        }
        trans.replace(R.id.flContent, frag)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        trans.commit()
        return true
    }

    private fun readCSCAs() {
        val directory = resources.assets.list("CSCA")
        val tmpCerts = ArrayList<X509Certificate>()
        if (directory != null && dg1.issuerCode != null) {
            var path : Array<String>? = null
            try {
                path = resources.assets.list("CSCA/${dg1.issuerCode}")
            } catch (_ : Exception) {

            }
            if (path != null) {
                val ce = CertificateFactory.getInstance("X509")
                for (cert in path) {
                    try {
                        val c = ce.generateCertificate(assets.open("CSCA/${dg1.issuerCode}/$cert")) as X509Certificate
                        tmpCerts.add(c)
                    } catch (e : Exception) {
                        println(e)
                    }
                }
            }
        }
        certs = tmpCerts.toTypedArray()
    }
}