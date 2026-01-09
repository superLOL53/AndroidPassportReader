package com.example.emrtdapplication.utils

import android.nfc.Tag
import android.nfc.tech.IsoDep
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcRespondCodeSW1
import com.example.emrtdapplication.constants.NfcRespondCodeSW2
import com.example.emrtdapplication.constants.ZERO_BYTE
import java.io.IOException
import javax.crypto.Cipher

/**
 * Enum class for NFC types. eMRTD uses ISO DEP as communication protocol. All other are insufficient and not used.
 */
enum class NfcUse {
    UNDEFINED,
    ISO_DEP
}

/**
 * Constants for the class APDUControl
 */
const val INIT_SUCCESS = 0
const val CONNECT_SUCCESS = 1
const val CLOSE_SUCCESS = 2
const val ERROR_NO_NFC_TAG = -1
const val ERROR_NO_ISO_DEP_SUPPORT = -2
const val ERROR_UNABLE_TO_CONNECT = -3
const val ERROR_ISO_DEP_NOT_SELECTED = -4
const val ERROR_UNABLE_TO_CLOSE = -5

/**
 * Class for sending and receiving APDUs from the ePassport
 * @property crypto Class for doing cryptography (e.g, en-/decrypting, padding, MAC computation)
 * @property maxResponseLength The maximum length of the received APDU
 * @property maxCommandLength The maximum length of the sending APDU
 * @property isoDepSupport Indicates the support for ISO DEP of the discovered tag
 * @property isoDep Used to transceive APDUs to and from the ePassport
 * @property nfcTechUse Tells the NFC Technology to use (ISO DEP, NfcA, NfcB,...)
 * @property maxTransceiveLength The maximum length for APDUs according to [isoDep]
 * @property sendEncryptedAPDU Tells if the APDU should be sent as an encrypted APDU
 * @property isAES Tells if the encryption uses AES
 * @property encryptionKey The key used to encrypt the APDU
 * @property encryptionKeyMAC The key used to generate the MAC of the APDU
 * @property ssc The sequence counter used for the MAC computation
 */
class APDUControl(private val crypto: Crypto = Crypto()) {
    var maxResponseLength = 0
    var maxCommandLength = 0
    private var isoDepSupport : Boolean = false
    private var isoDep : IsoDep? = null
    private var nfcTechUse : NfcUse = NfcUse.UNDEFINED
    private var maxTransceiveLength : Int = 0
    var sendEncryptedAPDU = false
    var isAES = false
    private var encryptionKey = byteArrayOf(0)
    private var encryptionKeyMAC = byteArrayOf(0)
    private var ssc = byteArrayOf(0)

    /**
     * Initialize communication with the eMRTD
     * @param tag: The NFC tag to connect to
     * @return Initialize success(0), no NFC tag(-1) or no ISO DEP support(-2)
     */
    fun init(tag: Tag?) : Int {
        if (tag == null) {
            return ERROR_NO_NFC_TAG
        }
        val isoDep = IsoDep.get(tag)
            ?: return ERROR_NO_ISO_DEP_SUPPORT
        isoDepSupport = true
        this.isoDep = isoDep
        if (nfcTechUse == NfcUse.UNDEFINED) {
            nfcTechUse = NfcUse.ISO_DEP
            maxTransceiveLength = this.isoDep!!.maxTransceiveLength
        }
        this.isoDep!!.timeout = 2000
        return INIT_SUCCESS
    }

    /**
     * Sends and receives APDUs from the eMRTD. If encryption is used, it sends the APDU to other functions
     * for further processing
     * @param apdu: The APDU to be sent to the eMRTD
     * @return The received APDU from the eMRTD
     */
    fun sendAPDU(apdu : APDU) : ByteArray {
        if (sendEncryptedAPDU) {
            return sendEncryptedAPDU(apdu)
        } else {
            sendEncryptedAPDU = false
            return sendISODEP(apdu)
        }
    }

    /**
     * Send and receive unencrypted APDUs
     * @param apdu The APDU to send
     * @return The received byte array from the ePassport
     */
    @OptIn(ExperimentalStdlibApi::class)
    private fun sendISODEP(apdu: APDU) : ByteArray {
        return isoDep!!.transceive(apdu.getByteArray())
    }

    /**
     * Set the 3DES Encryption key of the BAC protocol
     * @param key: The BAC encryption key
     */
    fun setEncryptionKeyBAC(key : ByteArray) {
        encryptionKey = key
    }

    /**
     * Set the MAC Encryption key of the BAC protocol
     */
    fun setEncryptionKeyMAC(key: ByteArray) {
        encryptionKeyMAC = key
    }

    /**
     * Set the sequence counter
     * @param counter: The sequence counter as byte array
     */
    fun setSequenceCounter(counter : ByteArray) {
        ssc = counter
    }

