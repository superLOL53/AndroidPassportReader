package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.SecurityInfoConstants.TERMINAL_AUTHENTICATION_TYPE
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence TerminalAuthenticationInfo:
 *
 *      TerminalAuthenticationInfo ::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-TA),
 *          version INTEGER -- MUST be 1
 *      }
 *
 * @param tlv TLV structure containing an encoded instance of TerminalAuthenticationInfo
 * @property version The protocol version. Must be one
 * @throws IllegalArgumentException If [tlv] does not contain an encoded instance of TerminalAuthenticationInfo
 */
class TerminalAuthenticationInfo(tlv: TLV) : SecurityInfo(tlv, TERMINAL_AUTHENTICATION_TYPE) {

    val version : Int = if (!requiredData.isValid || requiredData.tag.size != 1 ||
        requiredData.tag[0] != TlvTags.INTEGER || requiredData.value == null ||
        requiredData.value!!.size != 1 || requiredData.value!![0].toInt() != 1) {
        throw IllegalArgumentException()
    } else {
        1
    }
}