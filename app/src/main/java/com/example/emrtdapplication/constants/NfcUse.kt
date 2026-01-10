package com.example.emrtdapplication.constants

/**
 * Enum class for NFC types. eMRTD uses ISO DEP as communication protocol. All other are insufficient and not used.
 */
enum class NfcUse {
    UNDEFINED,
    ISO_DEP
}