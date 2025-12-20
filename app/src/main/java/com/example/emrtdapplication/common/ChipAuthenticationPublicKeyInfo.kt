package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TlvTags
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
            if (optionalData!!.tag.size != 1 || optionalData!!.tag[0] != TlvTags.INTEGER ||
                optionalData!!.value == null || optionalData!!.value!!.size != 1) {
                throw IllegalArgumentException()
            } else {
                optionalData!!.value!![0].toInt()
            }
        } else {
            null
        }
    }
}