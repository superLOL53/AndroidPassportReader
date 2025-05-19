package com.example.emrtdapplication

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Constants for the PACE class
 */
const val PACE_TAG = "PACE"
const val PACE_ENABLE_LOGGING = true
const val NO_PASSWORD = -1
const val NO_PACE_OID = -2
const val INVALID_MSE_COMMAND = -3
const val INVALID_GENERAL_AUTHENTICATE = -4
const val INVALID_NONCE = -5
/**
 * Class implementing the PACE protocol.
 */
class PACE {
    private var mrzInformation : String? = null
    private var can : String? = null
    private var useCAN = false
    private var idPACEOid : ByteArray? = null

    /**
     * Initializes the PACE protocol with the MRZ information or CAN from the manual input.
     * @param mrz: The MRZ information from the manual input
     * @param can: The CAN from the manual input
     * @param paceOid: The supported cryptographic PACE protocol
     * @return Success (0) or error code indicating a failure
     */
    fun init(mrz :String?, can : String?, paceOid : ByteArray?): Int {
        mrzInformation = null
        this.can = null
        idPACEOid = null
        if (paceOid == null) {
            return log(NO_PACE_OID, "No PACE OID present")
        } else {
            idPACEOid = paceOid
        }
        if (mrz == null && can == null) {
            return log(NO_PASSWORD, "No MRZ and CAN present")
        } else if (can == null) {
            useCAN = false
            mrzInformation = mrz
            return SUCCESS
        } else {
            useCAN = true
            this.can = can
            return SUCCESS
        }
    }

    /**
     * Implements the PACE protocol.
     * @return Success(0) or error code indicating a failure
     */
    fun paceProtocol() : Int {
        if (idPACEOid == null) {
            return log(NO_PACE_OID, "No PACE OID present")
        }
        var key : ByteArray
        var info = byteArrayOf(TLV_TAGS.CRYPTOGRAPHIC_REFERENCE, idPACEOid!!.size.toByte()) + idPACEOid!! + byteArrayOf(TLV_TAGS.KEY_REFERENCE, 0x01)
        if (can == null && mrzInformation == null) {
            return NO_PASSWORD
        } else if (useCAN) {
            key = computeKey(can!!.toByteArray())
            info += 0x02
        } else {
            key = computeKey(mrzInformation!!.toByteArray())
            info += 0x01
        }
        key = computeKey(key + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x03))
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.MANAGE_SECURITY_ENVIRONMENT, NfcP1Byte.SET_AUTHENTICATION_TEMPLATE, NfcP2Byte.SET_AUTHENTICATION_TEMPLATE, true, 0x0F, ZERO_SHORT, info))
        if (info[info.size-2] != NfcRespondCodeSW1.OK && info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(INVALID_MSE_COMMAND, "Error code: ", info)
        }
        info = byteArrayOf(TLV_TAGS.NONCE_QUERY, ZERO_BYTE)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, true, 0x02, ZERO_SHORT, info, true, ZERO_BYTE, ZERO_SHORT))
        if (info[info.size-2] != NfcRespondCodeSW1.OK && info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(INVALID_GENERAL_AUTHENTICATE, "Error code: ", info)
        }
        val z = TLVCoder().decode(info.slice(0..info.size-3).toByteArray())[1].getValue()
            ?: return log(INVALID_NONCE, "Nonce is null")
        log("Encrypted nonce is: ", z)
        val s = decryptNonce(z, key)
        log("Decrypted nonce s is: ", s)
        /*val ret = when (id_PACE_OID!![id_PACE_OID!!.size-2]) {
            PACEInfoConstants.DH_GM ->
            PACEInfoConstants.DH_IM ->
            PACEInfoConstants.ECDH_GM ->
            PACEInfoConstants.ECDH_IM ->
            PACEInfoConstants.ECDH_CAM ->
            else -> return log(PACEConstants.INVALID_PACE_OID, "Invalid Pace protocol: ", id_PACE_OID!!)
        }
        if (ret != SUCCESS) {
            return log(ret, "PACE Protocol unsuccessful")
        } else {
            return log(ret, "PACE Protocol successful")
        }*/
        return NOT_IMPLEMENTED
    }

    /**
     * Computes a cryptographic key by hashing the input
     * @param b: The byte array to hash
     * @return A cryptographic key as byte array
     */
    private fun computeKey(b : ByteArray) : ByteArray {
        val hash = MessageDigest.getInstance("SHA-1")
        hash.update(b)
        return hash.digest()
    }

    /**
     * Decrypt a nonce value
     * @param nonce: The nonce to be decrypted
     * @param key: The decryption key
     * @return The decrypted nonce as byte array
     */
    private fun decryptNonce(nonce : ByteArray, key : ByteArray) : ByteArray {
        try {
            log("Decrypting Nonce")
            val k = SecretKeySpec(key, "AES")
            val i = IvParameterSpec(byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE))
            log("Initializing cipher...")
            val c = Cipher.getInstance("AES/CBC/NoPadding")
            log("Initialized")
            c.init(Cipher.DECRYPT_MODE, k, i)
            log("Decrypting...")
            return c.doFinal(nonce)
        } catch (e : Exception) {
            log("Exception: ${e.message}")
        }
        return byteArrayOf(0)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(PI_TAG, PI_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @return The error code
     */
    private fun log(error : Int, msg : String) : Int {
        return Logger.log(PI_TAG, PI_ENABLE_LOGGING, error, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log(PI_TAG, PI_ENABLE_LOGGING, msg, b)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(PI_TAG, PI_ENABLE_LOGGING, error, msg, b)
    }
}