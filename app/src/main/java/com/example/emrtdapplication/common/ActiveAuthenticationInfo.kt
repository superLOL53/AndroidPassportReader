package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.SecurityInfoConstants.ACTIVE_AUTHENTICATION_TYPE
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import org.bouncycastle.asn1.ASN1ObjectIdentifier

/**
 * Implements the ASN1 ActiveAuthenticationInfo Sequence:
 *
 *      ActiveAuthenticationInfo ::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-icao-mrtd-security-aaProtocolObject),
 *          version INTEGER, -- MUST be 1
 *          signatureAlgorithm OBJECT IDENTIFIER
 *      }
 *
 * where
 *
 *      id-icao-mrtd-security-aaProtocolObject OBJECT IDENTIFIER ::= { id-icao-mrtd-security 5 }
 *
 * @param tlv TLV structure containing an encoded ActiveAuthenticationInfo sequence
 * @property version protocol version
 * @property signatureAlgorithm OID representing the signature algorithm
 * @throws IllegalArgumentException If [tlv] does not contain an ActiveAuthenticationInfo sequence
 */
class ActiveAuthenticationInfo(tlv: TLV) : SecurityInfo(tlv) {
    var version : Int
        private set
    var signatureAlgorithm : String
        private set

    init {
        if (type != ACTIVE_AUTHENTICATION_TYPE) throw IllegalArgumentException("Active Authentication Info must have type of Active Authentication Info")
        if (!requiredData.isValid || requiredData.tag.size != 1 || requiredData.tag[0] != TlvTags.INTEGER ||
            requiredData.value == null || requiredData.value!!.size != 1 || requiredData.value!![0].toInt() != 1) {
            throw IllegalArgumentException()
        } else {
            version = 1
        }
        if (optionalData == null || !optionalData!!.isValid || optionalData!!.tag.size != 1 ||
            optionalData!!.tag[0] != TlvTags.OID || optionalData!!.value == null) {
            throw IllegalArgumentException()
        } else {
            signatureAlgorithm = ASN1ObjectIdentifier.getInstance(optionalData!!.toByteArray()).id
        }
    }
}