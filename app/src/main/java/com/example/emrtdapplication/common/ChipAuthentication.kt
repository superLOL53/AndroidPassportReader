package com.example.emrtdapplication.common

import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.constants.APDUConstants
import com.example.emrtdapplication.constants.BACConstants
import com.example.emrtdapplication.constants.ChipAuthenticationConstants
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.lds1.DG1
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import org.spongycastle.crypto.params.DHParameters
import org.spongycastle.crypto.params.DHPrivateKeyParameters
import org.spongycastle.crypto.params.DHPublicKeyParameters
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.crypto.util.PublicKeyFactory
import java.math.BigInteger

/**
 * Implements the chip authentication protocol
 *
 * @property publicKeyInfo Public key info for Chip Authentication protocol
 * @property chipAuthenticationData Decrypted Chip Authentication Data retrieved during the last step of the PACE protocol
 * @property chipAuthenticationInfo [ChipAuthenticationInfo] object containing information about the protocol itself
 * @property isDH If the protocol is a DH key agreement protocol
 * @property isEC If the protocol is a EC key agreement protocol
 * @property keyParamsDH DH parameters for the protocol
 * @property keyParamsECDH EC parameters for the protocol
 * @property publicKeyDH eMRTD's static public key stored in [com.example.emrtdapplication.lds1.DG14]
 * @property publicKeyECDH eMRTD's static public key stored in [com.example.emrtdapplication.lds1.DG14]
 * @property is3DES If the protocol uses 3DES as the symmetric protocol, otherwise AES is used
 */
class ChipAuthentication {
    private val publicKeyInfo: ChipAuthenticationPublicKeyInfo
    private val chipAuthenticationData : ByteArray?
    private val chipAuthenticationInfo: ChipAuthenticationInfo?
    private var isDH = false
    private var isEC = false
    private var keyParamsDH : DHParameters? = null
    private var keyParamsECDH : ECDomainParameters? = null
    private var publicKeyDH : DHPublicKeyParameters? = null
    private var publicKeyECDH : ECPublicKeyParameters? = null
    private var is3DES = false

    /**
     * Creates a ChipAuthentication object for authenticating the eMRTD's chip
     *
     * @param publicKeyInfo The eMRTD's static public key stored in [com.example.emrtdapplication.lds1.DG14]
     * @param chipAuthenticationInfo Information about the protocol stored in [com.example.emrtdapplication.lds1.DG14]
     */
    constructor(publicKeyInfo: ChipAuthenticationPublicKeyInfo, chipAuthenticationInfo: ChipAuthenticationInfo) {
        this.publicKeyInfo = publicKeyInfo
        this.chipAuthenticationInfo = chipAuthenticationInfo
         chipAuthenticationData = null
    }

    /**
     * Creates a ChipAuthentication object for PACE with Chip Authentication Mapping
     *
     * @param publicKeyInfo Public key info for the Chip Authentication protocol
     * @param chipAuthenticationData Decrypted Chip Authentication Data retrieved during the last step in the PACE protocol
     * @param publicKey eMRTD's public key during the mapping phase of the PACE protocol
     */
    constructor(publicKeyInfo: ChipAuthenticationPublicKeyInfo, chipAuthenticationData : ByteArray, publicKey : ECPublicKeyParameters) {
        this.chipAuthenticationData = chipAuthenticationData
        publicKeyECDH = publicKey
        chipAuthenticationInfo = null
        this.publicKeyInfo = publicKeyInfo
    }

