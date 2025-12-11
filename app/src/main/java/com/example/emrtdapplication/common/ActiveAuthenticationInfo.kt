package com.example.emrtdapplication.common

import com.example.emrtdapplication.ACTIVE_AUTHENTICATION_TYPE
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV_TAGS
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1InputStream

class ActiveAuthenticationInfo(rawFileContent : ByteArray) : SecurityInfo(rawFileContent) {
    var version : Int
        private set
    var signatureAlgorithm : String
        private set

    init {
        if (type != ACTIVE_AUTHENTICATION_TYPE) throw IllegalArgumentException("Active Authentication Info must have type of Active Authentication Info")
        if (!requiredData.getIsValid() || requiredData.getTag().size != 1 || requiredData.getTag()[0] != TLV_TAGS.INTEGER ||
            requiredData.getValue() == null || requiredData.getValue()!!.size != 1 || requiredData.getValue()!![0].toInt() != 1) {
            throw IllegalArgumentException()
        } else {
            version = 1
        }
        if (optionalData == null || !optionalData!!.getIsValid() || optionalData!!.getTag().size != 1 ||
            optionalData!!.getTag()[0] != TLV_TAGS.OID || optionalData!!.getValue() == null) {
            throw IllegalArgumentException()
        } else {
            signatureAlgorithm = ASN1ObjectIdentifier.getInstance(ASN1InputStream(optionalData!!.toByteArray())).id
        }
    }
}