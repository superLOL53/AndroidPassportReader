package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.SUCCESS
import java.security.SecureRandom
import javax.crypto.Cipher
import kotlin.experimental.xor

/**
 * Constants for the class BAC
 */
const val BAC_TAG = "BAC"
const val BAC_ENABLE_LOGGING = true
const val ENCRYPTION_KEY_VALUE_C : Byte = 1
const val MAC_COMPUTATION_KEY_VALUE_C : Byte = 2
const val BAC_PROTOCOL_SUCCESS = 0
const val ERROR_UNINITIALIZED_MRZ_INFORMATION = -1
const val ERROR_NONCE_REQUEST_FAILED = -2
const val ERROR_BAC_PROTOCOL_FAILED = -3
const val ERROR_INVALID_MAC = -4
const val ERROR_INVALID_NONCE = -5
const val ERROR_NO_MRZ = -6

/**
 * Implements the Basic Access Control (BAC) protocol
 *
 * @property apduControl Used for sending and receiving APDUs
 * @property crypto Used for cryptographic operations
 * @property mrzInformation The MRZ of the eMRTD
 */
class BAC(private var apduControl: APDUControl, private var crypto: Crypto = Crypto(), private var random: SecureRandom? = SecureRandom()) {
    private var mrzInformation : String? = null

    //private var sr : SecureRandom

    /**
     * Initializes the BAC protocol with the MRZ information from the manual input
     * @param newMRZ: The MRZ information used to derive the cryptographic keys for the BAC protocol
     * @return Success(0) or Error(-6) if newMRZ was empty or null
     */
    fun init(newMRZ : String?) : Int {
        if (newMRZ.isNullOrEmpty()) {
            return ERROR_NO_MRZ
        }
        mrzInformation = newMRZ
        return SUCCESS
    }

    /**
     * Implements the BAC protocol, derives the cryptographic keys and stores them in the APDUControl class
     * for further application. The LDS1 application has to be selected before BAC can be run.
     * @return Success(0) if protocol was successful or a negative error code
     */
    fun bacProtocol() : Int {
        if (mrzInformation == null) {
            return ERROR_UNINITIALIZED_MRZ_INFORMATION
        }
        var info = apduControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.REQUEST_RANDOM_NUMBER,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                0x08
            )
        )
        if (!apduControl.checkResponse(info)) {
            return ERROR_NONCE_REQUEST_FAILED
        }
        val rndIfd = if (random == null) {
            byteArrayOf(0x78, 0x17, 0x23, 0x86.toByte(), 0x0C, 0x06, 0xC2.toByte(), 0x26)
        } else {
            val rand = ByteArray(8)
            random!!.nextBytes(rand)
            rand
        }
        val kIfd = if (random == null) {
            byteArrayOf(0x0B, 0x79, 0x52, 0x40, 0xCB.toByte(), 0x70, 0x49, 0xB0.toByte(), 0x1C, 0x19, 0xB3.toByte(), 0x3E, 0x32, 0x80.toByte(), 0x4F, 0x0B)
        } else {
            val rand = ByteArray(16)
            random!!.nextBytes(rand)
            rand
        }
        val s = ByteArray(32)
        for (i in 0..7) {
            s[i] = rndIfd[i]
        }
        for (i in 0..7) {
            s[i+8] = info[i]
        }
        for (i in 0..15) {
            s[i+16] = kIfd[i]
        }
        val hash = crypto.hash("SHA-1", mrzInformation!!.toByteArray())
        val kSeed = hash.slice(0..15).toByteArray()
        var kEnc = crypto.computeKey("SHA-1", kSeed, ENCRYPTION_KEY_VALUE_C, true).slice(0..15).toByteArray()
        kEnc += kEnc.slice(0..7).toByteArray()
        var kMAC = crypto.computeKey("SHA-1", kSeed, MAC_COMPUTATION_KEY_VALUE_C, true).slice(0..15).toByteArray()
        kMAC += kMAC.slice(0..7).toByteArray()
        val eIfd = crypto.cipher3DES(s, kEnc)
        val mIfd = crypto.computeMAC(eIfd, kMAC)
        info = apduControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.EXTERNAL_AUTHENTICATE,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                eIfd + mIfd,
                0x28
            )
        )
        if (!apduControl.checkResponse(info)) {
            return ERROR_BAC_PROTOCOL_FAILED
        }
        info = apduControl.removeRespondCodes(info)
        val encData = info.slice(0..31).toByteArray()
        val mIC = info.slice(32..39).toByteArray()
        if (!crypto.checkMAC(encData, mIC, kMAC)) {
            return ERROR_INVALID_MAC
        }
        val con = crypto.cipher3DES(encData, kEnc, Cipher.DECRYPT_MODE)
        if (!con.slice(8..15).toByteArray().contentEquals(rndIfd)) {
            return ERROR_INVALID_NONCE
        }
        val rndIC = con.slice(0..15).toByteArray()
        val kIC = con.slice(16..31).toByteArray()
        val kSessionSeed = ByteArray(16)
        for (i in 0..15) {
            kSessionSeed[i] = kIC[i] xor kIfd[i]
        }
        apduControl.setEncryptionKeyBAC(crypto.computeKey("SHA-1", kSessionSeed, ENCRYPTION_KEY_VALUE_C, true).slice(0..15).toByteArray())
        apduControl.setEncryptionKeyMAC(crypto.computeKey("SHA-1", kSessionSeed, MAC_COMPUTATION_KEY_VALUE_C, true).slice(0..15).toByteArray())
        apduControl.setSequenceCounter(
            (rndIC.slice(4..7).toByteArray() + rndIfd.slice(4..7).toByteArray())
        )
        apduControl.sendEncryptedAPDU = true
        apduControl.isAES = false
        return BAC_PROTOCOL_SUCCESS
    }
}