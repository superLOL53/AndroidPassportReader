package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TlvTags
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.x509.AlgorithmIdentifier

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence PACEDomainParameterInfo:
 *
 *      PACEDomainParameterInfo ::= SEQUENCE {
 *              protocol OBJECT IDENTIFIER(
 *                      id-PACE-DH-GM |
 *                      id-PACE-ECDH-GM |
 *                      id-PACE-DH-IM |
 *                      id-PACE-ECDH-IM |
 *                      id-PACE-ECDH-CAM),
 *              domainParameter AlgorithmIdentifier,
 *              parameterId INTEGER OPTIONAL
 *      }
 *
 * @param tlv TLV structure containing an encoded instance of PACEDomainParameterInfo
 * @property parameterId The ID of the cryptographic domain parameters
 * @property algorithmIdentifier ASN1 Algorithm Identifier
 */
class PACEDomainParameterInfo(tlv: TLV) : SecurityInfo(tlv) {
    var parameterId : Int?
        private set
    var algorithmIdentifier : AlgorithmIdentifier
        private set

    init {
        algorithmIdentifier = AlgorithmIdentifier.getInstance(ASN1InputStream(requiredData.toByteArray()).readAllBytes())
        parameterId = if (optionalData == null || optionalData!!.tag.size != 1 || optionalData!!.tag[0] != TlvTags.INTEGER ||
            optionalData!!.value == null || optionalData!!.value!!.size != 1) {
            null
        } else {
            optionalData!!.value!![0].toInt()
        }
    }
}
