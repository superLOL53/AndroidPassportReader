package com.example.emrtdapplication.constants

object ElementaryFileTemplateConstants {
    /**
     * First byte of the long identifier for EFs
     */
    const val LONG_EF_ID : Byte = 0x01

    /**
     * Bytes to read from an EF to determine the length of the file
     */
    const val READ_LENGTH = 0x10

    /**
     * Modulo value for UByte conversion
     */
    const val U_BYTE_MODULO = 256

    /**
     * Modulo value for Byte conversion
     */
    const val BYTE_MODULO = 128
}