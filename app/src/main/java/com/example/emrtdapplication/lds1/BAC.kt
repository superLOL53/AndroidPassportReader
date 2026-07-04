package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.SUCCESS
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.DES_KEY_SIZE
import com.example.emrtdapplication.utils.SHA_1
import java.security.SecureRandom
import javax.crypto.Cipher
import kotlin.experimental.xor

/**
 * Additional seed value for encryption key computation
 */
const val ENCRYPTION_KEY_VALUE_C: Byte = 1

/**
 * Additional seed value for MAC key computation
 */
const val MAC_COMPUTATION_KEY_VALUE_C: Byte = 2

/**
 * Successful execution of the BAC protocol
 */
const val BAC_PROTOCOL_SUCCESS = 0

/**
 * Error code for BAC protocol execution attempt without MRZ information
 */
const val ERROR_UNINITIALIZED_MRZ_INFORMATION = -1

/**
 * Error code for failure to request/get a nonce from the eMRTD
 */
const val ERROR_NONCE_REQUEST_FAILED = -2

/**
 * General error code for BAC protocol failure
 */
const val ERROR_BAC_PROTOCOL_FAILED = -3

/**
 * eMRTD returned invalid MAC during BAC protocol execution
 */
const val ERROR_INVALID_MAC = -4

/**
 * eMRTD returned invalid nonce during BAC protocol execution
 */
const val ERROR_INVALID_NONCE = -5

/**
 * No MRZ given for BAC protocol initialization
 */
const val ERROR_NO_MRZ = -6

const val RANDOM_NUMBER_BYTE_LENGTH = 8
const val SEQUENCE_COUNTER_START_INDEX = 4
const val SEQUENCE_COUNTER_END_INDEX = 7
const val BAC_KEY_LENGTH = 16
const val SESSION_SEED_SIZE = 16
const val MAC_START_INDEX = 32
const val MAC_END_INDEX = 39
const val ENCRYPTED_DATA_END_INDEX = 31
const val BAC_LAST_RESPONSE_APDU_SIZE = 40
const val RANDOM_IC_END_INDEX = 15
const val KEYING_MATERIAL_START_INDEX = 16
const val KEYING_MATERIAL_END_INDEX = 31
const val RANDOM_IFD_START_INDEX = 8
const val RANDOM_IFD_END_INDEX = 15
const val KEYING_MATERIAL_IFD_SIZE = 16
const val CONCATENATION_SIZE = 32

/**
 * Implements the Basic Access Control (BAC) protocol
 *
 * @property random Secure random number generator
 * @property mrzInformation The MRZ of the eMRTD
 */
class BAC(private var random: SecureRandom? = SecureRandom()) {
    private var mrzInformation: String? = null

    /**
     * Initializes the BAC protocol with the MRZ information from the manual input
     *
     * @param newMRZ: The MRZ information used to derive the
     * cryptographic keys for the BAC protocol
     * @return [SUCCESS] or [ERROR_NO_MRZ] if no MRZ is given
     */
    fun init(newMRZ: String?): Int {
        if (newMRZ.isNullOrEmpty()) {
            return ERROR_NO_MRZ
        }
        mrzInformation = newMRZ
        return SUCCESS
    }

