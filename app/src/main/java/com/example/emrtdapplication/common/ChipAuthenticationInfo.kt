package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV_TAGS

class ChipAuthenticationInfo(rawFileContent: ByteArray) : SecurityInfo(rawFileContent) {
    var version : Int
        private set
    var keyId : Int?
        private set

    init {
        if (requiredData.getTag().size != 1 || requiredData.getTag()[0] != TLV_TAGS.INTEGER ||
            requiredData.getValue() == null || requiredData.getValue()!!.size != 1 ||
            requiredData.getValue()!![0].toInt() != 1) {
            throw IllegalArgumentException()
        } else {
            version = 1
        }
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