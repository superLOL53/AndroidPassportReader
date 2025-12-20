package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.TLV
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.X509EncodedKeySpec

const val ID_CA = "0.4.0.127.0.7.2.2.3"
const val ID_CA_DH = "0.4.0.127.0.7.2.2.3.1"
const val ID_CA_DH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.1.1"
const val ID_CA_DH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.1.2"
const val ID_CA_DH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.1.3"
const val ID_CA_DH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.1.4"
const val ID_CA_ECDH = "0.4.0.127.0.7.2.2.3.2"
const val ID_CA_ECDH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.2.1"
const val ID_CA_ECDH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.2.2"
const val ID_CA_ECDH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.2.3"
const val ID_CA_ECDH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.2.4"
const val ID_PK = "0.4.0.127.0.7.2.2.1"
const val ID_PK_DH = "0.4.0.127.0.7.2.2.1.1"
const val ID_PK_ECDH = "0.4.0.127.0.7.2.2.1.2"

/**
 * Implements the chip authentication protocol
 *
 */
//TODO: Implement
class ChipAuthentication(private val apduControl: APDUControl, private val chipAuthenticationData : ByteArray?,
                         private val iv : ByteArray, private val publicKeyInfo: SubjectPublicKeyInfo,
                         private val random: SecureRandom = SecureRandom(), private val crypto: Crypto = Crypto(),
                         private val chipAuthenticationInfo: ChipAuthenticationInfo?) {

    /**
     * Authenticate the chip
     */
    fun authenticate() : Int {
        return if (chipAuthenticationInfo == null) {
            FAILURE
        } else {
            if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_ECDH_3DES_CBC_CBC) ||
                chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_DH_3DES_CBC_CBC)) {
                authenticate3DES()
            } else {
                authenticateAES()
            }
        }
    }

    private fun authenticate3DES() : Int {
        val priv = generateKeyPair()
        if (priv == null) return FAILURE
        //val pub = generatePublicKey(priv)
        //if (pub == null) return FAILURE
        val pubData = TLV(0x91.toByte(), priv.public.encoded)
        val info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
            NfcP1Byte.SET_KEY_AGREEMENT_TEMPLATE,
            NfcP2Byte.SET_KEY_AGREEMENT_TEMPLATE,
            pubData.toByteArray()
        ))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        return NOT_IMPLEMENTED
    }

    private fun authenticateAES() : Int {
        val keyPair = generateKeyPair()
        if (keyPair == null) return FAILURE
        val protocol = TLV(0x80.toByte(), chipAuthenticationInfo!!.protocol + 0x1)
        val ar = protocol.toByteArray()
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.COMMAND_CHAINING,
            NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
            NfcP1Byte.SET_KEY_AGREEMENT_TEMPLATE,
            NfcP2Byte.SET_AUTHENTICATION_TEMPLATE,
            ar
        ))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        val pubData = TLV(0x80.toByte(), keyPair.public.encoded)
        val tlv = TLV(0x7C, pubData.toByteArray())
        info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.GENERAL_AUTHENTICATE,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO,
            tlv.toByteArray()
        ))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        return NOT_IMPLEMENTED
    }

    private fun generateKeyPair() : KeyPair? {
        if (chipAuthenticationInfo == null) return null
        try {
            val spec = X509EncodedKeySpec(publicKeyInfo.encoded)
            val kf = KeyFactory.getInstance(publicKeyInfo.algorithm.algorithm.id, "BC")
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
            val keyfac = KeyPairGenerator.getInstance(publicKeyInfo.algorithm.algorithm.id, "BC")
            keyfac.initialize(params)
            return keyfac.generateKeyPair()
        } catch (e : Exception) {
            println(e)
        }
        return null
    }
}