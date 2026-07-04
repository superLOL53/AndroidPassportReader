package com.example.emrtdapplication.common

import com.example.emrtdapplication.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import java.math.BigInteger

const val ILLEGAL_REQUIRED_DATA_CHIP_AUTHENTICATION_PUBLIC_KEY =
    "Required data does not contain a SubjectPublicKeyInfo for ChipAuthenticationPublicKeyInfo!"
const val INVALID_KEY_ID =
    "Invalid key identifier for ChipAuthenticationPublicKeyInfo!"
/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence ChipAuthenticationPublicKeyInfo:
 *
 *      ChipAuthenticationInfo::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-PK-DH | id-PK-ECDH),
 *          chipAuthenticationPublicKey SubjectPublicKeyInfo,
 *          keyId INTEGER OPTIONAL
 *      }
 *
 * @param tlv TLV structure containing an encoded
 * instance of ChipAuthenticationPublicKeyInfo
 * @property publicKeyInfo The public key encoded
 * as [SubjectPublicKeyInfo]
 * @property keyId ID of the public key if multiple
 * public keys are present
 * @throws IllegalArgumentException If [tlv] does not contain
 * an instance of ChipAuthenticationPublicKeyInfo
 */
class ChipAuthenticationPublicKeyInfo(
    tlv: TLV
): SecurityInfo(tlv, CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE) {
    var publicKeyInfo: SubjectPublicKeyInfo
        private set
    var keyId: BigInteger?
        private set

    init {
        try {
            publicKeyInfo = SubjectPublicKeyInfo.getInstance(requiredData.toByteArray())
        } catch (_: Exception) {
            throw IllegalArgumentException(ILLEGAL_REQUIRED_DATA_CHIP_AUTHENTICATION_PUBLIC_KEY)
        }
        keyId = if (optionalData != null) {
            if (optionalData.tag.size != 1 || optionalData.tag[0] != TlvTags.INTEGER ||
                optionalData.value == null) {
                throw IllegalArgumentException(INVALID_KEY_ID)
            } else {
                BigInteger(optionalData.value!!)
            }
        } else {
            null
        }
    }
}