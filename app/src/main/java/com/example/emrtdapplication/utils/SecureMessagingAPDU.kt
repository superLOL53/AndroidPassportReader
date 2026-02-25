package com.example.emrtdapplication.utils

import com.example.emrtdapplication.constants.APDUControlConstants.APDU_NO_DATA_SIZE
import com.example.emrtdapplication.constants.APDUControlConstants.MIN_APDU_SIZE_FOR_MAC_VERIFICATION
import com.example.emrtdapplication.constants.APDUControlConstants.PADDING_SIZE
import com.example.emrtdapplication.constants.APDUControlConstants.RESPOND_CODE_SIZE
import com.example.emrtdapplication.constants.APDUControlConstants.SINGLE_KEY_SIZE_3DES
import com.example.emrtdapplication.constants.CryptoConstants.AES
import com.example.emrtdapplication.constants.CryptoConstants.AES_ECB_NO_PADDING
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.BYTE_MODULO
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.U_BYTE_MODULO
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.TlvTags.DO01
import com.example.emrtdapplication.constants.TlvTags.DO08
import com.example.emrtdapplication.constants.TlvTags.DO85
import com.example.emrtdapplication.constants.TlvTags.DO87
import com.example.emrtdapplication.constants.TlvTags.DO8E
import com.example.emrtdapplication.constants.TlvTags.DO97
import com.example.emrtdapplication.constants.TlvTags.DO97_EXTENDED_LE_LENGTH
import com.example.emrtdapplication.constants.TlvTags.DO97_LE_LENGTH
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.or

/**
 * Class for en-/decrypting secure messaging APDUs
 *
 * @property sequenceCounter The sequence counter for the APDU en-/decryption
 * @property encryptionKey The encryption key for the cipher
 * @property encryptionKeyMAC The key for the MAC calculation
 * @property isAES Whether or not to use AES cipher
 * @property apduArray The decrypted APDU as byte array
 * @property encryptedAPDUArray The encrypted APDU as byte array
 */
class SecureMessagingAPDU {
    private val sequenceCounter : ByteArray
    private val encryptionKey : ByteArray
    private val encryptionKeyMAC: ByteArray
    private val isAES : Boolean
    val apduArray : ByteArray
    val encryptedAPDUArray : ByteArray

    /**
     * Constructor for creating secure messaging APDUs from normal APDUs
     * @param sequenceCounter The sequence counter for the encryption and MAC calculation
     * @param encryptionKey The encryption key for the cipher
     * @param encryptionKeyMAC The key for the MAC calculation
     * @param isAES If AES is used as the cipher
     * @param apdu The APDU to encrypt for secure messaging
     */
    constructor(sequenceCounter : ByteArray, encryptionKey : ByteArray,
                encryptionKeyMAC: ByteArray, isAES : Boolean,
                apdu: APDU) {
        this.sequenceCounter = sequenceCounter
        this.encryptionKey = encryptionKey
        this.encryptionKeyMAC = encryptionKeyMAC
        this.isAES = isAES
        apduArray = apdu.getByteArray()
        encryptedAPDUArray = encryptAPDU(apdu)
    }

    /**
     * Constructor for decrypting received APDUs
     * @param sequenceCounter The sequence counter for decryption and MAC verification
     * @param encryptionKey The key for decrypting the received APDU
     * @param encryptionKeyMAC The key for verifying the MAC
     * @param isAES If AES is used as the cipher
     * @param rApdu The received APDU to be decrypted
     */
    constructor(sequenceCounter : ByteArray, encryptionKey : ByteArray,
                encryptionKeyMAC: ByteArray, isAES : Boolean,
                rApdu: ByteArray) {
        this.sequenceCounter = sequenceCounter
        this.encryptionKey = encryptionKey
        this.encryptionKeyMAC = encryptionKeyMAC
        this.isAES = isAES
        apduArray = extractAPDU(rApdu)
        encryptedAPDUArray = rApdu
    }

    /**
     * Builds and sends encrypted APDUs. The received APDU from the eMRTD is verified, decrypted and
     * returned
     * @param apdu: The APDU to be encrypted and sent to the eMRTD
     * @return The verified and decrypted APDU received from the eMRTD
     */
    private fun encryptAPDU(apdu: APDU) : ByteArray {
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
        finalApdu += do8e + 0
        if (apdu.useLeExt || apdu.useLcExt) {
            finalApdu += 0
        }
        return finalApdu
    }

    /**
     * Constructs the header for secure messaging
     * @param apdu: The apdu for which the header is formatted
     * @return The header for secure messaging without padding
     */
    private fun headerSM(apdu: APDU) : ByteArray {
        val header = apdu.getHeader()
        header[0] = (NfcClassByte.SECURE_MESSAGING or apdu.getHeader()[0])
        return header
    }

