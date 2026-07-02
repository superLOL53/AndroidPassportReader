package com.example.emrtdapplication.constants

object BiometricHeaderTemplateConstants {
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
}