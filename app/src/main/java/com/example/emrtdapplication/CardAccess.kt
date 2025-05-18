package com.example.emrtdapplication


class CardAccess : EMRTDFile {
    private var paceInfo = PACEInfo()
    private var paceDomainParams = PACEDomainParameterInfo()

    @OptIn(ExperimentalStdlibApi::class)
    override fun read() : Int {
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Read Card Access Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x01, 0x1c)))
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Card Access info answer: " + info.toHexString())
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, FILE_UNABLE_TO_SELECT, "Could not read Card Access info file. Error Code: " + info.toHexString())
        }
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Read Card Access info file. Contents are: " + info.toHexString())
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0xff.toByte(), ZERO_SHORT))
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Card Access info answer: " + info.toHexString())
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, FILE_UNABLE_TO_READ, "Could not read Card access info file. Error Code: " + info.toHexString())
        }
        return Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, parse(info.slice(0..info.size-3).toByteArray()), "Read Card Access info file. Contents are: " + info.toHexString())
    }

    override fun getData() : Int {
        return NOT_IMPLEMENTED
    }

    fun getPACEInfo() : PACEInfo {
        return paceInfo
    }

    private fun parse(b : ByteArray) : Int {
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Parsing...")
        val tlv = TLVCoder().decode(b)
        var i = 0
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Reading PACE infos...")
        while (i < tlv.size) {
            //TODO: Read PACE Domain Parameters
            if (tlv[i].getTag()[0] == 0x30.toByte()) {
                i++
                if (tlv[i].getTag()[0] == 0x06.toByte()) {
                    if (paceInfo.setProtocol(tlv[i]) != SUCCESS) {
                        return FILE_UNABLE_TO_READ
                    }
                    i++
                    if (tlv[i].getTag()[0] == TLV_TAGS.INTEGER) {
                        if (paceInfo.setVersion(tlv[i]) != SUCCESS) {
                            return FILE_UNABLE_TO_READ
                        }
                        i++
                        if (tlv[i].getTag()[0] == TLV_TAGS.INTEGER) {
                            if (paceInfo.setParameterId(tlv[i]) != SUCCESS) {
                                return FILE_UNABLE_TO_READ
                            }
                            i++
                        }
                    }
                }
            } else {
                i++
            }
        }
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "PACE parameters are: ")
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Protocol is ${paceInfo.getProtocol()}")
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "MAC is ${paceInfo.getMAC()}")
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "Version is ${paceInfo.getVersion()}")
        Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, "ParameterId is ${paceInfo.getParameterId()}")
        return FILE_SUCCESSFUL_READ
    }
}