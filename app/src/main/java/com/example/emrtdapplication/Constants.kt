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

const val MASTER_LIST_PATH = "MasterList"

const val MESSAGE_STRING = "Message: "

const val INVALID_RESPONSE_MAC = "Invalid MAC in the response APDU!"

const val DO8X_TAG_OVERHEAD_LENGTH = 3

const val INVALID_MASTER_LIST_STRING = "Byte array does not contain an encoded Master List!"

const val INVALID_MASTER_LIST_CONTENT = "Master List does not contain expected content!"

const val UNABLE_TO_DECODE_MASTER_LIST_CERTIFICATES = "Unable to decode Master List signer certificates!"

const val UNABLE_TO_DECODE_CSCA_CERTIFICATES = "Unable to decode CSCA certificates from the Master List!"

const val EF_TYPE_TEMPLATE_TAG_SIZE = 1

const val EF_TYPE_TEMPLATE_SEQUENCE_SIZE = 1

const val FILLER_CHARACTER = '<'

const val X509 = "X.509"

const val PACE_INFO_OID_SIZE = 10

const val PACE_DOMAIN_PARAMETER_INFO_OID_SIZE = 9

const val DG12_TAG_SIZE_2_FIRST_BYTE: Byte = 0x5F

const val UNABLE_TO_DECODE_IMAGE = "Unable to decode image!"

const val NAMES_MIN_SEQUENCE = 2

