package com.example.emrtdapplication

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class PACE {
    private var mrzInformation : String? = null
    private var CAN : String? = null
    private var useCAN = false
    private var id_PACE_OID : ByteArray? = null

    fun init(mrz :String?, can : String?, pace_OID : ByteArray?): Int {
        mrzInformation = null
        CAN = null
        id_PACE_OID = null
        if (pace_OID == null) {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, PACEConstants.NO_PACE_OID, "No PACE OID present")
        } else {
            id_PACE_OID = pace_OID
        }
        if (mrz == null && can == null) {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, PACEConstants.NO_PASSWORD, "No MRZ and CAN present")
        } else if (can == null) {
            useCAN = false
            mrzInformation = mrz
            return SUCCESS
        } else {
            useCAN = true
            CAN = can
            return SUCCESS
        }
    }

    fun PACE_Protoccol() : Int {
        if (id_PACE_OID == null) {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, PACEConstants.NO_PACE_OID, "No PACE OID present")
        }
        var key : ByteArray
        var info = byteArrayOf(TLV_TAGS.CRYPTOGRAPHIC_REFERENCE, id_PACE_OID!!.size.toByte()) + id_PACE_OID!! + byteArrayOf(TLV_TAGS.KEY_REFERENCE, 0x01)
        if (CAN == null && mrzInformation == null) {
            return PACEConstants.NO_PASSWORD
        } else if (useCAN) {
            key = computeKey(CAN!!.toByteArray())
            info += 0x02
        } else {
            key = computeKey(mrzInformation!!.toByteArray())
            info += 0x01
        }
        key = computeKey(key + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x03))
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.MANAGE_SECURITY_ENVIRONMENT, NfcP1Byte.SET_AUTHENTICATION_TEMPLATE, NfcP2Byte.SET_AUTHENTICATION_TEMPLATE, true, 0x0F, ZERO_SHORT, info))
        if (info[info.size-2] != NfcRespondCodeSW1.OK && info[info.size-1] != NfcRespondCodeSW2.OK) {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, PACEConstants.INVALID_MSE_COMMAND, "Error code: ", info)
        }
        info = byteArrayOf(TLV_TAGS.NONCE_QUERY, ZERO_BYTE)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, true, 0x02, ZERO_SHORT, info, true, ZERO_BYTE, ZERO_SHORT))
        if (info[info.size-2] != NfcRespondCodeSW1.OK && info[info.size-1] != NfcRespondCodeSW2.OK) {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, PACEConstants.INVALID_GENERAL_AUTHENTICATE, "Error code: ", info)
        }
        var z = TLVCoder().decode(info.slice(0..info.size-3).toByteArray())[1].getValue()
        if (z == null) {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, PACEConstants.INVALID_NONCE, "Nonce is null")
        }
        Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, "Encrypted nonce is: ", z)
        val s = decryptNonce(z, key)
        /*val ret = when (id_PACE_OID!![id_PACE_OID!!.size-2]) {
            PACEInfoConstants.DH_GM ->
            PACEInfoConstants.DH_IM ->
            PACEInfoConstants.ECDH_GM ->
            PACEInfoConstants.ECDH_IM ->
            PACEInfoConstants.ECDH_CAM ->
            else -> return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, PACEConstants.INVALID_PACE_OID, "Invalid Pace protocol: ", id_PACE_OID!!)
        }
        if (ret != SUCCESS) {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, ret, "PACE Protocol unsuccessful")
        } else {
            return Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, ret, "PACE Protocol successful")
        }*/
        return NOT_IMPLEMENTED
    }

    private fun computeKey(b : ByteArray) : ByteArray {
        val hash = MessageDigest.getInstance("SHA-1")
        hash.update(b)
        return hash.digest()
    }

    private fun decryptNonce(nonce : ByteArray, key : ByteArray) : ByteArray {
        try {
            Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, "Decrypting Nonce")
            val k = SecretKeySpec(key, "AES")
            val i = IvParameterSpec(byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, ZERO_BYTE))
            Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, "Initializing cipher...")
            val c = Cipher.getInstance("AES/CBC/NoPadding")
            Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, "Initialized")
            c.init(Cipher.DECRYPT_MODE, k, i)
            Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, "Decrypting...")
            return c.doFinal(nonce)
        } catch (e : Exception) {
            Logger.log(PACEConstants.TAG, PACEConstants.ENABLE_LOGGING, "Exception: ${e.message}")
        }
        return byteArrayOf(0)
    }
}