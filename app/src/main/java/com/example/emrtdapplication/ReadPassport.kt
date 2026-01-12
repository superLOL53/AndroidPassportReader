package com.example.emrtdapplication

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emrtdapplication.constants.ChipAuthenticationConstants.ID_CA_DH
import com.example.emrtdapplication.constants.ChipAuthenticationConstants.ID_CA_ECDH
import com.example.emrtdapplication.constants.SUCCESS
import net.sf.scuba.smartcards.APDUEvent
import net.sf.scuba.smartcards.APDUListener
import net.sf.scuba.smartcards.CardService
import net.sf.scuba.smartcards.WrappingCardService
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.jmrtd.BACKey
import org.jmrtd.PassportService
import org.jmrtd.lds.ChipAuthenticationInfo
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo
import org.jmrtd.lds.LDSFileUtil
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.protocol.EACCAProtocol
import java.math.BigInteger
import java.security.PublicKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class ReadPassport : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private lateinit var nfcAdapter : NfcAdapter
    private var mrz : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_passport_view)
        if (savedInstanceState != null) {
            val savedMRZ = savedInstanceState.getString("MRZ")
            if (savedMRZ != null) {
                EMRTD.mrz = savedMRZ
            }
        } else {
            EMRTD.mrz = intent.getStringExtra("MRZ")
        }
        val prov = BouncyCastleProvider()
        Security.removeProvider("BC")
        Security.addProvider(prov)
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

    override fun onResume() {
        super.onResume()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
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
     * Reading eMRTD Parameters from all available files prior to application selection(EF.DIR, EF.ATR/INFO)
     */
    fun readeMRTD(tag: Tag) {

        /*val nfc = IsoDep.get(tag)
        val service = PassportService(CardService.getInstance(nfc), 1000, 1000, false, true)


        service.open()

        service.sendSelectApplet(false)
        val bacKey = BACKey("U1194584", "000707", "260801")

        service.doBAC(bacKey)
        val is14 = service.getInputStream(PassportService.EF_DG14)
        val dg14 = LDSFileUtil.getLDSFile(PassportService.EF_DG14, is14) as DG14File
        val keyInfo = dg14.securityInfos
        var chipInfo : ChipAuthenticationInfo? = null
        var publicKey : ChipAuthenticationPublicKeyInfo? = null
        for (info in keyInfo) {
            if (info.objectIdentifier.startsWith(ID_CA_DH) || info.objectIdentifier.startsWith(ID_CA_ECDH)) {
                chipInfo = info as ChipAuthenticationInfo
            } else if (info.objectIdentifier.startsWith("0.4.0.127.0.7.2.2.1")) {
                publicKey = info as ChipAuthenticationPublicKeyInfo
            }
        }
        if (chipInfo == null || publicKey == null) return
        val apdus = NewAPDUListener()
        service.addAPDUListener(apdus)
        val res = service.doEACCA(chipInfo.keyId, chipInfo.objectIdentifier, publicKey.objectIdentifier , publicKey.subjectPublicKey)
        println(res)*/
        EMRTD.connectToNFCTag(tag)
        changeProgressBar(getString(R.string.reading_common_files), 0)
        EMRTD.readCommonFiles()
        changeProgressBar(getString(R.string.initialize_secure_messaging), 10)
        //pace.init(mrz, useCAN, idPaceOid, ca.paceInfos[0].parameterId!!)
        //pace.paceProtocol()
        if (EMRTD.ldS1Application.selectApplication() != SUCCESS) {
            return
        }
        EMRTD.ldS1Application.performBACProtocol()
        EMRTD.ldS1Application.readFiles(this)
        changeProgressBar(getString(R.string.reading_cscas), 5)
        readCSCAs()
        EMRTD.ldS1Application.verify(this)
        changeProgressBar(getString(R.string.passport_verified), 5)
        EMRTD.closeNFC()
    }

    /**
     * Read the CSCAs from the issuing country/organization of the eMRTD
     */
    private fun readCSCAs() {
        val directory = resources.assets.list("CSCA")
        val tmpCerts = ArrayList<X509Certificate>()
        if (directory != null && EMRTD.ldS1Application.dg1.issuerCode != null) {
            var path : Array<String>? = null
            try {
                path = resources.assets.list("CSCA/${EMRTD.ldS1Application.dg1.issuerCode}")
            } catch (_ : Exception) {

            }
            if (path != null) {
                val ce = CertificateFactory.getInstance("X509")
                for (cert in path) {
                    try {
                        val c = ce.generateCertificate(assets.open("CSCA/${EMRTD.ldS1Application.dg1.issuerCode}/$cert")) as X509Certificate
                        tmpCerts.add(c)
                    } catch (e : Exception) {
                        println(e)
                    }
                }
            }
        }
        EMRTD.ldS1Application.certs = tmpCerts.toTypedArray()
    }

    fun changeProgressBar(text : String, increment : Int) {
        runOnUiThread {
            findViewById<TextView>(R.id.progressBarText).text = text
            findViewById<ProgressBar>(R.id.progressBar).incrementProgressBy(increment)
        }
    }
}

class NewAPDUListener : APDUListener {
    val exchangedAPDUs = ArrayList<APDUEvent?>()

    override fun exchangedAPDU(e: APDUEvent?) {
        exchangedAPDUs.add(e)
    }
}