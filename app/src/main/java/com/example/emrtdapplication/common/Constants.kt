package com.example.emrtdapplication.common


const val INVALID_TYPE =
    "Active Authentication Info must have type of Active Authentication Info"
const val ILLEGAL_REQUIRED_DATA_ACTIVE_AUTHENTICATION_INFO =
    "Illegal required data for Active Authentication Info!"
const val ILLEGAL_OPTIONAL_DATA =
    "Illegal optional data for Active Authentication Info!"
const val ILLEGAL_OID =
    "Illegal OID in Active Authentication Info!"

/**
 * Constants for the class AttributeInfo
 */
const val CARD_CAPABILITY_TAG: Byte = 0x47
const val SUPPORT_RECORD_NUMBER: Byte = 0x02
const val SUPPORT_SHORT_EF_ID: Byte = 0x04
const val SUPPORT_DF_FULL_NAME_SELECTION: Byte = 0x80.toByte()
const val UNIT_SIZE: Byte = 0x01
const val MASK_UNIT_SIZE: Byte = 0xF
const val SUPPORT_COMMAND_CHAINING: Byte = 0x80.toByte()
const val SUPPORT_EXTENDED_LENGTHS: Byte = 0x40
const val EXTENDED_LENGTH_INFO_IN_ATRINFO: Byte = 0x20
const val EXTENDED_LENGTH_TAG_1: Byte = 0x7F
const val EXTENDED_LENGTH_TAG_2: Byte = 0x66
const val AI_ID_1: Byte = 0x2F
const val AI_ID_2: Byte = 0x01
const val AI_MIN_LENGTH = 12
const val CARD_CAPABILITIES_LENGTH = 3
const val EXTENDED_LENGTH_INFO_SEQUENCE_LENGTH = 2
const val CARD_ACCESS_ID_1: Byte = 0x01
const val CARD_ACCESS_ID_2: Byte = 0x1C
const val UNKNOWN_SECURITY_INFO = "Unknown Security Info in EF.CardAccess!"

const val CARD_SECURITY_ID_1: Byte = 0x01
const val CARD_SECURITY_ID_2: Byte = 0x1D
//const val ID_CA = "0.4.0.127.0.7.2.2.3"
/**
 * OID for chip authentication with DH
 */
const val ID_CA_DH = "0.4.0.127.0.7.2.2.3.1"

/**
 * OID for chip authentication with DH and 3DES secure messaging
 */
const val ID_CA_DH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.1.1"
//const val ID_CA_DH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.1.2"
//const val ID_CA_DH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.1.3"
//const val ID_CA_DH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.1.4"

/**
 * OID for chip authentication with ECDH
 */
const val ID_CA_ECDH = "0.4.0.127.0.7.2.2.3.2"

/**
 * OID for chip authentication with ECDH and 3DES secure messaging
 */
const val ID_CA_ECDH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.2.1"
//const val ID_CA_ECDH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.2.2"
//const val ID_CA_ECDH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.2.3"
//const val ID_CA_ECDH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.2.4"
//const val ID_PK = "0.4.0.127.0.7.2.2.1"
//const val ID_PK_DH = "0.4.0.127.0.7.2.2.1.1"
//const val ID_PK_ECDH = "0.4.0.127.0.7.2.2.1.2"

const val CHIP_AUTHENTICATION_INFO_DH_ID = 1
const val CHIP_AUTHENTICATION_INFO_ECDH_ID = 2
const val ILLEGAL_REQUIRED_DATA_CHIP_AUTHENTICATION_PUBLIC_KEY =
    "Required data does not contain a SubjectPublicKeyInfo for ChipAuthenticationPublicKeyInfo!"
const val INVALID_KEY_ID =
    "Invalid key identifier for ChipAuthenticationPublicKeyInfo!"
/**
 * First byte of the file identifier for EF.DIR
 */
const val DIR_ID_1: Byte = 0x2F

/**
 * Second byte of the file identifier for EF.DIR
 */
const val DIR_ID_2: Byte = 0x00

/**
 * Length for a TLV holding an application identifier
 */
const val TEMPLATE_LENGTH = 9

/**
 * Length of application identifiers
 */
const val AID_LENGTH = 7

/**
 * Identifier for eMRTD applications
 */
const val AID = "A000000247"

/**
 * First byte of the LDS1 application identifier
 */
const val LDS1_ID_1: Byte = 0x10

/**
 * Second byte of the LDS1 application identifier
 */
const val LDS1_ID_2: Byte = 0x01

/**
 * LDS2 application identifier
 */
const val LDS2_ID: Byte = 0x20

/**
 * Application identifier Travel Records application
 */
const val TRAVEL_RECORDS_APPLICATION_ID: Byte = 0x01

/**
 * Application identifier Visa Records application
 */
const val VISA_RECORDS_APPLICATION_ID: Byte = 0x02

/**
 * Application identifier Additional Biometrics application
 */
const val ADDITIONAL_BIOMETRICS_APPLICATION_ID: Byte = 0x03
const val AID_EQUAL_LENGTH = 4
const val LDS_ID_INDEX_1 = 5
const val LDS_ID_INDEX_2 = 6
/**
 * Error code for no password to initialize PACE
 */
const val NO_PASSWORD = -1

/**
 * Error code for when no PACE protocol OID is given/supported
 */
const val NO_PACE_OID = -2

/**
 * Error code indicating a failure in setting up PACE
 */
const val INVALID_MSE_COMMAND = -3

/**
 * Error code indicating a failure in retrieving a nonce from the eMRTD
 */
const val INVALID_GENERAL_AUTHENTICATE = -4

