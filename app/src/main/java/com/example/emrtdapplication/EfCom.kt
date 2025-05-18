package com.example.emrtdapplication

class EfCom : EMRTDFile {
    private var LDS_Version = 0
    private var LDS_UpdateLevel = 0
    private var unicodeMajorVersion = 0
    private var unicodeMinorVersion = 0
    private var unicodeReleaseVersion = 0
    private var tagList : ByteArray? = null

    @OptIn(ExperimentalStdlibApi::class)
    override fun read() : Int {
        log("Reading EFCOM File...")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x01, 0x1e)))
        if (info[info.size-2] != NfcRespondCodeSW1.OK || info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(FILE_UNABLE_TO_SELECT, "Cannot select EFCOM File. Error Code: " + info.toHexString())
        }
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x04, ZERO_SHORT))
        if (info[info.size-2] != NfcRespondCodeSW1.OK || info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(FILE_UNABLE_TO_READ, "Cannot read EFCOM File. Error Code: " + info.toHexString())
        }
        return parse(info.slice(0..info.size-3).toByteArray())
    }

    override fun getData() : Int {
        return NOT_IMPLEMENTED
    }

    private fun parse(contents : ByteArray) : Int {
        log("Parsing: ", contents)
        val decode = TLVCoder().decode(contents)
        var i = 0
        if (decode[i].getTag()[0] != EFCOMConstants.TAG60) {
            return FILE_UNABLE_TO_READ
        }
        i++
        if (decode[i].getTag()[0] != EFCOMConstants.LDS_VERSION_TAG_1 || decode[i].getTag()[1] != EFCOMConstants.LDS_VERSION_TAG_2) {
            return FILE_UNABLE_TO_READ
        }
        var value = decode[i].getValue()
        if (value == null || decode[i].getLength() != 4.toByte()) {
            return FILE_UNABLE_TO_READ
        }
        LDS_Version = value[0]*10 + value[1]
        LDS_UpdateLevel = value[2]*10 + value[3]
        i++
        if (decode[i].getTag()[0] != EFCOMConstants.UNICODE_VERSION_TAG_1 || decode[i].getTag()[1] != EFCOMConstants.UNICODE_VERSION_TAG_2) {
            return FILE_UNABLE_TO_READ
        }
        value = decode[i].getValue()
        if (value == null || decode[i].getLength() != 6.toByte()) {
            return FILE_UNABLE_TO_READ
        }
        unicodeMajorVersion = value[0]*10 + value[1]
        unicodeMinorVersion = value[2]*10 + value[3]
        unicodeReleaseVersion = value[4]*10 + value[5]
        i++
        if (decode[i].getTag()[0] != EFCOMConstants.TAG_LIST_TAG) {
            return FILE_UNABLE_TO_READ
        }
        tagList = decode[i].getValue()
        return FILE_SUCCESSFUL_READ
    }

    private fun log(msg: String) {
        Logger.log(EFCOMConstants.TAG, EFCOMConstants.ENABLE_LOGGING, msg)
    }

    private fun log(error : Int, msg : String) : Int {
        return Logger.log(EFCOMConstants.TAG, EFCOMConstants.ENABLE_LOGGING, error, msg)
    }

    private fun log(msg : String, b : ByteArray) {
        return Logger.log(EFCOMConstants.TAG, EFCOMConstants.ENABLE_LOGGING, msg, b)
    }
}

object EFCOMConstants {
    const val TAG = "efcom"
    const val ENABLE_LOGGING = true
    const val TAG60 : Byte = 0x60
    const val LDS_VERSION_TAG_1 : Byte = 0x5F
    const val LDS_VERSION_TAG_2 : Byte = 0x01
    const val UNICODE_VERSION_TAG_1 : Byte = 0x5F
    const val UNICODE_VERSION_TAG_2 : Byte = 0x36
    const val TAG_LIST_TAG : Byte = 0x5C
}