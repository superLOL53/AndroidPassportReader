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

/**
 * Constants for the class BAC
 */
const val BAC_TAG = "BAC"
const val BAC_ENABLE_LOGGING = true
const val ENCRYPTION_KEY_VALUE_C : Byte = 1
const val MAC_COMPUTATION_KEY_VALUE_C : Byte = 2
const val BAC_INIT_SUCCESS = 1
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
class BAC {
    private var mrzInformation : String? = null

    /**
     * Initializes the BAC protocol with the MRZ information from the manual input
     * @param newMRZ: The MRZ information used to derive the cryptographic keys for the BAC protocol
     * @return Success(0) or Error(-6) if newMRZ was empty or null
     */
    fun init(newMRZ : String?) : Int {
        if (newMRZ.isNullOrEmpty()) {
            return log(ERROR_NO_MRZ, "No MRZ in init function")
        }
        mrzInformation = newMRZ
        return BAC_INIT_SUCCESS
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
        log("MRZ is $mrzInformation")
        log("Initializing BAC")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.REQUEST_RANDOM_NUMBER, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x08, 0))
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(ERROR_NONCE_REQUEST_FAILED, "Random Nonce request not Ok. Error code: ", info)
        }
        log("Random Nonce request OK. Nonce is: ", info)
        val rndIfd = ByteArray(8)
        val kIfd = ByteArray(16)
        log("Nonce is:", info)
        log("Initializing Random number and Key")
        SecureRandom().nextBytes(rndIfd)
        SecureRandom().nextBytes(kIfd)
        val s = ByteArray(32)
        log("Concatenate Random numbers and Key")
        for (i in 0..7) {
            s[i] = rndIfd[i]
        }
        log("S is ", s)
        log("Copied RND IFD")
        for (i in 0..7) {
            s[i+8] = info[i]
        }
        log("S is ", s)
        log("Copied RND.IC")
        for (i in 0..15) {
            s[i+16] = kIfd[i]
        }
        log("S is ", s)
        log("Copied K.IFD")
        log("RND.IFD is: ", rndIfd)
        log("RND.IC is: ", info)
        log("K.IFD is: ", kIfd)
        log("S is ", s)
        val hash = hash(mrzInformation!!.toByteArray())
        log("SHA-1 hash of MRZ information: ", hash)
        val kSeed = hash.slice(0..15).toByteArray()
        log("K_seed is: ", kSeed)
        val kEnc = computeKeyBAC(kSeed, ENCRYPTION_KEY_VALUE_C)
        log("Encryption Key is: ", kEnc)
        val kMAC = computeKeyBAC(kSeed, MAC_COMPUTATION_KEY_VALUE_C)
        log("MAC Key is: ", kMAC)
        val eIfd = encrypt3DES(s, byteArrayOf(0,0,0,0,0,0,0,0), kEnc)
        log("Encrypted message is: ", eIfd)
        val mIfd = computeMAC(eIfd, kMAC)
        log("Computed MAC is: ", mIfd)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.EXTERNAL_AUTHENTICATE, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x28, ZERO_SHORT, eIfd+mIfd, true, 0x28, ZERO_SHORT))
        log("Response: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            log(ERROR_BAC_PROTOCOL_FAILED, "BAC Unsuccessful")
        }
        val encData = info.slice(0..31).toByteArray()
        log("Encrypted data is: ", encData)
        val mIC = info.slice(32..39).toByteArray()
        log("MAC is: ", mIC)
        if (!checkMAC(encData, mIC, kMAC)) {
            log(ERROR_INVALID_MAC, "MAC is invalid.")
        }
        log("MAC is OK.")
        val con = decrypt3DES(encData, byteArrayOf(0,0,0,0,0,0,0,0), kEnc)
        log("Content is: ", con)
        if (!con.slice(8..15).toByteArray().contentEquals(rndIfd)) {
            log(ERROR_INVALID_NONCE, "RND IFD not equal")
        }
        log("RND IFD is equal")
        val rndIC = con.slice(0..15).toByteArray()
        val kIC = con.slice(16..31).toByteArray()
        val kSessionSeed = ByteArray(16)
        for (i in 0..15) {
            kSessionSeed[i] = kIC[i] xor kIfd[i]
        }
        APDUControl.setEncryptionKeyBAC(computeKeyBAC(kSessionSeed, ENCRYPTION_KEY_VALUE_C))
        APDUControl.setEncryptionKeyMAC(computeKeyBAC(kSessionSeed, MAC_COMPUTATION_KEY_VALUE_C))
        APDUControl.setSequenceCounter((rndIC.slice(4..7).toByteArray() + rndIfd.slice(4..7).toByteArray()))
        APDUControl.useBAC = true
        return log(BAC_PROTOCOL_SUCCESS, "BAC Successful")
    }

    /**
     * Verifies the MAC of the reply APDU
     * @param c: The encrypted data of the reply APDU
     * @param mac: The MAC of the reply APDU
     * @param k: The encryption key for the MAC computation
     * @return True if the computed MAC and given MAC are equal otherwise False
     */
    private fun checkMAC(c : ByteArray, mac : ByteArray, k : ByteArray) : Boolean {
        try {
            val m = ISO9797Alg3Mac(DESEngine(), 64, ISO7816d4Padding())
            m.init(KeyParameter(k))
            m.update(c, 0, c.size)
            val out = ByteArray(8)
            m.doFinal(out, 0)
            return out.contentEquals(mac)
        } catch (e : Exception) {
            log("Exception: ${e.message}")
        }
        return false
    }

    /**
     * Decrypts a 3DES encrypted byte array in EDE mode
     * @param decrypt: The byte array to decrypt
     * @param iv: The initialization vector for the decryption
     * @param key: The decryption key for the 3DES decryption
     * @return The decrypted byte array
     */
    private fun decrypt3DES(decrypt : ByteArray, iv : ByteArray, key : ByteArray) : ByteArray {
        log("Initialize params...")
        val k = SecretKeySpec(key, "DESede")
        val i = IvParameterSpec(iv)
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        log("Initialize Cipher...")
        c.init(Cipher.DECRYPT_MODE, k, i)
        log("Decrypt...")
        return c.doFinal(decrypt)
    }

    /**
     * Computes the MAC for a given byte array with the given key
     * @param m: The byte array over which a MAC is computed
     * @param k: The key for computing the MAC
     * @return The computed MAC as byte array
     */
    private fun computeMAC(m : ByteArray, k : ByteArray) : ByteArray {
        try {
            val mac = ISO9797Alg3Mac(DESEngine(), 64, ISO7816d4Padding())
            mac.init(KeyParameter(k))
            mac.update(m, 0, m.size)
            val out = ByteArray(8)
            mac.doFinal(out, 0)
            return out
        } catch (e : Exception) {
            log("Exception: ${e.message}")
        }
        return byteArrayOf(0)
    }

    /**
     * Encrypts a byte array with 3DES with an IV and key
     * @param toEncrypt: The byte array to encrypt
     * @param iv: The initialization vector for the encryption
     * @param key: The encryption key
     * @return The encrypted byte array
     */
    private fun encrypt3DES(toEncrypt : ByteArray, iv : ByteArray, key : ByteArray) : ByteArray {
        val k = SecretKeySpec(key, "DESede")
        val i = IvParameterSpec(iv)
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, k, i)
        return c.doFinal(toEncrypt)
    }

    /**
     * Computes the cryptographic key for de- and encryption and MAC
     * @param seed: The seed for computing the key
     * @param c: A constant byte value. For the de-/encryption key, it is 1, for the MAC key it is 2
     * @return The derived cryptographic key
     */
    private fun computeKeyBAC(seed : ByteArray, c : Byte) : ByteArray {
        val keys = hash(seed + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, c))
        for (i in keys.indices) {
            if ((keys[i] and 0xFE.toByte()).countOneBits() % 2 == 0) {
                keys[i] = keys[i] or 0x1
            } else {
                keys[i] = keys[i] and 0xFE.toByte()
            }
        }
        return keys.slice(0..15).toByteArray()
    }

    /**
     * Computes the SHA-1 hash over the given byte array
     * @param hashBytes: The byte array over which the hash hashes
     * @return The hashed result over the input as byte array
     */
    private fun hash(hashBytes : ByteArray) : ByteArray {
        val md = MessageDigest.getInstance("SHA-1")
        md.update(hashBytes)
        return md.digest()
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(BAC_TAG, BAC_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @return The error code
     */
    private fun log(error : Int, msg : String) : Int {
        return Logger.log(BAC_TAG, BAC_ENABLE_LOGGING, error, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log(BAC_TAG, BAC_ENABLE_LOGGING, msg, b)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(BAC_TAG, BAC_ENABLE_LOGGING, error, msg, b)
    }
}