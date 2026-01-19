package com.example.emrtdapplication.constants

/**
 * Constants for SW1 respond codes in a response APDU
 */
object NfcRespondCodeSW1 {
    /**
     * Respond code indicating the command APDU was processed correctly and successfully
     */
    const val OK : Byte = 0x90.toByte()
}