package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.constants.APDUConstants
import com.example.emrtdapplication.constants.BACConstants.ENCRYPTION_KEY_VALUE_C
import com.example.emrtdapplication.constants.BACConstants.MAC_COMPUTATION_KEY_VALUE_C
import com.example.emrtdapplication.constants.ChipAuthenticationConstants.ID_CA_DH
import com.example.emrtdapplication.constants.ChipAuthenticationConstants.ID_CA_DH_3DES_CBC_CBC
import com.example.emrtdapplication.constants.ChipAuthenticationConstants.ID_CA_ECDH
import com.example.emrtdapplication.constants.ChipAuthenticationConstants.ID_CA_ECDH_3DES_CBC_CBC
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NOT_IMPLEMENTED
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.CRYPTOGRAPHIC_REFERENCE
import com.example.emrtdapplication.constants.TlvTags.DYNAMIC_AUTHENTICATION_DATA
import com.example.emrtdapplication.constants.TlvTags.EPHEMERAL_PUBLIC_KEY
import com.example.emrtdapplication.constants.TlvTags.PRIVATE_KEY_REFERENCE
import com.example.emrtdapplication.utils.TLV
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.spec.ECPublicKeySpec
import org.jmrtd.protocol.EACCAProtocol
import org.spongycastle.asn1.x9.DHPublicKey
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import org.spongycastle.crypto.agreement.ECDHBasicAgreement
import org.spongycastle.crypto.params.DHKeyParameters
import org.spongycastle.crypto.params.DHParameters
import org.spongycastle.crypto.params.DHPrivateKeyParameters
import org.spongycastle.crypto.params.DHPublicKeyParameters
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.crypto.util.PublicKeyFactory
import java.nio.ByteBuffer
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement

/**
 * Implements the chip authentication protocol
 *
 */
