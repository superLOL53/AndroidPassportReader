package com.example.emrtdapplication.constants

/**
 * Constants for the P1 argument byte in a command APDU
 */
object NfcP1Byte {
    /**
     * No argument given for the current command APDU instruction
     */
    const val ZERO : Byte = 0x00

    /**
     * Argument indicating the file identifier is in the data field of the command APDU
     */
    const val EF_ID_IN_DATA_FIELD : Byte = 0x01

    /**
     * Argument for selecting an EF file
     */
    const val SELECT_EF : Byte = 0x02

    /**
     * Argument for selecting an application
     */
    const val SELECT_DF : Byte = 0x04

    /**
     * Argument for setting an authentication template for terminal authentication
     */
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xC1.toByte()

    /**
     * Argument for setting a key agreement template for the chip authentication protocol
     */
    const val SET_KEY_AGREEMENT_TEMPLATE : Byte = 0x41

    /**
     * Argument for setting up a digital signature verification template on the eMRTD for terminal authentication
     */
    const val SET_DIGITAL_SIGNATURE_TEMPLATE_FOR_VERIFICATION : Byte = 0x81.toByte()
}