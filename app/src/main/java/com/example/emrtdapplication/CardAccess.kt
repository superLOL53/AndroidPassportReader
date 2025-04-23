package com.example.emrtdapplication

class CardAccess : EMRTDFile {
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
        return Logger.log(CardAccessConstants.TAG, CardAccessConstants.ENABLE_LOGGING, FILE_SUCCESSFUL_READ, "Read Card Access info file. Contents are: " + info.toHexString())
    }

    override fun getData() : Int {
        return -1
    }
}