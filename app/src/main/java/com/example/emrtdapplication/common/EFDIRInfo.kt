package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.SecurityInfoConstants.EF_DIR_TYPE
import com.example.emrtdapplication.utils.TLV

/**
 * Inherits from [SecurityInfo] and implements the ASN1Sequence EFDIRInfo:
 *
 *      EFDIRInfo ::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-EFDIR),
 *          EFDIR OCTET STRING
 *      }
 *
 * where
 *
 *      id-EFDIR OBJECT IDENTIFIER ::={
 *          id-icao-mrtd-security 13
 *      }
 *
 * @param tlv TLV structure containing an instance of a EF.DIR file
 * @property efDir The EF.DIR file
 */
class EFDIRInfo(tlv: TLV) : SecurityInfo(tlv, EF_DIR_TYPE) {
    val efDir = Directory(tlv.toByteArray())

    /*override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        //efDir.createView(context, parent)
    }*/
}