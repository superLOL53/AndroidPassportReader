package com.example.emrtdapplication.constants

/**
 * Constants for [com.example.emrtdapplication.utils.Crypto] class
 */
object CryptoConstants {
    /**
     * Constant for AES
     */
    const val AES = "AES"

    /**
     * Constant for DES in encryption-decryption-encryption mode
     */
    const val DES_EDE = "DESede"

    /**
     * Starting value for adding padding to payload
     */
    const val PAD_START_BYTE : Byte = 0x80.toByte()

    /**
     * Mask for counting 1 bits in a DES key
     */
    const val KEY_3DES_COUNT_ONES : Byte = 0xFE.toByte()

    /**
     * Constant for getting cipher for AES in CBC mode and no padding
     */
    const val AES_CBC_NO_PADDING = "$AES/CBC/NoPadding"

    /**
     * Constant for getting cipher for AES in ECB mode and no padding.
     * Used to get the IV for AES de-/encryption
     */
    const val AES_ECB_NO_PADDING = "$AES/ECB/NoPadding"

    /**
     * Constant for DESede in CBC mode and no padding
     */
    const val DES_EDE_CBC_NO_PADDING = "$DES_EDE/CBC/NoPadding"

    /**
     * Constant for converting number of bytes into number of bits
     */
    const val BYTE_TO_BITS = 8

    /**
     * MAC size for secure messaging APDUs
     */
    const val MAC_SIZE = 8

    /**
     * Constant value for random number generation for the PACE integrated mapping protocol
     */
    const val C_0_128 = "a668892a7c41e3ca739f40b057d85904"

    /**
     * Constant value for random number generation for the PACE integrated mapping protocol
     */
    const val C_1_128 = "a4e136ac725f738b01c1f60217c188ad"

    /**
     * Constant value for random number generation for the PACE integrated mapping protocol
     */
    const val C_0_256 = "d463d65234124ef7897054986dca0a174e28df758cbaa03f240616414d5a1676"

    /**
     * Constant value for random number generation for the PACE integrated mapping protocol
     */
    const val C_1_256 = "54bd7255f0aaf831bec3423fcf39d69b6cbf066677d0faae5aadd99df8e53517"

    /**
     * Constant for calculating the length of the random number for the PACE integrated mapping protocol
     */
    const val MAPPING_CONSTANT = 64
}