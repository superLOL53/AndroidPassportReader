package com.example.emrtdapplication

/**
 * Constants for the CardSecurity class
 */
const val CS_TAG = "cs"
const val CS_ENABLE_LOGGING = true

/**
 * Class for reading, parsing and storing information from the EF.CardSecurity file
 */
class CardSecurity {

    /**
     * Reading the EF.CardSecurity file from the EMRTD.
     * @return The return value indicating Success(0), unable to select the file(-1) or unable to read from the file(-2)
     */
    fun read() : Int {
        log("Read Card Security Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x01, 0x1d)))
        log("Card Security info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_SELECT, "Could not select Card Security info file. Error Code: ", info)
        }
        log("Read Card Security info file. Contents are: ", info)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0xff.toByte(), ZERO_SHORT))
        log("Attribute info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_READ, "Could not read Card Security info file. Error Code: ", info)
        }
        return log(parseData(), "Read Card Security info file. Contents are: ", info)
    }

    //TODO: Implement
    private fun parseData() : Int {
        return NOT_IMPLEMENTED
    }

    /**
     * Logs messages in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(CS_TAG, CS_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg: String, b: ByteArray) {
        Logger.log(CS_TAG, CS_ENABLE_LOGGING, msg, b)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b : ByteArray) : Int {
        return Logger.log(CS_TAG, CS_ENABLE_LOGGING, error, msg, b)
    }
}