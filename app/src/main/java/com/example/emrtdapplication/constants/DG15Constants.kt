package com.example.emrtdapplication.constants

/**
 * Constants for [com.example.emrtdapplication.lds1.DG15] class
 */
object DG15Constants {
    /**
     * Byte value indicating the use of SHA-1 as message digest in the active authentication protocol
     */
    const val SHA_1 : Byte = 0xBC.toByte()

    /**
     * Byte value indicating the use of SHA-224 as message digest in the active authentication protocol
     */
    const val SHA_224 : Byte = 0x38

    /**
     * Byte value indicating the use of SHA-256 as message digest in the active authentication protocol
     */
    const val SHA_256 : Byte = 0x34

    /**
     * Byte value indicating the use of SHA-384 as message digest in the active authentication protocol
     */
    const val SHA_384 : Byte = 0x36

    /**
     * Byte value indicating the use of SHA-512 as message digest in the active authentication protocol
     */
    const val SHA_512 : Byte = 0x35

    /**
     * Byte value indicating partial message recovery in the active authentication protocol
     */
    const val PARTIAL_MESSAGE_RECOVERY : Byte = 0x6A

    const val ECDSA_OID = "0.4.0.127.0.7.1.1.4"
}