    /**
     * Connecting to the eMRTD
     * @return Connect success(1), unable to connect(-3) or iso dep not selected(-4)
     */
    fun connectToNFC() : Int{
        try {
            when (nfcTechUse) {
                NfcUse.ISO_DEP -> {
                    isoDep!!.connect()
                    return CONNECT_SUCCESS
                }
                NfcUse.UNDEFINED -> {
                    return ERROR_ISO_DEP_NOT_SELECTED
                }
            }
        } catch (_ : IOException) {
            return ERROR_UNABLE_TO_CONNECT
        }
    }

    /**
     * Closes the NFC Connection from the eMRTD
     * @return Success (2) or unable to close (-5)
     */
    fun closeNFC() : Int {
        try {
            when (nfcTechUse) {
                NfcUse.ISO_DEP -> {
                    isoDep!!.close()
                    return CLOSE_SUCCESS
                }
                NfcUse.UNDEFINED -> {
                    return ERROR_UNABLE_TO_CLOSE
                }
            }
        } catch (_ : IOException) {
            return ERROR_UNABLE_TO_CLOSE
        }
    }

    /**
     * Checks if the ePassport responded with OK status codes
     * @param bytes The byte array for checking the respond codes
     * @return True if the ePassport responded with an OK status code, otherwise false
     */
    fun checkResponse(bytes: ByteArray): Boolean {
        return !(bytes.size < 2 || bytes[bytes.size-2] != NfcRespondCodeSW1.OK || bytes[bytes.size-1] != NfcRespondCodeSW2.OK)
    }

    /**
     * Removes the 2 byte respond codes from the byte array
     * @param bytes The byte array from which the respond codes are removed
     * @return The byte array without respond codes
     */
    fun removeRespondCodes(bytes: ByteArray): ByteArray {
        return bytes.slice(0..bytes.size-3).toByteArray()
    }

    /**
     * Builds and sends encrypted APDUs. The received APDU from the eMRTD is verified, decrypted and
     * returned
     * @param apdu: The APDU to be encrypted and sent to the eMRTD
     * @return The verified and decrypted APDU received from the eMRTD
     */
    private fun sendEncryptedAPDU(apdu: APDU) : ByteArray {
        inc()
        val headerSM = headerSM(apdu)
        val dataSM = dataSM(apdu)
        val do97 = do97(apdu)
        var tail : ByteArray? = null
        if (dataSM != null) {
            tail = dataSM
        }
        if (do97 != null) {
            if (tail == null) {
                tail = do97
            } else {
                tail += do97
            }
        }
        var m = addPadding(headerSM)
        if (tail != null) {
            m += tail
        }
        val do8e = do8E08(m)
        var length = do8e.size
        if (tail != null) {
            length += tail.size
        }
        val newLc = lcField(length, apdu.useLcExt || apdu.useLeExt)
        var finalApdu = headerSM + newLc
        if (tail != null) {
            finalApdu += tail
        }
        finalApdu += do8e + 0x0
        if (apdu.useLeExt || apdu.useLcExt) {
            finalApdu += 0x0
        }
        inc()
        val rApdu = isoDep!!.transceive(finalApdu)
        if (rApdu.size > 13) {
            verifyMAC(rApdu)
        }
        return extractAPDU(rApdu)
    }

    /**
     * Constructs the header for secure messaging
     * @param apdu: The apdu for which the header is formatted
     * @return The header for secure messaging without padding
     */
    private fun headerSM(apdu: APDU) : ByteArray {
        val header = apdu.getHeader()
        header[0] = (NfcClassByte.SECURE_MESSAGING.toInt() or (apdu.getHeader()[0].toInt() and 0xF0)).toByte()
        return header
    }

    /**
     * Builds the byte array containing the Lc field of the APDU
     * @param length The length of the data in the APDU to be sent
     * @param useExtendedLength If extended length should be used
     * @return The Lc field of the APDU as a byte array
     */
    private fun lcField(length : Int, useExtendedLength : Boolean = false) : ByteArray {
        return if (length > 256 || useExtendedLength) {
            byteArrayOf(0, (length/256).toByte(), (length % 256).toByte())
        } else {
            byteArrayOf(length.toByte())
        }
    }

    /**
     * Builds the formatted encrypted data for the BAC encrypted APDU
     * @param apdu: The apdu for which the data is encrypted
     * @return The byte array containing the formatted encrypted data or null if there is no data
     */
    private fun dataSM(apdu: APDU) : ByteArray? {
        val paddedData = addPadding(apdu.data)
        val encryptedData = encrypt(paddedData)
        var do8785 : ByteArray? = null
        if (apdu.useLc) {
            do8785 = if (apdu.getHeader()[1] % 2 == 0) {
                byteArrayOf(0x87.toByte(), 0x09, 0x01) + encryptedData
            } else {
                byteArrayOf(0x85.toByte(), 0x09, 0x01) + encryptedData
            }
        }
        return do8785
    }

