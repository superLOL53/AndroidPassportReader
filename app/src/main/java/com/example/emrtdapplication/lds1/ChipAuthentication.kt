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
import org.bouncycastle.jcajce.provider.digest.SHA256
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.spec.DHParameterSpec

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

//TODO: Implement
class ChipAuthentication(private val apduControl: APDUControl, private val chipAuthenticationData : ByteArray?,
                         private val iv : ByteArray, private val publicKeyInfo: SubjectPublicKeyInfo,
                         private val random: SecureRandom = SecureRandom(), private val crypto: Crypto = Crypto(),
                         private val chipAuthenticationInfo: ChipAuthenticationInfo?) {

    fun authenticate() : Int {
        return if (chipAuthenticationData == null && chipAuthenticationInfo == null) {
            FAILURE
        } else if (chipAuthenticationInfo != null) {
            if (chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_ECDH_3DES_CBC_CBC) ||
                chipAuthenticationInfo.objectIdentifier.startsWith(ID_CA_DH_3DES_CBC_CBC)) {
                authenticate3DES()
            } else {
                authenticateAES()
            }
        } else {
            authenticateCAM()
        }
    }

    private fun authenticate3DES() : Int {
        val priv = generatePrivateKey()
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
        val priv = generatePrivateKey()
        if (priv == null) return FAILURE
        //val pub = generatePublicKey(priv)
        //if (pub == null) return FAILURE
        val protocol = TLV(0x80.toByte(), chipAuthenticationInfo!!.protocol)
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
            NfcP1Byte.SET_KEY_AGREEMENT_TEMPLATE,
            NfcP2Byte.SET_AUTHENTICATION_TEMPLATE,
            protocol.toByteArray()
        ))
        if (!apduControl.checkResponse(info)) {
            return FAILURE
        }
        val pubData = TLV(0x80.toByte(), priv.public.encoded)
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

    private fun authenticateCAM() : Int {
        return NOT_IMPLEMENTED
    }

    private fun generatePrivateKey() : KeyPair? {
        if (chipAuthenticationInfo == null) return null
        try {
            val spec = X509EncodedKeySpec(publicKeyInfo.algorithm.parameters.toASN1Primitive().encoded)
            val kf = KeyPairGenerator.getInstance(publicKeyInfo.algorithm.algorithm.id)
            val set = ASN1Sequence.getInstance(publicKeyInfo.algorithm.parameters.toASN1Primitive().encoded)
            val enc = set.encoded
            var p : BigInteger?
            for (asn1 in set) {
                val setenc = asn1.toASN1Primitive().encoded
                when(asn1.toASN1Primitive().encoded[0]) {
                    0x81.toByte() -> {
                        p = ASN1Integer.getInstance(asn1.toASN1Primitive().encoded).value
                    }
                }
            }
            //val ec = ECParameterSpec()
            //kf.initialize(dh)
            return kf.generateKeyPair()
        } catch (e : Exception) {
            println(e)
        }
        return null
    }

    private fun generatePublicKey(privateKey: PrivateKey) : PublicKey? {
        if (chipAuthenticationInfo == null) return null
        try {
            val spec = X509EncodedKeySpec(privateKey.encoded)
            val kf = KeyFactory.getInstance(privateKey.algorithm, "BC")
            return kf.generatePublic(spec)
        } catch (e : Exception) {
            println(e)
        }
        return null
    }
}