    /**
     * Builds the byte array containing the Lc field of the APDU
     * @param length The length of the data in the APDU to be sent
     * @param useExtendedLength If extended length should be used
     * @return The Lc field of the APDU as a byte array
     */
    private fun lcField(length : Int, useExtendedLength : Boolean = false) : ByteArray {
        return if (length > U_BYTE_MODULO || useExtendedLength) {
            byteArrayOf(0, (length/U_BYTE_MODULO).toByte(), (length % U_BYTE_MODULO).toByte())
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
        if (apdu.data.isEmpty()) return null
        val paddedData = addPadding(apdu.data)
        val encryptedData = encrypt(paddedData)
        if (encryptedData == null) return null
        var do8785 : ByteArray? = null
        if (apdu.useLc) {
            do8785 = if (apdu.getHeader()[1] % 2 == 0) {
                byteArrayOf(DO87, (encryptedData.size+1).toByte(), DO01) + encryptedData
            } else {
                byteArrayOf(DO85, (encryptedData.size+1).toByte(), DO01) + encryptedData
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
                byteArrayOf(DO97, DO97_EXTENDED_LE_LENGTH, (apdu.le/U_BYTE_MODULO).toByte(), (apdu.le%U_BYTE_MODULO).toByte())
            } else {
                byteArrayOf(DO97, DO97_LE_LENGTH, apdu.le.toByte())
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
        val n = if (isAES) {
            addPadding(sequenceCounter + m)
        } else {
            addPadding(sequenceCounter + m)
        }
        val cc = computeMAC(n)
        val do8e = byteArrayOf(DO8E, DO08) + cc
        return do8e
    }

    /**
     * Extracts the APDU from the BAC encrypted APDU received from the eMRTD
     * @param rApdu: The received, encrypted APDU from the eMRTD
     * @return The decrypted APDU without padding
     */
    private fun extractAPDU(rApdu: ByteArray) : ByteArray {
        if (rApdu.size > MIN_APDU_SIZE_FOR_MAC_VERIFICATION) {
            verifyMAC(rApdu)
        }
        val normalAPDU : ByteArray = if (rApdu[0] == DO87 || rApdu[0] == DO85) {
            val l = if (rApdu[1] < 0) {
                (rApdu[1] + BYTE_MODULO) + 3
            } else {
                3
            }
            val decrypted = decrypt(rApdu.slice(l..rApdu.size-APDU_NO_DATA_SIZE).toByteArray())
            if (decrypted == null) {
                rApdu.slice(rApdu.size - RESPOND_CODE_SIZE..<rApdu.size).toByteArray()
            } else {
                Crypto.removePadding(decrypted) + rApdu.slice(rApdu.size - RESPOND_CODE_SIZE..<rApdu.size).toByteArray()
            }
        } else {
            rApdu.slice(rApdu.size-RESPOND_CODE_SIZE..<rApdu.size).toByteArray()
        }
        return normalAPDU
    }


    /**
     * Computes the MAC for the byte array
     * @param m: The byte array for which the MAC is computed
     * @return The computed MAC of the input
     */
    private fun computeMAC(m : ByteArray) : ByteArray {
        return if (isAES) {
            Crypto.computeCMAC(m, encryptionKeyMAC)
        } else {
            Crypto.computeMAC(m, encryptionKeyMAC, usePadding = false)
        }
    }

    /**
     * Verifies the MAC of a received APDU
     * @param rApdu: The received APDU from the eMRTD
     * @return True if the MAC verification succeeds, otherwise False
     */
    private fun verifyMAC(rApdu : ByteArray) : Boolean {
        val n = addPadding(sequenceCounter + rApdu.slice(0..rApdu.size-MIN_APDU_SIZE_FOR_MAC_VERIFICATION))
        val cc = computeMAC(n)
        return cc.contentEquals(rApdu.slice(rApdu.size-(MIN_APDU_SIZE_FOR_MAC_VERIFICATION-RESPOND_CODE_SIZE-1)..<rApdu.size-RESPOND_CODE_SIZE).toByteArray())
    }

    /**
     * Encrypts the incoming byte array
     * @param bytes: The byte array to be encrypted with BAC
     * @return The encrypted byte array
     */
    private fun encrypt(bytes: ByteArray) : ByteArray? {
        return if (isAES) {
            Crypto.cipherAES(bytes, encryptionKey, Cipher.ENCRYPT_MODE, computeAESIV())
        } else {
            Crypto.cipher3DES(bytes, encryptionKey + encryptionKey.slice(0..<SINGLE_KEY_SIZE_3DES).toByteArray())
        }
    }

    /**
     * Decrypts the incoming byte array
     * @param bytes The ByteArray to be decrypted with AES or 3DES
     * @return The decrypted byte array
     */
    private fun decrypt(bytes: ByteArray) : ByteArray? {
        return if (isAES) {
            Crypto.cipherAES(bytes, encryptionKey, Cipher.DECRYPT_MODE, computeAESIV())
        } else {
            Crypto.cipher3DES(bytes, encryptionKey + encryptionKey.slice(0..<SINGLE_KEY_SIZE_3DES).toByteArray(), Cipher.DECRYPT_MODE)
        }
    }

    /**
     * Adds padding to the byte array
     * @param byteArray: The byte array to be padded
     * @return The padded byte array
     */
    private fun addPadding(byteArray: ByteArray) : ByteArray {
        return if (isAES) {
            Crypto.addPadding(byteArray, encryptionKey.size)
        } else {
            Crypto.addPadding(byteArray, PADDING_SIZE)
        }
    }

    /**
     * Computes the IV parameter for the AES de-/encryption for secure messaging APDUs
     * @return IV parameter for de-/encrypting the APDUs
     */
    private fun computeAESIV() : ByteArray {
        val k = SecretKeySpec(encryptionKey, AES)
        val c = Cipher.getInstance(AES_ECB_NO_PADDING)
        c.init(Cipher.ENCRYPT_MODE, k)
        return c.doFinal(sequenceCounter)
    }
}