//TODO: Implement
class ChipAuthentication(private val apduControl: APDUControl, private val chipAuthenticationData : ByteArray?,
                         private val iv : ByteArray, private val publicKeyInfo: ChipAuthenticationPublicKeyInfo,
                         private val random: SecureRandom = SecureRandom(), private val crypto: Crypto = Crypto(),
                         private val chipAuthenticationInfo: ChipAuthenticationInfo?) {

    private var isDH = false
    private var isEC = false
    private var keyParamsDH : DHParameters? = null
    private var keyParamsECDH : ECDomainParameters? = null
    private var publicKeyDH : DHPublicKeyParameters? = null
    private var publicKeyECDH : ECPublicKeyParameters? = null
    private var is3DES = false
    /**
     * Authenticate the chip
     */
    fun authenticate() : Int {
        return if (chipAuthenticationInfo == null) {
            FAILURE
        } else {
            if (chipAuthenticationData != null) {
                authenticatePACECAMMapping()
            }
            if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_DH)) {
                isDH = true
            } else if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_ECDH)) {
                isEC = true
            }
            is3DES = (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_ECDH_3DES_CBC_CBC) ||
                chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_DH_3DES_CBC_CBC))
            setParameters()
            if (keyParamsDH == null && keyParamsECDH == null) {
                return FAILURE
            }
            val keyPair = generateKeyPair()
            if (keyPair == null) return FAILURE
            val publicKeyData = getEncodedPublicKey(keyPair)
            if (publicKeyData == null) return FAILURE
            val agreement = if (isDH && publicKeyDH != null) {
                crypto.calculateDHAgreement(keyPair.private as DHPrivateKeyParameters, publicKeyDH!!)
            } else if (isEC && publicKeyECDH != null) {
                crypto.calculateECDHAgreement(keyPair.private as ECPrivateKeyParameters, publicKeyECDH!!)
            } else {
                null
            }
            if (agreement == null) {
                FAILURE
            }
            val success = if (is3DES) {
                authenticate3DES(publicKeyData)
            } else {
                authenticateAES(publicKeyData)
            }
            if (success != SUCCESS) {
                FAILURE
            }
            val ar = agreement!!.toByteArray()
            computeKeys(agreement!!.toByteArray())
            val dg1 = DG1(apduControl)
            dg1.read()
            dg1.parse()
            SUCCESS
        }
    }

    private fun authenticate3DES(publicKeyData : ByteArray) : Int {
        val pubData = TLV(EPHEMERAL_PUBLIC_KEY, publicKeyData)
        val keyRef = if (publicKeyInfo.keyId == null) {
            null
        } else {
            TLV(PRIVATE_KEY_REFERENCE, publicKeyInfo.keyId!!.toByteArray())
        }
        val data = if (keyRef != null) {
            pubData.toByteArray() + keyRef.toByteArray()
        } else {
            pubData.toByteArray()
        }
        val info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
            NfcP1Byte.SET_KEY_AGREEMENT_TEMPLATE,
            NfcP2Byte.SET_KEY_AGREEMENT_TEMPLATE,
            data
        ))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        return SUCCESS
    }

    private fun authenticateAES(publicKeyData: ByteArray) : Int {
        val protocol = TLV(CRYPTOGRAPHIC_REFERENCE, chipAuthenticationInfo!!.protocol)
        val keyId = if (publicKeyInfo.keyId != null) {
                        publicKeyInfo.keyId!!.toByteArray()
                    } else {
                        null
                    }
        var ar = protocol.toByteArray()
        if (keyId != null) {
            ar = ar + keyId
        }
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
            NfcP1Byte.SET_KEY_AGREEMENT_TEMPLATE,
            NfcP2Byte.SET_AUTHENTICATION_TEMPLATE,
            ar
        ))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        val pubData = TLV(CRYPTOGRAPHIC_REFERENCE, publicKeyData)
        val tlv = TLV(DYNAMIC_AUTHENTICATION_DATA, pubData.toByteArray())
        info = apduControl.sendAPDU(APDU(
            NfcClassByte.COMMAND_CHAINING,
            NfcInsByte.GENERAL_AUTHENTICATE,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO,
            tlv.toByteArray(),
            APDUConstants.LE_MAX
        ))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        return SUCCESS
    }

    private fun authenticatePACECAMMapping() : Int {
        TODO()
    }

    private fun generateKeyPair() : AsymmetricCipherKeyPair? {
        return if (isDH && keyParamsDH != null) {
            crypto.generateDHKeyPair(keyParamsDH!!)
        } else if (isEC && keyParamsECDH != null) {
            crypto.generateECKeyPair(keyParamsECDH!!)
        } else {
            null
        }
    }

    private fun getEncodedPublicKey(keyPair: AsymmetricCipherKeyPair) : ByteArray? {
        var publicKeyData : ByteArray? = null
        try {
            if (isDH) {
                val publicKey = keyPair.public as DHPublicKeyParameters
                publicKeyData = publicKey.y.toByteArray()
            } else if (isEC) {
                val publicKey = keyPair.public as ECPublicKeyParameters
                publicKeyData = publicKey.q.getEncoded(false)
            }
        } catch (e : Exception) {
            println(e)
        }
        return publicKeyData
    }

    private fun setParameters() {
        try {
            val publicKey = PublicKeyFactory.createKey(publicKeyInfo.publicKeyInfo)
            if (isEC) {
                publicKeyECDH = publicKey as ECPublicKeyParameters
                keyParamsECDH = ECDomainParameters(publicKeyECDH!!.parameters.curve, publicKeyECDH!!.parameters.g, publicKeyECDH!!.parameters.n, publicKeyECDH!!.parameters.h)
            } else if (isDH) {
                publicKeyDH = publicKey as DHPublicKeyParameters
                keyParamsDH = DHParameters(publicKeyDH!!.parameters.p, publicKeyDH!!.parameters.g, publicKeyDH!!.parameters.q)
            }
        } catch (e : Exception) {

        }
    }

    private fun computeKeys(agreement : ByteArray) {
        if (chipAuthenticationInfo == null) return
        val oid = chipAuthenticationInfo.objectIdentifier
        apduControl.setEncryptionKeyBAC(crypto.computeKey(agreement, (oid[oid.length-1] - '0').toByte(), ENCRYPTION_KEY_VALUE_C))
        apduControl.setEncryptionKeyMAC(crypto.computeKey(agreement, (oid[oid.length-1] - '0').toByte(), MAC_COMPUTATION_KEY_VALUE_C))
        apduControl.isAES = !is3DES
        if (is3DES) {
            apduControl.setSequenceCounter(ByteArray(8))
        } else {
            apduControl.setSequenceCounter(ByteArray(16))
        }
    }
}