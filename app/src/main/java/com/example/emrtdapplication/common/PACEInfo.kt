package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.INVALID_ARGUMENT
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV_TAGS

/**
 * Constants for the PACEInfo class
 */
const val PI_TAG = "PACEInfo"
const val PI_ENABLE_LOGGING = true
const val UNDEFINED : Byte = -1
const val PACE_INFO_TAG: Byte = 0x30
const val OID_TAG: Byte = 0x06
const val OID_LENGTH = 10
const val ID_PACE = "04007f0007020204"
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

/**
 * Class representing information about the PACE protocol
 */
class PACEInfo(rawFileContent : ByteArray): SecurityInfo(rawFileContent) {
    var asymmetricProtocol : Byte = UNDEFINED
        private set
    var symmetricProtocol : Byte = UNDEFINED
        private set
    var version = -1
        private set
    var parameterId : Byte? = null
        private set

    init {
        if (extractProtocols() != SUCCESS) {
            throw IllegalArgumentException("Invalid protocols for PACE")
        }
        if (requiredData.getTag().size != 1 || requiredData.getTag()[0] != TLV_TAGS.INTEGER ||
            requiredData.getValue() == null || requiredData.getValue()!!.size != 1 || requiredData.getValue()!![0].toInt() != 2) {
            throw IllegalArgumentException("Invalid version for PACE protocol")
        } else {
            version = 2
        }
        if (optionalData != null) {
            if (optionalData!!.getValue() == null || optionalData!!.getValue()!!.size != 1) {
                throw IllegalArgumentException("Invalid parameter tag")
            }
            parameterId = optionalData!!.getValue()!![0]
            if (!(parameterId!! in 0..2 || parameterId!! in 8..18)) {
                throw IllegalArgumentException("Invalid parameter identifier for PACE")
            }
        } else {
            parameterId = null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun extractProtocols(): Int {
        if (!protocol.toHexString().startsWith(ID_PACE) || protocol.size != 10) {
            return INVALID_ARGUMENT
        }
        asymmetricProtocol = protocol[8]
        if (!(asymmetricProtocol in 1..4 || asymmetricProtocol.toInt() == 6)) {
            return INVALID_ARGUMENT
        }
        symmetricProtocol = protocol[9]
        if (symmetricProtocol !in 1..4) {
            return INVALID_ARGUMENT
        }
        if (asymmetricProtocol.toInt() == 6 && symmetricProtocol.toInt() == 1) {
            return INVALID_ARGUMENT
        }
        return SUCCESS
    }
}