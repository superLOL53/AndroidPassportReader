package com.example.emrtdapplication

class PACEInfo {
    private var protocol : Byte = 0
    private var MAC : Byte = 0
    private var version = 0
    private var parameterId : Byte = 0
    private var id_PACE_OID : ByteArray? = null

    fun setProtocol(tlv: TLV) : Int {
        Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Setting protocol...")
        protocol = PACEInfoConstants.UNDEFINED
        MAC = PACEInfoConstants.UNDEFINED
        if (tlv.getValue() == null || tlv.getTag()[0] != 0x06.toByte() || tlv.getLength() != 0x0A.toByte()) {
            Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Invalid argument")
            return INVALID_ARGUMENT
        }
        val s = StringBuilder()
        for (b : Byte in tlv.getValue()!!) {
            s.append(b.toInt().toString())
            s.append('.')
        }
        if (s.toString().startsWith(PACEInfoConstants.id_PACE)) {
            Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Invalid string...")
            return INVALID_ARGUMENT
        }
        Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Setting...")
        protocol = when (tlv.getValue()!![8]) {
            PACEInfoConstants.DH_GM -> PACEInfoConstants.DH_GM
            PACEInfoConstants.DH_IM -> PACEInfoConstants.DH_IM
            PACEInfoConstants.ECDH_GM -> PACEInfoConstants.ECDH_GM
            PACEInfoConstants.ECDH_IM -> PACEInfoConstants.ECDH_IM
            PACEInfoConstants.ECDH_CAM -> PACEInfoConstants.ECDH_CAM
            else -> {
                return Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, INVALID_ARGUMENT, "Unknown cipher: ", tlv.getValue()!!)
            }
        }
        Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Setting MAC...")
        when (tlv.getValue()!![9]) {
            PACEInfoConstants.DES_CBC_CBC -> MAC = PACEInfoConstants.DES_CBC_CBC
            PACEInfoConstants.AES_CBC_CMAC_128 -> MAC = PACEInfoConstants.AES_CBC_CMAC_128
            PACEInfoConstants.AES_CBC_CMAC_192 -> MAC = PACEInfoConstants.AES_CBC_CMAC_192
            PACEInfoConstants.AES_CBC_CMAC_256 -> PACEInfoConstants.AES_CBC_CMAC_256
            else -> {
                protocol = 0
                return Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, INVALID_ARGUMENT, "Unknown MAC: ", tlv.getValue()!!)
            }
        }
        if (protocol == PACEInfoConstants.ECDH_CAM && MAC == PACEInfoConstants.DES_CBC_CBC) {
            protocol = PACEInfoConstants.UNDEFINED
            MAC = PACEInfoConstants.UNDEFINED
            return INVALID_ARGUMENT
        }
        id_PACE_OID = tlv.getValue()
        Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Protocol set")
        return SUCCESS
    }

    fun getProtocol() : Byte {
        return protocol
    }

    fun getMAC() : Byte {
        return MAC
    }

    fun getPACEOID() : ByteArray? {
        return id_PACE_OID
    }

    fun setVersion(tlv: TLV) : Int {
        Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Setting version...")
        if (tlv.getTag()[0] != 2.toByte() || tlv.getLength() != 1.toByte() || tlv.getValue() == null || tlv.getValue()!![0] != 2.toByte()) {
            return INVALID_ARGUMENT
        } else {
            version = 2
            Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Version set")
            return SUCCESS
        }
    }

    fun getVersion() : Int {
        return version
    }

    fun setParameterId(tlv: TLV) : Int {
        Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "Setting parameterId...")
        if (tlv.getTag()[0] != 2.toByte() || tlv.getLength() != 1.toByte() || tlv.getValue() == null) {
            return INVALID_ARGUMENT
        }
        if (tlv.getValue()!![0] in 0..2 || tlv.getValue()!![0] in 8..18) {
            parameterId = tlv.getValue()!![0]
        } else {
            return Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, INVALID_ARGUMENT, "Unknown parameterId: ", tlv.getValue()!!)
        }
        Logger.log(PACEInfoConstants.TAG, PACEInfoConstants.ENABLE_LOGGING, "ParameterId set")
        return SUCCESS
    }

    fun getParameterId() : Byte {
        return parameterId
    }
}