package com.example.emrtdapplication.constants

/**
 * Constants for the class [com.example.emrtdapplication.lds1.BAC]
 */
object BACConstants {
    /**
     * Additional seed value for encryption key computation
     */
    const val ENCRYPTION_KEY_VALUE_C : Byte = 1

    /**
     * Additional seed value for MAC key computation
     */
    const val MAC_COMPUTATION_KEY_VALUE_C : Byte = 2

    /**
     * Successful execution of the BAC protocol
     */
    const val BAC_PROTOCOL_SUCCESS = 0

    /**
     * Error code for BAC protocol execution attempt without MRZ information
     */
    const val ERROR_UNINITIALIZED_MRZ_INFORMATION = -1

    /**
     * Error code for failure to request/get a nonce from the eMRTD
     */
    const val ERROR_NONCE_REQUEST_FAILED = -2

    /**
     * General error code for BAC protocol failure
     */
    const val ERROR_BAC_PROTOCOL_FAILED = -3

    /**
     * eMRTD returned invalid MAC during BAC protocol execution
     */
    const val ERROR_INVALID_MAC = -4

    /**
     * eMRTD returned invalid nonce during BAC protocol execution
     */
    const val ERROR_INVALID_NONCE = -5

    /**
     * No MRZ given for BAC protocol initialization
     */
    const val ERROR_NO_MRZ = -6
}