package com.example.emrtdapplication.utils

const val ZERO_BYTE : Byte = 0x0
const val ZERO_SHORT : Short = 0
const val FILE_SUCCESSFUL_READ = 0
const val SUCCESS = 0
const val FAILURE = -1
const val FILE_UNABLE_TO_SELECT = -1
const val FILE_UNABLE_TO_READ = -2
const val NOT_IMPLEMENTED = -3
const val INVALID_ARGUMENT = -4

const val SELECT_APPLICATION_SUCCESS = 0
const val UNABLE_TO_SELECT_APPLICATION = -1
const val ADDITIONAL_ENCRYPTION_LENGTH = 30

object TLV_TAGS {
    const val CONSTRUCT_BIT : Byte = 0x20
    const val INTEGER : Byte = 0x02
    const val CRYPTOGRAPHIC_REFERENCE : Byte = 0x80.toByte()
    const val KEY_REFERENCE : Byte = 0x83.toByte()
    const val NONCE_QUERY : Byte = 0x7C
    const val DYNAMIC_AUTHENTICATION_DATA : Byte = 0x7C
    const val MAPPING_DATA : Byte = 0x81.toByte()
    const val PUBLIC_KEY : Byte = 0x83.toByte()
    const val TERMINAL_AUTHENTICATION_TOKEN : Byte = 0x85.toByte()
    const val EC_PUBLIC_POINT : Byte = 0x86.toByte()
    const val OID : Byte = 0x06
    const val UNSIGNED_INTEGER : Byte = 0x84.toByte()
    const val SEQUENCE: Byte = 0x30
}

object NfcClassByte {
    const val ZERO : Byte = 0x0
    const val SECURE_MESSAGING : Byte = 0x0c
    const val COMMAND_CHAINING : Byte = 0x10
}

object NfcInsByte {
    const val SELECT : Byte = 0xA4.toByte()
    const val READ_BINARY : Byte = 0xB0.toByte()
    const val REQUEST_RANDOM_NUMBER = 0x84.toByte()
    const val EXTERNAL_AUTHENTICATE : Byte = 0x82.toByte()
    const val MANAGE_SECURITY_ENVIRONMENT : Byte = 0x22
    const val GENERAL_AUTHENTICATE : Byte = 0x86.toByte()
    const val INTERNAL_AUTHENTICATE : Byte = 0x88.toByte()
}

object NfcP1Byte {
    const val ZERO : Byte = 0x00
    const val SELECT_EF : Byte = 0x02
    const val SELECT_DF : Byte = 0x04
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xC1.toByte()
    const val SET_KEY_AGREEMENT_TEMPLATE : Byte = 0x41
}

object NfcP2Byte {
    const val ZERO : Byte = 0x00
    const val SELECT_FILE : Byte = 0x0C
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xA4.toByte()
    const val SET_KEY_AGREEMENT_TEMPLATE : Byte = 0xA6.toByte()
}

object NfcRespondCodeSW1 {
    const val OK : Byte = 0x90.toByte()
}

object NfcRespondCodeSW2 {
    const val OK : Byte = 0x00
}
