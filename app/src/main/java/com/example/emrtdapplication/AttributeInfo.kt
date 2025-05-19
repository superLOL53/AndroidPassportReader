package com.example.emrtdapplication

import kotlin.experimental.and

/**
 * Constants for the class AttributeInfo
 */
const val AI_TAG = "ai"
const val AI_ENABLE_LOGGING = true
const val CARD_CAPABILITY_TAG : Byte = 0x47
const val SUPPORT_RECORD_NUMBER : Byte = 0x02
const val SUPPORT_SHORT_EF_ID : Byte = 0x04
const val SUPPORT_DF_FULL_NAME_SELECTION : Byte = 0x80.toByte()
const val UNIT_SIZE : Byte = 0x01
const val MASK_UNIT_SIZE : Byte = 0xF
const val SUPPORT_COMMAND_CHAINING : Byte = 0x80.toByte()
const val SUPPORT_EXTENDED_LENGTHS : Byte = 0x40
const val EXTENDED_LENGTH_INFO_IN_ATRINFO : Byte = 0x20
const val EXTENDED_LENGTH_TAG_1 : Byte = 0x7F
const val EXTENDED_LENGTH_TAG_2 : Byte = 0x66

/**
 * Class for reading, parsing and storing information from EF.ATR/INFO file from the EMRTD
 */
class AttributeInfo {
    //Variables for storing the information
    private var supportFullDFNameSelection = false
    private var supportShortEFNameSelection = false
    private var supportRecordNumber = false
    private var supportCommandChaining = false
    private var supportExtendedLength = false
    private var extendedLengthInfoInFile = false
    private var maxAPDUTransferBytes = 0
    private var maxAPDUReceiveBytes = 0

