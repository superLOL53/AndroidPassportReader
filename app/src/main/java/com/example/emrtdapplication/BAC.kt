package com.example.emrtdapplication

import org.spongycastle.crypto.engines.DESEngine
import org.spongycastle.crypto.macs.ISO9797Alg3Mac
import org.spongycastle.crypto.paddings.ISO7816d4Padding
import org.spongycastle.crypto.params.KeyParameter
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

class BAC {
    private var mrzInformation : String? = null

    fun init(newMRZ : String?) : Int {
        if (newMRZ == null) {
            return Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, BACConstants.ERROR_NO_MRZ, "No MRZ in init function")
        }
        mrzInformation = newMRZ
        return BACConstants.BAC_INIT_SUCCESS
    }

    fun bacProtocol() : Int {
        if (mrzInformation == null) {
            return BACConstants.ERROR_UNINITIALIZED_MRZ_INFORMATION
        }
        //mrzInformation = "L898902C<369080619406236"
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "MRZ is $mrzInformation")
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Initializing BAC")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.REQUEST_RANDOM_NUMBER, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x08, 0))
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, BACConstants.ERROR_NONCE_REQUEST_FAILED, "Random Nonce request not Ok. Error code: ", info)
        }
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Random Nonce request OK. Nonce is: ", info)
        val rndIfd = ByteArray(8)
        val kIfd = ByteArray(16)
        //info = byteArrayOf(0x46, 0x08, 0xF9.toByte(), 0x19, 0x88.toByte(), 0x70, 0x22, 0x12, 0x90.toByte(), 0x00)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Nonce is:", info)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Initializing Random number and Key")
        SecureRandom().nextBytes(rndIfd)
        SecureRandom().nextBytes(kIfd)
        //rndIfd = byteArrayOf(0x78, 0x17, 0x23, 0x86.toByte(), 0x0C, 0x06, 0xC2.toByte(), 0x26)
        //kIfd = byteArrayOf(0x0B, 0x79, 0x52, 0x40, 0xCB.toByte(), 0x70, 0x49, 0xB0.toByte(), 0x1C, 0x19, 0xB3.toByte(), 0x3E, 0x32, 0x80.toByte(), 0x4F, 0x0B)
        val s = ByteArray(32)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Concatenate Random numbers and Key")
        for (i in 0..7) {
            s[i] = rndIfd[i]
        }
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "S is ", s)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Copied RND IFD")
        for (i in 0..7) {
            s[i+8] = info[i]
        }
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "S is ", s)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Copied RND.IC")
        for (i in 0..15) {
            s[i+16] = kIfd[i]
        }
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "S is ", s)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Copied K.IFD")
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "RND.IFD is: ", rndIfd)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "RND.IC is: ", info)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "K.IFD is: ", kIfd)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "S is ", s)
        val hash = hash("SHA-1", mrzInformation!!.toByteArray())
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "SHA-1 hash of MRZ information: ", hash)
        val kSeed = hash.slice(0..15).toByteArray()
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "K_seed is: ", kSeed)
        val kEnc = computeKeyBAC(kSeed, BACConstants.ENCRYPTION_KEY_VALUE_C)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Encryption Key is: ", kEnc)
        val kMAC = computeKeyBAC(kSeed, BACConstants.MAC_COMPUTATION_KEY_VALUE_C)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "MAC Key is: ", kMAC)
        val eIfd = encrypt3DES(s, byteArrayOf(0,0,0,0,0,0,0,0), kEnc)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Encrypted message is: ", eIfd)
        val mIfd = computeMAC(eIfd, kMAC)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Computed MAC is: ", mIfd)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.EXTERNAL_AUTHENTICATE, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x28, ZERO_SHORT, eIfd+mIfd, true, 0x28, ZERO_SHORT))
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Response: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, BACConstants.ERROR_BAC_PROTOCOL_FAILED, "BAC Unsuccessful")
        }
        val encData = info.slice(0..31).toByteArray()
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Encrypted data is: ", encData)
        val mIC = info.slice(32..39).toByteArray()
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "MAC is: ", mIC)
        if (!checkMAC(encData, mIC, kMAC)) {
            Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, BACConstants.ERROR_INVALID_MAC, "MAC is invalid.")
        }
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "MAC is OK.")
        val con = decrypt3DES(encData, byteArrayOf(0,0,0,0,0,0,0,0), kEnc)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Content is: ", con)
        if (!con.slice(8..15).toByteArray().contentEquals(rndIfd)) {
            Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, BACConstants.ERROR_INVALID_NONCE, "RND IFD not equal")
        }
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "RND IFD is equal")
        val rndIC = con.slice(0..15).toByteArray()
        val kIC = con.slice(16..31).toByteArray()
        val kSessionSeed = ByteArray(16)
        for (i in 0..15) {
            kSessionSeed[i] = kIC[i] xor kIfd[i]
        }
        APDUControl.setEncryptionKeyBAC(computeKeyBAC(kSessionSeed, BACConstants.ENCRYPTION_KEY_VALUE_C))
        APDUControl.setEncryptionKeyMAC(computeKeyBAC(kSessionSeed, BACConstants.MAC_COMPUTATION_KEY_VALUE_C))
        APDUControl.setSequenceCounter((rndIC.slice(4..7).toByteArray() + rndIfd.slice(4..7).toByteArray()))
        APDUControl.useBAC = true
        return Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, BACConstants.BAC_PROTOCOL_SUCCESS, "BAC Successful")
    }

    private fun checkMAC(c : ByteArray, mac : ByteArray, k : ByteArray) : Boolean {
        try {
            val m = ISO9797Alg3Mac(DESEngine(), 64, ISO7816d4Padding())
            m.init(KeyParameter(k))
            m.update(c, 0, c.size)
            val out = ByteArray(8)
            m.doFinal(out, 0)
            return out.contentEquals(mac)
        } catch (e : Exception) {
            Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Exception: ${e.message}")
        }
        return false
    }

    private fun decrypt3DES(decrypt : ByteArray, iv : ByteArray, key : ByteArray) : ByteArray {
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Initialize params...")
        val k = SecretKeySpec(key, "DESede")
        val i = IvParameterSpec(iv)
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Initialize Cipher...")
        c.init(Cipher.DECRYPT_MODE, k, i)
        Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Decrypt...")
        return c.doFinal(decrypt)
    }

    private fun computeMAC(m : ByteArray, k : ByteArray) : ByteArray {
        try {
            val mac = ISO9797Alg3Mac(DESEngine(), 64, ISO7816d4Padding())
            mac.init(KeyParameter(k))
            mac.update(m, 0, m.size)
            val out = ByteArray(8)
            mac.doFinal(out, 0)
            return out
        } catch (e : Exception) {
            Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Exception: ${e.message}")
        }
        return byteArrayOf(0)
    }

    private fun encrypt3DES(toEncrypt : ByteArray, iv : ByteArray, key : ByteArray) : ByteArray {
        val k = SecretKeySpec(key, "DESede")
        val i = IvParameterSpec(iv)
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, k, i)
        return c.doFinal(toEncrypt)
    }

    private fun computeKeyBAC(seed : ByteArray, c : Byte) : ByteArray {
        val keys = hash("SHA-1", seed+ byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, c))
        for (i in keys.indices) {
            if ((keys[i] and 0xFE.toByte()).countOneBits() % 2 == 0) {
                keys[i] = keys[i] or 0x1
            } else {
                keys[i] = keys[i] and 0xFE.toByte()
            }
        }
        return keys.slice(0..15).toByteArray()
    }

    private fun hash(algorithm : String, hashBytes : ByteArray) : ByteArray {
        val md = MessageDigest.getInstance(algorithm)
        md.update(hashBytes)
        return md.digest()
    }
}