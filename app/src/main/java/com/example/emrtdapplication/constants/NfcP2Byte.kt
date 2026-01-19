package com.example.emrtdapplication.constants

object NfcP2Byte {
    /**
     * No argument for current command APDU instruction
     */
    const val ZERO : Byte = 0x00

    /**
     * Argument for selecting a file on the eMRTD
     */
    const val SELECT_FILE : Byte = 0x0C

    /**
     * Argument for setting up authentication
     */
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xA4.toByte()

    /**
     * Argument for setting up a key agreement
     */
    const val SET_KEY_AGREEMENT_TEMPLATE : Byte = 0xA6.toByte()

    /**
     * Argument for reading the number of records in an EF
     */
    const val NUMBER_OF_RECORDS : Byte = 0x04

    /**
     * Argument for reading a single record from the eMRTD
     */
    const val READ_SINGLE_RECORD : Byte = 0x04

    /**
     * Argument for setting up digital signature verification on the eMRTD
     */
    const val SET_DIGITAL_SIGNATURE_TEMPLATE_FOR_VERIFICATION : Byte = 0xB6.toByte()

    /**
     * Argument for verifying the generated signature from the reader on the eMRTD
     */
    const val VERIFY_SELF_DESCRIPTIVE_CERTIFICATE : Byte = 0xBE.toByte()

    /**
     * Argument for setting up authentication for the reader
     */
    const val TERMINAL_AUTHENTICATION : Byte = 0xA4.toByte()
}