    /**
     * Implements the BAC protocol, derives the cryptographic keys and
     * stores them in the APDUControl class for further application. The
     * LDS1 application has to be selected before BAC can be run.
     *
     * @return [BAC_PROTOCOL_SUCCESS] if protocol was successful or a negative error code
     */
    fun bacProtocol(): Int {
        if (mrzInformation == null) {
            return ERROR_UNINITIALIZED_MRZ_INFORMATION
        }
        var info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.REQUEST_RANDOM_NUMBER,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                RANDOM_NUMBER_BYTE_LENGTH
            )
        )
        if (!APDUControl.checkResponse(info)) {
            return ERROR_NONCE_REQUEST_FAILED
        }
        val rndIfd = if (random == null) {
            byteArrayOf(0x78, 0x17, 0x23, 0x86.toByte(), 0x0C, 0x06, 0xC2.toByte(), 0x26)
        } else {
            val rand = ByteArray(RANDOM_NUMBER_BYTE_LENGTH)
            random!!.nextBytes(rand)
            rand
        }
        val kIfd = if (random == null) {
            byteArrayOf(
                0x0B, 0x79, 0x52, 0x40,
                0xCB.toByte(), 0x70, 0x49, 0xB0.toByte(),
                0x1C, 0x19, 0xB3.toByte(), 0x3E,
                0x32, 0x80.toByte(), 0x4F, 0x0B
            )
        } else {
            val rand = ByteArray(KEYING_MATERIAL_IFD_SIZE)
            random!!.nextBytes(rand)
            rand
        }
        val s = ByteArray(CONCATENATION_SIZE)
        for (i in s.indices) {
            if (i < RANDOM_NUMBER_BYTE_LENGTH) {
                s[i] = rndIfd[i]
            } else if (i < KEYING_MATERIAL_IFD_SIZE) {
                s[i] = info[i-RANDOM_NUMBER_BYTE_LENGTH]
            } else {
                s[i] = kIfd[i-KEYING_MATERIAL_IFD_SIZE]
            }
        }
        val hash = Crypto.hash(
            SHA_1,
            mrzInformation!!.toByteArray()
        )
        val kSeed = hash?.slice(
            0..<SESSION_SEED_SIZE
        )?.toByteArray() ?: return ERROR_BAC_PROTOCOL_FAILED
        var kEnc = Crypto.computeKey(
            SHA_1,
            kSeed,
            ENCRYPTION_KEY_VALUE_C,
            true
        )?.
            slice(0..<BAC_KEY_LENGTH)?.
            toByteArray() ?: return ERROR_BAC_PROTOCOL_FAILED
        kEnc += kEnc.slice(0..<DES_KEY_SIZE).toByteArray()
        var kMAC =
            Crypto.computeKey(
                SHA_1,
                kSeed,
                MAC_COMPUTATION_KEY_VALUE_C,
                true
            )?.
            slice(0..<BAC_KEY_LENGTH)?.
            toByteArray() ?: return ERROR_BAC_PROTOCOL_FAILED
        kMAC += kMAC.slice(0..<DES_KEY_SIZE).toByteArray()
        val eIfd = Crypto.cipher3DES(
            s,
            kEnc
        ) ?: return ERROR_BAC_PROTOCOL_FAILED
        val mIfd = Crypto.computeMAC(eIfd, kMAC)
        info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.EXTERNAL_AUTHENTICATE,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                eIfd + mIfd,
                BAC_LAST_RESPONSE_APDU_SIZE
            )
        )
        if (!APDUControl.checkResponse(info)) {
            return ERROR_BAC_PROTOCOL_FAILED
        }
        info = APDUControl.removeRespondCodes(info)
        if (info.size != BAC_LAST_RESPONSE_APDU_SIZE) {
            return ERROR_BAC_PROTOCOL_FAILED
        }
        val encData = info.slice(0..ENCRYPTED_DATA_END_INDEX).toByteArray()
        val mIC = info.slice(MAC_START_INDEX..MAC_END_INDEX).toByteArray()
        if (!Crypto.checkMAC(encData, mIC, kMAC)) {
            return ERROR_INVALID_MAC
        }
        val con = Crypto.cipher3DES(encData, kEnc, Cipher.DECRYPT_MODE)
            ?: return ERROR_BAC_PROTOCOL_FAILED
        if (
            !con.slice(
                RANDOM_IFD_START_INDEX..RANDOM_IFD_END_INDEX
            ).toByteArray().contentEquals(rndIfd)
        ) {
            return ERROR_INVALID_NONCE
        }
        val rndIC = con.slice(0..RANDOM_IC_END_INDEX).toByteArray()
        val kIC =
            con.slice(
                KEYING_MATERIAL_START_INDEX..KEYING_MATERIAL_END_INDEX
            ).toByteArray()
        val kSessionSeed = ByteArray(SESSION_SEED_SIZE)
        for (i in 0..<SESSION_SEED_SIZE) {
            kSessionSeed[i] = kIC[i] xor kIfd[i]
        }
        val encryptionKeyBAC =
            Crypto.computeKey(
                SHA_1,
                kSessionSeed,
                ENCRYPTION_KEY_VALUE_C,
                true
            )?.
            slice(0..<BAC_KEY_LENGTH)?.
            toByteArray() ?: return ERROR_BAC_PROTOCOL_FAILED
        APDUControl.setEncryptionKeyBAC(encryptionKeyBAC)
        val encryptionKeyMAC =
            Crypto.computeKey(
                SHA_1,
                kSessionSeed,
                MAC_COMPUTATION_KEY_VALUE_C,
                true
            )?.
            slice(0..<BAC_KEY_LENGTH)?.
            toByteArray() ?: return ERROR_BAC_PROTOCOL_FAILED
        APDUControl.setEncryptionKeyMAC(encryptionKeyMAC)
        APDUControl.setSequenceCounter(
            (rndIC.
            slice(
                SEQUENCE_COUNTER_START_INDEX..
                        SEQUENCE_COUNTER_END_INDEX
            ).
            toByteArray() +
                    rndIfd.
                    slice(
                        SEQUENCE_COUNTER_START_INDEX..
                                SEQUENCE_COUNTER_END_INDEX
                    ).
                    toByteArray())
        )
        APDUControl.sendEncryptedAPDU = true
        APDUControl.isAES = false
        return BAC_PROTOCOL_SUCCESS
    }
}