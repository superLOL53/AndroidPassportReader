package com.example.emrtdapplication.common

import android.content.Context
import android.widget.LinearLayout
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

    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        super.createViews(context, parent)
        if (tableLayout != null) {
            var row = createRow(context, parent)
            provideTextForRow(row, "Asymmetric protocol:", decodeAsymmetricProtocol())
            tableLayout!!.addView(row)
            row = createRow(context, parent)
            provideTextForRow(row, "Symmetric protocol:", decodeSymmetricProtocol())
            tableLayout!!.addView(row)
            row = createRow(context, parent)
            provideTextForRow(row, "Version:", version.toString())
            tableLayout!!.addView(row)
            if (parameterId != null) {
                row = createRow(context, parent)
                provideTextForRow(row, "Parameter ID:", parameterId.toString())
            }
        }
    }

    private fun decodeAsymmetricProtocol() : String {
        return when(asymmetricProtocol) {
            1.toByte() -> "PACE-DH-GM"
            2.toByte() -> "PACE-ECDH-GM"
            3.toByte() -> "PACE-DH-IM"
            4.toByte() -> "PACE-ECDH-IM"
            6.toByte() -> "PACE-ECDH-CAM"
            else -> "Unknown"
        }
    }

    private fun decodeSymmetricProtocol() : String {
        return when(symmetricProtocol) {
            1.toByte() -> "3DES-CBC-CBC"
            2.toByte() -> "AES-CBC-CMAC-128"
            3.toByte() -> "AES-CBC-CMAC-192"
            4.toByte() -> "AES-CBC-CMAC-256"
            else -> "Unknown"
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