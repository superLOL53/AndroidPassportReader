package com.example.emrtdapplication.constants

/**
 * Constants for the [com.example.emrtdapplication.utils.APDUControl] class
 */
object APDUControlConstants {
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
    const val TIME_OUT = 2000

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
}