    /**
     * Reading the EF.ATR/INFO file from the EMRTD
     * @return The return value to indicate success(0), unable to select the file(-1) or unable to read the file(-2)
     */
    fun read() : Int {
        reset()
        log("Select Attribute Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x2F, 0x01)))
        log("Select Attribute info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_SELECT, "Could not select Attribute info file. Error Code: ", info)
        }
        log("Attribute info answer: ", info)
        log("Read Attribute Info")
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x26, ZERO_SHORT))
        log("Attribute info answer: ", info)
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return log(FILE_UNABLE_TO_READ, "Could not read Attribute info file. Error Code: ", info)
        }
        if (parse(info.slice(0..info.size-3).toByteArray()) != FILE_SUCCESSFUL_READ) {
            reset()
            return log(FILE_UNABLE_TO_READ, "Unable to read Attribute info file. Contents are: ", info)
        } else {
            return log(FILE_SUCCESSFUL_READ, "Successfully read Attribute info file. Contents are: ", info)
        }
    }

    /**
     * Deletes all information stored in the class
     */
    private fun reset() {
        maxAPDUReceiveBytes = 0
        maxAPDUTransferBytes = 0
        supportExtendedLength = false
        supportCommandChaining = false
        supportRecordNumber =  false
        supportShortEFNameSelection = false
        supportFullDFNameSelection = false
        extendedLengthInfoInFile = false
    }

    /**
     * Parsing the contents of the EF.ATR/INFO file. The file is structured as a TLV structure. The information
     * contained in the EF.ATR/INFO file is stored in the variables of this class
     * @param contents: The contents of the EF.ATR/INFO file without the respond code of the APDU
     * @return Success(0) or Failure(-1) on parsing the data
     */
    //TODO: Refactor
    private fun parse(contents : ByteArray) : Int {
        log( "Parsing...")
        val decode = TLVCoder().decode(contents)
        var code = FILE_SUCCESSFUL_READ
        var i = 0
        while (i < decode.size) {
            var tlv = decode[i]
            when (tlv.getTag()[0]) {
                CARD_CAPABILITY_TAG -> code = parseCardCapabilities(tlv)
                EXTENDED_LENGTH_TAG_1 -> {
                    when (tlv.getTag()[1]) {
                        EXTENDED_LENGTH_TAG_2 -> {
                            val length = tlv.getLength().toInt()
                            tlv = decode[i+1]
                            var curLength = tlv.getLength().toInt() + tlv.getTag().size + 1
                            maxAPDUTransferBytes = byteArrayToInt(tlv.getValue())
                            tlv = decode[i+2]
                            curLength += tlv.getLength().toInt() + tlv.getTag().size + 1
                            maxAPDUReceiveBytes = byteArrayToInt(tlv.getValue())
                            if (length != curLength || maxAPDUReceiveBytes < 1000 || maxAPDUTransferBytes < 1000) {
                                return log(FILE_UNABLE_TO_READ, "Extended Length Information: Length mismatch")
                            }
                            i += 2
                        }
                        else -> return log(FILE_UNABLE_TO_READ, "Unknown Tag", tlv.getTag())
                    }
                }
                else -> return log(FILE_UNABLE_TO_READ,"Unknown Tag",tlv.getTag())
            }
            if (code != FILE_SUCCESSFUL_READ) {
                return code
            }
            i++
        }
        log("Parsing successful")
        return code
    }

    /**
     * Parses the card capability information
     * @param tlv: The TLV containing the card capability information
     * @return Success(0) or unable to read(-2)
     */
    private fun parseCardCapabilities(tlv: TLV) : Int {
        if (tlv.getLength() != 0x3.toByte() || tlv.getValue() == null || tlv.getValue()!!.size != 3) {
            return FILE_UNABLE_TO_READ
        }
        parseByte1(tlv.getValue()!![0])
        val code = parseByte2(tlv.getValue()!![1])
        if (code != FILE_SUCCESSFUL_READ) {
            return code
        }
        parseByte3(tlv.getValue()!![2])
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Parses the first byte in the card capability information
     * @param byte: The first byte of the card capability
     */
    private fun parseByte1(byte: Byte) {
        if (byte and SUPPORT_DF_FULL_NAME_SELECTION == SUPPORT_DF_FULL_NAME_SELECTION) {
            log("Full DF Name Selection supported")
            supportFullDFNameSelection = true
        }
        if (byte and SUPPORT_SHORT_EF_ID == SUPPORT_SHORT_EF_ID) {
            log("Short EF Name Selection supported")
            supportShortEFNameSelection = true
        }
        if (byte and SUPPORT_RECORD_NUMBER == SUPPORT_RECORD_NUMBER) {
            log("Record Number supported")
            supportRecordNumber = true
        }
    }

    /**
     * Parses the second byte in the card capability information
     * @param byte: The second byte of the card capability
     * @return Success (0) or unable to read(-2) based on the validity of the information
     */
    private fun parseByte2(byte: Byte) : Int {
        return if (byte and MASK_UNIT_SIZE == UNIT_SIZE) {
            FILE_SUCCESSFUL_READ
        } else {
            FILE_UNABLE_TO_READ
        }
    }

    /**
     * Parses the third byte in the card capability information
     * @param byte: The third byte of the card capability
     */
    private fun parseByte3(byte: Byte) {
        if (byte and SUPPORT_COMMAND_CHAINING == SUPPORT_COMMAND_CHAINING) {
            log("Command Chaining supported")
            supportCommandChaining = true
        }
        if (byte and SUPPORT_EXTENDED_LENGTHS == SUPPORT_EXTENDED_LENGTHS) {
            log("Extended Length supported")
            supportExtendedLength = true
        }
        if (byte and EXTENDED_LENGTH_INFO_IN_ATRINFO == EXTENDED_LENGTH_INFO_IN_ATRINFO) {
            log("Extended Length info in file")
            extendedLengthInfoInFile = true
        }
    }

    /**
     * Function to convert a byte array into an integer
     * @param b: The byte array to convert
     * @return The integer value of the byte array
     */
    private fun byteArrayToInt(b : ByteArray?) : Int {
        if (b != null) {
            log("Byte Array to convert", b)
        }
        if (b == null || b.size > 4) {
            return log(FILE_UNABLE_TO_READ, "Invalid byte array for conversion to integer")
        }
        var i = 0
        for (byte : Byte in b) {
            i = i*256 + byte.toInt()
        }
        log("Integer is $i")
        return i
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     */
    private fun log(msg: String) {
        Logger.log(AI_TAG, AI_ENABLE_LOGGING, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @return The error code
     */
    private fun log(error : Int, msg : String) : Int {
        return Logger.log(AI_TAG, AI_ENABLE_LOGGING, error, msg)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     */
    private fun log(msg : String, b : ByteArray) {
        return Logger.log(AI_TAG, AI_ENABLE_LOGGING, msg, b)
    }

    /**
     * Logs message in the android logcat
     * @param msg: The message to be printed in the log
     * @param error: The error code to be printed and propagated
     * @param b: The byte array to be printed in the log as hexadecimal bytes
     * @return The error code
     */
    private fun log(error : Int, msg : String, b: ByteArray) : Int {
        return Logger.log(AI_TAG, AI_ENABLE_LOGGING, error, msg, b)
    }
}