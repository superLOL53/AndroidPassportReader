package com.example.emrtdapplication

class Directory : EMRTDFile {
    override fun read() : Int {
        Logger.log(DirectoryConstants.TAG, DirectoryConstants.ENABLE_LOGGING, "Read Directory Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x2, ZERO_SHORT, byteArrayOf(0x2f, 0x00)))
        Logger.log(DirectoryConstants.TAG, DirectoryConstants.ENABLE_LOGGING, "Directory info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(DirectoryConstants.TAG, DirectoryConstants.ENABLE_LOGGING, FILE_UNABLE_TO_SELECT, "Could not select Directory info file. Error Code: ", info)
        }
        Logger.log(DirectoryConstants.TAG, DirectoryConstants.ENABLE_LOGGING, "Read Directory info file. Contents are: ", info)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0xff.toByte(), ZERO_SHORT))
        Logger.log(DirectoryConstants.TAG, DirectoryConstants.ENABLE_LOGGING, "Directory info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            Logger.log(DirectoryConstants.TAG, DirectoryConstants.ENABLE_LOGGING, FILE_UNABLE_TO_READ, "Could not read Directory info file. Error Code: ", info)
        }
        return Logger.log(DirectoryConstants.TAG, DirectoryConstants.ENABLE_LOGGING, FILE_SUCCESSFUL_READ, "Read Directory info file. Contents are: ", info)
    }

    override fun getData() : Int {
        return NOT_IMPLEMENTED
    }
    
    private fun parseData() :Int {
        return NOT_IMPLEMENTED
    }
}