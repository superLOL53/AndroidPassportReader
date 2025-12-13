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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.example.emrtdapplication.common.Directory
import com.example.emrtdapplication.common.PACE
import com.example.emrtdapplication.databinding.Lds1Binding
import com.example.emrtdapplication.display.displayLDS1
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
import org.spongycastle.asn1.x509.Certificate
import java.io.BufferedInputStream
import java.security.SecureRandom
import java.security.Security

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
    private var efMap = mapOf<Byte, ElementaryFileTemplate>(
        DG1.shortEFIdentifier to DG1,
        DG2.shortEFIdentifier to DG2,
        DG3.shortEFIdentifier to DG3,
        DG4.shortEFIdentifier to DG4,
        DG5.shortEFIdentifier to DG5,
        DG6.shortEFIdentifier to DG6,
        DG7.shortEFIdentifier to DG7,
        DG8.shortEFIdentifier to DG8,
        DG9.shortEFIdentifier to DG9,
        DG10.shortEFIdentifier to DG10,
        DG11.shortEFIdentifier to DG11,
        DG12.shortEFIdentifier to DG12,
        DG13.shortEFIdentifier to DG13,
        DG14.shortEFIdentifier to DG14,
        DG15.shortEFIdentifier to DG15,
        DG16.shortEFIdentifier to DG16,
    )
    private var certs : Array<Certificate>? = null


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
        b.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000000)
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, Bundle())
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
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionBar(toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout);
        val navigationView = findViewById<NavigationView>(R.id.nvView);

        navigationView.setNavigationItemSelectedListener(this);

        val drawerToggle = ActionBarDrawerToggle(this,drawerLayout,null,
            0,0);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState()

        val directory = resources.assets.list("certificates")
        val tmpCerts = ArrayList<Certificate>()
        if (directory != null) {
            for (file in directory) {
                val path  = assets.open("certificates/$file")
                val buffer = ByteArray(path.available())
                path.read(buffer)
                path.close()
                tmpCerts.add(Certificate.getInstance(buffer))
            }
        }
        certs = tmpCerts.toTypedArray()
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
            findViewById<LinearLayout>(R.id.Reading).visibility = View.GONE
            findViewById<DrawerLayout>(R.id.drawerLayout).visibility = View.VISIBLE
            lds1Binding = DataBindingUtil.setContentView(this, R.layout.lds1)
            lds1Binding.dg1 = DG1
            lds1Binding.dg2 = DG2
            lds1Binding.dg11 = DG11
            try {
                val dg1layout = findViewById<LinearLayout>(R.id.dg1layout)
                lds1ViewLayout = dg1layout.parent as LinearLayout
            } catch (e:Exception) {

            }
            if (DG2.biometricInformation != null && DG2.biometricInformation!!.biometricInformations != null) {
                for (bios in DG2.biometricInformation!!.biometricInformations) {
                    if (bios == null) continue
                    val image = bios.biometricDataBlock.facialRecordData.image
                    val view = ImageView(this)
                    view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                    view.setImageBitmap(image)
                    findViewById<LinearLayout>(R.id.dg2layout).addView(view)
                }
            }
            if (DG3.biometricInformation != null && DG3.biometricInformation!!.biometricInformations != null) {
                for (bios in DG3.biometricInformation!!.biometricInformations) {
                    if (bios == null) continue
                    val image = bios.biometricDataBlock.facialRecordData.image
                    val view = ImageView(this)
                    view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                    view.setImageBitmap(image)
                    findViewById<LinearLayout>(R.id.dg3layout).addView(view)

                }
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
                        0x02.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x03.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x04.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x05.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x06.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x07.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x08.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x09.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x0A.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x0B.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x0C.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x0D.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x0E.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x0F.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                        0x10.toByte() -> findViewById<LinearLayout>(R.id.dg1layout).addView(unableReadView)
                    }
                } else {
                    if (ef.key == 0xB.toByte()) {
                        DG11.createViews(findViewById<TableLayout>(R.id.dg11table), this)
                    }
                }
            }
            //Removing rows if values are null:
            //val table = findViewById<TableLayout>(R.id.lds1table)
            //table.removeView(findViewById<TableRow>(R.id.name))
        }
    }

    /**
     * Reading EMRTD Parameters from all available files prior to application selection(EF.DIR, EF.ATR/INFO)
     * @return Success(0) if everything was read without error or Failure(-1) if something went wrong
     */
    private fun readeMRTDParams() {
        log("Reading EMRTD Params...")
        /*val mrz = MRZ(TestValues.MRZ)
        mrz.extractMRZInformation()
        Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING, "Extracted MRZ: ${mrz.getMRZInfoString()}")
        MRZ = mrz.getMRZInfoString()*/
        //Logger.log(TAG, "Reading Parameters")
        if (cs.read() != SUCCESS) {
            log("Unable to read Card Security")
        }
        if (ai.read() != SUCCESS) {
            log("Unable to read AttributeInfo")
            return
        } else {
            log("Successfully read AI")
        }
        if (ai.getExtendedLengthInfoInFile()) {
            apduControl.maxResponseLength = ai.getMaxReceiveLength() - ADDITIONAL_ENCRYPTION_LENGTH
            apduControl.maxCommandLength = ai.getMaxTransferLength() - ADDITIONAL_ENCRYPTION_LENGTH
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
        val list = ca.getPACEInfo()
        for (info in list) {
            if (info.parameterId != null) {
                idPaceOid = info.protocol
                break
            }
        }
        /*pace.init(mrz, useCAN, idPaceOid, ca.getPACEInfo()[0].parameterId!!)
        pace.paceProtocol()*/
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
        log("Finished Reading")
    }

    @SuppressLint("NewApi")
    private fun readLDS1Files() {
        /*if (efCOM.read() != SUCCESS) {
            log("Unable to read EF COM")
        }
        for (ef in efMap) {
            ef.value.read()
            ef.value.parse()
        }*/
        if (efSod.read() != SUCCESS) {
            log("Unable to read EF SOD")
        }
        val stream = assets.open("certificates/CSCAAUSTRIAcacert005.crt")
        //val path = FileInputStream("/home/oliver/StudioProjects/AndroidPassportReader/app/src/main/assets/certificates/CSCAAUSTRIAcacert005.crt")
        val buffer = BufferedInputStream(stream)
        val arr = buffer.readAllBytes()
        val cert = org.spongycastle.asn1.x509.Certificate.getInstance(arr)
        efSod.parse()
        efSod.checkHashes(efMap)
        efSod.passiveAuthentication(org.spongycastle.asn1.x509.Certificate.getInstance(arr))
        //val sig = Signature.getInstance(certs?.get(0)?.signatureAlgorithm?.oid.toString())
        DG15.activeAuthentication(SecureRandom())
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
            R.id.nav_lds1 -> frag = displayLDS1()
        }
        if (frag == null) {
            return false
        }
        trans.replace(R.id.flContent, frag)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        trans.commit()
        return true
    }
}