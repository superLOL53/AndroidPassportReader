package com.example.emrtdapplication.constants

/**
 * Enum class for NFC types. eMRTD uses ISO DEP as communication protocol. All other are insufficient and not used.
 */
enum class NfcUse {
    /**
     * Unknown or unsupported NFC types for eMRTD
     */
    UNDEFINED,

    /**
     * eMRTD communication NFC type
     */
    ISO_DEP
}