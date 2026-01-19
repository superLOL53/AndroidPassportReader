package com.example.emrtdapplication.constants

/**
 * Constants for the INS byte in a command APDU
 */
object NfcInsByte {
    /**
     * Selecting a file/application on the eMRTD
     */
    const val SELECT : Byte = 0xA4.toByte()

    /**
     * Reading a binary file from the eMRTD. Offset must be less or equal to 32767 bytes
     */
    const val READ_BINARY : Byte = 0xB0.toByte()

    /**
     * Requests a random number from the eMRTD
     */
    const val REQUEST_RANDOM_NUMBER = 0x84.toByte()

    /**
     * External authentication instruction
     */
    const val EXTERNAL_AUTHENTICATE : Byte = 0x82.toByte()

    /**
     * Manages the current security environment between the reader and the eMRTD
     */
    const val MANAGE_SECURITY_ENVIRONMENT : Byte = 0x22

    /**
     * General authentication instruction
     */
    const val GENERAL_AUTHENTICATE : Byte = 0x86.toByte()

    /**
     * Internal authentication instruction
     */
    const val INTERNAL_AUTHENTICATE : Byte = 0x88.toByte()

    /**
     * Manages files and memory. Used to determine the number of records in an EF
     */
    const val FILE_AND_MEMORY_MANAGEMENT : Byte = 0x5E

    /**
     * Reading a record from the selected file
     */
    const val READ_RECORD : Byte = 0xB2.toByte()

    /**
     * The eMRTD validates a signature from the reader
     */
    const val PERFORM_SECURITY_OPERATION : Byte = 0x2A

    /**
     * Reading a binary file from the eMRTD. Offset must be greater than 32767 bytes
     */
    const val READ_BINARY_LARGE_OFFSET : Byte = 0xB1.toByte()
}