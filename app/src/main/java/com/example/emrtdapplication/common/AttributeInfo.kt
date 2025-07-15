package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FILE_SUCCESSFUL_READ
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
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
const val AI_ID_1: Byte = 0x2F
const val AI_ID_2: Byte = 0x01
const val AI_MIN_LENGTH = 12
const val AI_EXTENDED_LENGTH_INFORMATION_START = 5
const val AI_EXTENDED_LENGTH_INFORMATION_NUMBER_OF_TAGS = 2
const val INTEGER_MAX_BYTEARRAY_SIZE = 4

/**
 * Class for reading, parsing and storing information from EF.ATR/INFO file from the EMRTD
 */
class AttributeInfo(private var apduControl: APDUControl) {
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
        //log("Select Attribute Info")
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(AI_ID_1, AI_ID_2)))
        //log("Select Attribute info answer: ", info)
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        //log("Attribute info answer: ", info)
        //log("Read Attribute Info")
        info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 256
        ))
        //log("Attribute info answer: ", info)
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        if (parse(apduControl.removeRespondCodes(info)) != FILE_SUCCESSFUL_READ) {
            reset()
            return FILE_UNABLE_TO_READ
        } else {
            return FILE_SUCCESSFUL_READ
        }
    }

    fun getSupportFullDFName() : Boolean {
        return supportFullDFNameSelection
    }

    fun getSupportShortEFName() : Boolean {
        return supportShortEFNameSelection
    }

    fun getSupportRecordNumber() : Boolean {
        return supportRecordNumber
    }

    fun getSupportCommandChaining() : Boolean {
        return supportCommandChaining
    }

    fun getSupportExtendedLength() : Boolean {
        return supportExtendedLength
    }

    fun getExtendedLengthInfoInFile() : Boolean {
        return extendedLengthInfoInFile
    }

    fun getMaxTransferLength() : Int {
        return maxAPDUTransferBytes
    }

    fun getMaxReceiveLength() : Int {
        return maxAPDUReceiveBytes
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
    private fun parse(contents : ByteArray) : Int {
        //log( "Parsing...")
        if (contents.size < AI_MIN_LENGTH) {
            return FILE_UNABLE_TO_READ
        }
        val decode = TLVSequence(contents)
        for (tlv in decode.getTLVSequence()) {
            when (tlv.getTag()[0]) {
                CARD_CAPABILITY_TAG -> {
                    val cardCapabilities = tlv.getValue()
                    if (cardCapabilities != null && cardCapabilities.size == 3) {
                        parseByte1(cardCapabilities[0])
                        parseByte2(cardCapabilities[1])
                        parseByte3(cardCapabilities[2])
                    }
                }
                EXTENDED_LENGTH_TAG_1 -> {
                    if (tlv.getTag().size == 2 && tlv.getTag()[1] == EXTENDED_LENGTH_TAG_2) {
                        val lengthInfo = tlv.getTLVSequence()
                        if (lengthInfo != null && lengthInfo.getTLVSequence().size == 2) {
                            parseExtendedLengthInfo(lengthInfo.getTLVSequence())
                        }
                    }
                }
            }
        }
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Parses the first byte in the card capability information
     * @param byte: The first byte of the card capability
     */
    private fun parseByte1(byte: Byte) {
        if (byte and SUPPORT_DF_FULL_NAME_SELECTION == SUPPORT_DF_FULL_NAME_SELECTION) {
            //log("Full DF Name Selection supported")
            supportFullDFNameSelection = true
        }
        if (byte and SUPPORT_SHORT_EF_ID == SUPPORT_SHORT_EF_ID) {
            //log("Short EF Name Selection supported")
            supportShortEFNameSelection = true
        }
        if (byte and SUPPORT_RECORD_NUMBER == SUPPORT_RECORD_NUMBER) {
            //log("Record Number supported")
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
            //log("Command Chaining supported")
            supportCommandChaining = true
        }
        if (byte and SUPPORT_EXTENDED_LENGTHS == SUPPORT_EXTENDED_LENGTHS) {
            //log("Extended Length supported")
            supportExtendedLength = true
        }
        if (byte and EXTENDED_LENGTH_INFO_IN_ATRINFO == EXTENDED_LENGTH_INFO_IN_ATRINFO) {
            //log("Extended Length info in file")
            extendedLengthInfoInFile = true
        }
    }

    private fun parseExtendedLengthInfo(lengthInfo: ArrayList<TLV>) {
        if (lengthInfo[0].getValue() != null) {
            maxAPDUTransferBytes = byteArrayToInt(lengthInfo[0].getValue()!!)
        }
        if (lengthInfo[1].getValue() != null) {
            maxAPDUReceiveBytes = byteArrayToInt(lengthInfo[1].getValue()!!)
        }
    }

    /**
     * Function to convert a byte array into an integer
     * @param b: The byte array to convert
     * @return The integer value of the byte array
     */
    private fun byteArrayToInt(b : ByteArray) : Int {
        var i = 0
        for (byte : Byte in b) {
            i = i*256 + byte.toUByte().toInt()
        }
        return i
    }
}