/**
 * Error code indicating a failure in extracting the nonce from the eMRTD
 */
const val INVALID_NONCE = -5

/**
 * Constant for generating positive BigInteger numbers
 */
const val POSITIVE_NUMBER = 1

const val TOKEN_DATA_TAG_1: Byte = 0x7F
const val TOKEN_DATA_TAG_2: Byte = 0x49
const val SEQUENCE_COUNTER_SIZE = 16
const val IV_VECTOR_SIZE = 16
const val CHIP_AUTHENTICATION_DATA_SEQUENCE_INDEX = 1
const val PACE_MRZ_ID: Byte = 1
const val PACE_CAN_ID: Byte = 2
const val PACE_KEY_COMPUTATION_SEED: Byte = 3
const val P_192 = "P-192"
const val P_224 = "P-224"
const val P_256 = "P-256"
const val P_384 = "P-384"
const val P_521 = "P-521"
const val BP_P_192 = "brainpoolp192r1"
const val BP_P_224 = "brainpoolp224r1"
const val BP_P_256 = "brainpoolp256r1"
const val BP_P_320 = "brainpoolp320r1"
const val BP_P_384 = "brainpoolp384r1"
const val BP_P_512 = "brainpoolp512r1"
const val PACE_INFO_ASYMMETRIC_PROTOCOL_INDEX = 8
const val PACE_INFO_SYMMETRIC_PROTOCOL_INDEX = 9
const val PACE_OID_SIZE = 11
const val PACE_VERSION = 2
const val PACE_INFO_INVALID_PACE_VERSION = "Invalid version for PACE protocol!"
const val PACE_INFO_INVALID_PROTOCOLS = "Invalid protocols for PACE!"
const val PACE_INFO_INVALID_PARAMETER_TAG = "Invalid parameter tag for PACE!"
const val PACE_INFO_INVALID_PARAMETER_ID = "Invalid parameter identifier for PACE!"
/**
 * Unknown or undefined protocol
 */
const val UNDEFINED: Byte = -1

/**
 * Constant identifier for the asymmetric protocol used by the PACE protocol
 */
const val DH_GM: Byte = 1

/**
 * Constant identifier for the asymmetric protocol used by the PACE protocol
 */
const val ECDH_GM: Byte = 2

/**
 * Constant identifier for the asymmetric protocol used by the PACE protocol
 */
const val DH_IM: Byte = 3

/**
 * Constant identifier for the asymmetric protocol used by the PACE protocol
 */
const val ECDH_IM: Byte = 4

/**
 * Constant identifier for the asymmetric protocol used by the PACE protocol
 */
const val ECDH_CAM: Byte = 6

/**
 * Constant identifier for the symmetric protocol used by the eMRTD
 */
const val DES_CBC_CBC: Byte = 1

/**
 * Constant identifier for the symmetric protocol used by the eMRTD
 */
const val AES_CBC_CMAC_128: Byte = 2

/**
 * Constant identifier for the symmetric protocol used by the eMRTD
 */
const val AES_CBC_CMAC_192: Byte = 3

/**
 * Constant identifier for the symmetric protocol used by the eMRTD
 */
const val AES_CBC_CMAC_256: Byte = 4

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val MOD_P_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP: Byte = 0

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val MOD_P_2048_BIT_GROUP_WITH_224_BIT_PRIME_ORDER_SUBGROUP: Byte = 1

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val MOD_P_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP: Byte = 2

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val NIST_P192: Byte = 8

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val BRAIN_POOL_P192R1: Byte = 9

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val NIST_P224: Byte = 10

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val BRAIN_POOL_P224R1: Byte = 11

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val NIST_P256: Byte = 12

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val BRAIN_POOL_P256R1: Byte = 13

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val BRAIN_POOL_P320R1: Byte = 14

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val NIST_P384: Byte = 15

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val BRAIN_POOL_P384R1: Byte = 16

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val BRAIN_POOL_P512R1: Byte = 17

/**
 * Constant identifier for the asymmetric protocol parameters used by the PACE protocol
 */
const val NIST_P521: Byte = 18
/**
 * OID for digital signature algorithm RSA-PSS with SHA-256
 */
const val ID_TA_RSA_PSS_SHA_256 = "0.4.0.127.0.7.2.2.2.1.4"

/**
 * OID for digital signature algorithm RSA-PSS with SHA-512
 */
const val ID_TA_RSA_PSS_SHA_512 = "0.4.0.127.0.7.2.2.2.1.6"

/**
 * OID for digital signature algorithm EC with SHA-224
 */
const val ID_TA_ECDSA_SHA_224 = "0.4.0.127.0.7.2.2.2.2.2"

/**
 * OID for digital signature algorithm EC with SHA-256
 */
const val ID_TA_ECDSA_SHA_256 = "0.4.0.127.0.7.2.2.2.2.3"

/**
 * OID for digital signature algorithm EC with SHA-384
 */
const val ID_TA_ECDSA_SHA_384 = "0.4.0.127.0.7.2.2.2.2.4"

/**
 * OID for digital signature algorithm EC with SHA-512
 */
const val ID_TA_ECDSA_SHA_512 = "0.4.0.127.0.7.2.2.2.2.5"

/**
 * OID size of PACE Domain Parameter Info type
 */
const val PACE_DOMAIN_PARAMETER_INFO_TYPE_SIZE = 10

/**
 * OID size of PACE Info type
 */
const val PACE_INFO_TYPE_SIZE = 11

/**
 * General error code for an invalid argument
 */
const val INVALID_ARGUMENT = -4
