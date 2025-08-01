package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV_TAGS

class ActiveAuthenticationInfo(rawFileContent : ByteArray) : SecurityInfo(rawFileContent) {
    var version : Int
        private set
    var signatureAlgorithm : ByteArray
        private set

    init {
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
            signatureAlgorithm = optionalData!!.getValue()!!
        }
    }
}