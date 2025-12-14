package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLV_TAGS

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence TerminalAuthenticationInfo:
 *
 *      TerminalAuthenticationInfo ::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-TA),
 *          version INTEGER -- MUST be 1
 *      }
 *
 * @property version The protocol version. Must be one
 */
class TerminalAuthenticationInfo(tlv: TLV) : SecurityInfo(tlv) {
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