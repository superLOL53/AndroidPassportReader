package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_DOMAIN_PARAMETER_INFO_TYPE
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import org.spongycastle.asn1.x509.AlgorithmIdentifier
import java.math.BigInteger

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence PACEDomainParameterInfo:
 *
 *      PACEDomainParameterInfo ::= SEQUENCE {
 *              protocol OBJECT IDENTIFIER(
 *                      id-PACE-DH-GM |
 *                      id-PACE-ECDH-GM |
 *                      id-PACE-DH-IM |
 *                      id-PACE-ECDH-IM |
 *                      id-PACE-ECDH-CAM),
 *              domainParameter AlgorithmIdentifier,
 *              parameterId INTEGER OPTIONAL
 *      }
 *
 * @param tlv TLV structure containing an encoded instance of PACEDomainParameterInfo
 * @property parameterId The ID of the cryptographic domain parameters
 * @property algorithmIdentifier ASN1 Algorithm Identifier
 */
class PACEDomainParameterInfo(tlv: TLV) : SecurityInfo(tlv, PACE_DOMAIN_PARAMETER_INFO_TYPE) {
    val parameterId : BigInteger?
    val algorithmIdentifier : AlgorithmIdentifier

    init {
        try {
            algorithmIdentifier = AlgorithmIdentifier.getInstance(requiredData.toByteArray())
        } catch (_ : Exception) {
            throw IllegalArgumentException("Required data does not contain an AlgorithmIdentifier!")
        }
        parameterId = if (optionalData == null || optionalData.tag.size != 1 || optionalData.tag[0] != TlvTags.INTEGER ||
            optionalData.value == null) {
            null
        } else {
            BigInteger(optionalData.value!!)
        }
    }

    /*override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        super.createViews(context, parent)
        var row = createRow(context, parent)
        provideTextForRow(row, "Algorithm OID:", algorithmIdentifier.algorithm.id)
        tableLayout!!.addView(row)
        if (parameterId != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Parameter ID:", parameterId.toString(16))
            tableLayout!!.addView(row)
        }
    }*/
}
