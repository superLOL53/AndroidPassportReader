package com.example.emrtdapplication.constants

/**
 * Constants for the class BAC
 */
object BACConstants {
    const val ENCRYPTION_KEY_VALUE_C : Byte = 1
    const val MAC_COMPUTATION_KEY_VALUE_C : Byte = 2
    const val BAC_PROTOCOL_SUCCESS = 0
    const val ERROR_UNINITIALIZED_MRZ_INFORMATION = -1
    const val ERROR_NONCE_REQUEST_FAILED = -2
    const val ERROR_BAC_PROTOCOL_FAILED = -3
    const val ERROR_INVALID_MAC = -4
    const val ERROR_INVALID_NONCE = -5
    const val ERROR_NO_MRZ = -6
}