package com.example.emrtdapplication.lds1

/**
 * Additional seed value for encryption key computation
 */
const val ENCRYPTION_KEY_VALUE_C: Byte = 1

/**
 * Additional seed value for MAC key computation
 */
const val MAC_COMPUTATION_KEY_VALUE_C: Byte = 2

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

const val RANDOM_NUMBER_BYTE_LENGTH = 8
const val SEQUENCE_COUNTER_START_INDEX = 4
const val SEQUENCE_COUNTER_END_INDEX = 7
const val BAC_KEY_LENGTH = 16
const val SESSION_SEED_SIZE = 16
const val MAC_START_INDEX = 32
const val MAC_END_INDEX = 39
const val ENCRYPTED_DATA_END_INDEX = 31
const val BAC_LAST_RESPONSE_APDU_SIZE = 40
const val RANDOM_IC_END_INDEX = 15
const val KEYING_MATERIAL_START_INDEX = 16
const val KEYING_MATERIAL_END_INDEX = 31
const val RANDOM_IFD_START_INDEX = 8
const val RANDOM_IFD_END_INDEX = 15
const val KEYING_MATERIAL_IFD_SIZE = 16
const val CONCATENATION_SIZE = 32
const val TD1_SIZE = 90
const val TD2_SIZE = 72
const val TD3_SIZE = 88
const val DOCUMENT_CODE_START_INDEX = 0
const val DOCUMENT_CODE_END_INDEX = 1
const val ISSUER_CODE_START_INDEX = 2
const val ISSUER_CODE_END_INDEX = 4
const val HOLDER_NAME_START_INDEX = 5
const val HOLDER_NAME_TD3_END_INDEX = 43
const val DOCUMENT_NUMBER_TD3_START_INDEX = 44
const val DOCUMENT_NUMBER_TD3_END_INDEX = 52
const val CHECK_DIGIT_DOCUMENT_NUMBER_TD3_INDEX = 53
const val NATIONALITY_TD3_START_INDEX = 54
const val NATIONALITY_TD3_END_INDEX = 56
const val BIRTH_DATE_TD3_START_INDEX = 57
const val BIRTH_DATE_TD3_END_INDEX = 62
const val CHECK_DIGIT_BIRTH_DATE_TD3_INDEX = 63
const val SEX_TD3_INDEX = 64
const val EXPIRATION_DATE_TD3_START_INDEX = 65
const val EXPIRATION_DATE_TD3_END_INDEX = 70
const val CHECK_DIGIT_EXPIRATION_DATE_TD3_INDEX = 71
const val OPTIONAL_DATA_TD3_START_INDEX = 72
const val OPTIONAL_DATA_TD3_END_INDEX = 85
const val CHECK_DIGIT_TD3_INDEX = 86
const val COMPOSITE_CHECK_DIGIT_TD3_INDEX = 87
const val HOLDER_NAME_TD2_END_INDEX = 35
const val DOCUMENT_NUMBER_TD2_START_INDEX = 36
const val DOCUMENT_NUMBER_TD2_END_INDEX = 44
const val CHECK_DIGIT_DOCUMENT_NUMBER_TD2 = 45
const val NATIONALITY_TD2_START_INDEX = 46
const val NATIONALITY_TD2_END_INDEX = 48
const val BIRTH_DATE_TD2_START_INDEX = 49
const val BIRTH_DATE_TD2_END_INDEX = 54
const val CHECK_DIGIT_BIRTH_DATE_TD2 = 55
const val SEX_TD2_INDEX = 56
const val EXPIRATION_DATE_TD2_START_INDEX = 57
const val EXPIRATION_DATE_TD2_END_INDEX = 62
const val CHECK_DIGIT_EXPIRATION_DATE_TD2_INDEX = 63
const val OPTIONAL_DATA_TD2_START_INDEX = 64
const val OPTIONAL_DATA_TD2_END_INDEX = 70
const val COMPOSITE_CHECK_DIGIT_TD2_INDEX = 71
const val DOCUMENT_NUMBER_TD1_START_INDEX = 5
const val DOCUMENT_NUMBER_TD1_END_INDEX = 13
const val CHECK_DIGIT_DOCUMENT_NUMBER_TD1 = 14
const val OPTIONAL_DATA_DOCUMENT_NUMBER_TD1_START_INDEX = 15
const val OPTIONAL_DATA_DOCUMENT_NUMBER_TD1_END_INDEX = 29
const val BIRTH_DATE_TD1_START_INDEX = 30
const val BIRTH_DATE_TD1_END_INDEX = 35
const val CHECK_DIGIT_BIRTH_DATE_TD1 = 36
const val SEX_TD1_INDEX = 37
const val EXPIRATION_DATE_TD1_START_INDEX = 38
const val EXPIRATION_DATE_TD1_END_INDEX = 43
const val CHECK_DIGIT_EXPIRATION_DATE_TD1_INDEX = 44
const val NATIONALITY_TD1_START_INDEX = 45
const val NATIONALITY_TD1_END_INDEX = 47
const val OPTIONAL_DATA_TD1_START_INDEX = 48
const val OPTIONAL_DATA_TD1_END_INDEX = 58
const val COMPOSITE_CHECK_DIGIT_TD1_INDEX = 59
const val HOLDER_NAME_TD1_START_INDEX = 60
const val HOLDER_NAME_TD1_END_INDEX = 89
/**
 * Byte value indicating the use of SHA-1 as message digest in the active authentication protocol
 */
