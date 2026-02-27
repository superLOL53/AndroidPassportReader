package com.example.emrtdapplication

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.MasterList
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
/*
import android.nfc.tech.IsoDep
import net.sf.scuba.smartcards.APDUEvent
import net.sf.scuba.smartcards.CardService
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.math.ec.ECCurve
import org.jmrtd.BACKey
import org.jmrtd.PassportService
import org.spongycastle.crypto.params.ECDomainParameters
import java.math.BigInteger
import java.security.spec.ECFieldFp
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve*/

/**
 * Activity for reading from the eMRTD
 *
 * @property nfcAdapter Default local NFC adapter from the android phone
 * @property mrz Encoded MRZ string from [ManualInput]
 *
 */
class ReadPassport : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private lateinit var nfcAdapter : NfcAdapter
    private var mrz : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_passport_view)
        if (savedInstanceState != null) {
            mrz = savedInstanceState.getString("MRZ")
            if (mrz != null) {
                EMRTD.mrz = mrz
            }
        } else {
            EMRTD.mrz = intent.getStringExtra("MRZ")
        }
        val prov = BouncyCastleProvider()
        Security.removeProvider("BC")
        Security.addProvider(prov)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val b = Bundle()
        b.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 60000)
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE or
                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, b)
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

    override fun onResume() {
        super.onResume()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!nfcAdapter.isEnabled) {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.VISIBLE
        } else {
            findViewById<LinearLayout>(R.id.overlayNFCEnable).visibility = View.GONE
        }
        val options = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 60000)
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE or
                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, options)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("MRZ", mrz)
        super.onSaveInstanceState(outState)
    }

    override fun onTagDiscovered(tag: Tag?) {
        if (tag == null) {
            return
        }
        runOnUiThread {
            findViewById<LinearLayout>(R.id.beforeRead).visibility = View.GONE
            findViewById<LinearLayout>(R.id.Reading).visibility = View.VISIBLE
        }
        readeMRTD(tag)
        runOnUiThread {
            val intent = Intent(this, EMRTDView()::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Reading the whole eMRTD
     *
     * @param tag The discovered tag from the [nfcAdapter]
     *
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun readeMRTD(tag: Tag) {
        /*val isodep = IsoDep.get(tag)
        isodep.timeout = 50000
        val cs = CardService.getInstance(isodep)
        val apdus = ArrayList<APDUEvent>()
        val p = PassportService(cs,256,256,false,true)
        p.addAPDUListener { e -> apdus.add(e)  }
        p.open()
        val k = BACKey("U1194584", "000707", "260801")
        val params = ECNamedCurveTable.getParameterSpec("brainpoolp256r1")
        val c = EllipticCurve(ECFieldFp(params.curve.field.characteristic), params.curve.a.toBigInteger(), params.curve.b.toBigInteger())
        val point = ECPoint(params.g.xCoord.toBigInteger(), params.g.yCoord.toBigInteger())
        val res = p.doPACE(k, "0.4.0.127.0.7.2.2.4.2.2",
            ECParameterSpec(c, point, params.n, params.h.toInt()),
            BigInteger("D", 16))*/


        EMRTD.connectToNFCTag(tag)
        changeProgressBar(getString(R.string.reading_common_files), 0)
        EMRTD.readCommonFiles()
        changeProgressBar(getString(R.string.initialize_secure_messaging), 10)
        EMRTD.pace.init(EMRTD.mrz, false, EMRTD.idPaceOid, EMRTD.ca.paceInfos[0].parameterId!!)
        val isPACESuccess = EMRTD.pace.paceProtocol() == SUCCESS
        Log.d("PACESuccess", "PACE status: $isPACESuccess")
        if (isPACESuccess) {
            EMRTD.cs.read()
        }
        if (EMRTD.ldS1Application.selectApplication() != SUCCESS) {
            return
        }

        if (!isPACESuccess && EMRTD.ldS1Application.performBACProtocol() != SUCCESS) {
            return
        }
        EMRTD.ldS1Application.readFiles(this)
        if (EMRTD.ldS1Application.efSod.documentSignerCertificate != null) {
            changeProgressBar(getString(R.string.reading_cscas), 5)
            readCSCAs()
            EMRTD.ldS1Application.verify(this)
            changeProgressBar(getString(R.string.passport_verified), 5)
        }
        if (isPACESuccess) {
            if (EMRTD.dir.hasVisaRecordsApplication) {
                if (EMRTD.visaRecords.selectApplication() == SUCCESS) {
                    EMRTD.visaRecords.readFiles(this)
                }
            }
            if (EMRTD.dir.hasTravelRecordsApplication) {
                if (EMRTD.travelRecords.selectApplication() == SUCCESS) {
                    EMRTD.travelRecords.readFiles(this)
                }
            }
            if (EMRTD.dir.hasAdditionalBiometricsApplication) {
                if (EMRTD.additionalBiometrics.selectApplication() == SUCCESS) {
                    EMRTD.additionalBiometrics.readFiles(this)
                }
            }
        }
        EMRTD.closeNFC()
    }

    /**
     * Read the CSCAs from the issuing country/organization of the eMRTD
     */
    private fun readCSCAs() {
        val directory = resources.assets.list("MasterList")
        if (directory != null) {
            val filename = directory[0]
            val readFile = resources.assets.open("MasterList/$filename")
            if (EMRTD.ldS1Application.efSod.documentSignerCertificate == null) return
            val masterList = MasterList(readFile.readAllBytes(), EMRTD.ldS1Application.efSod.documentSignerCertificate!!.issuer)
            if (EMRTD.ldS1Application.efSod.documentSignerCertificate != null) {
                EMRTD.ldS1Application.certs = masterList.certificateMap
            }
        }
    }

    /**
     * Changes the title and progress of the progress bar while reading from the eMRTD
     * @param text The text to be displayed while reading
     * @param increment Progress amount of the current reading operation
     */
    fun changeProgressBar(text : String, increment : Int) {
        runOnUiThread {
            findViewById<TextView>(R.id.progressBarText).text = text
            findViewById<ProgressBar>(R.id.progressBar).incrementProgressBy(increment)
        }
    }
}