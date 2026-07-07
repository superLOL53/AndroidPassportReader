package com.example.emrtdapplication.lds2


/**
 * International application identifier for the LDS2 Additional Biometric application
 */
const val ADDITIONAL_BIOMETRICS_APPLICATION_ID = "A0000002472003"

/**
 * First half of the identifier for a biometric file in the Additional Biometric application
 */
const val BIOMETRIC_FILE_ID: Byte = 0x02

/**
 * Maximum number of biometric files in the Additional Biometrics application
 */
const val MAX_BIOMETRIC_FILES = 0x40

const val BIOMETRIC_RECORD_SIZE = 3
const val UNABLE_TO_VERIFY_SIGNATURE = "Unable to verify signature for biometric data!"
const val INVALID_CERTIFICATE_REFERENCE = "Empty or invalid certificate reference!"
const val EMPTY_SIGNATURE = "Empty signature for Additional Biometrics file!"
const val EMPTY_BIOMETRIC_DATA = "Empty Biometric Data!"
const val ILLEGAL_TAG = "Illegal tag for Biometric Data Template!"
const val BIOMETRIC_TAG_SIZE = 2
/**
 * TLV record size of a certificate record
 */
const val CERTIFICATE_RECORD_SIZE = 2

/**
 * Minimum length for the certificate serial number
 */
const val MIN_CERTIFICATE_SERIAL_NUMBER_LENGTH = 3

const val CERTIFICATE_SERIAL_NUMBER_TAG_SIZE = 2
const val INVALID_CERTIFICATE_RECORD_SIZE = "Certificate record size must be ${CERTIFICATE_RECORD_SIZE}!"
const val INVALID_CERTIFICATE_SERIAL_NUMBER_TAG = "Invalid tag for certificate serial number"
const val EMPTY_CERTIFICATE_SERIAL_NUMBER = "Empty certificate serial number!"
const val INVALID_CERTIFICATE_SERIAL_NUMBER = "Content of certificate serial number is too short!"
const val INVALID_X509_TAG = "Invalid tag for X.509 certificate"
const val UNABLE_TO_DECODE_CERTIFICATE = "Unable to decode certificate!"

const val TRAVEL_RECORD_SIZE = 4
const val INVALID_RECORD_SIZE = "Record sequence must be of size $TRAVEL_RECORD_SIZE!"
const val INVALID_SIGNED_INFO_TAG = "Invalid tag for signed info in an Entry/Exit Record!"
const val INVALID_TAG = "Invalid tag in an Entry/Exit Record!"
const val TRAVEL_MODE_AIR_BYTE = 'A'.code.toByte()
const val TRAVEL_MODE_SEA_BYTE = 'S'.code.toByte()
const val TRAVEL_MODE_LAND_BYTE = 'L'.code.toByte()
const val UNSPECIFIED_CERTIFICATE_REFERENCE = "Unspecified certificate reference in Entry/Exit Record!"
const val UNSPECIFIED_SIGNATURE = "Unspecified signature in Entry/Exit Record!"
const val UNSPECIFIED_INSPECTOR_REFERENCE = "Unspecified inspector reference in Entry/Exit Record!"
const val UNSPECIFIED_INSPECTION_LOCATION = "Unspecified inspection location in Entry/Exit Record!"
const val UNSPECIFIED_INSPECTION_AUTHORITY = "Unspecified inspection authority in Entry/Exit Record!"
const val UNSPECIFIED_DATE = "Unspecified date in Entry/Exit Record!"
const val UNSPECIFIED_STATE_ENTRIES = "State entries are not present or mismatch!"
const val UNSPECIFIED_SIGNED_INFORMATION = "No signed information in the record!"
/**
 * Travel Records application identifier
 */
const val APPLICATION_ID = "A0000002472001"

/**
 * First byte of the file identifier for Entry Records
 */
const val ENTRY_RECORDS_ID_1: Byte = 0x01

/**
 * Second byte of the file identifier for Entry Records
 */
const val ENTRY_RECORDS_ID_2: Byte = 0x01

/**
 * First byte of the file identifier for Exit Records
 */
const val EXIT_RECORDS_ID_1: Byte = 0x01

/**
 * Second byte of the file identifier for Exit Records
 */
const val EXIT_RECORDS_ID_2: Byte = 0x02

/**
 * First byte of the file identifier for Certificate Records
 */
const val CERTIFICATE_RECORDS_ID_1: Byte = 0x01

/**
 * Second byte of the file identifier for Certificate Records
 */
const val CERTIFICATE_RECORDS_ID_2: Byte = 0x01A

const val VISA_RECORD_SIZE = 4
const val INVALID_VISA_RECORD_SIZE = "Record sequence must be of size $VISA_RECORD_SIZE!"
const val EMPTY_VISA_RECORD = "Empty visa record!"
const val INVALID_TAG_IN_SEQUENCE = "Invalid tag in record sequence!"
const val UNSPECIFIED_ISSUANCE_PLACE = "Unspecified issuance place in Visa Record!"
const val UNSPECIFIED_ISSUANCE_DATE = "Unspecified issuance date in Visa Record!"
const val UNSPECIFIED_EXPIRATION_DATE = "Unspecified expiration date in Visa Record!"
const val UNSPECIFIED_DOCUMENT_NUMBER = "Unspecified document number in Visa Record!"
const val UNSPECIFIED_HOLDER_NAME = "Unspecified holder name in Visa Record!"
const val UNSPECIFIED_SURNAME = "Unspecified surname in Visa Record!"
const val UNSPECIFIED_GIVEN_NAME = "Unspecified given name in Visa Record!"
const val UNSPECIFIED_SEX = "Unspecified sex in Visa Record!"
const val UNSPECIFIED_BIRTH_DATE = "Unspecified birth date in Visa Record!"
const val UNSPECIFIED_NATIONALITY = "Unspecified nationality in Visa Record!"
const val UNSPECIFIED_MRZ = "Unspecified MRZ in Visa Record!"
const val UNSPECIFIED_DOCUMENT_TYPE = "Unspecified document type in Visa Record!"
const val ILLEGAL_BIOMETRIC_FILE_REFERENCE = "Illegal length for the Biometrics EF file reference"
const val UNKNOWN_CERTIFICATE_REFERENCE = "Unspecified or invalid certificate reference in Visa Record!"
const val UNABLE_TO_VERIFY_SIGNATURE_VISA_RECORD = "Unable to verify signature of the Visa Record!"
const val INVALID_STAY_DURATION = "Invalid length for duration of stays!"
const val NO_SIGNED_INFORMATION = "No signed information in the record!"
const val MISSING_STATE_ENTRIES = "State entries are not present or mismatch!"
/**
 * Visa Records application identifier
 */
const val VISA_RECORD_APPLICATION_ID = "A0000002472002"

/**
 * First byte of the Visa Record file identifier
 */
const val VISA_RECORD_ID_1: Byte = 0x01

/**
 * Second byte of the Visa Record file identifier
 */
const val VISA_RECORD_ID_2: Byte = 0x03
