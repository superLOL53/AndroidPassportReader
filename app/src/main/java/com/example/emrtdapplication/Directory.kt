package com.example.emrtdapplication

const val DIR_TAG = "dir"
const val DIR_ENABLE_LOGGING = true

/**
 * Class representing the EF.DIR file from the EMRTD
 */
class Directory {

    /**
     * Reading the EF.DIR file from the EMRTD
     * @return The return value to indicate success(0), unable to select the file(-1) or unable to read the file(-2)
     */
    fun read() : Int {
        log("Read Directory Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x2, ZERO_SHORT, byteArrayOf(0x2f, 0x00)))
        log("Directory info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_SELECT, "Could not select Directory info file. Error Code: ", info)
        }
        log("Read Directory info file. Contents are: ", info)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0xff.toByte(), ZERO_SHORT))
        log("Directory info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_READ, "Could not read Directory info file. Error Code: ", info)
        }
        return log(parseData(), "Read Directory info file. Contents are: ", info)
    }

    /**
     * Parses the data from the EF.DIR file. Currently not implemented
     * @return Not implemented(-3)
     */
    //TODO: Implement
    private fun parseData() :Int {
        return NOT_IMPLEMENTED
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(DIR_TAG, DIR_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log(DIR_TAG, DIR_ENABLE_LOGGING, msg, b)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(DIR_TAG, DIR_ENABLE_LOGGING, error, msg, b)
    }
}