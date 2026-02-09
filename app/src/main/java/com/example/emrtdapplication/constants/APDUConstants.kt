package com.example.emrtdapplication.constants

/**
 * Constants for the [com.example.emrtdapplication.utils.APDU] class
 */

object APDUConstants {
    /**
     * Minimum length of a command APDU
     */
    const val MIN_APDU_LENGTH = 4

    /**
     * Maximum value for the Lc field of a command APDU, excluding 256 (0x00)
     */
    const val LC_MAX = 255

    /**
     * Maximum value for the extended Lc field of a command APDU
     */
    const val LC_EXT_MAX = 65535

    /**
     * Minimum value for the Le field to be present in a command APDU
     */
    const val LE_MIN = 0

    /**
     * Maximum value for the Le field of a command APDU
     */
    const val LE_MAX = 256

    /**
     * Maximum value for the extended Le field of a command APDU
     */
    const val LE_EXT_MAX = 65536

    /**
     * Byte mask for converting command APDU length field(s) to a byte
     */
    const val BYTE_MASK = 0xFF

    /**
     * Number of bits to be shifted to convert the high byte of a command APDU length field
     * (Le or Lc) to a byte
     */
    const val EXTENDED_LENGTH_SHIFT_COUNT = 8
}