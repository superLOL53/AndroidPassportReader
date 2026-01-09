package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.INVALID_ARGUMENT
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.constants.ZERO_BYTE
import org.spongycastle.asn1.x9.ECNamedCurveTable
import org.spongycastle.asn1.x9.X9ECParameters
import org.spongycastle.crypto.agreement.DHStandardGroups
import org.spongycastle.crypto.params.DHParameters
import org.spongycastle.crypto.params.DHPrivateKeyParameters
import org.spongycastle.crypto.params.DHPublicKeyParameters
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import java.math.BigInteger
import java.security.SecureRandom
import javax.crypto.Cipher

/**
 * Constants for the PACE class
 */
//const val PACE_TAG = "PACE"
//const val PACE_ENABLE_LOGGING = true
const val NO_PASSWORD = -1
const val NO_PACE_OID = -2
const val INVALID_MSE_COMMAND = -3
const val INVALID_GENERAL_AUTHENTICATE = -4
const val INVALID_NONCE = -5
const val POSITIVE_NUMBER = 1
/**
 * Implements the PACE protocol.
 *
 * @property apduControl Used for sending and receiving APDUs
 * @property crypto Used for cryptographic operations
 * @property mrzInformation The MRZ information of the eMRTD
 * @property useCAN If the CAN is used for deriving keys
 * @property useLongConstants Indicates if integrated mapping uses the long(256 bits) or short(128 bits) c0 and c1 constants
 * @property idPACEOid The object identifier of the PACE protocol to use
 * @property encKey The encryption key
 * @property macKey The MAC key
 * @property parameters The ID of the parameters to use
 * @property chipAuthenticationData The data for the chip authentication protocol. Only used with PACE-CAM.
 * @property chipPublicKey The public key for the chip authentication protocol. Only used with PACE-CAM.
 */
//TODO: Documentation
class PACE(private var apduControl: APDUControl, private val crypto : Crypto = Crypto(), private val random: SecureRandom? = SecureRandom()) {
    private var mrzInformation : String? = null
    private var useCAN = false
    private var useLongConstants = false
    private var idPACEOid : ByteArray? = null
    private var encKey : ByteArray? = null
    private var macKey : ByteArray? = null
    private var parameters : Byte = -1
    var chipAuthenticationData: ByteArray? = null
        private set
    var chipPublicKey: ECPublicKeyParameters? = null
        private set

    /**
     * Initializes the PACE protocol with the MRZ information or CAN from the manual input.
     * @param mrz: The MRZ information from the manual input
     * @param can: The CAN from the manual input
     * @param paceOid: The supported cryptographic PACE protocol
     * @return [SUCCESS] or error code indicating a failure
     */
    fun init(mrz :String?, can : Boolean, paceOid : ByteArray?, parameters: Byte): Int {
        mrzInformation = null
        idPACEOid = null
        this.parameters = parameters
        if (paceOid == null) {
            return NO_PACE_OID
        } else {
            idPACEOid = paceOid
        }
        mrzInformation = mrz
        if (mrz == null) {
            return NO_PASSWORD
        } else if (!can) {
            useCAN = false
            return SUCCESS
        } else {
            useCAN = true
            return SUCCESS
        }
    }

