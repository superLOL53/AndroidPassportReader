package com.example.emrtdapplication

/**
 * Constant encoding a zero byte (0x00)
 */
const val ZERO_BYTE: Byte = 0x0

/**
 * File was successfully read from the eMRTD
 */
const val FILE_SUCCESSFUL_READ = 0

/**
 * General return value for successful completion
 */
const val SUCCESS = 0

/**
 * General return value for unsuccessful completion
 */
const val FAILURE = -1

/**
 * File was unable to be selected on the eMRTD
 */
const val FILE_UNABLE_TO_SELECT = -1

/**
 * Unable to read file from the eMRTD
 */
const val FILE_UNABLE_TO_READ = -2

/**
 * Additional maximum overhead for secure messaging APDUs due to encryption
 */
const val ADDITIONAL_ENCRYPTION_LENGTH = 30

/**
 * The OID for the Authority Key Identifier
 */
const val AUTHORITY_KEY_IDENTIFIER_OID = "2.5.29.35"

const val ANDROID_LOG_INFO_TAG = "eMRTDInfo"

const val UNKNOWN_STRING = "Unknown"

const val BYTE_BIT_SIZE = 8

const val MRZ_STRING = "MRZ"

const val BOUNCY_CASTLE_STRING = "BC"

const val NFC_PRESENCE_CHECK_DELAY = 60000

const val PACE_STATUS_STRING = "PACE status: "

const val MAXIMUM_EF_FILE_SIZE = 32767

const val MAX_LINES_IN_DISPLAY = 10

const val MASTER_LIST_PATH = "MasterList"

const val MESSAGE_STRING = "Message: "

const val INVALID_MASTER_LIST_STRING = "Byte array does not contain an encoded Master List!"

const val INVALID_MASTER_LIST_CONTENT = "Master List does not contain expected content!"

const val UNABLE_TO_DECODE_MASTER_LIST_CERTIFICATES = "Unable to decode Master List signer certificates!"

const val UNABLE_TO_DECODE_CSCA_CERTIFICATES = "Unable to decode CSCA certificates from the Master List!"

const val EF_TYPE_TEMPLATE_TAG_SIZE = 1

const val EF_TYPE_TEMPLATE_SEQUENCE_SIZE = 1

const val FILLER_CHARACTER = '<'


const val SECURITY_INFO_MIN_SEQUENCE_SIZE = 2
const val SECURITY_INFO_MAX_SEQUENCE_SIZE = 3
const val REQUIRED_DATA_SEQUENCE_INDEX = 1
const val PROTOCOL_OID_SEQUENCE_INDEX = 0
const val OPTIONAL_DATA_SEQUENCE_INDEX = 2

/**
 * Type of Security Info is PACE Info
 */
const val PACE_INFO_TYPE = 0

/**
 * Type of Security Info is Active Authentication Info
 */
const val ACTIVE_AUTHENTICATION_TYPE = 1

/**
 * Type of Security Info is Chip Authentication Info
 */
const val CHIP_AUTHENTICATION_TYPE = 2

/**
 * Type of Security Info is Chip Authentication Public Key Info
 */
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE = 3

/**
 * Type of Security Info is Terminal Authentication Info
 */
const val TERMINAL_AUTHENTICATION_TYPE = 4

/**
 * Type of Security Info is EF.DIR Info
 */
const val EF_DIR_TYPE = 5

/**
 * Type of Security Info is PACE Domain Parameter Info
 */
const val PACE_DOMAIN_PARAMETER_INFO_TYPE = 6

/**
 * OID for PACE Info or PACE Domain Parameter Info Security Info type
 */
const val PACE_OID = "0.4.0.127.0.7.2.2.4"

/**
 * OID for Active Authentication Info Security Info type
 */
const val ACTIVE_AUTHENTICATION_OID = "2.23.136.1.1.5"

/**
 * OID for Chip Authentication Info Security Info type
 */
const val CHIP_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.3"

/**
 * OID for Chip Authentication Public Key Info Security Info type
 */
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID = "0.4.0.127.0.7.2.2.1"

/**
 * OID for Terminal Authentication Info Security Info type
 */
const val TERMINAL_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.2"

/**
 * OID for EF.DIR Info Security Info type
 */
const val EF_DIR_OID = "2.23.136.1.1.13"

/**
 * Constant for converting an upper case letter to a numerical value according to ICAO Doc9303-3
 */
const val UPPER_CASE_DIGIT = 55

/**
 * Constant for converting a lower case letter to a numerical value according to ICAO Doc9303-3
 */
const val LOWER_CASE_DIGIT = 87

/**
 * Length of the passport number field in the MRZ
 */
const val PASSPORT_NUMBER_LENGTH = 8

/**
 * Length of the expiration/birthday date field in the MRZ
 */
const val DATE_LENGTH = 6

/**
 * First byte in the Check Digit sequence
 */
const val CHECK_DIGIT_SEQUENCE_1: Byte = 7

/**
 * Second byte in the Check Digit sequence
 */
const val CHECK_DIGIT_SEQUENCE_2: Byte = 3

/**
 * Third byte in the Check Digit sequence
 */
const val CHECK_DIGIT_SEQUENCE_3: Byte = 1

const val CHECK_DIGIT_MODULO = 10
const val PASSPORT_NUMBER_STRING = "passportNumber"
const val BIRTHDAY_STRING = "birthday"
const val EXPIRATION_DATE_STRING = "expirationDate"
const val LDS1_FRAGMENT_ID = 0
const val TRAVEL_RECORDS_FRAGMENT_ID = 1
const val VISA_RECORDS_FRAGMENT_ID = 2
const val BIOMETRICS_FRAGMENT_ID = 3
const val SETTINGS_FRAGMENT_ID = 4
const val CURRENT_FRAGMENT_STRING = "currentFragment"
const val ACTION_BAR_TITLE_STRING = "actionBarTitle"

/**
 * First byte of the long identifier for EFs
 */
const val LONG_EF_ID : Byte = 0x01

/**
 * Modulo value for UByte conversion
 */
const val U_BYTE_MODULO = 256

/**
 * Modulo value for Byte conversion
 */
const val BYTE_MODULO = 128

const val TLV_LENGTH_INDEX = 1
