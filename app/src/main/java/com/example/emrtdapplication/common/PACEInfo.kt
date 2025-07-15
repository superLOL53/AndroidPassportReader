package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.INVALID_ARGUMENT
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV
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
class PACEInfo {
    private var asymmetricProtocol : Byte = UNDEFINED
    private var symmetricProtocol : Byte = UNDEFINED
    private var version = -1
    private var parameterId : Byte = UNDEFINED
    private var idPACEOid : ByteArray? = null

    /**
     * Sets the PACE info variables
     * @param tlv: The TLV structure containing information about the supported PACE protocol
     * @return Success(0) or invalid argument(-4)
     */
    fun setInfo(tlv: TLV) : Int {
        if (!tlv.getIsValid() || tlv.getTag().size != 1 || tlv.getTag()[0] != PACE_INFO_TAG) {
            return INVALID_ARGUMENT
        }
        for (i in tlv.getTLVSequence()!!.getTLVSequence().indices) {
            val tag = tlv.getTLVSequence()!!.getTLVSequence()[i]
            if (!tag.getIsValid() || tag.getTag().size != 1 || tag.getLength() > Byte.MAX_VALUE) {
                return INVALID_ARGUMENT
            }
            if (tag.getTag()[0] == OID_TAG) {
                idPACEOid = tag.getValue()
                if (extractProtocols() != SUCCESS) {
                    return INVALID_ARGUMENT
                }
            } else if (tag.getTag()[0] == TLV_TAGS.INTEGER) {
                if (i == 1) {
                    version = tag.getValue()?.get(0)?.toInt() ?: 0
                } else if (i == 2) {
                    parameterId = tag.getValue()?.get(0) ?: 0
                    if (!(parameterId in 0..2 || parameterId in 8..18)) {
                        return INVALID_ARGUMENT
                    }
                }
            } else {
                return INVALID_ARGUMENT
            }
        }
        return SUCCESS
    }

    fun getPaceOid(): ByteArray? {
        return idPACEOid
    }

    fun getParameterID(): Byte {
        return parameterId
    }

    fun getVersion(): Int {
        return version
    }

    fun getSymmetricProtocol(): Byte {
        return symmetricProtocol
    }

    fun getAsymmetricProtocol(): Byte {
        return asymmetricProtocol
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun extractProtocols(): Int {
        if (idPACEOid == null || !idPACEOid!!.toHexString().startsWith(ID_PACE) || idPACEOid!!.size != 10) {
            return INVALID_ARGUMENT
        }
        asymmetricProtocol = idPACEOid!![8]
        if (!(asymmetricProtocol in 1..4 || asymmetricProtocol.toInt() == 6)) {
            return INVALID_ARGUMENT
        }
        symmetricProtocol = idPACEOid!![9]
        if (symmetricProtocol !in 1..4) {
            return INVALID_ARGUMENT
        }
        return SUCCESS
    }
}