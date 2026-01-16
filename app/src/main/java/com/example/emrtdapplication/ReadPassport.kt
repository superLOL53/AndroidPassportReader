package com.example.emrtdapplication

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emrtdapplication.constants.SUCCESS
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

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
        /*val nfc = IsoDep.get(tag)
        val service = PassportService(CardService.getInstance(nfc), 1000, 1000, false, true)


        service.open()
        var info = service.transmit(CommandAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(0x01, 0x1C)).getByteArray()))
        info = service.transmit(CommandAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 256
        ).getByteArray()))
        val ca = CardAccess()
        ca.parse(info.data)
        val oid = ca.paceInfos[0].objectIdentifier
        val paramId = BigInteger(ca.paceInfos[0].parameterId!!.toHexString(), 16)
        val paramsName = when (ca.paceInfos[0].parameterId) {
            NIST_P192 -> "P-192"
            NIST_P224 -> "P-224"
            NIST_P256 -> "P-256"
            NIST_P384 -> "P-384"
            NIST_P521 -> "P-521"
            BRAIN_POOL_P192R1 -> "brainpoolp192r1"
            BRAIN_POOL_P224R1 -> "brainpoolp224r1"
            BRAIN_POOL_P256R1 -> "brainpoolp256r1"
            BRAIN_POOL_P320R1 -> "brainpoolp320r1"
            BRAIN_POOL_P384R1 -> "brainpoolp384r1"
            BRAIN_POOL_P512R1 -> "brainpoolp512r1"
            else -> null
        }
        try {
            val inst = AlgorithmParameters.getInstance("EC")
            inst.init(ECGenParameterSpec("secp256r1"))
            val keySpec = PACEKeySpec.createMRZKey(BACKey("U1194584", "000707", "260801"))
            val params = inst.getParameterSpec(ECParameterSpec::class.java)
            val res = service.doPACE(keySpec, oid, params, paramId)
            println()
        } catch (e : Exception) {
            println(e)
        }*/

        /*service.sendSelectApplet(false)
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
        EMRTD.pace.init(EMRTD.mrz, false, EMRTD.idPaceOid, EMRTD.ca.paceInfos[0].parameterId!!)
        val isPACESuccess = EMRTD.pace.paceProtocol() == SUCCESS
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
        changeProgressBar(getString(R.string.reading_cscas), 5)
        readCSCAs()
        EMRTD.ldS1Application.verify(this)
        changeProgressBar(getString(R.string.passport_verified), 5)
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
        //TODO: Implement Master List read/search
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