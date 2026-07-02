package com.example.emrtdapplication.constants

/**
 * Constant encoding a zero byte (0x00)
 */
const val ZERO_BYTE : Byte = 0x0

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
 * General error code for an invalid argument
 */
const val INVALID_ARGUMENT = -4

/**
 * Additional maximum overhead for secure messaging APDUs due to encryption
 */
const val ADDITIONAL_ENCRYPTION_LENGTH = 30

/**
 * The OID for the CRL Distribution Points
 */
const val CRL_DISTRIBUTION_POINT_OID = "2.5.29.31"

/**
 * The OID for the Authority Key Identifier
 */
const val AUTHORITY_KEY_IDENTIFIER_OID = "2.5.29.35"

const val ANDROID_LOG_INFO_TAG = "eMRTDInfo"

const val UNSPECIFIED_STRING = "Unspecified"

const val MALE_STRING = "Male"

const val FEMALE_STRING = "Female"

const val UNKNOWN_STRING = "Unknown"

const val RESERVED_STRING = "Reserved"

const val VENDOR_SPECIFIC_STRING = "Vendor Specific"

const val SMILE_STRING = "Smile"

const val CLOSED_SMILE_STRING = "Smile (closed mouth)"

const val RAISED_EYEBROWS_STRING = "Raised eyebrows"

const val EYES_LOOKING_AWAY_STRING = "Eyes looking away from camera"

const val SQUINTING_STRING = "Squinting"

const val FROWNING_STRING = "Frowning"

const val BALD_STRING = "Bald"

const val BLACK_STRING = "Black"

const val BLONDE_STRING = "Blonde"

const val BROWN_STRING = "Brown"

const val GRAY_STRING = "Gray"

const val WHITE_STRING = "White"

const val RED_STRING = "Red"

const val GREEN_STRING = "Green"

const val BLUE_STRING = "Blue"

const val NEUTRAL_STRING = "Neutral"

const val PINK_STRING = "Pink"

const val MULTI_COLOURED_STRING = "Multi-Coloured"

const val BYTE_BIT_SIZE = 8

const val BASIC_STRING = "Basic"

const val FULL_FRONTAL_STRING = "Full Frontal"

const val TOKEN_FRONTAL_STRING = "Token Frontal"

const val OTHER_STRING = "Other"

const val JPEG_STRING = "JPEG"

const val JPEG_2000_STRING = "JPEG2000"

const val RGB_24_BIT_STRING = "24-bit RGB"

const val YUV_422_STRING = "YUV422"

const val GREYSCALE_8_BIT_STRING = "8-bit Greyscale"

const val MRZ_STRING = "MRZ"

const val BOUNCY_CASTLE_STRING = "BC"

const val NFC_PRESENCE_CHECK_DELAY = 60000

const val PACE_STATUS_STRING = "PACE status: "

const val MAXIMUM_EF_FILE_SIZE = 32767

const val MAX_LINES_IN_DISPLAY = 10

const val MASTER_LIST_PAHT = "MasterList"

const val MESSAGE_STRING = "Message: "

const val INVALID_RESPONSE_MAC = "Invalid MAC in the response APDU!"

const val DO8X_TAG_OVERHEAD_LENGTH = 3

const val INVALID_MASTER_LIST_STRING = "Byte array does not contain an encoded Master List!"

const val INVALID_MASTER_LIST_CONTENT = "Master List does not contain expected content!"

const val UNABLE_TO_DECODE_MASTER_LIST_CERTIFICATES = "Unable to decode Master List signer certificates!"

const val UNABLE_TO_DECODE_CSCA_CERTIFICATES = "Unable to decode CSCA certificates from the Master List!"

const val EF_TYPE_TEMPLATE_TAG_SIZE = 1

const val EF_TYPE_TEMPLATE_SEQUENCE_SIZE = 1