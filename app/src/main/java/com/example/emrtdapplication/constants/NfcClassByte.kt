package com.example.emrtdapplication.constants

/**
 * Constants for the CLA Byte in a command APDU
 */
object NfcClassByte {
    /**
     * Empty CLA Byte
     */
    const val ZERO : Byte = 0x0

    /**
     * CLA Byte for secure messaging APDUs
     */
    const val SECURE_MESSAGING : Byte = 0x0c

    /**
     * CLA Byte for command chaining
     */
    const val COMMAND_CHAINING : Byte = 0x10
}