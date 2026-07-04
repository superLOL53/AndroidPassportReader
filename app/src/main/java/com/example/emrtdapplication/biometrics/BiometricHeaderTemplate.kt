package com.example.emrtdapplication.biometrics

import com.example.emrtdapplication.BYTE_BIT_SIZE
import com.example.emrtdapplication.utils.TLV

const val BIOMETRIC_HEADER_TEMPLATE_TAG_SIZE = 1
const val BIOMETRIC_HEADER_TEMPLATE_TAG = 0xA1.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_MIN_SEQUENCE_SIZE = 2
const val BIOMETRIC_HEADER_TEMPLATE_MAX_SEQUENCE_SIZE = 8
const val BIOMETRIC_HEADER_TEMPLATE_INVALID_TAG_STRING = "Illegal Tag in the Biometric Header Template"
const val BIOMETRIC_HEADER_TEMPLATE_INVALID_TEMPLATE_STRING = "Biometric Header Template does not conform to the Specification"
const val BIOMETRIC_HEADER_TEMPLATE_HEADER_TAG = 0x80.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_BIOMETRIC_TYPE_TAG = 0x81.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_SUBTYPE_TAG = 0x82.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_CREATION_TIME_TAG = 0x83.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_VALIDITY_PERIOD_TAG = 0x84.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_CREATOR_TAG = 0x86.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_OWNER_TAG = 0x87.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_FORMAT_TYPE_TAG = 0x88.toByte()
const val BIOMETRIC_HEADER_TEMPLATE_UNKNOWN_OWNER_OR_TYPE_STRING = "Owner and/or Type is not present"
const val BIOMETRIC_HEADER_TEMPLATE_HEADER_SIZE = 2
const val BIOMETRIC_HEADER_TEMPLATE_INVALID_HEADER_SIZE_STRING = "Header version has invalid length"
const val BIOMETRIC_HEADER_TEMPLATE_SUBTYPE_SIZE = 1
const val BIOMETRIC_HEADER_TEMPLATE_INVALID_SUBTYPE_SIZE_STRING = "Invalid Subtype value"
const val BIOMETRIC_HEADER_TEMPLATE_OWNER_SIZE = 2
const val BIOMETRIC_HEADER_TEMPLATE_INVALID_OWNER_SIZE_STRING = "Invalid length for the owner field"
const val BIOMETRIC_HEADER_TEMPLATE_FORMAT_TYPE_SIZE = 2
const val BIOMETRIC_HEADER_TEMPLATE_INVALID_FORMAT_TYPE_SIZE_STRING = "Invalid length for the format type field"

/**
 * Class representing a Biometric Header Template (BHT)
 * @param biometricHeaderTemplate A TLV structure encoding a BHT
 * @property headerVersion Version of the header template
 * @property biometricType The type of the encoded biometric feature in the Biometric Data Block
 * @property biometricSubType The subtype of the encoded biometric feature
 * @property creationTime Time and date the biometric feature was created
 * @property validityPeriod Valid time period of the biometric feature
 * @property biometricReferenceDataCreator Creator of the biometric feature
 * @property formatOwner Owner of the format of the encoded BDB
 * @property formatType Type of the format of the encoded BDB
 * @throws IllegalArgumentException If the [biometricHeaderTemplate] contains an invalid encoded BHT
 */
class BiometricHeaderTemplate(biometricHeaderTemplate: TLV) {
    var headerVersion: Short? = null
    var biometricType: ByteArray? = null
    var biometricSubType: Byte? = null
    var creationTime: ByteArray? = null
    var validityPeriod: ByteArray? = null
    var biometricReferenceDataCreator: Int? = null
    val formatOwner: Short
    val formatType: Short

