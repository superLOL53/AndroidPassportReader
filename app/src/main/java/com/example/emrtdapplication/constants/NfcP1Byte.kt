package com.example.emrtdapplication.constants

object NfcP1Byte {
    const val ZERO : Byte = 0x00
    const val EF_ID_IN_DATA_FIELD : Byte = 0x01
    const val SELECT_EF : Byte = 0x02
    const val SELECT_DF : Byte = 0x04
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xC1.toByte()
    const val SET_KEY_AGREEMENT_TEMPLATE : Byte = 0x41
    const val SET_DIGITAL_SIGNATURE_TEMPLATE_FOR_VERIFICATION : Byte = 0x81.toByte()
    const val TERMINAL_AUTHENTICATION : Byte = 0x81.toByte()
}