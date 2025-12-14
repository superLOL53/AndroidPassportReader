package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.TLV

/**
 * Inherits from [SecurityInfo] and implements the ASN1Sequence EFDIRInfo:
 *
 *      EFDIRInfo ::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-EFDIR),
 *          eFDIR OCTET STRING
 *      }
 *
 * where
 *
 *      id-EFDIR OBJECT IDENTIFIER ::={
 *          id-icao-mrtd-security 13
 *      }
 *
 * @property efDir The EF.DIR file
 */
class EFDIRInfo(tlv: TLV) : SecurityInfo(tlv) {
    val efDir = Directory(tlv.toByteArray())
}