    init {
        if (biometricHeaderTemplate.tag.size != BIOMETRIC_HEADER_TEMPLATE_TAG_SIZE ||
            biometricHeaderTemplate.tag[0] != BIOMETRIC_HEADER_TEMPLATE_TAG ||
            biometricHeaderTemplate.list == null ||
            biometricHeaderTemplate.list!!.tlvSequence.size <
                BIOMETRIC_HEADER_TEMPLATE_MIN_SEQUENCE_SIZE ||
            BIOMETRIC_HEADER_TEMPLATE_MAX_SEQUENCE_SIZE <
                biometricHeaderTemplate.list!!.tlvSequence.size
        ) {
            throw IllegalArgumentException(BIOMETRIC_HEADER_TEMPLATE_INVALID_TEMPLATE_STRING)
        }
        var owner: Short? = null
        var type: Short? = null
        for (tlv in biometricHeaderTemplate.list!!.tlvSequence) {
            if (tlv.tag.size != BIOMETRIC_HEADER_TEMPLATE_TAG_SIZE) {
                throw IllegalArgumentException(BIOMETRIC_HEADER_TEMPLATE_INVALID_TAG_STRING)
            }
            when (tlv.tag[0]) {
                BIOMETRIC_HEADER_TEMPLATE_HEADER_TAG -> setHeaderVersion(tlv.value)
                BIOMETRIC_HEADER_TEMPLATE_BIOMETRIC_TYPE_TAG -> biometricType = tlv.value
                BIOMETRIC_HEADER_TEMPLATE_SUBTYPE_TAG -> setSubType(tlv.value)
                BIOMETRIC_HEADER_TEMPLATE_CREATION_TIME_TAG -> creationTime = tlv.value
                BIOMETRIC_HEADER_TEMPLATE_VALIDITY_PERIOD_TAG -> validityPeriod = tlv.value
                BIOMETRIC_HEADER_TEMPLATE_CREATOR_TAG -> setCreator(tlv.value)
                BIOMETRIC_HEADER_TEMPLATE_OWNER_TAG -> owner = setOwner(tlv.value)
                BIOMETRIC_HEADER_TEMPLATE_FORMAT_TYPE_TAG -> type = setFormatType(tlv.value)
            }
        }
        if (owner == null || type == null) {
            throw IllegalArgumentException(BIOMETRIC_HEADER_TEMPLATE_UNKNOWN_OWNER_OR_TYPE_STRING)
        }
        formatOwner = owner
        formatType = type
    }

    private fun setHeaderVersion(header: ByteArray?) {
        if (header == null) return
        if (header.size != BIOMETRIC_HEADER_TEMPLATE_HEADER_SIZE) {
            throw IllegalArgumentException(BIOMETRIC_HEADER_TEMPLATE_INVALID_HEADER_SIZE_STRING)
        }
        headerVersion = ((header[0].toInt() shl BYTE_BIT_SIZE) + header[1]).toShort()
    }

    private fun setSubType(subType: ByteArray?) {
        if (subType == null) return
        if (subType.size != BIOMETRIC_HEADER_TEMPLATE_SUBTYPE_SIZE) {
            throw IllegalArgumentException(BIOMETRIC_HEADER_TEMPLATE_INVALID_SUBTYPE_SIZE_STRING)
        }
        biometricSubType = subType[0]
    }

    private fun setCreator(creator: ByteArray?) {
        if (creator == null) return
        biometricReferenceDataCreator = 0
        for (b in creator) {
            biometricReferenceDataCreator = (biometricReferenceDataCreator!! shl BYTE_BIT_SIZE) + b
        }
    }

    private fun setOwner(owner: ByteArray?): Short {
        if (owner == null) return 0
        if (owner.size != BIOMETRIC_HEADER_TEMPLATE_OWNER_SIZE) {
            throw IllegalArgumentException(BIOMETRIC_HEADER_TEMPLATE_INVALID_OWNER_SIZE_STRING)
        }
        return ((owner[0].toInt() shl BYTE_BIT_SIZE) + owner[1]).toShort()
    }

    private fun setFormatType(type: ByteArray?): Short {
        if (type == null || type.size != BIOMETRIC_HEADER_TEMPLATE_FORMAT_TYPE_SIZE) {
            throw IllegalArgumentException(
                BIOMETRIC_HEADER_TEMPLATE_INVALID_FORMAT_TYPE_SIZE_STRING
            )
        }
        return ((type[0].toInt() shl BYTE_BIT_SIZE)+type[1]).toShort()
    }
}