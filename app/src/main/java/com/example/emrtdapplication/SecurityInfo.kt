package com.example.emrtdapplication

import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1ObjectIdentifier

const val SECURITY_INFO_MIN_SEQUENCE_SIZE = 2
const val SECURITY_INFO_MAX_SEQUENCE_SIZE = 3
const val REQUIRED_DATA_SEQUENCE_INDEX = 1
const val PROTOCOL_OID_SEQUENCE_INDEX = 0
const val OPTIONAL_DATA_SEQUENCE_INDEX = 2
/**
 * Type of Security Info is PACE Info
 */
const val PACE_INFO_TYPE = 0

/**
 * Type of Security Info is Active Authentication Info
 */
const val ACTIVE_AUTHENTICATION_TYPE = 1

/**
 * Type of Security Info is Chip Authentication Info
 */
const val CHIP_AUTHENTICATION_TYPE = 2

/**
 * Type of Security Info is Chip Authentication Public Key Info
 */
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE = 3

/**
 * Type of Security Info is Terminal Authentication Info
 */
const val TERMINAL_AUTHENTICATION_TYPE = 4

/**
 * Type of Security Info is EF.DIR Info
 */
const val EF_DIR_TYPE = 5

/**
 * Type of Security Info is PACE Domain Parameter Info
 */
const val PACE_DOMAIN_PARAMETER_INFO_TYPE = 6

/**
 * OID for PACE Info or PACE Domain Parameter Info Security Info type
 */
const val PACE_OID = "0.4.0.127.0.7.2.2.4"

/**
 * OID for Active Authentication Info Security Info type
 */
const val ACTIVE_AUTHENTICATION_OID = "2.23.136.1.1.5"

/**
 * OID for Chip Authentication Info Security Info type
 */
const val CHIP_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.3"

/**
 * OID for Chip Authentication Public Key Info Security Info type
 */
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID = "0.4.0.127.0.7.2.2.1"

/**
 * OID for Terminal Authentication Info Security Info type
 */
const val TERMINAL_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.2"

/**
 * OID for EF.DIR Info Security Info type
 */
const val EF_DIR_OID = "2.23.136.1.1.13"

/**
 * OID size of PACE Info type
 */
const val PACE_INFO_TYPE_SIZE = 11

/**
 * OID size of PACE Domain Parameter Info type
 */
const val PACE_DOMAIN_PARAMETER_INFO_TYPE_SIZE = 10

/**
 * Class representing the ASN1 Sequence SecurityInfo:
 *
 *      SecurityInfo ::= SEQUENCE {
 *          protocol    OBJECT IDENTIFIER
 *          requiredData ANY DEFINED BY protocol
 *          optionalData ANY DEFINED BY protocol OPTIONAL
 *      }
 *
 * The following security infos are supported in this project:
 * - PACEInfo
 * - PACEDomainParameterInfo
 * - ActiveAuthenticationInfo
 * - ChipAuthenticationInfo
 * - ChipAuthenticationPublicKeyInfo
 * - TerminalAuthenticationInfo
 *
 * @param tlv The [TLV] Structure representing an ASN1 SecurityInfo Sequence
 * @property objectIdentifier The Object Identifier of the protocol as a [String]
 * @property requiredData The required data of the protocol as a [TLV] Structure.
 * @property optionalData Optional data of the protocol as a [TLV] Structure. Can be null.
 * @property type The type of instance of the class. Can be either unknown for unsupported protocols
 * or one of the supported protocols
 * @property protocol The [objectIdentifier] represented as a [ByteArray]
 * @throws IllegalArgumentException If [tlv] does not contain any of the subclasses of a SecurityInfo
 */
open class SecurityInfo(tlv: TLV, val type: Int) {
    val objectIdentifier: String
    val requiredData: TLV
    val optionalData: TLV?
    val protocol: ByteArray

    init {
        if (!tlv.isValid || !tlv.isConstruct() || tlv.list == null ||
            tlv.list!!.tlvSequence.size < SECURITY_INFO_MIN_SEQUENCE_SIZE ||
            SECURITY_INFO_MAX_SEQUENCE_SIZE < tlv.list!!.tlvSequence.size) {
                throw IllegalArgumentException("Invalid Sequence for SecurityInfo")
        }
        try {
            objectIdentifier = ASN1ObjectIdentifier.getInstance(
                tlv.list!!.tlvSequence[PROTOCOL_OID_SEQUENCE_INDEX].toByteArray()
            ).id
        } catch (_: Exception) {
            throw IllegalArgumentException("Unable to decode object identifier in SecurityInfo!")
        }
        requiredData = tlv.list!!.tlvSequence[REQUIRED_DATA_SEQUENCE_INDEX]
        protocol = tlv.list!!.tlvSequence[PROTOCOL_OID_SEQUENCE_INDEX].value!!
        if (tlv.list!!.tlvSequence.size == SECURITY_INFO_MAX_SEQUENCE_SIZE) {
            optionalData = tlv.list!!.tlvSequence[OPTIONAL_DATA_SEQUENCE_INDEX]
            if (!optionalData.isValid) {
                throw IllegalArgumentException("Invalid optional data for SecurityInfo!")
            }
        } else {
            optionalData = null
        }
    }
}