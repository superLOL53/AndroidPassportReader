package com.example.emrtdapplication.constants

/**
 * Constants for the PACE class
 */
object PACEConstants {
    /**
     * Error code for no password to initialize PACE
     */
    const val NO_PASSWORD = -1

    /**
     * Error code for when no PACE protocol OID is given/supported
     */
    const val NO_PACE_OID = -2

    /**
     * Error code indicating a failure in setting up PACE
     */
    const val INVALID_MSE_COMMAND = -3

    /**
     * Error code indicating a failure in retrieving a nonce from the eMRTD
     */
    const val INVALID_GENERAL_AUTHENTICATE = -4

    /**
     * Error code indicating a failure in extracting the nonce from the eMRTD
     */
    const val INVALID_NONCE = -5

    /**
     * Constant for generating positive BigInteger numbers
     */
    const val POSITIVE_NUMBER = 1
}