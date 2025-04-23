package com.example.emrtdapplication

class CardSecurity : EMRTDFile {
    override fun read() : Int {
        Logger.log(CardSecurityConstants.TAG, CardSecurityConstants.ENABLE_LOGGING, "Read Card Security Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x01, 0x1d)))
        Logger.log(CardSecurityConstants.TAG, CardSecurityConstants.ENABLE_LOGGING, "Card Security info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(CardSecurityConstants.TAG, CardSecurityConstants.ENABLE_LOGGING, FILE_UNABLE_TO_SELECT, "Could not select Card Security info file. Error Code: ", info)
        }
        Logger.log(CardSecurityConstants.TAG, CardSecurityConstants.ENABLE_LOGGING, "Read Card Security info file. Contents are: ", info)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0xff.toByte(), ZERO_SHORT))
        Logger.log(CardSecurityConstants.TAG, CardSecurityConstants.ENABLE_LOGGING, "Attribute info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(CardSecurityConstants.TAG, CardSecurityConstants.ENABLE_LOGGING, FILE_UNABLE_TO_READ, "Could not read Card Security info file. Error Code: ", info)
        }
        return Logger.log(CardSecurityConstants.TAG, CardSecurityConstants.ENABLE_LOGGING, FILE_SUCCESSFUL_READ, "Read Card Security info file. Contents are: ", info)
    }

    override fun getData() : Int {
        return NOT_IMPLEMENTED
    }

    private fun parseData() : Int {
        return NOT_IMPLEMENTED
    }
}