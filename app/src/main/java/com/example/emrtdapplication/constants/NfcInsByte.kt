package com.example.emrtdapplication.constants

object NfcInsByte {
    const val SELECT : Byte = 0xA4.toByte()
    const val READ_BINARY : Byte = 0xB0.toByte()
    const val REQUEST_RANDOM_NUMBER = 0x84.toByte()
    const val EXTERNAL_AUTHENTICATE : Byte = 0x82.toByte()
    const val MANAGE_SECURITY_ENVIRONMENT : Byte = 0x22
    const val GENERAL_AUTHENTICATE : Byte = 0x86.toByte()
    const val INTERNAL_AUTHENTICATE : Byte = 0x88.toByte()
    const val FILE_AND_MEMORY_MANAGEMENT : Byte = 0x5E
    const val READ_RECORD : Byte = 0xB2.toByte()
    const val PERFORM_SECURITY_OPERATION : Byte = 0x2A
    const val READ_BINARY_LARGE_OFFSET : Byte = 0xB1.toByte()
}