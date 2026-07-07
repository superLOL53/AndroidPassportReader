package com.example.emrtdapplication

import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1ObjectIdentifier

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