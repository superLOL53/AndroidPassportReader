package com.example.emrtdapplication.utils

import android.nfc.Tag
import android.nfc.tech.IsoDep
import com.example.emrtdapplication.constants.APDUControlConstants.CLOSE_SUCCESS
import com.example.emrtdapplication.constants.APDUControlConstants.CONNECT_SUCCESS
import com.example.emrtdapplication.constants.APDUControlConstants.ERROR_ISO_DEP_NOT_SELECTED
import com.example.emrtdapplication.constants.APDUControlConstants.ERROR_NO_ISO_DEP_SUPPORT
import com.example.emrtdapplication.constants.APDUControlConstants.ERROR_NO_NFC_TAG
import com.example.emrtdapplication.constants.APDUControlConstants.ERROR_UNABLE_TO_CLOSE
import com.example.emrtdapplication.constants.APDUControlConstants.ERROR_UNABLE_TO_CONNECT
import com.example.emrtdapplication.constants.APDUControlConstants.INIT_SUCCESS
import com.example.emrtdapplication.constants.APDUControlConstants.RESPOND_CODE_SIZE
import com.example.emrtdapplication.constants.APDUControlConstants.TIME_OUT
import com.example.emrtdapplication.constants.NfcRespondCodeSW1
import com.example.emrtdapplication.constants.NfcRespondCodeSW2
import com.example.emrtdapplication.constants.NfcUse
import com.example.emrtdapplication.constants.ZERO_BYTE
import java.io.IOException

/**
 * Class for sending and receiving APDUs from the ePassport
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
object APDUControl {
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
     * @return [INIT_SUCCESS], [ERROR_NO_NFC_TAG] or [ERROR_NO_ISO_DEP_SUPPORT]
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
        this.isoDep!!.timeout = TIME_OUT
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
        return if (isoDep != null) {
            isoDep!!.transceive(apdu.getByteArray())
        } else {
            ByteArray(0)
        }
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
     * Connects to the eMRTD
     * @return [CONNECT_SUCCESS], [ERROR_UNABLE_TO_CONNECT] or [ERROR_ISO_DEP_NOT_SELECTED]
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
     * @return [CLOSE_SUCCESS] or [ERROR_UNABLE_TO_CLOSE]
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
        return !(bytes.size < RESPOND_CODE_SIZE || bytes[bytes.size-RESPOND_CODE_SIZE] != NfcRespondCodeSW1.OK || bytes[bytes.size-1] != NfcRespondCodeSW2.OK)
    }

    /**
     * Removes the 2 byte respond codes from the byte array
     * @param bytes The byte array from which the respond codes are removed
     * @return The byte array without respond codes
     */
    fun removeRespondCodes(bytes: ByteArray): ByteArray {
        return bytes.slice(0..<bytes.size-RESPOND_CODE_SIZE).toByteArray()
    }

    /**
     * Increments the sequence counter by 1
     */
    fun inc() {
        for (i in ssc.indices.reversed()) {
            ssc[i] = (ssc[i] + 1).toByte()
            if (ssc[i] != ZERO_BYTE) {
                return
            }
        }
    }

    /**
     * Sends an encrypted APDU to the eMRTD
     *
     * @param apdu The APDU to encrypt and send to the eMRTD
     * @return The decrypted response APDU
     */
    private fun sendEncryptedAPDU(apdu: APDU) : ByteArray {
        if (isoDep == null) return ByteArray(0)
        inc()
        val secureAPDU = SecureMessagingAPDU(ssc, encryptionKey, encryptionKeyMAC, isAES, apdu)
        val responseAPDU = isoDep!!.transceive(secureAPDU.encryptedAPDUArray)
        inc()
        return SecureMessagingAPDU(ssc, encryptionKey, encryptionKeyMAC, isAES, responseAPDU).apduArray
    }
}