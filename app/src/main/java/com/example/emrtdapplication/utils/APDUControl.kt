package com.example.emrtdapplication.utils

import android.nfc.Tag
import android.nfc.tech.IsoDep
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Enum class for NFC types. EMRTD uses ISO DEP as communication protocol. All other are insufficient and not used.
 */
enum class NfcUse {
    UNDEFINED,
    ISO_DEP
}

/**
 * Constants for the class APDUControl
 */
const val AC_TAG = "APDUControl"
const val AC_ENABLE_LOGGING = true
const val INIT_SUCCESS = 0
const val CONNECT_SUCCESS = 1
const val CLOSE_SUCCESS = 2
const val ERROR_NO_NFC_TAG = -1
const val ERROR_NO_ISO_DEP_SUPPORT = -2
const val ERROR_UNABLE_TO_CONNECT = -3
const val ERROR_ISO_DEP_NOT_SELECTED = -4
const val ERROR_UNABLE_TO_CLOSE = -5

/**
 * Class for interacting with the EMRTD. All interactions with the EMRTD (e.g. transceive-calls) go through here
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
     * Initialize communication with the EMRTD
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
     * Sends and receives APDUs from the EMRTD. If encryption is used, it sends the APDU to other functions
     * for further processing
     * @param apdu: The APDU to be sent to the EMRTD
     * @return The received APDU from the EMRTD
     */
    fun sendAPDU(apdu : APDU) : ByteArray {
        if (sendEncryptedAPDU) {
            return sendEncryptedAPDU(apdu)
        } else {
            sendEncryptedAPDU = false
            return sendISODEP(apdu)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun sendISODEP(apdu: APDU) : ByteArray {
        log(apdu.getByteArray().toHexString())
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
     * Connecting to the EMRTD
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
        } catch (e : IOException) {
            return ERROR_UNABLE_TO_CONNECT
        }
    }

    /**
     * Closes the NFC Connection from the EMRTD
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
        } catch (e : IOException) {
            return ERROR_UNABLE_TO_CLOSE
        }
    }

    fun checkResponse(bytes: ByteArray): Boolean {
        return !(bytes.size < 2 || bytes[bytes.size-2] != NfcRespondCodeSW1.OK || bytes[bytes.size-1] != NfcRespondCodeSW2.OK)
    }

    fun removeRespondCodes(bytes: ByteArray): ByteArray {
        return bytes.slice(0..bytes.size-3).toByteArray()
    }

    /**
     * Builds and sends BAC encrypted APDUs. The received APDU from the EMRTD is verified, decrypted and
     * returned
     * @param apdu: The APDU to be encrypted and sent to the EMRTD
     * @return The verified and decrypted APDU received from the EMRTD
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
        val newLc = lcField(length, apdu.getUseLcExt() || apdu.getUseLeExt())
        var finalApdu = headerSM + newLc
        if (tail != null) {
            finalApdu += tail
        }
        finalApdu += do8e + 0x0
        if (apdu.getUseLeExt() || apdu.getUseLcExt()) {
            finalApdu += 0x0
        }
        log("ProtectedAPDU: ", finalApdu)
        inc()
        val rapdu = isoDep!!.transceive(finalApdu)
        log("RAPDU: ", rapdu)
        if (rapdu.size > 13) {
            verifyMAC(rapdu)
        }
        return extractAPDU(rapdu)
    }

    /**
     * Constructs the header for secure messaging
     * @param apdu: The apdu for which the header is formatted
     * @return The header for secure messaging without padding
     */
    private fun headerSM(apdu: APDU) : ByteArray {
        val header = apdu.getHeader()
        header[0] = NfcClassByte.SECURE_MESSAGING
        return header
    }

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
        val paddedData = addPadding(apdu.getData())
        val encryptedData = encrypt(paddedData)
        var do8785 : ByteArray? = null
        if (apdu.getUseLc()) {
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
        if (apdu.getUseLe()) {
            do97 = if (apdu.getUseLeExt()) {
                byteArrayOf(0x97.toByte(), 0x02, (apdu.getLe()/256).toByte(), (apdu.getLe()%256).toByte())
            } else {
                byteArrayOf(0x97.toByte(), 0x01, apdu.getLe().toByte())
            }
        }
        return do97
    }

    /**
     * Appends the bytes 0x8E and 0x08 a
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
     * Extracts the APDU from the BAC encrypted APDU received from the EMRTD
     * @param bytes: The received, encrypted APDU from the EMRTD
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
     * Removes the padding of the input array
     * @param bytes: The byte array for which the padding is removed
     * @return The byte array without padding
     */
    private fun removePadding(bytes: ByteArray) : ByteArray {
        val last = bytes.lastIndexOf(0x80.toByte())
        return if (last == -1) {
            bytes
        } else {
            bytes.slice(0..<last).toByteArray()
        }
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
        /*try {
            val mac = ISO9797Alg3Mac(DESEngine(), 64)
            mac.init(KeyParameter(encryptionKeyMAC))
            mac.update(m, 0, m.size)
            val out = ByteArray(8)
            mac.doFinal(out, 0)
            return out
        } catch (e : Exception) {
            log("Exception: ${e.message}")
        }
        return byteArrayOf(0)*/
        return if (isAES) {
            crypto.computeCMAC(m, encryptionKeyMAC)
        } else {
            crypto.computeMAC(m, encryptionKeyMAC, usePadding = false)
        }
    }

    /**
     * Verifies the MAC of a received APDU
     * @param rapdu: The received APDU from the EMRTD
     * @return True if the MAC verification succeeds, otherwise False
     */
    private fun verifyMAC(rapdu : ByteArray) : Boolean {
        val n = addPadding(ssc + rapdu.slice(0..rapdu.size-13))
        val cc = computeMAC(n)
        return cc.contentEquals(rapdu.slice(rapdu.size-10..rapdu.size-3).toByteArray())
    }

    /**
     * Encrypt a byte array with BAC
     * @param bytes: The byte array to be encrypted with BAC
     * @return The encrypted byte array
     */
    private fun encrypt(bytes: ByteArray) : ByteArray {
        if (isAES) {
            return crypto.cipherAES(bytes, encryptionKey)
            //return encryptAES(bytes)
        } else {
            return crypto.cipher3DES(bytes, encryptionKey + encryptionKey.slice(0..7).toByteArray())
            //return encrypt3DES(bytes)
        }
    }

    private fun encryptAES(bytes: ByteArray) : ByteArray {
        val k = SecretKeySpec(encryptionKey, "AES")
        val c = Cipher.getInstance("AES/CBC/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, k)
        return c.doFinal(bytes)
    }

    private fun encrypt3DES(bytes: ByteArray) : ByteArray {
        val k = SecretKeySpec(encryptionKey + encryptionKey.slice(0..7).toByteArray(), "DESede")
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        val i = IvParameterSpec(byteArrayOf(0,0,0,0,0,0,0,0))
        c.init(Cipher.ENCRYPT_MODE, k,i)
        return c.doFinal(bytes)
    }

    private fun decrypt(bytes: ByteArray) : ByteArray {
        return if (isAES) {
            crypto.cipherAES(bytes, encryptionKey, Cipher.DECRYPT_MODE)
            //decryptAES(bytes)
        } else {
            crypto.cipher3DES(bytes, encryptionKey + encryptionKey.slice(0..7).toByteArray(), Cipher.DECRYPT_MODE)
            //decrypt3DES(bytes)
        }
    }

    private fun decryptAES(bytes: ByteArray) : ByteArray {
        val k = SecretKeySpec(encryptionKey, "AES")
        val c = Cipher.getInstance("AES/CBC/NoPadding")
        c.init(Cipher.DECRYPT_MODE, k)
        return c.doFinal(bytes)
    }

    /**
     * Decrypt BAC encrypted data received from the EMRTD
     * @param data: The encrypted data of the received APDU
     * @return The decrypted data
     */
    private fun decrypt3DES(data: ByteArray) : ByteArray {
        val k = SecretKeySpec(encryptionKey + encryptionKey.slice(0..7).toByteArray(), "DESede")
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        val i = IvParameterSpec(byteArrayOf(0,0,0,0,0,0,0,0))
        c.init(Cipher.DECRYPT_MODE, k, i)
        return c.doFinal(data)
    }

    /**
     * Adds padding to the byte array
     * @param byteArray: The byte array to be padded
     * @return The padded byte array
     */
    private fun addPadding(byteArray: ByteArray) : ByteArray {
        /*val pad = 8 - byteArray.size % 8
        if (pad == 8) {
            return byteArray + byteArrayOf(0x80.toByte(), 0,0,0,0,0,0,0)
        }
        var padArray = byteArray + 0x80.toByte()
        for (i in 1..<pad) {
            padArray += 0x00
        }
        return padArray*/
        return if (isAES) {
            crypto.addPadding(byteArray, encryptionKey.size)
        } else {
            crypto.addPadding(byteArray, 8)
        }
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg : String) {
        Logger.log(AC_TAG, AC_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        Logger.log(AC_TAG, AC_ENABLE_LOGGING, msg, b)
    }
}