package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.Crypto.computeKey
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.SUCCESS
import java.security.SecureRandom
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
 * Class implementing the BAC protocol.
 */
class BAC(private var apduControl: APDUControl) {
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
        val rndIfd = ByteArray(8)
        val kIfd = ByteArray(16)
        SecureRandom().nextBytes(rndIfd)
        SecureRandom().nextBytes(kIfd)
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
        val hash = Crypto.hash("SHA-1", mrzInformation!!.toByteArray())
        val kSeed = hash.slice(0..15).toByteArray()
        val kEnc = computeKey("SHA-1", kSeed, ENCRYPTION_KEY_VALUE_C, true).slice(0..15).toByteArray()
        val kMAC = computeKey("SHA-1", kSeed, MAC_COMPUTATION_KEY_VALUE_C, true).slice(0..15).toByteArray()
        val eIfd = Crypto.encrypt3DES(s, kEnc)
        val mIfd = Crypto.computeMAC(eIfd, kMAC)
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
        if (!Crypto.checkMAC(encData, mIC, kMAC)) {
            return ERROR_INVALID_MAC
        }
        val con = Crypto.decrypt3DES(encData, kEnc)
        if (!con.slice(8..15).toByteArray().contentEquals(rndIfd)) {
            return ERROR_INVALID_NONCE
        }
        val rndIC = con.slice(0..15).toByteArray()
        val kIC = con.slice(16..31).toByteArray()
        val kSessionSeed = ByteArray(16)
        for (i in 0..15) {
            kSessionSeed[i] = kIC[i] xor kIfd[i]
        }
        apduControl.setEncryptionKeyBAC(computeKey("SHA-1", kSessionSeed, ENCRYPTION_KEY_VALUE_C).slice(0..15).toByteArray())
        apduControl.setEncryptionKeyMAC(computeKey("SHA-1", kSessionSeed, MAC_COMPUTATION_KEY_VALUE_C).slice(0..15).toByteArray())
        apduControl.setSequenceCounter(
            (rndIC.slice(4..7).toByteArray() + rndIfd.slice(4..7).toByteArray())
        )
        apduControl.sendEncryptedAPDU = true
        apduControl.isAES = false
        return BAC_PROTOCOL_SUCCESS
    }
}