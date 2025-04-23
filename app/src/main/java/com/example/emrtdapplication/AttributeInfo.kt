package com.example.emrtdapplication

class AttributeInfo : EMRTDFile {
    @OptIn(ExperimentalStdlibApi::class)
    override fun read() : Int {
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Select Attribute Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x2F, 0x01)))
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Select Attribute info answer: " + info.toHexString())
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_UNABLE_TO_SELECT, "Could not select Attribute info file. Error Code: " + info.toHexString())
        }
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Attribute info answer: " + info.toHexString())
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Read Attribute Info")
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0xff.toByte(), ZERO_SHORT))
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Attribute info answer: " + info.toHexString())
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_UNABLE_TO_READ, "Could not read Attribute info file. Error Code: " + info.toHexString())
        }
        return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_SUCCESSFUL_READ, "Read Attribute info file. Contents are: " + info.toHexString())
    }

    override fun getData() : Int {
        return -1
    }
}