package com.example.emrtdapplication

/**
 * Constants for EfCom
 */
const val EF_COM_TAG = "ef.com"
const val EF_COM_ENABLE_LOGGING = true
const val TAG60 : Byte = 0x60
const val LDS_VERSION_TAG_1 : Byte = 0x5F
const val LDS_VERSION_TAG_2 : Byte = 0x01
const val UNICODE_VERSION_TAG_1 : Byte = 0x5F
const val UNICODE_VERSION_TAG_2 : Byte = 0x36
const val TAG_LIST_TAG : Byte = 0x5C

/**
 * Class for reading, parsing and storing information of EF.COM File
 */
class EfCom {
    //Variables for storing the information from EF.COM
    private var ldsVersion = 0
    private var ldsUpdateLevel = 0
    private var unicodeMajorVersion = 0
    private var unicodeMinorVersion = 0
    private var unicodeReleaseVersion = 0
    private var tagList : ByteArray? = null

    /**
     * Reading the EF.COM file from the EMRTD
     * @return The return value to indicate success(0), unable to select the file(-1) or unable to read the file(-2)
     */
    fun read() : Int {
        log("Reading EF.COM File...")
        //Select the EF.COM file
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x01, 0x1e)))
        if (info[info.size-2] != NfcRespondCodeSW1.OK || info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(FILE_UNABLE_TO_SELECT, "Cannot select EF.COM File. Error Code: ", info)
        }
        //Extract the length of the EF.COM file
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x02, ZERO_SHORT))
        if (info[info.size-2] != NfcRespondCodeSW1.OK || info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(FILE_UNABLE_TO_READ, "Cannot read EF.COM File. Error Code: ", info)
        }
        val le = (2 + info[1]).toByte()
        //Read the whole EF.COM file
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, le, ZERO_SHORT))
        if (info[info.size-2] != NfcRespondCodeSW1.OK || info[info.size-1] != NfcRespondCodeSW2.OK) {
            return log(FILE_UNABLE_TO_READ, "Cannot read EF.COM File. Error Code: ", info)
        }
        return parse(info.slice(0..info.size-3).toByteArray())
    }

    /**
     * Parsing the contents of the EF.COM file. The file is structured as a TLV structure. The information
     * contained in the EF.COM file is stored in the variables of this class
     * @param contents: The contents of the EF.COM file without the respond code of the APDU
     * @return Success(0) or Failure(-1) on parsing the data
     */
    //TODO: Refactor
    private fun parse(contents : ByteArray) : Int {
        log("Parsing: ", contents)
        val decode = TLVCoder().decode(contents)
        log("Decoded TLV Structure")
        var i = 0
        var tag = decode[i].getTag()
        if (tag[0] != TAG60) {
            return log(FILE_UNABLE_TO_READ, "Return TAG60")
        }
        i++
        tag = decode[i].getTag()
        if (tag.size != 2 || tag[0] != LDS_VERSION_TAG_1 || tag[1] != LDS_VERSION_TAG_2) {
            return log(FILE_UNABLE_TO_READ, "Return LDS Version $tag")
        }
        var value = decode[i].getValue()
        if (value == null || decode[i].getLength() != 4.toByte()) {
            return log(FILE_UNABLE_TO_READ, "Return LDS Length ${decode[i].getLength()}")
        }
        ldsVersion = (value[0] - 48)*10 + value[1] - 48
        ldsUpdateLevel = (value[2] - 48)*10 + value[3] - 48
        i++
        tag = decode[i].getTag()
        if (tag.size != 2 || tag[0] != UNICODE_VERSION_TAG_1 || tag[1] != UNICODE_VERSION_TAG_2) {
            return log(FILE_UNABLE_TO_READ, "Return Unicode Version $tag")
        }
        value = decode[i].getValue()
        if (value == null || decode[i].getLength() != 6.toByte()) {
            return log(FILE_UNABLE_TO_READ, "Return Unicode Length ${decode[i].getLength()}")
        }
        unicodeMajorVersion = (value[0] - 48)*10 + value[1] - 48
        unicodeMinorVersion = (value[2] - 48)*10 + value[3] - 48
        unicodeReleaseVersion = (value[4] - 48)*10 + value[5] - 48
        i++
        tag = decode[i].getTag()
        if (tag[0] != TAG_LIST_TAG) {
            return log(FILE_UNABLE_TO_READ, "Return Tag list ${decode[i].getTag()}")
        }
        tagList = decode[i].getValue()
        log("Decoded: ")
        log("LDS: $ldsVersion, $ldsUpdateLevel")
        log("Unicode: $unicodeMajorVersion, $unicodeMinorVersion, $unicodeReleaseVersion")
        tagList?.let { log("Tag list: ", it) }
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(EF_COM_TAG, EF_COM_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @return The error code
     */
    private fun log(error : Int, msg : String) : Int {
        return Logger.log(EF_COM_TAG, EF_COM_ENABLE_LOGGING, error, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log(EF_COM_TAG, EF_COM_ENABLE_LOGGING, msg, b)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(EF_COM_TAG, EF_COM_ENABLE_LOGGING, error, msg, b)
    }
}