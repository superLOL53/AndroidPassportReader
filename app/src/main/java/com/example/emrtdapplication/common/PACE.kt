package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.INVALID_ARGUMENT
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLV_TAGS
import com.example.emrtdapplication.utils.ZERO_BYTE
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
const val POSITIVE_NUMBER = 1
/**
 * Class implementing the PACE protocol.
 */
class PACE(private var apduControl: APDUControl) {
    private var mrzInformation : String? = null
    private var useCAN = false
    private var idPACEOid : ByteArray? = null
    private var encKey : ByteArray? = null
    private var macKey : ByteArray? = null
    private var parameters : Byte = -1

    /**
     * Initializes the PACE protocol with the MRZ information or CAN from the manual input.
     * @param mrz: The MRZ information from the manual input
     * @param can: The CAN from the manual input
     * @param paceOid: The supported cryptographic PACE protocol
     * @return Success (0) or error code indicating a failure
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
     * @return Success(0) or error code indicating a failure
     */
    fun paceProtocol() : Int {
        if (idPACEOid == null) {
            return NO_PACE_OID
        }
        var info = byteArrayOf(TLV_TAGS.CRYPTOGRAPHIC_REFERENCE, idPACEOid!!.size.toByte()) + idPACEOid!! + byteArrayOf(
            TLV_TAGS.KEY_REFERENCE, 0x01)
        if (mrzInformation == null) {
            return NO_PASSWORD
        } else if (useCAN) {
            info += 0x02
        } else {
            info += 0x01
        }
        var key = Crypto.hash("SHA-1", mrzInformation!!.toByteArray())
        key = Crypto.computeKey("SHA-1", key, 3).slice(0..15).toByteArray()
        info = apduControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.MANAGE_SECURITY_ENVIRONMENT, NfcP1Byte.SET_AUTHENTICATION_TEMPLATE, NfcP2Byte.SET_AUTHENTICATION_TEMPLATE, info))
        if (!apduControl.checkResponse(info)) {
            return INVALID_MSE_COMMAND
        }
        info = byteArrayOf(TLV_TAGS.NONCE_QUERY, ZERO_BYTE)
        //info = ByteArray(0)
        info = apduControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, info, 256))
        if (!apduControl.checkResponse(info)) {
            return INVALID_GENERAL_AUTHENTICATE
        }
        val z = TLV(apduControl.removeRespondCodes(info)).getTLVSequence()!!.getTLVSequence()[0].getValue()
            ?: return INVALID_NONCE
        val s = Crypto.decrypt3DES(z, key)
        return when (idPACEOid!![idPACEOid!!.size-2]) {
            DH_GM -> paceDHGM(s)
            DH_IM -> paceDHIM(s)
            ECDH_GM -> paceECGM(s)
            ECDH_IM -> paceECIM(s)
            ECDH_CAM -> paceECCAM(s)
            else -> return INVALID_ARGUMENT
        }
    }

    private fun paceECGM(nonce: ByteArray) : Int {
        val params = getECParams() ?: return INVALID_ARGUMENT
        var domainParameters = ECDomainParameters(params.curve, params.g, params.n, params.h)
        var keys = Crypto.generateECKeyPair(domainParameters)
        var publicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TLV_TAGS.MAPPING_DATA)
        val sa = Crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, publicKey)
        val h = Crypto.getECPointFromBigInteger(sa, domainParameters)
        val g = Crypto.genericMappingEC(domainParameters.g, nonce, h)
        domainParameters = ECDomainParameters(domainParameters.curve, g, domainParameters.n, domainParameters.h)
        keys = Crypto.generateECKeyPair(domainParameters)
        publicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TLV_TAGS.PUBLIC_KEY)
        val sharedSecret = Crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as ECPublicKeyParameters, publicKey)
    }

    private fun paceDHGM(nonce: ByteArray) : Int {
        var params = getDHParams() ?: return INVALID_ARGUMENT
        var keys = Crypto.generateDHKeyPair(params)
        var publicKey = exchangeKeys(keys.public as DHPublicKeyParameters, TLV_TAGS.MAPPING_DATA)
        val h = Crypto.calculateDHAgreement(keys.private as DHPrivateKeyParameters, publicKey)
        val g = Crypto.genericMappingDH(params.g, nonce, params.p, h)
        params = DHParameters(params.p, g, params.q)
        keys = Crypto.generateDHKeyPair(params)
        publicKey = exchangeKeys(keys.public as DHPublicKeyParameters, TLV_TAGS.PUBLIC_KEY)
        val sharedSecret = Crypto.calculateDHAgreement(keys.private as DHPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as DHPublicKeyParameters, publicKey)
    }

    private fun paceECIM(nonce: ByteArray) : Int {
        val params = getECParams() ?: return INVALID_ARGUMENT
        var domainParams = ECDomainParameters(params.curve, params.g, params.n, params.h)
        val t = ByteArray(nonce.size)
        SecureRandom().nextBytes(t)
        sendNonce(t)
        val r = Crypto.integratedMappingPRNG(nonce, t, params.curve.field.characteristic)
        val x = Crypto.integratedMappingEC(r, params.curve.a.toBigInteger(), params.curve.b.toBigInteger(), params.curve.field.characteristic, params.curve.cofactor)
        val g = Crypto.getECPointFromBigInteger(x, domainParams)
        domainParams = ECDomainParameters(params.curve, g, params.n, params.h)
        val keys = Crypto.generateECKeyPair(domainParams)
        val publicKey = exchangeKeys(keys.public as ECPublicKeyParameters, TLV_TAGS.PUBLIC_KEY)
        val sharedSecret = Crypto.calculateECDHAgreement(keys.private as ECPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as ECPublicKeyParameters, publicKey)
    }

    private fun paceDHIM(nonce: ByteArray) : Int {
        var params = getDHParams() ?: return INVALID_ARGUMENT
        val t = ByteArray(nonce.size)
        SecureRandom().nextBytes(t)
        sendNonce(t)
        val r = Crypto.integratedMappingPRNG(nonce, t, params.p)
        val g = Crypto.integratedMappingDH(r, params.p, params.q)
        params = DHParameters(params.p, g, params.q)
        val keys = Crypto.generateDHKeyPair(params)
        val publicKey = exchangeKeys(keys.public as DHPublicKeyParameters, TLV_TAGS.PUBLIC_KEY)
        val sharedSecret = Crypto.calculateDHAgreement(keys.private as DHPrivateKeyParameters, publicKey)
        computeKeys(sharedSecret.toByteArray())
        return tokenAuthentication(keys.public as DHPublicKeyParameters, publicKey)
    }

    private fun paceECCAM(nonce: ByteArray) : Int {
        return NOT_IMPLEMENTED
    }

    private fun getDHParams() : DHParameters? {
        return when (parameters) {
            MODP_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP -> return DHStandardGroups.rfc5114_1024_160
            MODP_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP -> return DHStandardGroups.rfc5114_2048_224
            MODP_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP -> return DHStandardGroups.rfc5114_2048_256
            else -> null
        }
    }

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

    private fun computeKeys(secret: ByteArray) {
        val key = if (secret[0] == 0.toByte()) {
            secret.slice(1..<secret.size).toByteArray()
        } else {
            secret
        }
        when (idPACEOid!![idPACEOid!!.size-1]) {
            DES_CBC_CBC -> {
                apduControl.isAES = false
                encKey = Crypto.computeKey("SHA-1", key, 1, true).slice(0..15).toByteArray()
                macKey = Crypto.computeKey("SHA-1", key, 2, true).slice(0..15).toByteArray()
            }
            AES_CBC_CMAC_128 -> {
                apduControl.isAES = true
                encKey = Crypto.computeKey("SHA-1", key, 1).slice(0..15).toByteArray()
                macKey = Crypto.computeKey("SHA-1", key, 2).slice(0..15).toByteArray()

            }
            AES_CBC_CMAC_192 -> {
                apduControl.isAES = true
                encKey = Crypto.computeKey("SHA-256", key, 1).slice(0..23).toByteArray()
                macKey = Crypto.computeKey("SHA-256", key, 2).slice(0..23).toByteArray()

            }
            AES_CBC_CMAC_256 -> {
                apduControl.isAES = true
                encKey = Crypto.computeKey("SHA-256", key, 1)
                macKey = Crypto.computeKey("SHA-256", key, 2)
            }
        }
    }

    private fun tokenAuthentication(publicKey: ECPublicKeyParameters, chipPublicKey: ECPublicKeyParameters) : Int {
        val token = generateToken(publicKey)
        val chipToken = generateToken(chipPublicKey)
        return checkToken(token, chipToken)
    }

    private fun tokenAuthentication(publicKey: DHPublicKeyParameters, chipPublicKey: DHPublicKeyParameters) : Int {
        val token = generateToken(publicKey)
        val chipToken = generateToken(chipPublicKey)
        return checkToken(token, chipToken)
    }

    private fun checkToken(token: ByteArray, chipToken: ByteArray) : Int {
        var data = TLV(TLV_TAGS.TERMINAL_AUTHENTICATION_TOKEN, Crypto.computeCMAC(chipToken, macKey!!))
        data = TLV(TLV_TAGS.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        val info = apduControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.GENERAL_AUTHENTICATE, NfcP1Byte.ZERO, NfcP2Byte.ZERO, data.toByteArray(), 256))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        if (info.contentEquals(Crypto.computeCMAC(token, macKey!!))) {
            apduControl.sendEncryptedAPDU = true
            return SUCCESS
        } else {
            return FAILURE
        }
    }

    private fun generateToken(publicKey: ECPublicKeyParameters) : ByteArray {
        val oid = TLV(TLV_TAGS.OID, idPACEOid!!)
        var x = publicKey.q.xCoord.toBigInteger().toByteArray()
        if (x[0] == 0.toByte()) {
            x = x.slice(1..<x.size).toByteArray()
        }
        var y = publicKey.q.yCoord.toBigInteger().toByteArray()
        if (y[0] == 0.toByte()) {
            y = x.slice(1..<x.size).toByteArray()
        }
        val pub = TLV(TLV_TAGS.EC_PUBLIC_POINT, byteArrayOf(0x04) + x + y)
        return TLV(byteArrayOf(0x7F, 0x49), oid.toByteArray() + pub.toByteArray()).toByteArray()
    }

    private fun generateToken(publicKey: DHPublicKeyParameters) : ByteArray {
        val oid = TLV(TLV_TAGS.OID, idPACEOid!!)
        val pub = TLV(TLV_TAGS.UNSIGNED_INTEGER, publicKey.y.toByteArray())
        return TLV(byteArrayOf(0x7F, 0x49), oid.toByteArray()+pub.toByteArray()).toByteArray()
    }

    private fun exchangeKeys(publicKey: ECPublicKeyParameters, tag: Byte) : ECPublicKeyParameters {
        var data = TLV(tag, publicKey.q.getEncoded(false))
        data = TLV(TLV_TAGS.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        val response = TLV(apduControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, data.toByteArray(), 256)))
        return ECPublicKeyParameters(publicKey.parameters.curve.decodePoint(response.getTLVSequence()!!.getTLVSequence()[0].getValue()!!), publicKey.parameters)
    }

    private fun exchangeKeys(publicKey: DHPublicKeyParameters, tag: Byte) : DHPublicKeyParameters {
        var data = TLV(tag, publicKey.y.toByteArray())
        data = TLV(TLV_TAGS.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        val response = TLV(apduControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, data.toByteArray(), 256)))
        return DHPublicKeyParameters(BigInteger(response.getValue()), publicKey.parameters)
    }

    private fun sendNonce(nonce: ByteArray) {
        var data = TLV(TLV_TAGS.MAPPING_DATA, nonce)
        data = TLV(TLV_TAGS.DYNAMIC_AUTHENTICATION_DATA, data.toByteArray())
        apduControl.sendAPDU(APDU(NfcClassByte.COMMAND_CHAINING, NfcInsByte.GENERAL_AUTHENTICATE, ZERO_BYTE, ZERO_BYTE, data.toByteArray(), 256))
    }

    /**
     * Decrypt a nonce value
     * @param nonce: The nonce to be decrypted
     * @param key: The decryption key
     * @return The decrypted nonce as byte array
     */
    private fun decryptNonce(nonce : ByteArray, key : ByteArray) : ByteArray {
        val k = SecretKeySpec(key, "AES")
        val c = Cipher.getInstance("AES/CBC/NoPadding")
        c.init(Cipher.DECRYPT_MODE, k)
        return c.doFinal(nonce)
    }
}