const val ACTIVE_AUTHENTICATION_SHA_1: Byte = 0xBC.toByte()

/**
 * Byte value indicating the use of SHA-224 as message digest in the active authentication protocol
 */
const val ACTIVE_AUTHENTICATION_SHA_224: Byte = 0x38

/**
 * Byte value indicating the use of SHA-256 as message digest in the active authentication protocol
 */
const val ACTIVE_AUTHENTICATION_SHA_256: Byte = 0x34

/**
 * Byte value indicating the use of SHA-384 as message digest in the active authentication protocol
 */
const val ACTIVE_AUTHENTICATION_SHA_384: Byte = 0x36

/**
 * Byte value indicating the use of SHA-512 as message digest in the active authentication protocol
 */
const val ACTIVE_AUTHENTICATION_SHA_512: Byte = 0x35

/**
 * Byte value indicating partial message recovery in the active authentication protocol
 */
const val PARTIAL_MESSAGE_RECOVERY: Byte = 0x6A

const val ECDSA_OID = "0.4.0.127.0.7.1.1.4"
const val RSA_CIPHER = "RSA/None/NoPadding"
const val UNABLE_TO_DECRYPT_RSA = "Unable to decrypt RSA message in Active Authentication protocol"
/**
 * Byte length for value in a TLV holding LDS version information
 */
const val LDS_VERSION_LENGTH = 0x04

/**
 * Byte length for value in a TLV holding Unicode version information
 */
const val UNICODE_VERSION_LENGTH = 0x06

const val EF_COM_TAG: Byte = 0x60
const val EF_COM_SHORT_ID: Byte = 0x1E
const val VERSION_TAG_SIZE = 2
const val VERSION_COMPUTATION_DIFFERENCE = 48
const val VERSION_COMPUTATION_MULTIPLIER = 10
const val EF_SOD_TAG: Byte = 77
const val EF_SOD_SHORT_EF_ID: Byte = 0x1D
const val ORIGINAL_HASH_OID = "1.2.840.113549.1.9.4"
const val SIGNING_TIME_OID = "1.2.840.113549.1.9.5"
const val CONTENT_TYPE_OID = "1.2.840.113549.1.9.3"
const val CONTENT_TYPE_ID = "2.23.136.1.1.1"
const val UNABLE_TO_CHECK_CRL = "Unable to check CRL list!"
const val HTTP = "http"
const val HTTP_COLUMN = "http:"
const val HTTPS_COLUMN = "https:"

/**
 * Application identifier for the LDS1 application
 */
const val APPLICATION_ID = "A0000002471001"

/**
 * Constant value for incrementing the progress bar while reading from the eMRTD
 */
const val INCREMENT_PROGRESS_BAR = 3

const val READING_FILES = "Reading files..."
const val READING_EF_SOD = "Reading EF.SOD file..."
const val PERFORMING_ACTIVE_AUTHENTICATION = "Performing Active Authentication..."
const val PERFORMING_CHIP_AUTHENTICATION = "Performing Chip Authentication..."
const val PERFORMING_PASSIVE_AUTHENTICATION = "Performing Passive Authentication..."
const val READING_DG = "Reading DG"
const val FILE = " file..."

/**
 * The OID for the CRL Distribution Points
 */
const val CRL_DISTRIBUTION_POINT_OID = "2.5.29.31"

const val X509 = "X.509"

const val PACE_INFO_OID_SIZE = 10

const val PACE_DOMAIN_PARAMETER_INFO_OID_SIZE = 9

const val DG12_TAG_SIZE_2_FIRST_BYTE: Byte = 0x5F

const val UNABLE_TO_DECODE_IMAGE = "Unable to decode image!"

const val NAMES_MIN_SEQUENCE = 2
const val UNABLE_TO_DECODE_IRIS_DATA = "Unable to decode iris biometric data!"
const val UNABLE_TO_DECODE_FINGERPRINT_DATA = "Unable to decode fingerprint biometric data!"
