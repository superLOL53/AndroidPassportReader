package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FILE_SUCCESSFUL_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
import kotlin.experimental.and

/**
 * Constants for the class AttributeInfo
 */
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

/**
 * Implements the EF.ATR/INFO EF. The file is optional if only LDS1 application is present on the ePassport
 * @property apduControl Used for sending and receiving APDUs
 * @property supportFullDFNameSelection Indicates support for full DF name selection
 * @property supportShortEFNameSelection Indicates support for short DF name selection
 * @property supportRecordNumber
 * @property supportCommandChaining Indicates support for APDU command chaining
 * @property supportExtendedLength Indicates support for extended length APDUs
 * @property extendedLengthInfoInFile Indicates that the EF.ATR/INFO contains the maximum length for command and response APDUs
 * @property maxAPDUTransferBytes The maximum bytes that can be sent with a single APDU
 * @property maxAPDUReceiveBytes The maximum bytes that can be received from a single APDU
 */
class AttributeInfo(private var apduControl: APDUControl) {
    var supportFullDFNameSelection = false
        private set
    var supportShortEFNameSelection = false
        private set
    var supportRecordNumber = false
        private set
    var supportCommandChaining = false
        private set
    var supportExtendedLength = false
        private set
    var extendedLengthInfoInFile = false
        private set
    var maxAPDUTransferBytes = 0
        private set
    var maxAPDUReceiveBytes = 0
        private set

    /**
     * Reading the EF.ATR/INFO file from the eMRTD
     * @return [FILE_UNABLE_TO_SELECT], [FILE_UNABLE_TO_READ] or [FILE_SUCCESSFUL_READ]
     */
    fun read() : Int {
        reset()
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(AI_ID_1, AI_ID_2)))
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 256
        ))
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
     * @return [FILE_UNABLE_TO_READ] or [FILE_SUCCESSFUL_READ] on parsing the data
     */
    private fun parse(contents : ByteArray) : Int {
        if (contents.size < AI_MIN_LENGTH) {
            return FILE_UNABLE_TO_READ
        }
        val decode = TLVSequence(contents)
        for (tlv in decode.tlvSequence) {
            when (tlv.tag[0]) {
                CARD_CAPABILITY_TAG -> {
                    val cardCapabilities = tlv.value
                    if (cardCapabilities != null && cardCapabilities.size == 3) {
                        parseByte1(cardCapabilities[0])
                        parseByte2(cardCapabilities[1])
                        parseByte3(cardCapabilities[2])
                    }
                }
                EXTENDED_LENGTH_TAG_1 -> {
                    if (tlv.tag.size == 2 && tlv.tag[1] == EXTENDED_LENGTH_TAG_2) {
                        val lengthInfo = tlv.list
                        if (lengthInfo != null && lengthInfo.tlvSequence.size == 2) {
                            parseExtendedLengthInfo(lengthInfo.tlvSequence)
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
            supportFullDFNameSelection = true
        }
        if (byte and SUPPORT_SHORT_EF_ID == SUPPORT_SHORT_EF_ID) {
            supportShortEFNameSelection = true
        }
        if (byte and SUPPORT_RECORD_NUMBER == SUPPORT_RECORD_NUMBER) {
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
            supportCommandChaining = true
        }
        if (byte and SUPPORT_EXTENDED_LENGTHS == SUPPORT_EXTENDED_LENGTHS) {
            supportExtendedLength = true
        }
        if (byte and EXTENDED_LENGTH_INFO_IN_ATRINFO == EXTENDED_LENGTH_INFO_IN_ATRINFO) {
            extendedLengthInfoInFile = true
        }
    }

    /**
     * Parses the extended length info in the file.
     * @param lengthInfo List of TLV structures containing the extended length info
     */
    private fun parseExtendedLengthInfo(lengthInfo: ArrayList<TLV>) {
        if (lengthInfo[0].value != null) {
            maxAPDUTransferBytes = byteArrayToInt(lengthInfo[0].value!!)
        }
        if (lengthInfo[1].value != null) {
            maxAPDUReceiveBytes = byteArrayToInt(lengthInfo[1].value!!)
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