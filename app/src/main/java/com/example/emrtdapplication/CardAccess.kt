package com.example.emrtdapplication

/**
 * Constants for CardAccess class
 */
const val CA_TAG = "ca"
const val CA_ENABLE_LOGGING = true
/**
 * Class for reading, parsing and storing the information of EF.CardAccess from the EMRTD
 */
class CardAccess {
    //Variables containing the information from EF.CardAccess
    private var paceInfo = PACEInfo()
    private var paceDomainParams = PACEDomainParameterInfo()

    /**
     * Reading the EF.CardAccess file from the EMRTD.
     * @return The return value indicating success(0), unable to select(-1) or unable to read from file(-2)
     */
    fun read() : Int {
        log("Read Card Access Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x01, 0x1c)))
        log("Card Access info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_SELECT, "Could not read Card Access info file. Error Code: ", info)
        }
        log("Read Card Access info file. Contents are: ", info)
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0xff.toByte(), ZERO_SHORT))
        log("Card Access info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_READ, "Could not read Card access info file. Error Code: ", info)
        }
        return log(parse(info.slice(0..info.size-3).toByteArray()), "Read Card Access info file. Contents are: ", info)
    }

    /**
     * Parsing the contents of the EF.CardAccess file read from the EMRTD
     * @param b: The contents of the EF.CardAccess file
     * @return The return value indicating Success(0) or unable to read from file(-2)
     */
    //TODO: Refactor
    private fun parse(b : ByteArray) : Int {
        log("Parsing...")
        val tlv = TLVCoder().decode(b)
        var i = 0
        log("Reading PACE infos...")
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
        log("PACE parameters are: ")
        log("Protocol is ${paceInfo.getProtocol()}")
        log("MAC is ${paceInfo.getMAC()}")
        log("Version is ${paceInfo.getVersion()}")
        log("ParameterId is ${paceInfo.getParameterId()}")
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Returns the PACE information stored on EF.CardAccess
     * @return The PACE information
     */
    fun getPACEInfo() : PACEInfo {
        return paceInfo
    }

    /**
     * Logs messages in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg : String) {
        Logger.log(CA_TAG, CA_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b: ByteArray) {
        Logger.log(CA_TAG, CA_ENABLE_LOGGING, msg, b)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(CA_TAG, CA_ENABLE_LOGGING, error, msg, b)
    }
}