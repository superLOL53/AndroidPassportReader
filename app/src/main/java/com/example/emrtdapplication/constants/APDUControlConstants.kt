package com.example.emrtdapplication.constants

object APDUControlConstants {
    /**
     * Constants for the class APDUControl
     */
    const val INIT_SUCCESS = 0
    const val CONNECT_SUCCESS = 1
    const val CLOSE_SUCCESS = 2
    const val ERROR_NO_NFC_TAG = -1
    const val ERROR_NO_ISO_DEP_SUPPORT = -2
    const val ERROR_UNABLE_TO_CONNECT = -3
    const val ERROR_ISO_DEP_NOT_SELECTED = -4
    const val ERROR_UNABLE_TO_CLOSE = -5
    const val TIME_OUT = 2000
    const val RESPOND_CODE_SIZE = 2
    const val MIN_APDU_SIZE_FOR_MAC_VERIFICATION = 13
    const val PADDING_SIZE = 8
    const val SINGLE_KEY_SIZE_3DES = 8
    const val APDU_NO_DATA_SIZE = 17
    const val SECURE_MESSAGING_MASK : Byte = 0xF0.toByte()
}