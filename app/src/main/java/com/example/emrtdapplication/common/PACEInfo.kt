package com.example.emrtdapplication.common

import com.example.emrtdapplication.INVALID_ARGUMENT
import com.example.emrtdapplication.PACE_INFO_TYPE
import com.example.emrtdapplication.PACE_OID
import com.example.emrtdapplication.SUCCESS
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.utils.TLV

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
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence PACEInfo:
 *
 *      PACEInfo::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(
 *                  id-PACE-DH-GM-3DES-CBC-CBC |
 *                  id-PACE-DH-GM-AES-CBC-CMAC-128 |
 *                  id-PACE-DH-GM-AES-CBC-CMAC-192 |
 *                  id-PACE-DH-GM-AES-CBC-CMAC-256 |
 *                  id-PACE-ECDH-GM-3DES-CBC-CBC |
 *                  id-PACE-ECDH-GM-AES-CBC-CMAC-128 |
 *                  id-PACE-ECDH-GM-AES-CBC-CMAC-192 |
 *                  id-PACE-ECDH-GM-AES-CBC-CMAC-256 |
 *                  id-PACE-DH-IM-3DES-CBC-CBC |
 *                  id-PACE-DH-IM-AES-CBC-CMAC-128 |
 *                  id-PACE-DH-IM-AES-CBC-CMAC-192 |
 *                  id-PACE-DH-IM-AES-CBC-CMAC-256 |
 *                  id-PACE-ECDH-IM-3DES-CBC-CBC |
 *                  id-PACE-ECDH-IM-AES-CBC-CMAC-128 |
 *                  id-PACE-ECDH-IM-AES-CBC-CMAC-192 |
 *                  id-PACE-ECDH-IM-AES-CBC-CMAC-256 |
 *                  id-PACE-ECDH-CAM-AES-CBC-CMAC-128 |
 *                  id-PACE-ECDH-CAM-AES-CBC-CMAC-192 |
 *                  id-PACE-ECDH-CAM-AES-CBC-CMAC-256),
 *          version INTEGER, -- MUST be 2
 *          parameterId INTEGER OPTIONAL
 *      }
 *
 * @param tlv TLV structure containing an encoded instance of PACEInfo
 * @property asymmetricProtocol The asymmetric protocol identifier
 * to use for PACE. Must be one of DH-IM, DH-GM, ECDH-IM, ECDH-GM and ECDH-CAM
 * @property symmetricProtocol The symmetric protocol identifier
 * to use for PACE. Must be one of 3DES-CBC-CBC, AES-CBC-CMAC-128,
 * AES-CBC-CMAC-192 and AES-CBC-CMAC-256
 * @property version The protocol version. Must be 2
 * @property parameterId The parameter identifier for the asymmetric protocol
 * @throws IllegalArgumentException If [tlv] does not contain an encoded instance of PACEInfo
 */
class PACEInfo(tlv: TLV): SecurityInfo(tlv, PACE_INFO_TYPE) {
    var asymmetricProtocol: Byte = UNDEFINED
        private set
    var symmetricProtocol: Byte = UNDEFINED
        private set
    var version = -1
        private set
    var parameterId: Byte? = null
        private set

    init {
        if (extractProtocols() != SUCCESS) {
            throw IllegalArgumentException(PACE_INFO_INVALID_PROTOCOLS)
        }
        if (requiredData.tag.size != 1 ||
            requiredData.tag[0] != TlvTags.INTEGER ||
            requiredData.value == null ||
            requiredData.value!!.size != 1 ||
            requiredData.value!![0].toInt() != PACE_VERSION
        ) {
            throw IllegalArgumentException(PACE_INFO_INVALID_PACE_VERSION)
        } else {
            version = PACE_VERSION
        }
        if (optionalData != null) {
            if (optionalData.value == null || optionalData.value!!.size != 1) {
                throw IllegalArgumentException(PACE_INFO_INVALID_PARAMETER_TAG)
            }
            parameterId = optionalData.value!![0]
            if (!(parameterId!! in
                        MOD_P_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP ..
                        MOD_P_2048_BIT_GROUP_WITH_256_BIT_PRIME_ORDER_SUBGROUP ||
                parameterId!! in NIST_P192..NIST_P521)
            ) {
                throw IllegalArgumentException(PACE_INFO_INVALID_PARAMETER_ID)
            }
        } else {
            parameterId = null
        }
    }

    /**
     * Extract and validate the asymmetric and symmetric protocols and their combination
     * @return [SUCCESS] or [INVALID_ARGUMENT] if the protocols are invalid
     */
    private fun extractProtocols(): Int {
        if (!objectIdentifier.startsWith(PACE_OID) ||
            objectIdentifier.split(".").size != PACE_OID_SIZE
        ) {
            return INVALID_ARGUMENT
        }
        asymmetricProtocol = protocol[PACE_INFO_ASYMMETRIC_PROTOCOL_INDEX]
        if (!(asymmetricProtocol in DH_GM..ECDH_IM || asymmetricProtocol == ECDH_CAM)) {
            return INVALID_ARGUMENT
        }
        symmetricProtocol = protocol[PACE_INFO_SYMMETRIC_PROTOCOL_INDEX]
        if (symmetricProtocol !in DES_CBC_CBC..AES_CBC_CMAC_256) {
            return INVALID_ARGUMENT
        }
        if (asymmetricProtocol == ECDH_CAM && symmetricProtocol == DES_CBC_CBC) {
            return INVALID_ARGUMENT
        }
        return SUCCESS
    }
}