    /**
     * Builds the DO97 byte array
     * @param apdu: The APDU for which the DO97 is build
     * @return The DO97 byte array
     */
    private fun do97(apdu: APDU) : ByteArray? {
        var do97 : ByteArray? = null
        if (apdu.useLe) {
            do97 = if (apdu.useLeExt) {
                byteArrayOf(0x97.toByte(), 0x02, (apdu.le/256).toByte(), (apdu.le%256).toByte())
            } else {
                byteArrayOf(0x97.toByte(), 0x01, apdu.le.toByte())
            }
        }
        return do97
    }

    /**
     * Builds the DO8E08 byte array
     * @param m: The byte array for which the MAC is calculated
     * @return The byte array containing the bytes 0x8E, 0x08 and the computed MAC
     */
    private fun do8E08(m : ByteArray) : ByteArray {
        //log("M: ", m)
        //log("SSC: ", ssc)
        //log("Incremented SSC: ", ssc)
        val n = addPadding(ssc + m)
        //log("N: ", n)
        val cc = computeMAC(n)
        //log("CC: ", cc)
        val do8e = byteArrayOf(0x8E.toByte(), 0x08) + cc
        //log("DO8E: ", do8e)
        return do8e
    }

    /**
     * Extracts the APDU from the BAC encrypted APDU received from the eMRTD
     * @param bytes: The received, encrypted APDU from the eMRTD
     * @return The decrypted APDU without padding
     */
    private fun extractAPDU(bytes: ByteArray) : ByteArray {
        val normalAPDU : ByteArray = if (bytes[0] == 0x87.toByte() || bytes[0] == 0x85.toByte()) {
            val l = if (bytes[1] < 0) {
                (bytes[1] + 128) + 3
            } else {
                3
            }
            crypto.removePadding(decrypt(bytes.slice(l..bytes.size-17).toByteArray())) + bytes.slice(bytes.size-2..<bytes.size).toByteArray()
        } else {
            bytes.slice(bytes.size-2..<bytes.size).toByteArray()
        }
        return normalAPDU
    }

    /**
     * Increments the sequence counter by 1
     */
    private fun inc() {
        for (i in ssc.indices.reversed()) {
            ssc[i] = (ssc[i] + 1).toByte()
            if (ssc[i] != ZERO_BYTE) {
                return
            }
        }
    }

    /**
     * Computes the MAC for the byte array
     * @param m: The byte array for which the MAC is computed
     * @return The computed MAC of the input
     */
    private fun computeMAC(m : ByteArray) : ByteArray {
        return if (isAES) {
            crypto.computeCMAC(m, encryptionKeyMAC)
        } else {
            crypto.computeMAC(m, encryptionKeyMAC, usePadding = false)
        }
    }

    /**
     * Verifies the MAC of a received APDU
     * @param rApdu: The received APDU from the eMRTD
     * @return True if the MAC verification succeeds, otherwise False
     */
    private fun verifyMAC(rApdu : ByteArray) : Boolean {
        val n = addPadding(ssc + rApdu.slice(0..rApdu.size-13))
        val cc = computeMAC(n)
        return cc.contentEquals(rApdu.slice(rApdu.size-10..rApdu.size-3).toByteArray())
    }

    /**
     * Encrypt a byte array
     * @param bytes: The byte array to be encrypted with BAC
     * @return The encrypted byte array
     */
    private fun encrypt(bytes: ByteArray) : ByteArray {
        return if (isAES) {
            crypto.cipherAES(bytes, encryptionKey)
        } else {
            crypto.cipher3DES(bytes, encryptionKey + encryptionKey.slice(0..7).toByteArray())
        }
    }

    /**
     * Decrypts the
     * @param bytes The ByteArray to be decrypted with AES or 3DES
     * @return The decrypted ByteArray
     */
    private fun decrypt(bytes: ByteArray) : ByteArray {
        return if (isAES) {
            crypto.cipherAES(bytes, encryptionKey, Cipher.DECRYPT_MODE)
        } else {
            crypto.cipher3DES(bytes, encryptionKey + encryptionKey.slice(0..7).toByteArray(), Cipher.DECRYPT_MODE)
        }
    }

    /**
     * Adds padding to the byte array
     * @param byteArray: The byte array to be padded
     * @return The padded byte array
     */
    private fun addPadding(byteArray: ByteArray) : ByteArray {
        return if (isAES) {
            crypto.addPadding(byteArray, encryptionKey.size)
        } else {
            crypto.addPadding(byteArray, 8)
        }
    }
}