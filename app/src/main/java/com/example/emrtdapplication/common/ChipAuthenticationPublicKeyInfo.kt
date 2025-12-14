package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLV_TAGS
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence ChipAuthenticationPublicKeyInfo:
 *
 *      ChipAuthenticationInfo ::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-PK-DH | id-PK-ECDH),
 *          chipAuthenticationPublicKey SubjectPublicKeyInfo,
 *          keyId INTEGER OPTIONAL
 *      }
 * @property publicKeyInfo The public key encoded as [SubjectPublicKeyInfo]
 * @property keyId Id of the public key if multiple public keys are present
 */
class ChipAuthenticationPublicKeyInfo(tlv: TLV) : SecurityInfo(tlv) {
    var publicKeyInfo : SubjectPublicKeyInfo
        private set
    var keyId : Int?
        private set

    init {
        publicKeyInfo = SubjectPublicKeyInfo.getInstance(requiredData.toByteArray())
        keyId = if (optionalData != null) {
            if (optionalData!!.getTag().size != 1 || optionalData!!.getTag()[0] != TLV_TAGS.INTEGER ||
                optionalData!!.getValue() == null || optionalData!!.getValue()!!.size != 1) {
                throw IllegalArgumentException()
            } else {
                optionalData!!.getValue()!![0].toInt()
            }
        } else {
            null
        }
    }
}