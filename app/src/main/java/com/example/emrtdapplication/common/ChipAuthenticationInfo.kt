package com.example.emrtdapplication.common

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_TYPE
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import java.math.BigInteger

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
 * @param tlv TLV structure containing an encoded instance of ChipAuthenticationInfo
 * @property version Protocol version, must be 1
 * @property keyId Id of the public key if multiple public keys for chip authentication are present
 * @throws IllegalArgumentException If [tlv] does not contain a ChipAuthenticationInfo
 */
class ChipAuthenticationInfo(tlv: TLV) : SecurityInfo(tlv, CHIP_AUTHENTICATION_TYPE) {
    var version : Int
        private set
    var keyId : BigInteger?
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
            if (optionalData.tag.size != 1 || optionalData.tag[0] != TlvTags.INTEGER ||
                optionalData.value == null) {
                throw IllegalArgumentException()
            } else {
                BigInteger(optionalData.value!!)
            }
        } else {
            null
        }
    }

    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        super.createViews(context, parent)
        if (tableLayout != null) {
            var row = createRow(context, parent)
            provideTextForRow(row, "Version:", version.toString())
            tableLayout!!.addView(row)
            if (keyId != null) {
                row = createRow(context, parent)
                provideTextForRow(row, "Key identifier:", keyId!!.toString(16))
                tableLayout!!.addView(row)
            }
        }
    }
}