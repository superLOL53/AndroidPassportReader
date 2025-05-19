package com.example.emrtdapplication

/**
 * Constants for the PACEInfo class
 */
const val PI_TAG = "PACEInfo"
const val PI_ENABLE_LOGGING = true
const val UNDEFINED : Byte = -1
const val id_PACE = "0.4.0.127.0.7.2.2.4"
const val DH_GM : Byte = 1
const val ECDH_GM : Byte = 2
const val DH_IM : Byte = 3
const val ECDH_IM : Byte = 4
const val ECDH_CAM : Byte = 6
const val DES_CBC_CBC : Byte = 1
const val AES_CBC_CMAC_128 : Byte = 2
const val AES_CBC_CMAC_192 : Byte = 3
const val AES_CBC_CMAC_256 : Byte = 4
/*const val MODP_1024_BIT_GROUP_WITH_160_BIT_PRIME_ORDER_SUBGROUP : Byte = 0
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
const val NIST_P521 : Byte = 18*/

/**
 * Class representing information about the PACE protocol
 */
class PACEInfo {
    private var protocol : Byte = UNDEFINED
    private var mac : Byte = UNDEFINED
    private var version = -1
    private var parameterId : Byte = UNDEFINED
    private var idPACEOid : ByteArray? = null
    private var paceOid : String? = null

    /**
     * Sets the PACE info variables
     * @param tlv: The TLV structure containing information about the supported PACE protocol
     * @return Success(0) or invalid argument(-4)
     */
    fun setProtocol(tlv: TLV) : Int {
        log("Setting protocol...")
        protocol = UNDEFINED
        mac = UNDEFINED
        if (tlv.getValue() == null || tlv.getTag()[0] != 0x06.toByte() || tlv.getLength() != 0x0A.toByte()) {
            log("Invalid argument")
            return INVALID_ARGUMENT
        }
        val s = StringBuilder()
        for (b : Byte in tlv.getValue()!!) {
            s.append(b.toInt().toString())
            s.append('.')
        }
        if (s.toString().startsWith(id_PACE)) {
            log("Invalid string...")
            return INVALID_ARGUMENT
        }
        paceOid = s.toString()
        log("PACE OID is $paceOid")
        log("Setting...")
        protocol = when (tlv.getValue()!![8]) {
            DH_GM -> DH_GM
            DH_IM -> DH_IM
            ECDH_GM -> ECDH_GM
            ECDH_IM -> ECDH_IM
            ECDH_CAM -> ECDH_CAM
            else -> {
                return log(INVALID_ARGUMENT, "Unknown cipher: ", tlv.getValue()!!)
            }
        }
        log("Setting MAC...")
        when (tlv.getValue()!![9]) {
            DES_CBC_CBC -> mac = DES_CBC_CBC
            AES_CBC_CMAC_128 -> mac = AES_CBC_CMAC_128
            AES_CBC_CMAC_192 -> mac = AES_CBC_CMAC_192
            AES_CBC_CMAC_256 -> AES_CBC_CMAC_256
            else -> {
                protocol = 0
                return log(INVALID_ARGUMENT, "Unknown MAC: ", tlv.getValue()!!)
            }
        }
        if (protocol == ECDH_CAM && mac == DES_CBC_CBC) {
            protocol = UNDEFINED
            mac = UNDEFINED
            return INVALID_ARGUMENT
        }
        idPACEOid = tlv.getValue()
        log("Protocol set")
        return SUCCESS
    }

    /**
     * Returns the PACE OID as an byte array
     * @return The PACE OID as an byte array
     */
    fun getPACEOid() : ByteArray? {
        return idPACEOid
    }

    /**
     * Returns the cryptographic protocol used by PACE to establish symmetric keys
     * @return The cryptographic protocol as OID byte
     */
    fun getProtocol() : Byte {
        return protocol
    }

    /**
     * Returns the symmetric, cryptographic protocol and MAC used by PACE
     * @return The symmetric, cryptographic protocols used after PACE is established as OID byte
     */
    fun getMAC() : Byte {
        return mac
    }

    /**
     * Sets the version of the PACE protocol
     * @param tlv: The TLV structure containing the version number
     * @return Success(0) or invalid argument(-4)
     */
    fun setVersion(tlv: TLV) : Int {
        log("Setting version...")
        if (tlv.getTag()[0] != 2.toByte() || tlv.getLength() != 1.toByte() || tlv.getValue() == null || tlv.getValue()!![0] != 2.toByte()) {
            return INVALID_ARGUMENT
        } else {
            version = 2
            log("Version set")
            return SUCCESS
        }
    }

    /**
     * Returns the version of PACE
     * @return The version of the PACE protocol
     */
    fun getVersion() : Int {
        return version
    }

    /**
     * Sets the parameter ID of PACE
     * @param tlv: The TLV structure containing the parameter ID
     * @return Success(0) or invalid argument(-4)
     */
    fun setParameterId(tlv: TLV) : Int {
        log("Setting parameterId...")
        if (tlv.getTag()[0] != 2.toByte() || tlv.getLength() != 1.toByte() || tlv.getValue() == null) {
            return INVALID_ARGUMENT
        }
        if (tlv.getValue()!![0] in 0..2 || tlv.getValue()!![0] in 8..18) {
            parameterId = tlv.getValue()!![0]
        } else {
            return log(INVALID_ARGUMENT, "Unknown parameterId: ", tlv.getValue()!!)
        }
        log("ParameterId set")
        return SUCCESS
    }

    /**
     * Returns the parameter ID
     * @return The parameter ID of PACE
     */
    fun getParameterId() : Byte {
        return parameterId
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(PI_TAG, PI_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(PI_TAG, PI_ENABLE_LOGGING, error, msg, b)
    }
}