package com.example.emrtdapplication

import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1ObjectIdentifier
import com.example.emrtdapplication.common.PACEInfo
import com.example.emrtdapplication.common.ActiveAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.common.PACEDomainParameterInfo
import com.example.emrtdapplication.common.TerminalAuthenticationInfo

const val UNKNOWN_TYPE = -1
const val PACE_INFO_TYPE = 0
const val ACTIVE_AUTHENTICATION_TYPE = 1
const val CHIP_AUTHENTICATION_TYPE = 2
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE = 3
const val TERMINAL_AUTHENTICATION_TYPE = 4
const val EF_DIR_TYPE = 5
const val PACE_DOMAIN_PARAMETER_INFO_TYPE = 6
const val PACE_OID = "0.4.0.127.0.7.2.2.4"
const val ACTIVE_AUTHENTICATION_OID = "2.23.136.1.1.5"
const val CHIP_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.3"
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID = "0.4.0.127.0.7.2.2.1"
const val TERMINAL_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.2"
const val EF_DIR_OID = "2.23.136.1.1.13"

/**
 * Class representing the ASN1 Sequence SecurityInfo:
 *
 *      SecurityInfo ::= SEQUENCE {
 *          protocol    OBJECT IDENTIFIER
 *          requiredData ANY DEFINED BY protocol
 *          optionalData ANY DEFINED BY protocol OPTIONAL
 *      }
 *
 * The following protocols are supported in this project:
 * - PACEInfo
 * - PACEDomainParameterInfo
 * - ActiveAuthenticationInfo
 * - ChipAuthenticationInfo
 * - ChipAuthenticationPublicKeyInfo
 * - TerminalAuthenticationInfo
 *
 * Subclasses:
 *
 * [PACEInfo], [PACEDomainParameterInfo], [ActiveAuthenticationInfo],
 * [ChipAuthenticationInfo], [ChipAuthenticationPublicKeyInfo], [TerminalAuthenticationInfo]
 *
 * @property objectIdentifier The Object Identifier of the protocol as a [String]
 * @property requiredData The required data of the protocol as a [TLV] Structure.
 * @property optionalData Optional data of the protocol as a [TLV] Structure. Can be null.
 * @property type The type of an instance of the class. Can be either unknown for unsupported protocols or one of the supported protocols
 * @property protocol The [objectIdentifier] represented as a [ByteArray]
 *
 * @param tlv The [TLV] Structure representing an ASN1 SecurityInfo Sequence
 */
open class SecurityInfo(tlv: TLV) {
    //var rawFileContent : ByteArray = rawFileContent
    //    private set
    var objectIdentifier : String
        private set
    var requiredData : TLV
        private set
    var optionalData : TLV? = null
        private set
    var type : Int = UNKNOWN_TYPE
        private set
    var protocol : ByteArray
        private set

    init {
        if (!tlv.getIsValid() || !tlv.isConstruct() || tlv.getTLVSequence() == null || tlv.getTLVSequence()!!.getTLVSequence().size < 2 || 3 < tlv.getTLVSequence()!!.getTLVSequence().size) {
            throw IllegalArgumentException("Invalid Sequence for type SecurityInfo")
        }
        objectIdentifier = ASN1ObjectIdentifier.getInstance(tlv.getTLVSequence()!!.getTLVSequence()[0].toByteArray()).id
        requiredData = tlv.getTLVSequence()!!.getTLVSequence()[1]
        protocol = tlv.getTLVSequence()!!.getTLVSequence()[0].getValue()!!
        if (tlv.getTLVSequence()!!.getTLVSequence().size == 3) {
            optionalData = tlv.getTLVSequence()!!.getTLVSequence()[2]
            if (!optionalData!!.getIsValid()) {
                throw IllegalArgumentException("Invalid present optional data for type SecurityInfo")
            }
        }
        if (objectIdentifier.startsWith(PACE_OID)) {
            objectIdentifier.split(".").size
            if (objectIdentifier.split(".").size == 10) {
                type = PACE_DOMAIN_PARAMETER_INFO_TYPE
            } else if (objectIdentifier.split(".").size == 11) {
                type = PACE_INFO_TYPE
            }
        } else if (objectIdentifier.startsWith(ACTIVE_AUTHENTICATION_OID)) {
            type = ACTIVE_AUTHENTICATION_TYPE
        } else if (objectIdentifier.startsWith(CHIP_AUTHENTICATION_OID)) {
            type = CHIP_AUTHENTICATION_TYPE
        } else if (objectIdentifier.startsWith(CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID)) {
            type = CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
        } else if (objectIdentifier.startsWith(TERMINAL_AUTHENTICATION_OID)) {
            type = TERMINAL_AUTHENTICATION_TYPE
        } else if (objectIdentifier.startsWith(EF_DIR_OID)) {
            type = EF_DIR_TYPE
        }
    }
}