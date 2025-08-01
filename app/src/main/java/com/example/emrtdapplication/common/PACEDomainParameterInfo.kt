package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV_TAGS
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.x509.AlgorithmIdentifier

/**
 * Class representing the parameters for the supported cryptographic asymmetric PACE protocol
 */
class PACEDomainParameterInfo(rawFileContent : ByteArray) : SecurityInfo(rawFileContent) {
    var parameterId : Int?
        private set
    var algorithmIdentifier : AlgorithmIdentifier
        private set

    init {
        algorithmIdentifier = AlgorithmIdentifier.getInstance(ASN1InputStream(requiredData.toByteArray()).readAllBytes())
        parameterId = if (optionalData == null || optionalData!!.getTag().size != 1 || optionalData!!.getTag()[0] != TLV_TAGS.INTEGER ||
            optionalData!!.getValue() == null || optionalData!!.getValue()!!.size != 1) {
            null
        } else {
            optionalData!!.getValue()!![0].toInt()
        }
    }
}
