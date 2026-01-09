package com.example.emrtdapplication.constants

object TlvTags {
    const val CONSTRUCT_BIT : Byte = 0x20
    const val INTEGER : Byte = 0x02
    const val CRYPTOGRAPHIC_REFERENCE : Byte = 0x80.toByte()
    const val KEY_REFERENCE : Byte = 0x83.toByte()
    const val NONCE_QUERY : Byte = 0x7C
    const val DYNAMIC_AUTHENTICATION_DATA : Byte = 0x7C
    const val MAPPING_DATA : Byte = 0x81.toByte()
    const val PUBLIC_KEY : Byte = 0x83.toByte()
    const val TERMINAL_AUTHENTICATION_TOKEN : Byte = 0x85.toByte()
    const val EC_PUBLIC_POINT : Byte = 0x86.toByte()
    const val OID : Byte = 0x06
    const val UNSIGNED_INTEGER : Byte = 0x84.toByte()
    const val DO51 : Byte = 0x51
    //const val SEQUENCE: Byte = 0x30
}