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
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x02, ZERO_SHORT))
        if (info[info.size-2] != NfcRespondCodeSW1.OK || info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(FILE_UNABLE_TO_READ, "Cannot read EFCOM File. Error Code: " + info.toHexString())
        }
        val Le = (2 + info[1]).toByte()
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, Le, ZERO_SHORT))
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
        log("Decoded TLV Structure")
        var i = 0
        if (decode[i].getTag()[0] != EFCOMConstants.TAG60) {
            return log(FILE_UNABLE_TO_READ, "Return TAG60")
        }
        i++
        if (decode[i].getTag()[0] != EFCOMConstants.LDS_VERSION_TAG_1 || decode[i].getTag()[1] != EFCOMConstants.LDS_VERSION_TAG_2) {
            return log(FILE_UNABLE_TO_READ, "Return LDS Version ${decode[i].getTag()}")
        }
        var value = decode[i].getValue()
        if (value == null || decode[i].getLength() != 4.toByte()) {
            return log(FILE_UNABLE_TO_READ, "Return LDS Length ${decode[i].getLength()}")
        }
        LDS_Version = (value[0] - 48)*10 + value[1] - 48
        LDS_UpdateLevel = (value[2] - 48)*10 + value[3] - 48
        i++
        if (decode[i].getTag()[0] != EFCOMConstants.UNICODE_VERSION_TAG_1 || decode[i].getTag()[1] != EFCOMConstants.UNICODE_VERSION_TAG_2) {
            return log(FILE_UNABLE_TO_READ, "Return Unicode Version ${decode[i].getTag()}")
        }
        value = decode[i].getValue()
        if (value == null || decode[i].getLength() != 6.toByte()) {
            return log(FILE_UNABLE_TO_READ, "Return Unicode Length ${decode[i].getLength()}")
        }
        unicodeMajorVersion = (value[0] - 48)*10 + value[1] - 48
        unicodeMinorVersion = (value[2] - 48)*10 + value[3] - 48
        unicodeReleaseVersion = (value[4] - 48)*10 + value[5] - 48
        i++
        if (decode[i].getTag()[0] != EFCOMConstants.TAG_LIST_TAG) {
            return log(FILE_UNABLE_TO_READ, "Return Tag list ${decode[i].getTag()}")
        }
        tagList = decode[i].getValue()
        log("Decoded: ")
        log("LDS: $LDS_Version, $LDS_UpdateLevel")
        log("Unicode: $unicodeMajorVersion, $unicodeMinorVersion, $unicodeReleaseVersion")
        tagList?.let { log("Tag list: ", it) }
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