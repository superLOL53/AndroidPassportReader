package com.example.emrtdapplication.constants

object VisaRecordConstants {
    const val VISA_RECORD_SIZE = 4
    const val SIGNED_INFO_TAG : Byte = 0x71
    const val VISA_TAG_1 : Byte = 0x5F
    const val ISSUING_AUTHORITY_TAG : Byte = 0x28
    const val AUTHENTICITY_TOKEN_TAG : Byte = 0x37
    const val CERTIFICATE_REFERENCE_TAG : Byte = 0x38
    const val DOCUMENT_TYPE_TAG : Byte = 0x43
    const val ISSUANCE_PLACE_TAG : Byte = 0x49
    const val DOCUMENT_NUMBER_TAG : Byte = 0x5A
    const val HOLDER_NAME_TAG : Byte = 0x5B
    const val VISA_TYPE_A_TAG : Byte = 0x71
    const val VISA_TYPE_B_TAG : Byte = 0x72
    const val NUMBER_OF_ENTRIES_TAG : Byte = 0x73
    const val STAY_DURATION_TAG : Byte = 0x74
    const val PASSPORT_NUMBER_TAG : Byte = 0x75
    const val VISA_TYPE_TAG : Byte = 0x76
    const val TERRITORY_INFORMATION_TAG : Byte = 0x77
    const val ISSUANCE_DATE_TAG : Byte = 0x25
    const val EXPIRATION_DATE_TAG : Byte = 0x24
    const val ADDITIONAL_INFORMATION_TAG : Byte = 0x32
    const val SURNAME_TAG : Byte = 0x33
    const val GIVEN_NAME_TAG : Byte = 0x34
    const val SEX_TAG : Byte = 0x35
    const val BIRTHDATE_TAG : Byte = 0x2B
    const val NATIONALITY_TAG : Byte = 0x2C
    const val MRZ_TAG : Byte = 0x1F
    const val ADDITIONAL_BIOMETRICS_REFERENCE_TAG : Byte = 0x40
}