package com.example.emrtdapplication.constants

object BiometricDataBlockConstants {
    const val BIOMETRIC_DATA_BLOCK_TAG_SIZE = 2
    const val BIOMETRIC_DATA_BLOCK_TAG_1 = 0x2E.toByte()
    const val BIOMETRIC_DATA_BLOCK_TAG_2_1 = 0x5F.toByte()
    const val BIOMETRIC_DATA_BLOCK_TAG_2_2 = 0x7F.toByte()
    const val BIOMETRIC_DATA_BLOCK_INVALID_TAG_STRING = "Invalid Tag for Biometric Data Block"
    const val BIOMETRIC_DATA_BLOCK_NO_VALUE_STRING = "No value for Biometric Data Block (BDB)"
}