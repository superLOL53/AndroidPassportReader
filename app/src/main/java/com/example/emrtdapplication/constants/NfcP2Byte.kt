package com.example.emrtdapplication.constants

object NfcP2Byte {
    const val ZERO : Byte = 0x00
    const val SELECT_FILE : Byte = 0x0C
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xA4.toByte()
    const val SET_KEY_AGREEMENT_TEMPLATE : Byte = 0xA6.toByte()
    const val NUMBER_OF_RECORDS : Byte = 0x04
    const val READ_SINGLE_RECORD : Byte = 0x04
    const val SET_DIGITAL_SIGNATURE_TEMPLATE_FOR_VERIFICATION : Byte = 0xB6.toByte()
    const val VERIFY_SELF_DESCRIPTIVE_CERTIFICATE : Byte = 0xBE.toByte()
    const val TERMINAL_AUTHENTICATION : Byte = 0xA4.toByte()
}