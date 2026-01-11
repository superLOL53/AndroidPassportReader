package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
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
import com.example.emrtdapplication.utils.TLV
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.spongycastle.asn1.x9.DHPublicKey
import org.spongycastle.crypto.params.DHPublicKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import java.nio.ByteBuffer
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.X509EncodedKeySpec

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
    /**
     * Authenticate the chip
     */
    fun authenticate() : Int {
        return if (chipAuthenticationInfo == null) {
            FAILURE
        } else {
            apduControl.sendEncryptedAPDU = false
            if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_DH)) {
                isDH = true
            } else if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_ECDH)) {
                isEC = true
            }
            if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_ECDH_3DES_CBC_CBC) ||
                chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_DH_3DES_CBC_CBC)) {
                authenticate3DES()
            } else {
                authenticateAES()
            }
        }
    }

    private fun authenticate3DES() : Int {
        val keyPair = generateKeyPair()
        if (keyPair == null) return FAILURE
        val publicKeyData = getEncodedPublicKey(keyPair)
        if (publicKeyData == null) return FAILURE
        val pubData = TLV(0x91.toByte(), publicKeyData)
        val keyRef = if (publicKeyInfo.keyId == null) {
            null
        } else {
            TLV(0x84.toByte(), ByteBuffer.allocate(Int.SIZE_BYTES).putInt(publicKeyInfo.keyId!!).array())
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
        return NOT_IMPLEMENTED
    }

    private fun authenticateAES() : Int {
        val protocol = TLV(0x80.toByte(), chipAuthenticationInfo!!.protocol)
        val ar = protocol.toByteArray()
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
        val keyPair = generateKeyPair()
        if (keyPair == null) return FAILURE
        val publicKeyData = getEncodedPublicKey(keyPair)
        if (publicKeyData == null) return FAILURE
        val pubData = TLV(0x80.toByte(), publicKeyData)
        val tlv = TLV(0x7C, pubData.toByteArray()).toByteArray()
        val test = TLV(tlv)
        val apdu = APDU(
            NfcClassByte.ZERO,
            NfcInsByte.GENERAL_AUTHENTICATE,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO,
            tlv
        )
        val apduArray = apdu.getByteArray()
        info = apduControl.sendAPDU(apdu)
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        return NOT_IMPLEMENTED
    }

    private fun generateKeyPair() : KeyPair? {
        if (chipAuthenticationInfo == null) return null
        try {
            val spec = X509EncodedKeySpec(publicKeyInfo.publicKeyInfo.encoded)
            val kf = KeyFactory.getInstance(publicKeyInfo.publicKeyInfo.algorithm.algorithm.id, "BC")
            var pub : PublicKey? = null
            var params : AlgorithmParameterSpec? = null
            if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_ECDH)) {
                pub = kf.generatePublic(spec) as ECPublicKey
                params = pub.parameters
            } else if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_DH)) {
                pub = kf.generatePublic(spec) as javax.crypto.interfaces.DHPublicKey
                params = pub.params
            } else {
                null
            }
            if (pub == null || params == null) return null
            val keyFac = KeyPairGenerator.getInstance(publicKeyInfo.publicKeyInfo.algorithm.algorithm.id, "BC")
            keyFac.initialize(params)
            return keyFac.generateKeyPair()
        } catch (e : Exception) {
            println(e)
        }
        return null
    }

    private fun getEncodedPublicKey(keyPair: KeyPair) : ByteArray? {
        var publicKeyData : ByteArray? = null
        try {
            if (isDH) {
                val publicKey = keyPair.public as BCDHPublicKey
                publicKeyData = publicKey.y.toByteArray()
            } else if (isEC) {
                val publicKey = keyPair.public as BCECPublicKey
                publicKeyData = publicKey.q.getEncoded(false)
            }
        } catch (e : Exception) {
            println(e)
        }
        return publicKeyData
    }
}