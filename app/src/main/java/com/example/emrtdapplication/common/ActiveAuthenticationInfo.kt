package com.example.emrtdapplication.common

import com.example.emrtdapplication.ACTIVE_AUTHENTICATION_OID
import com.example.emrtdapplication.ACTIVE_AUTHENTICATION_TYPE
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.utils.TLV
import org.bouncycastle.asn1.ASN1ObjectIdentifier

const val INVALID_TYPE =
    "Active Authentication Info must have type of Active Authentication Info"
const val ILLEGAL_REQUIRED_DATA_ACTIVE_AUTHENTICATION_INFO =
    "Illegal required data for Active Authentication Info!"
const val ILLEGAL_OPTIONAL_DATA =
    "Illegal optional data for Active Authentication Info!"
const val ILLEGAL_OID =
    "Illegal OID in Active Authentication Info!"

/**
 * Implements the ASN1 ActiveAuthenticationInfo Sequence:
 *
 *      ActiveAuthenticationInfo::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-icao-mrtd-security-aaProtocolObject),
 *          version INTEGER, -- MUST be 1
 *          signatureAlgorithm OBJECT IDENTIFIER
 *      }
 *
 * where
 *
 *      id-icao-mrtd-security-aaProtocolObject OBJECT IDENTIFIER::= { id-icao-mrtd-security 5 }
 *
 * @param tlv TLV structure containing an encoded ActiveAuthenticationInfo sequence
 * @property version protocol version
 * @property signatureAlgorithm OID representing the signature algorithm
 * @throws IllegalArgumentException If [tlv] does not contain an ActiveAuthenticationInfo sequence
 */
class ActiveAuthenticationInfo(
    tlv: TLV
): SecurityInfo(tlv, ACTIVE_AUTHENTICATION_TYPE) {
    var version: Int
        private set
    var signatureAlgorithm: String
        private set

    init {
        if (type != ACTIVE_AUTHENTICATION_TYPE ||
            objectIdentifier != ACTIVE_AUTHENTICATION_OID
        ) {
            throw IllegalArgumentException(INVALID_TYPE)
        }
        if (!requiredData.isValid ||
            requiredData.tag.size != 1 ||
            requiredData.tag[0] != TlvTags.INTEGER ||
            requiredData.value == null ||
            requiredData.value!!.size != 1 ||
            requiredData.value!![0].toInt() != 1
        ) {
            throw IllegalArgumentException(ILLEGAL_REQUIRED_DATA_ACTIVE_AUTHENTICATION_INFO)
        } else {
            version = 1
        }
        if (optionalData == null ||
            !optionalData.isValid ||
            optionalData.tag.size != 1 ||
            optionalData.tag[0] != TlvTags.OID ||
            optionalData.value == null
        ) {
            throw IllegalArgumentException(ILLEGAL_OPTIONAL_DATA)
        } else {
            try {
                signatureAlgorithm = ASN1ObjectIdentifier.getInstance(
                    optionalData.toByteArray()
                ).id
            } catch (_: Exception) {
                throw IllegalArgumentException(ILLEGAL_OID)
            }
        }
    }
}