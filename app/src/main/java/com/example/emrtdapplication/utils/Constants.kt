package com.example.emrtdapplication.utils


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

const val EXTENDED_LENGTH_SIZE_IN_BYTES = 3

const val NORMAL_LENGTH_SIZE_IN_BYTES = 1
/**
 * Initialization of the NFC IsoDep was successful
 */
const val INIT_SUCCESS = 0

/**
 * Connection to the discovered NFC Tag was a success
 */
const val CONNECT_SUCCESS = 1

/**
 * Closing the NFC Tag connection was a success
 */
const val CLOSE_SUCCESS = 2

/**
 * Discovered tag was null
 */
const val ERROR_NO_NFC_TAG = -1

/**
 * Discovered tag does not support IsoDep, which is mandatory for eMRTDs
 */
const val ERROR_NO_ISO_DEP_SUPPORT = -2

/**
 * Error code for NFC tag connection establishment failure
 */
const val ERROR_UNABLE_TO_CONNECT = -3

/**
 * Error code for uninitialized connection attempt to the NFC tag
 */
const val ERROR_ISO_DEP_NOT_SELECTED = -4

/**
 * Error code for failure in closing NFC connection
 */
const val ERROR_UNABLE_TO_CLOSE = -5

/**
 * NFC response timeout setting
 */
const val TIME_OUT = 50000

/**
 * Byte array size for APDU response codes
 */
const val RESPOND_CODE_SIZE = 2

/**
 * Minimum size for a response APDU with a MAC
 */
const val MIN_APDU_SIZE_FOR_MAC_VERIFICATION = 13

/**
 * Padding size for encryption and MAC ciphers
 */
const val PADDING_SIZE = 8

/**
 * DES key size in bytes
 */
const val SINGLE_KEY_SIZE_3DES = 8

/**
 * Response APDU data offset counting from the end
 */
const val APDU_NO_DATA_SIZE = 17

const val SENDING_APDU = "Sending APDU: "

const val RECEIVED_APDU = "Received APDU: "

const val SENDING_SECURED_APDU = "Sending secured APDU: "

const val RECEIVED_SECURED_APDU = "Received secured APDU: "
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
const val PAD_START_BYTE: Byte = 0x80.toByte()

/**
 * Mask for counting 1-bits in a DES key
 */
const val KEY_3DES_COUNT_ONES: Byte = 0xFE.toByte()

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

const val UNABLE_TO_HASH = "Unable to hash with the algorithm "

const val SHA_1 = "SHA-1"

const val SHA_256 = "SHA-256"

const val ILLEGAL_CIPHER_ALGORITHM = "Illegal cipher algorithm for key computation!"

const val AES_128_KEY_SIZE = 16

const val AES_192_KEY_SIZE = 24

const val DES_KEY_SIZE = 8

const val UNABLE_TO_DE_ENCRYPT = "Unable to en-/decrypt with AES or DES.\n"

const val INVALID_RESPONSE_MAC = "Invalid MAC in the response APDU!"

const val DO8X_TAG_OVERHEAD_LENGTH = 3