    /**
     * Authenticate the chip
     *
     * @return [com.example.emrtdapplication.constants.SUCCESS] if the protocol was successful, otherwise [com.example.emrtdapplication.constants.FAILURE]
     */
    fun authenticate() : Int {
        return if (chipAuthenticationData != null && publicKeyECDH != null) {
            authenticatePACECAMMapping()
        } else if(chipAuthenticationInfo != null) {
            if (chipAuthenticationInfo.objectIdentifier.startsWith(
                    ChipAuthenticationConstants.ID_CA_DH
                )) {
                isDH = true
            } else if (chipAuthenticationInfo.objectIdentifier.startsWith(
                    ChipAuthenticationConstants.ID_CA_ECDH
                )) {
                isEC = true
            }
            is3DES = (chipAuthenticationInfo.objectIdentifier.startsWith(
                ChipAuthenticationConstants.ID_CA_ECDH_3DES_CBC_CBC
            ) ||
                        chipAuthenticationInfo.objectIdentifier.startsWith(
                            ChipAuthenticationConstants.ID_CA_DH_3DES_CBC_CBC
                        ))
            setParameters()
            if (keyParamsDH == null && keyParamsECDH == null) {
                return FAILURE
            }
            val keyPair = generateKeyPair()
            if (keyPair == null) return FAILURE
            val publicKeyData = getEncodedPublicKey(keyPair)
            if (publicKeyData == null) return FAILURE
            val agreement = if (isDH && publicKeyDH != null) {
                Crypto.calculateDHAgreement(
                    keyPair.private as DHPrivateKeyParameters,
                    publicKeyDH!!
                )
            } else if (isEC && publicKeyECDH != null) {
                Crypto.calculateECDHAgreement(
                    keyPair.private as ECPrivateKeyParameters,
                    publicKeyECDH!!
                )
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
            computeKeys(agreement!!.toByteArray())
            verify()
        } else {
            FAILURE
        }
    }

    /**
     * Authenticates the eMRTD's chip using 3DES chip authentication protocols
     *
     * @param publicKeyData Public key of the reader encoded as byte array
     * @return [SUCCESS] if APDU exchange was successful, otherwise [FAILURE]
     */
    private fun authenticate3DES(publicKeyData : ByteArray) : Int {
        val pubData = TLV(TlvTags.EPHEMERAL_PUBLIC_KEY, publicKeyData)
        val keyRef = if (publicKeyInfo.keyId == null) {
            null
        } else {
            TLV(TlvTags.PRIVATE_KEY_REFERENCE, publicKeyInfo.keyId!!.toByteArray())
        }
        val data = if (keyRef != null) {
            pubData.toByteArray() + keyRef.toByteArray()
        } else {
            pubData.toByteArray()
        }
        val info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
                NfcP1Byte.SET_KEY_AGREEMENT_TEMPLATE,
                NfcP2Byte.SET_KEY_AGREEMENT_TEMPLATE,
                data
            )
        )
        if (!APDUControl.checkResponse(info)) {
            return FAILURE
        }
        return SUCCESS
    }

    /**
     * Authenticates the eMRTD's chip using AES chip authentication protocols
     *
     * @param publicKeyData Public key of the reader encoded as byte array
     * @return [SUCCESS] if APDU exchange was successful, otherwise [FAILURE]
     */
    private fun authenticateAES(publicKeyData: ByteArray) : Int {
        val protocol = TLV(TlvTags.CRYPTOGRAPHIC_REFERENCE, chipAuthenticationInfo!!.protocol)
        val keyId = if (publicKeyInfo.keyId != null) {
                        publicKeyInfo.keyId!!.toByteArray()
                    } else {
                        null
                    }
        var ar = protocol.toByteArray()
        if (keyId != null) {
            ar = ar + keyId
        }
        var info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
                NfcP1Byte.SET_KEY_AGREEMENT_TEMPLATE,
                NfcP2Byte.SET_AUTHENTICATION_TEMPLATE,
                ar
            )
        )
        if (!APDUControl.checkResponse(info)) {
            return FAILURE
        }
        val pubData = TLV(TlvTags.CRYPTOGRAPHIC_REFERENCE, publicKeyData)
        val tlv = TLV(TlvTags.DYNAMIC_AUTHENTICATION_DATA, pubData.toByteArray())
        info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.GENERAL_AUTHENTICATE,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                tlv.toByteArray(),
                APDUConstants.LE_MAX
            )
        )
        if (!APDUControl.checkResponse(info)) {
            return FAILURE
        }
        return SUCCESS
    }

    /**
     * Authenticates the eMRTD's chip using an DH or ECDH key agreement from PACE-CAM
     *
     * @return [SUCCESS] or [FAILURE]
     */
    private fun authenticatePACECAMMapping() : Int {
        val pub = ECPublicKeyParameters(publicKeyECDH!!.parameters.curve.decodePoint(publicKeyInfo.publicKeyInfo.publicKeyData.bytes), publicKeyECDH!!.parameters)
        val privateKey = ECPrivateKeyParameters(BigInteger(byteArrayOf(0x00) + chipAuthenticationData!!), pub.parameters)
        val agreement = Crypto.calculateECDHAgreement(privateKey, pub).toByteArray()
        return if (agreement.contentEquals(publicKeyECDH!!.q.xCoord.toBigInteger().toByteArray())) {
            SUCCESS
        } else {
            FAILURE
        }
    }

    /**
     * Generates key pair for the protocol
     *
     * @return Generated key pair
     */
    private fun generateKeyPair() : AsymmetricCipherKeyPair? {
        return if (isDH && keyParamsDH != null) {
            Crypto.generateDHKeyPair(keyParamsDH!!)
        } else if (isEC && keyParamsECDH != null) {
            Crypto.generateECKeyPair(keyParamsECDH!!)
        } else {
            null
        }
    }

    /**
     * Encodes a public key as byte array
     *
     * @param keyPair Key pair from which the public key is encoded
     * @return The public key as a byte array
     */
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

    /**
     * Sets the public key and key parameters for the protocol
     */
    private fun setParameters() {
        try {
            val publicKey = PublicKeyFactory.createKey(publicKeyInfo.publicKeyInfo)
            if (isEC) {
                publicKeyECDH = publicKey as ECPublicKeyParameters
                keyParamsECDH = ECDomainParameters(
                    publicKeyECDH!!.parameters.curve,
                    publicKeyECDH!!.parameters.g,
                    publicKeyECDH!!.parameters.n,
                    publicKeyECDH!!.parameters.h
                )
            } else if (isDH) {
                publicKeyDH = publicKey as DHPublicKeyParameters
                keyParamsDH = DHParameters(
                    publicKeyDH!!.parameters.p,
                    publicKeyDH!!.parameters.g,
                    publicKeyDH!!.parameters.q
                )
            }
        } catch (_ : Exception) {

        }
    }

    /**
     * Computes and sets the symmetric keys for restarting secure messaging
     *
     * @param agreement The computed agreement in the key agreement step in the protocol
     */
    private fun computeKeys(agreement : ByteArray) {
        if (chipAuthenticationInfo == null) return
        val oid = chipAuthenticationInfo.objectIdentifier
        APDUControl.setEncryptionKeyBAC(
            Crypto.computeKey(agreement, (oid[oid.length-1] - '0').toByte(),
                BACConstants.ENCRYPTION_KEY_VALUE_C
            ))
        APDUControl.setEncryptionKeyMAC(
            Crypto.computeKey(agreement, (oid[oid.length-1] - '0').toByte(),
                BACConstants.MAC_COMPUTATION_KEY_VALUE_C
            ))
        APDUControl.isAES = !is3DES
        if (is3DES) {
            APDUControl.setSequenceCounter(ByteArray(8))
        } else {
            APDUControl.setSequenceCounter(ByteArray(16))
        }
    }

    /**
     * Verify that the computed keys for chip authentication actually can read from the eMRTD
     * @return [SUCCESS] or [FAILURE]
     */
    private fun verify() : Int {
        val dg1 = DG1()
        dg1.read()
        dg1.parse()
        if (!dg1.isRead) {
            return FAILURE
        }
        return if (dg1.holderName.contentEquals(EMRTD.ldS1Application.dg1.holderName) &&
            dg1.documentNumber.contentEquals(EMRTD.ldS1Application.dg1.documentNumber) &&
            dg1.nationality.contentEquals(EMRTD.ldS1Application.dg1.nationality) &&
            dg1.sex == EMRTD.ldS1Application.dg1.sex &&
            dg1.dateOfBirth.contentEquals(EMRTD.ldS1Application.dg1.dateOfBirth) &&
            dg1.dateOfExpiry.contentEquals(EMRTD.ldS1Application.dg1.dateOfExpiry)) {
            SUCCESS
        } else {
            FAILURE
        }
    }
}