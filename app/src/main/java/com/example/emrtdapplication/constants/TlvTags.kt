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
    const val EPHEMERAL_PUBLIC_KEY : Byte = 0x91.toByte()
    const val OID : Byte = 0x06
    const val UNSIGNED_INTEGER : Byte = 0x84.toByte()
    const val PRIVATE_KEY_REFERENCE : Byte = 0x84.toByte()
    const val DO01 : Byte = 0x01
    const val DO08 : Byte = 0x08
    const val DO51 : Byte = 0x51
    const val DO85 : Byte = 0x85.toByte()
    const val DO87 : Byte = 0x87.toByte()
    const val DO8E : Byte = 0x8E.toByte()
    const val DO97 : Byte = 0x97.toByte()
    const val DO97_LE_LENGTH : Byte = 0x01
    const val DO97_EXTENDED_LE_LENGTH : Byte = 0x02
    const val TAG_MULTIPLE_BYTES : Byte = 0x1F
    const val LENGTH_MULTIPLE_BYTES : Byte = 0x7F
    const val CERTIFICATE_BODY : Byte = 0x4E
}