const val UNABLE_TO_DECODE_IRIS_DATA = "Unable to decode iris biometric data!"
const val UNABLE_TO_DECODE_FINGERPRINT_DATA = "Unable to decode fingerprint biometric data!"
const val SUBJECT = "Subject"
const val ISSUER = "Issuer"
const val VERSION = "Version:"
const val SERIAL_NUMBER = "Serial Number:"
const val START_DATE = "Start Date:"
const val END_DATE = "End Date:"
const val SIGNATURE_ALGORITHM_ID_STRING = "Signature Algorithm ID:"
const val CERTIFICATE_REFERENCE = "Certificate Reference:"
const val BIOMETRIC_FILE = "Biometric File "
const val CERTIFICATE_RECORD = "Certificate Record "
const val FAILURE_STRING = "Failure"
const val VERIFIED = "Verified"
const val UNDETERMINED = "Undetermined"
const val REVOKED = "Revoked"
const val UNREVOKED = "Unrevoked"
const val EXPIRED = "Expired"
const val INVALID_SIGNATURE = "Invalid Signature"
const val INVALID_HASH = "Invalid Hash"
const val DATE_FORMAT = "yyMMdd"
const val DOCUMENT_SIGNER_CERTIFICATE = "Document Signer Certificate"
const val CSCA_STRING = "Country Signing Certificate Authority"
const val CRL_STATUS = "CRL Status"
const val CHIP_AUTHENTICATION_STATUS = "Chip Authentication Status"
const val PASSIVE_AUTHENTICATION_STATUS = "Passive Authentication Status"
const val NAME = "Name"
const val ADDRESS = "Address"
const val TELEPHONE = "Telephone"
const val DATE_RECORDED = "Date data recorded"
const val EMERGENCY_CONTACT = "Emergency Contact "
const val ALGORITHM_IDENTIFIER = "Algorithm Identifier"
const val PUBLIC_KEY = "Public Key"
const val PROTOCOL_OID = "Protocol OID"
const val ASYMMETRIC_PROTOCOL = "Asymmetric Protocol"
const val SYMMETRIC_PROTOCOL = "Symmetric Protocol"
const val DOMAIN_PARAMETER_ID = "Domain Parameter ID"
const val DOMAIN_PARAMETER = "Domain Parameters"
const val PRESENT = "Present"
const val ABSENT = "Absent"
const val TRAVEL_RECORDS_APPLICATION = "Travel Records Application"
const val VISA_RECORDS_APPLICATION = "Visa Records Application"
const val ADDITIONAL_BIOMETRIC_APPLICATION = "Additional Biometrics Application"
const val EF_DIR_INFORMATION = "EF DIR Information"
const val PROTOCOL_VERSION = "Protocol version"
const val TERMINAL_AUTHENTICATION_INFORMATION = "Terminal Authentication Information"
const val KEY_ID = "Key ID"
const val PUBLIC_KEY_ALGORITHM_ID = "Public Key Algorithm ID"
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFORMATION = "Chip Authentication Public Key Information"
const val CHIP_AUTHENTICATION_INFORMATION = "Chip Authentication Information"
const val ACTIVE_AUTHENTICATION_INFORMATION = "Active Authentication Information"
const val SIGNATURE_ALGORITHM_OID = "Signature Algorithm OID"
const val PARAMETER_ID = "Parameter ID"
const val ALGORITHM_PARAMETER_OID = "Algorithm Parameter OID"
const val MOD_1024_WITH_160_SUBGROUP = "1024-bit MODP Group with 160-bit Prime Order Subgroup"
const val MOD_2048_WITH_224_SUBGROUP = "2048-bit MODP Group with 224-bit Prime Order Subgroup"
const val MOD_2048_WITH_256_SUBGROUP = "2048-bit MODP Group with 256-bit Prime Order Subgroup"
const val NIST_P192_STRING = "NIST P-192 (secp192r1)"
const val BP_P192 = "BrainpoolP192r1"
const val NIST_P224_STRING = "NIST P-224 (secp224r1)"
const val BP_P224 = "BrainpoolP224r1"
const val NIST_P256_STRING = "NIST P-256 (secp256r1)"
const val BP_P256 = "BrainpoolP256r1"
const val BP_P320 = "BrainpoolP320r1"
const val NIST_P384_STRING = "NIST P-384 (secp384r1)"
const val BP_P384 = "BrainpoolP384r1"
const val BP_P512 = "BrainpoolP512r1"
const val NIST_P521_STRING = "NIST P-521 (secp521r1)"
const val DES_CBC_CBC_STRING = "3DES-CBC-CBC"
const val AES_CBC_CMAC_128_STRING = "AES-CBC-CMAC-128"
const val AES_CBC_CMAC_192_STRING = "AES-CBC-CMAC-192"
const val AES_CBC_CMAC_256_STRING = "AES-CBC-CMAC-256"
const val PACE_DH_GM_STRING = "PACE-DH-GM"
const val PACE_ECDH_GM_STRING = "PACE-ECDH-GM"
const val PACE_DH_IM_STRING = "PACE-DH-IM"
const val PACE_ECDH_IM_STRING = "PACE-ECDH-IM"
const val PACE_ECDH_CAM_STRING = "PACE-ECDH-CAM"
const val ISSUING_AUTHORITY = "Issuing Authority"
const val OTHER_PERSON = "Other Person"
const val ENDORSEMENT = "Endorsement"
const val TAX_EXIT_REQUIREMENTS = "Tax/Exit Requirements"
const val PERSONALIZATION_TIME = "Document personalization time"
const val PERSONALIZATION_DEVICE_SERIAL_NUMBER = "Personalization device serial number"
const val FULL_NAME = "Full Name"
const val OTHER_NAME = "Other Name"
const val PERSONAL_NUMBER = "Personal Number"
const val BIRTH_PLACE = "Place of birth"
const val PROFESSION = "Profession"
const val TITLE = "Title"
const val SUMMARY = "Summary"
const val OTHER_TD_NUMBERS = "Other TD number"
const val CUSTODY_INFORMATION = "Custody information"
const val EYE_COLOR = "Eye color"
const val HAIR_COLOR = "Hair color"
const val GENDER = "Gender"
const val EXPRESSION = "Expression"
const val OPTIONAL_DATA = "Optional Data"
const val DOCUMENT_CODE = "Document Code"