    /**
     * Implements the PACE protocol.
     * @return [SUCCESS] or error code indicating a failure
     */
    fun paceProtocol() : Int {
        if (idPACEOid == null) {
            return NO_PACE_OID
        }
        var info = byteArrayOf(TlvTags.CRYPTOGRAPHIC_REFERENCE, idPACEOid!!.size.toByte()) + idPACEOid!! + byteArrayOf(
            TlvTags.KEY_REFERENCE, 0x01)
        info += if (mrzInformation == null) {
            return NO_PASSWORD
        } else if (useCAN) {
            0x02
        } else {
            0x01
        }
        val key = crypto.hash("SHA-1", mrzInformation!!.toByteArray())
        computeKeys(key, 3)
        info = apduControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.MANAGE_SECURITY_ENVIRONMENT, NfcP1Byte.SET_AUTHENTICATION_TEMPLATE, NfcP2Byte.SET_AUTHENTICATION_TEMPLATE, info))
        if (!apduControl.checkResponse(info)) {
            return INVALID_MSE_COMMAND
        }
        info = byteArrayOf(TlvTags.NONCE_QUERY, ZERO_BYTE)
        try {
            info = apduControl.sendAPDU(
                APDU(
                    NfcClassByte.COMMAND_CHAINING,
                    NfcInsByte.GENERAL_AUTHENTICATE,
                    ZERO_BYTE,
                    ZERO_BYTE,
                    info,
                    256
                )
            )
            if (!apduControl.checkResponse(info)) {
                return INVALID_GENERAL_AUTHENTICATE
            }
        } catch (_ : Exception) {
            return FAILURE
        }
        val z = TLV(apduControl.removeRespondCodes(info)).list!!.tlvSequence[0].value
            ?: return INVALID_NONCE
        val s = decryptNonce(z) ?: return INVALID_ARGUMENT
        return when (idPACEOid!![idPACEOid!!.size-2]) {
            DH_GM -> paceDHGM(s)
            DH_IM -> paceDHIM(s)
            ECDH_GM -> paceECGM(s)
            ECDH_IM -> paceECIM(s)
            ECDH_CAM -> paceECCAM(s)
            else -> return INVALID_ARGUMENT
        }
    }

    /**
     * Implements the PACE EC GM protocol
     * @param nonce The nonce used for generic mapping
     * @return [SUCCESS] if protocol was successful, otherwise [FAILURE]
     */
    private fun paceECGM(nonce: ByteArray) : Int {
        val params = getECParams() ?: return INVALID_ARGUMENT
        var domainParameters = ECDomainParameters(params.curve, params.g, params.n, params.h)
        var keys = crypto.generateECKeyPair(domainParameters)
        var publicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TlvTags.MAPPING_DATA)
        val sa = crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, publicKey)
        val h = crypto.getECPointFromBigInteger(sa, domainParameters)
        val g = crypto.genericMappingEC(domainParameters.g, nonce, h)
        domainParameters = ECDomainParameters(domainParameters.curve, g, domainParameters.n, domainParameters.h)
        keys = crypto.generateECKeyPair(domainParameters)
        publicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TlvTags.PUBLIC_KEY)
        val sharedSecret = crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as ECPublicKeyParameters, publicKey)
    }

    /**
     * Implements the PACE DH GM protocol
     * @param nonce The nonce used for generic mapping
     * @return [SUCCESS] if protocol was successful, otherwise [FAILURE]
     */
    private fun paceDHGM(nonce: ByteArray) : Int {
        var params = getDHParams() ?: return INVALID_ARGUMENT
        var keys = crypto.generateDHKeyPair(params)
        var publicKey = exchangeKeys(keys.public as DHPublicKeyParameters, TlvTags.MAPPING_DATA)
        val h = crypto.calculateDHAgreement(keys.private as DHPrivateKeyParameters, publicKey)
        val g = crypto.genericMappingDH(params.g, nonce, params.p, h)
        params = DHParameters(params.p, g, params.q)
        keys = crypto.generateDHKeyPair(params)
        publicKey = exchangeKeys(keys.public as DHPublicKeyParameters, TlvTags.PUBLIC_KEY)
        val sharedSecret = crypto.calculateDHAgreement(keys.private as DHPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as DHPublicKeyParameters, publicKey)
    }

    /**
     * Implements the PACE EC IM protocol
     * @param nonce The nonce used for generic mapping
     * @return [SUCCESS] if protocol was successful, otherwise [FAILURE]
     */
    private fun paceECIM(nonce: ByteArray) : Int {
        val params = getECParams() ?: return INVALID_ARGUMENT
        var domainParams = ECDomainParameters(params.curve, params.g, params.n, params.h)
        var t = ByteArray(nonce.size)
        if (random == null) {
            t = byteArrayOf(0x5D, 0xD4.toByte(), 0xCB.toByte(), 0xFC.toByte(), 0x96.toByte(), 0xF5.toByte(), 0x45, 0x3B, 0x13, 0x0D, 0x89.toByte(), 0x0A, 0x1C, 0xDB.toByte(), 0xAE.toByte(), 0x32)
        } else {
            random.nextBytes(t)
        }
        sendNonce(t)
        val r = crypto.integratedMappingPRNG(nonce, t, params.curve.field.characteristic, useLongConstants)
        val x = crypto.integratedMappingEC(r, params.curve.a.toBigInteger(), params.curve.b.toBigInteger(), params.curve.field.characteristic)
        val g = crypto.getECPointFromBigInteger(x, domainParams)
        domainParams = ECDomainParameters(params.curve, g, params.n, params.h)
        val keys = crypto.generateECKeyPair(domainParams)
        val publicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TlvTags.PUBLIC_KEY)
        val sharedSecret = crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as ECPublicKeyParameters, publicKey)
    }

    /**
     * Implements the PACE DH IM protocol
     * @param nonce The nonce used for generic mapping
     * @return [SUCCESS] if protocol was successful, otherwise [FAILURE]
     */
    private fun paceDHIM(nonce: ByteArray) : Int {
        var params = getDHParams() ?: return INVALID_ARGUMENT
        var t = ByteArray(nonce.size)
        if (random == null) {
            t = byteArrayOf(0xB3.toByte(), 0xA6.toByte(), 0xDB.toByte(), 0x3C, 0x87.toByte(), 0x0C, 0x3E, 0x99.toByte(), 0x24, 0x5E, 0x0D, 0x1C, 0x06, 0xB7.toByte(), 0x47, 0xDE.toByte())
        } else {
            random.nextBytes(t)
        }
        sendNonce(t)
        val r = crypto.integratedMappingPRNG(nonce, t, params.p, useLongConstants)
        val g = crypto.integratedMappingDH(r, params.p, params.q)
        params = DHParameters(params.p, g, params.q)
        val keys = crypto.generateDHKeyPair(params)
        val publicKey = exchangeKeys(keys.public as DHPublicKeyParameters, TlvTags.PUBLIC_KEY)
        val sharedSecret = crypto.calculateDHAgreement(keys.private as DHPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as DHPublicKeyParameters, publicKey)
    }


    /**
     * Implements the PACE EC CAM protocol
     * @param nonce The nonce used for generic mapping
     * @return [SUCCESS] if protocol was successful, otherwise [FAILURE]
     */
    private fun paceECCAM(nonce: ByteArray) : Int {
        val params = getECParams() ?: return INVALID_ARGUMENT
        var domainParams = ECDomainParameters(params.curve, params.g, params.n, params.h)
        var keys = crypto.generateECKeyPair(domainParams)
        chipPublicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TlvTags.MAPPING_DATA)
        val sa = crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, chipPublicKey!!)
        val h = crypto.getECPointFromBigInteger(sa, domainParams)
        val g = crypto.genericMappingEC(domainParams.g, nonce, h)
        domainParams = ECDomainParameters(params.curve, g, params.n, params.h)
        keys = crypto.generateECKeyPair(domainParams)
        val publicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TlvTags.PUBLIC_KEY)
        val sharedSecret = crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as ECPublicKeyParameters, publicKey, true)
    }

    /**
     * Get the DH parameters associated with the [parameters] ID or null
     * @return [DHParameters] or null
     */
    private fun getDHParams() : DHParameters? {
        return when (parameters) {
            MOD_P_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP -> return DHStandardGroups.rfc5114_1024_160
            MOD_P_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP -> return DHStandardGroups.rfc5114_2048_224
            MOD_P_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP -> return DHStandardGroups.rfc5114_2048_256
            else -> null
        }
    }
    /**
     * Get the EC parameters associated with the [parameters] ID or null
     * @return [X9ECParameters] or null
     */
    private fun getECParams() : X9ECParameters? {
        return when (parameters) {
            NIST_P192 -> ECNamedCurveTable.getByName("P-192")
            NIST_P224 -> ECNamedCurveTable.getByName("P-224")
            NIST_P256 -> ECNamedCurveTable.getByName("P-256")
            NIST_P384 -> ECNamedCurveTable.getByName("P-384")
            NIST_P521 -> ECNamedCurveTable.getByName("P-521")
            BRAIN_POOL_P192R1 -> ECNamedCurveTable.getByName("brainpoolp192r1")
            BRAIN_POOL_P224R1 -> ECNamedCurveTable.getByName("brainpoolp224r1")
            BRAIN_POOL_P256R1 -> ECNamedCurveTable.getByName("brainpoolp256r1")
            BRAIN_POOL_P320R1 -> ECNamedCurveTable.getByName("brainpoolp320r1")
            BRAIN_POOL_P384R1 -> ECNamedCurveTable.getByName("brainpoolp384r1")
            BRAIN_POOL_P512R1 -> ECNamedCurveTable.getByName("brainpoolp512r1")
            else -> null
        }
    }

    /**
     * Compute the keys used for communicating with the eMRTD
     * @param secret The secret for computing the keys
     * @param seed The seed for computing the keys
     */
    private fun computeKeys(secret: ByteArray, seed : Byte = 1) {
        val key = if (secret[0] == 0.toByte()) {
            secret.slice(1..<secret.size).toByteArray()
        } else {
            secret
        }
        when (idPACEOid!![idPACEOid!!.size-1]) {
            DES_CBC_CBC -> {
                apduControl.isAES = false
                encKey = crypto.computeKey("SHA-1", key, seed, true).slice(0..15).toByteArray()
                encKey = encKey!! + encKey!!.slice(0..7).toByteArray()
                macKey = crypto.computeKey("SHA-1", key, 2, true).slice(0..15).toByteArray()
                macKey = macKey!! + macKey!!.slice(0..7).toByteArray()
            }
            AES_CBC_CMAC_128 -> {
                apduControl.isAES = true
                encKey = crypto.computeKey("SHA-1", key, seed).slice(0..15).toByteArray()
                macKey = crypto.computeKey("SHA-1", key, 2).slice(0..15).toByteArray()

            }
            AES_CBC_CMAC_192 -> {
                useLongConstants = true
                apduControl.isAES = true
                encKey = crypto.computeKey("SHA-256", key, seed).slice(0..23).toByteArray()
                macKey = crypto.computeKey("SHA-256", key, 2).slice(0..23).toByteArray()

            }
            AES_CBC_CMAC_256 -> {
                useLongConstants = true
                apduControl.isAES = true
                encKey = crypto.computeKey("SHA-256", key, seed)
                macKey = crypto.computeKey("SHA-256", key, 2)
            }
        }
    }

    /**
     * Generate and check the tokens for PACE with EC
     * @param publicKey The public key from the phone
     * @param chipPublicKey The public key from the eMRTD
     * @param isCAM Indicates if PACE with CAM is used
     */
    private fun tokenAuthentication(publicKey: ECPublicKeyParameters, chipPublicKey: ECPublicKeyParameters, isCAM: Boolean = false) : Int {
        val token = generateTokenData(publicKey)
        val chipToken = generateTokenData(chipPublicKey)
        return checkToken(token, chipToken, isCAM)
    }

    /**
     * Generate and check the tokens for PACE with DH
     * @param publicKey The public key from the phone
     * @param chipPublicKey The public key from the eMRTD
     */
    private fun tokenAuthentication(publicKey: DHPublicKeyParameters, chipPublicKey: DHPublicKeyParameters) : Int {
        val token = generateTokenData(publicKey)
        val chipToken = generateTokenData(chipPublicKey)
        return checkToken(token, chipToken)
    }

    /**
     * Checks the generated and exchanged tokens
     * @param token The token generated by the phone
     * @param chipToken The token generated by the eMRTD
     * @param isCAM Indicates if PACE with CAM is used
     */
    private fun checkToken(token: ByteArray, chipToken: ByteArray, isCAM: Boolean = false) : Int {
        var data = TLV(TlvTags.TERMINAL_AUTHENTICATION_TOKEN, computeToken(chipToken))
        data = TLV(TlvTags.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        val info = apduControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.GENERAL_AUTHENTICATE, NfcP1Byte.ZERO, NfcP2Byte.ZERO, data.toByteArray(), 256))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        data = TLV(apduControl.removeRespondCodes(info))
        val receivedToken = data.list!!.tlvSequence[0].value
        if (isCAM) {
            chipAuthenticationData = data.list?.tlvSequence?.get(1)?.value
            var iv = ByteArray(encKey!!.size)
            iv.fill(-1)
            iv = crypto.cipherAES(iv, encKey!!, Cipher.ENCRYPT_MODE, ByteArray(encKey!!.size))
            chipAuthenticationData = crypto.cipherAES(chipAuthenticationData!!, encKey!!, Cipher.DECRYPT_MODE, iv)
            chipAuthenticationData = crypto.removePadding(chipAuthenticationData!!)
        }
        if (receivedToken.contentEquals(computeToken(token))) {
            apduControl.sendEncryptedAPDU = true
            apduControl.setEncryptionKeyBAC(encKey!!)
            apduControl.setEncryptionKeyMAC(macKey!!)
            return SUCCESS
        } else {
            return FAILURE
        }
    }

    /**
     * Computes the token for the byte array
     * @param byteArray The byte array for which the token is computed
     * @return The token as a ByteArray
     */
    private fun computeToken(byteArray: ByteArray) : ByteArray {
        return if (idPACEOid!![idPACEOid!!.size-1] == DES_CBC_CBC) {
            crypto.computeMAC(byteArray, macKey!!)
        } else {
            crypto.computeCMAC(byteArray, macKey!!)
        }
    }

    /**
     * Generates input data for the token computation
     * @param publicKey The public key for the token computation
     * @return Input data for the token computation
     */
    private fun generateTokenData(publicKey: ECPublicKeyParameters) : ByteArray {
        val oid = TLV(TlvTags.OID, idPACEOid!!)
        var x = publicKey.q.xCoord.toBigInteger().toByteArray()
        if (x[0] == 0.toByte()) {
            x = x.slice(1..<x.size).toByteArray()
        }
        var y = publicKey.q.yCoord.toBigInteger().toByteArray()
        if (y[0] == 0.toByte()) {
            y = y.slice(1..<y.size).toByteArray()
        }
        val pub = TLV(TlvTags.EC_PUBLIC_POINT, byteArrayOf(0x04) + x + y)
        return TLV(byteArrayOf(0x7F, 0x49), oid.toByteArray() + pub.toByteArray()).toByteArray()
    }

    /**
     * Generates input data for the token computation
     * @param publicKey The public key for the token computation
     * @return Input data for the token computation
     */
    private fun generateTokenData(publicKey: DHPublicKeyParameters) : ByteArray {
        val oid = TLV(TlvTags.OID, idPACEOid!!)
        var key = publicKey.y.toByteArray()
        if (key[0] == 0.toByte()) {
            key = key.slice(1..<key.size).toByteArray()
        }
        val pub = TLV(TlvTags.UNSIGNED_INTEGER, key)
        return TLV(byteArrayOf(0x7F, 0x49), oid.toByteArray()+pub.toByteArray()).toByteArray()
    }

    /**
     * Exchange EC public keys with the eMRTD
     * @param publicKey The public key to send to the eMRTD
     * @return The EC public key of the eMRTD
     */
    private fun exchangeKeys(publicKey: ECPublicKeyParameters, tag: Byte) : ECPublicKeyParameters {
        var data = TLV(tag, publicKey.q.getEncoded(false))
        data = TLV(TlvTags.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        val response = TLV(apduControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, data.toByteArray(), 256)))
        return ECPublicKeyParameters(publicKey.parameters.curve.decodePoint(response.list!!.tlvSequence[0].value!!), publicKey.parameters)
    }

    /**
     * Exchange DH public keys with the eMRTD
     * @param publicKey The public key to send to the eMRTD
     * @return The DH public key of the eMRTD
     */
    private fun exchangeKeys(publicKey: DHPublicKeyParameters, tag: Byte) : DHPublicKeyParameters {
        var key = publicKey.y.toByteArray()
        if (key[0] == 0.toByte()) {
            key = key.slice(1..<key.size).toByteArray()
        }
        var data = TLV(tag, key)
        data = TLV(TlvTags.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        val response = TLV(apduControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, data.toByteArray(), 256)))
        return DHPublicKeyParameters(BigInteger(POSITIVE_NUMBER, response.list!!.tlvSequence[0].value!!), publicKey.parameters)
    }

    /**
     * Sends a nonce to the eMRTD
     * @param nonce The nonce to send to the eMRTD
     */
    private fun sendNonce(nonce: ByteArray) {
        var data = TLV(TlvTags.MAPPING_DATA, nonce)
        data = TLV(TlvTags.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        apduControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, data.toByteArray(), 256))
    }

    /**
     * Decrypts a nonce received from the eMRTD
     * @param nonce The nonce to decrypt
     * @return The decrypted nonce or null
     */
    private fun decryptNonce(nonce: ByteArray) : ByteArray? {
        return when(idPACEOid!![idPACEOid!!.size-1]) {
            AES_CBC_CMAC_128 -> crypto.cipherAES(nonce, encKey!!, Cipher.DECRYPT_MODE, ByteArray(16))
            AES_CBC_CMAC_192 -> crypto.cipherAES(nonce, encKey!!, Cipher.DECRYPT_MODE)
            AES_CBC_CMAC_256 -> crypto.cipherAES(nonce, encKey!!, Cipher.DECRYPT_MODE)
            DES_CBC_CBC -> crypto.cipher3DES(nonce, encKey!!, Cipher.DECRYPT_MODE)
            else -> null
        }
    }
}