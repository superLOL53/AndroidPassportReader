package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLV_TAGS

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