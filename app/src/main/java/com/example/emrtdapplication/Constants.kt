package com.example.emrtdapplication

import android.util.Log

const val ZERO_BYTE : Byte = 0x0
const val ZERO_SHORT : Short = 0
const val FILE_SUCCESSFUL_READ = 0
const val SUCCESS = 0
const val FILE_UNABLE_TO_SELECT = -1
const val FILE_UNABLE_TO_READ = -2
const val NOT_IMPLEMENTED = -3
const val INVALID_ARGUMENT = -4

const val SELECT_APPLICATION_SUCCESS = 0
const val UNALBE_TO_SELECT_APPLICATION = -1

const val APDU_ID = 0
const val APDUControl_ID = 1
const val ApplicationeMRTD_ID = 2
const val AttributeInfo_ID = 3
const val BAC_ID = 4
const val CardAccess_ID = 5
const val CardSecurity_ID = 6
const val DG1_ID = 7
const val Directory_ID = 8
const val EfCom_ID = 9
const val EMRTD_ID = 10
const val MRZ_ID = 11
const val PACE_ID = 12
const val SecureAPDU_ID = 13

object TLV_TAGS {
    const val CONSTRUCT_BIT : Byte = 0x20
    const val INTEGER : Byte = 0x02
    const val OBJECT_ID : Byte = 0x06
    const val OCTET_STRING : Byte = 0x04
    const val CRYPTOGRAPHIC_REFERENCE : Byte = 0x80.toByte()
    const val KEY_REFERENCE : Byte = 0x83.toByte()
    const val KEY_REFERENCE_COMPUTING : Byte = 0x84.toByte()
    const val CERTIFICATE_HOLDER_AUTHORIZATION_TEMPLATE_1 : Byte = 0x7F
    const val CERTIFICATE_HOLDER_AUTHORIZATION_TEMPLATE_2 : Byte = 0x4C
    const val NONCE_QUERY : Byte = 0x7C
}

object APDUConstants {
    const val TAG = "APDU"
    const val ENABLE_LOGGING = true
}

/*enum class PaceDHAlgorithms {
    GM_3DES_CBC_CBC,
    GM_AES_CBC_CMAC,
    IM_3DES_CBC_CBC,
    IM_AES_CBC_CMAC
}*/

enum class NfcUse {
    UNDEFINED,
    ISO_DEP
}

object NfcClassByte {
    const val ZERO : Byte = 0x0
    const val SECURE_MESSAGING : Byte = 0x0c
    const val COMMAND_CHAINING : Byte = 0x10
}

object NfcInsByte {
    const val SELECT : Byte = 0xA4.toByte()
    const val READ_BINARY : Byte = 0xB0.toByte()
    const val REQUEST_RANDOM_NUMBER = 0x84.toByte()
    const val EXTERNAL_AUTHENTICATE : Byte = 0x82.toByte()
    const val MANAGE_SECURITY_ENVIRONMENT : Byte = 0x22
    const val GENERAL_AUTHENTICATE : Byte = 0x86.toByte()
}

object NfcP1Byte {
    const val ZERO : Byte = 0x00
    const val SELECT_EF : Byte = 0x02
    const val SELECT_DF : Byte = 0x04
    const val SELECT_ATRINFO_SHORT : Byte = 0x01
    const val SELECT_DIR_SHORT : Byte = 0x1E
    const val SELECT_CARD_ACCESS_SHORT : Byte = 0x1C
    const val SELECT_CARD_SECURITY_SHORT : Byte = 0x1D
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xC1.toByte()
}

object NfcP2Byte {
    const val ZERO : Byte = 0x00
    const val SELECT_FILE : Byte = 0x0C
    const val SET_AUTHENTICATION_TEMPLATE : Byte = 0xA4.toByte()
}

object NfcRespondCodeSW1 {
    const val OK : Byte = 0x90.toByte()
    const val SECURITY_STATUS_NOT_SATISFIED : Byte = 0x69
}

object NfcRespondCodeSW2 {
    const val OK : Byte = 0x00
    const val SECURITY_STATUS_NOT_SATISFIED : Byte = 0x82.toByte()
}

object ShortEFIdentifier {
    const val ATTRIBUTE_INFO : Byte = 0x01
    const val DIRECTORY : Byte = 0x1E
    const val CARD_ACCESS : Byte = 0x1C
    const val CARD_SECURITY : Byte = 0x1D
}

object BACConstants {
    const val TAG = "BAC"
    const val ENABLE_LOGGING = true
    const val ENCRYPTION_KEY_VALUE_C : Byte = 1
    const val MAC_COMPUTATION_KEY_VALUE_C : Byte = 2
    const val BAC_INIT_SUCCESS = 1
    const val BAC_PROTOCOL_SUCCESS = 0
    const val ERROR_UNINITIALIZED_MRZ_INFORMATION = -1
    const val ERROR_NONCE_REQUEST_FAILED = -2
    const val ERROR_BAC_PROTOCOL_FAILED = -3
    const val ERROR_INVALID_MAC = -4
    const val ERROR_INVALID_NONCE = -5
    const val ERROR_NO_MRZ = -6
    const val ERROR_CANNOT_EXTRACT_MRZ_INFORMATION = -7
}

object ApplicationConstants {
    const val TAG = "Application"
    const val ENABLE_LOGGING = true
}

object EMRTDConstants {
    const val TAG = "eMRTD"
    const val ENABLE_LOGGING = true
}

object CardAccessConstants {
    const val TAG = "CardAccessFile"
    const val ENABLE_LOGGING = true
}

