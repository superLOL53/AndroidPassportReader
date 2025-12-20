package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TlvTags

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence ChipAuthenticationInfo:
 *
 *      ChipAuthenticationInfo ::= SEQUENCE {
 *          protocol    OBJECT IDENTIFIER(
 *                      id-CA-DH-3DES-CBC-CBC |
 *                      id-CA-DH-AES-CBC-CMAC-128 |
 *                      id-CA-DH-AES-CBC-CMAC-192 |
 *                      id-CA-DH-AES-CBC-CMAC-256 |
 *                      id-CA-ECDH-3DES-CBC-CBC |
 *                      id-CA-ECDH-AES-CBC-CMAC-128 |
 *                      id-CA-ECDH-AES-CBC-CMAC-192 |
 *                      id-CA-ECDH-AES-CBC-CMAC-256),
 *          version INTEGER, -- MUST be 1
 *          keyId INTEGER OPTIONAL
 *      }
 *
 * @property version Protocol version, must be 1
 * @property keyId Id of the public key if multiple public keys for chip authentication are present
 */
class ChipAuthenticationInfo(tlv: TLV) : SecurityInfo(tlv) {
    var version : Int
        private set
    var keyId : Int?
        private set

    init {
        if (requiredData.tag.size != 1 || requiredData.tag[0] != TlvTags.INTEGER ||
            requiredData.value == null || requiredData.value!!.size != 1 ||
            requiredData.value!![0].toInt() != 1) {
            throw IllegalArgumentException()
        } else {
            version = 1
        }
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