package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV_TAGS

class TerminalAuthenticationInfo(rawFileContent: ByteArray) : SecurityInfo(rawFileContent) {
    var version : Int
        private set

    init {
        version = if (!requiredData.getIsValid() || requiredData.getTag().size != 1 ||
            requiredData.getTag()[0] != TLV_TAGS.INTEGER || requiredData.getValue() == null ||
            requiredData.getValue()!!.size != 1 || requiredData.getValue()!![0].toInt() != 1) {
            throw IllegalArgumentException()
        } else {
            1
        }
    }
}