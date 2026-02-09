package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.INVALID_ARGUMENT
import com.example.emrtdapplication.constants.PACEInfoConstants.UNDEFINED
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_OID
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence PACEInfo:
 *
 *      PACEInfo ::= SEQUENCE {
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
 * @property asymmetricProtocol The asymmetric protocol identifier to use for PACE. Must be one of DH-IM, DH-GM, ECDH-IM, ECDH-GM and ECDH-CAM
 * @property symmetricProtocol The symmetric protocol identifier to use for PACE. Must be one of 3DES-CBC-CBC, AES-CBC-CMAC-128, AES-CBC-CMAC-192 and AES-CBC-CMAC-256
 * @property version The protocol version. Must be 2
 * @property parameterId The parameter identifier for the asymmetric protocol
 * @throws IllegalArgumentException If [tlv] does not contain an encoded instance of PACEInfo
 */
class PACEInfo(tlv: TLV): SecurityInfo(tlv, PACE_INFO_TYPE) {
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
        if (requiredData.tag.size != 1 || requiredData.tag[0] != TlvTags.INTEGER ||
            requiredData.value == null || requiredData.value!!.size != 1 || requiredData.value!![0].toInt() != 2) {
            throw IllegalArgumentException("Invalid version for PACE protocol")
        } else {
            version = 2
        }
        if (optionalData != null) {
            if (optionalData.value == null || optionalData.value!!.size != 1) {
                throw IllegalArgumentException("Invalid parameter tag")
            }
            parameterId = optionalData.value!![0]
            if (!(parameterId!! in 0..2 || parameterId!! in 8..18)) {
                throw IllegalArgumentException("Invalid parameter identifier for PACE")
            }
        } else {
            parameterId = null
        }
    }

    /**
     * Extract and validate the asymmetric and symmetric protocols and their combination
     * @return [SUCCESS] or [INVALID_ARGUMENT] if the protocols are invalid
     */
    @OptIn(ExperimentalStdlibApi::class)
    private fun extractProtocols(): Int {
        if (!objectIdentifier.startsWith(PACE_OID) || objectIdentifier.split(".").size != 11) {
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