object PACEInfoConstants {
    const val TAG = "PACEInfo"
    const val ENABLE_LOGGING = true
    const val UNDEFINED : Byte = -1
    const val id_PACE = "0.4.0.127.0.7.2.2.4"
    const val DH_GM : Byte = 1
    const val ECDH_GM : Byte = 2
    const val DH_IM : Byte = 3
    const val ECDH_IM : Byte = 4
    const val ECDH_CAM : Byte = 6
    const val DES_CBC_CBC : Byte = 1
    const val AES_CBC_CMAC_128 : Byte = 2
    const val AES_CBC_CMAC_192 : Byte = 3
    const val AES_CBC_CMAC_256 : Byte = 4
    const val MODP_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP : Byte = 0
    const val MODP_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP : Byte = 1
    const val MODP_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP : Byte = 2
    const val NIST_P192 : Byte = 8
    const val BRAIN_POOL_P192R1 : Byte = 9
    const val NIST_P224 : Byte = 10
    const val BRAIN_POOL_P224R1 : Byte = 11
    const val NIST_P256 : Byte = 12
    const val BRAIN_POOL_P256R1 : Byte = 13
    const val BRAIN_POOL_P320R1 : Byte = 14
    const val NIST_P384 : Byte = 15
    const val BRAIN_POOL_P384R1 : Byte = 16
    const val BRAIN_POOL_P512R1 : Byte = 17
    const val NIST_P521 : Byte = 18
}

object PACEConstants {
    const val TAG = "PACE"
    const val ENABLE_LOGGING = true
    const val NO_PASSWORD = -1
    const val NO_PACE_OID = -2
    const val INVALID_MSE_COMMAND = -3
    const val INVALID_GENERAL_AUTHENTICATE = -4
    const val INVALID_NONCE = -5
    const val INVALID_PACE_OID = -6
}

object AttributeInfoConstants {
    const val TAG = "AttributeInfoFile"
    const val ENABLE_LOGGING = true
    const val CARD_CAPABILITY_TAG : Byte = 0x47
    const val SUPPORT_RECORD_NUMBER : Byte = 0x02
    const val SUPPORT_SHORT_EF_ID : Byte = 0x04
    const val SUPPORT_DF_FULL_NAME_SELECTION : Byte = 0x80.toByte()
    const val UNIT_SIZE : Byte = 0x01
    const val MASK_UNIT_SIZE : Byte = 0xF
    const val SUPPORT_COMMAND_CHAINING : Byte = 0x80.toByte()
    const val SUPPORT_EXTENDED_LENGTHS : Byte = 0x40
    const val EXTENDED_LENGTH_INFO_IN_ATRINFO : Byte = 0x20
    const val EXTENDED_LENGTH_TAG_1 : Byte = 0x7F
    const val EXTENDED_LENGTH_TAG_2 : Byte = 0x66
}

object CardSecurityConstants {
    const val TAG = "CardSecurityFile"
    const val ENABLE_LOGGING = true
}

object DirectoryConstants {
    const val TAG = "DirectoryFile"
    const val ENABLE_LOGGING = true
}

object MRZConstants {
    const val TAG = "MRZ"
    const val ENABLE_LOGGING = true
    const val MRZ_EXTRACTION_SUCCESSFUL = 0
    const val UNKNOWN_MRZ_TYPE = -1
    const val CHECK_DIGIT_MISMATCH = -2
}

object APDUControlConstants {
    const val TAG = "APDUControl"
    const val ENABLE_LOGGING = true
    const val INIT_SUCCESS = 0
    const val CONNECT_SUCCESS = 1
    const val CLOSE_SUCCESS = 2
    const val ERROR_NO_NFC_TAG = -1
    const val ERROR_NO_ISO_DEP_SUPPORT = -2
    const val ERROR_UNABLE_TO_CONNECT = -3
    const val ERROR_ISO_DEP_NOT_SELECTED = -4
    const val ERROR_UNABLE_TO_CLOSE = -5
}

object TLVCoderConstants {
    const val TAG = "TLVCoder"
    const val ENABLE_LOGGING = true

}

object LoggerConstants {
    const val RETURN_CODE = "\nReturn Code: "
}

object Logger {
    private const val ENABLE_LOGGING = true
    private const val APPLICATION_TAG = "AppLog"

    fun log(tag : String, enableClassLog : Boolean, message : String) {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message)
            } else {
                Log.d(APPLICATION_TAG, message)
            }
        }
    }

    fun log(tag : String, enableClassLog : Boolean, returnCode : Int, message : String) : Int {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message + LoggerConstants.RETURN_CODE + returnCode)
            } else {
                Log.d(APPLICATION_TAG, message + LoggerConstants.RETURN_CODE + returnCode)
            }
        }
        return returnCode
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun log(tag : String, enableClassLog : Boolean, message : String, info : ByteArray) {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message + info.toHexString())
            } else {
                Log.d(APPLICATION_TAG, message)
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun log(tag: String, enableClassLog : Boolean, returnCode : Int, message : String, info: ByteArray): Int {
        if (ENABLE_LOGGING) {
            if (enableClassLog) {
                Log.d(tag, message + info.toHexString() + LoggerConstants.RETURN_CODE + returnCode)
            } else {
                Log.d(APPLICATION_TAG, message + info.toHexString() + LoggerConstants.RETURN_CODE + returnCode)
            }
        }
        return returnCode
    }
}

object TestValues {
    const val MRZ = "PPAUTTROST<<OLIVER>WILLIBALD<<<<<<<<<<<<<<<<\nAP11269226AUT0007076M3503306<<<<<<<<<<<<<<<4"
    const val CAN = "208472"
    //const val MRZ = "I<UTOL898902C<3<<<<<<<<<<<<<<<\n6908061F9406236UTO<<<<<<<<<<<1\nERIKSSON<<ANNA<MARIA<<<<<<<<<<"
}