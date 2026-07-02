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