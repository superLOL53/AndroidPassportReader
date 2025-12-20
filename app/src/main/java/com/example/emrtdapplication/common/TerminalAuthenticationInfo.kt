package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TlvTags

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
        version = if (!requiredData.isValid || requiredData.tag.size != 1 ||
            requiredData.tag[0] != TlvTags.INTEGER || requiredData.value == null ||
            requiredData.value!!.size != 1 || requiredData.value!![0].toInt() != 1) {
            throw IllegalArgumentException()
        } else {
            1
        }
    }
}