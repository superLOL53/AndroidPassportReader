package com.example.emrtdapplication.constants

/**
 * Constants for the [com.example.emrtdapplication.lds2.AdditionalBiometrics] class
 */
object AdditionalBiometricsConstants {

    /**
     * International application identifier for the LDS2 Additional Biometric application
     */
    const val APPLICATION_ID = "A0000002472003"

    /**
     * First half of the identifier for a biometric file in the Additional Biometric application
     */
    const val BIOMETRIC_FILE_ID : Byte = 0x02

    /**
     * Maximum number of biometric files in the Additional Biometrics application
     */
    const val MAX_BIOMETRIC_FILES = 0x40
}