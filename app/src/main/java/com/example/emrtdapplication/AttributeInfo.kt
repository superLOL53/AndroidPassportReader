package com.example.emrtdapplication

import kotlin.experimental.and

class AttributeInfo : EMRTDFile {

    private var supportFullDFNameSelection = false
    private var supportShortEFNameSelection = false
    private var supportRecordNumber = false
    private var supportCommandChaining = false
    private var supportExtendedLength = false
    private var extendedLengthInfoInFile = false
    private var maxAPDUTransferBytes = 0
    private var maxAPDUReceiveBytes = 0

    @OptIn(ExperimentalStdlibApi::class)
    override fun read() : Int {
        reset()
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Select Attribute Info")
        var info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, true, 0x02, ZERO_SHORT, byteArrayOf(0x2F, 0x01)))
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Select Attribute info answer: " + info.toHexString())
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_UNABLE_TO_SELECT, "Could not select Attribute info file. Error Code: " + info.toHexString())
        }
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Attribute info answer: " + info.toHexString())
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Read Attribute Info")
        info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, true, 0x26, ZERO_SHORT))
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Attribute info answer: " + info.toHexString())
        if (!(info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK)) {
            return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_UNABLE_TO_READ, "Could not read Attribute info file. Error Code: " + info.toHexString())
        }
        return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, parse(info.slice(0..info.size-3).toByteArray()), "Read Attribute info file. Contents are: " + info.toHexString())
    }

    override fun getData() : Int {
        return -1
    }

    fun getMaxAPDUTransferBytes() : Int {
        return maxAPDUTransferBytes
    }

    fun getMaxAPDUReceiveBytes() : Int {
        return maxAPDUReceiveBytes
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

    fun getExtendedInfoInFile() : Boolean {
        return extendedLengthInfoInFile
    }

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

    private fun parse(contents : ByteArray) : Int {
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Parsing...")
        val decode = TLVCoder().decode(contents)
        var code = FILE_SUCCESSFUL_READ
        var i = 0
        while (i < decode.size) {
            var tlv = decode[i]
            when (tlv.getTag()[0]) {
                AttributeInfoConstants.CARD_CAPABILITY_TAG -> code = parseCardCapabilities(tlv)
                AttributeInfoConstants.EXTENDED_LENGTH_TAG_1 -> {
                    when (tlv.getTag()[1]) {
                        AttributeInfoConstants.EXTENDED_LENGTH_TAG_2 -> {
                            val length = tlv.getLength().toInt()
                            tlv = decode[i+1]
                            var curLength = tlv.getLength().toInt() + tlv.getTag().size + 1
                            maxAPDUTransferBytes = byteArrayToInt(tlv.getValue())
                            tlv = decode[i+2]
                            curLength += tlv.getLength().toInt() + tlv.getTag().size + 1
                            maxAPDUReceiveBytes = byteArrayToInt(tlv.getValue())
                            if (length != curLength || maxAPDUReceiveBytes < 1000 || maxAPDUTransferBytes < 1000) {
                                return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_UNABLE_TO_READ, "Extended Length Information: Length mismatch")
                            }
                            i += 2
                        }
                        else -> Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_SUCCESSFUL_READ, "Unknown Tag", tlv.getTag())
                    }
                }
                else -> Logger.log(AttributeInfoConstants.TAG,AttributeInfoConstants.ENABLE_LOGGING,FILE_SUCCESSFUL_READ,"Unknown Tag",tlv.getTag())
            }
            if (code != FILE_SUCCESSFUL_READ) {
                return code
            }
            i++
        }
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Parsing successful")
        return code
    }

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

    private fun parseByte1(byte: Byte) {
        if (byte and AttributeInfoConstants.SUPPORT_DF_FULL_NAME_SELECTION == AttributeInfoConstants.SUPPORT_DF_FULL_NAME_SELECTION) {
            Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Full DF Name Selection supported")
            supportFullDFNameSelection = true
        }
        if (byte and AttributeInfoConstants.SUPPORT_SHORT_EF_ID == AttributeInfoConstants.SUPPORT_SHORT_EF_ID) {
            Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Short EF Name Selection supported")
            supportShortEFNameSelection = true
        }
        if (byte and AttributeInfoConstants.SUPPORT_RECORD_NUMBER == AttributeInfoConstants.SUPPORT_RECORD_NUMBER) {
            Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Record Number supported")
            supportRecordNumber = true
        }
    }

    private fun parseByte2(byte: Byte) : Int {
        return if (byte and AttributeInfoConstants.MASK_UNIT_SIZE == AttributeInfoConstants.UNIT_SIZE) {
            FILE_SUCCESSFUL_READ
        } else {
            FILE_UNABLE_TO_READ
        }
    }

    private fun parseByte3(byte: Byte) {
        if (byte and AttributeInfoConstants.SUPPORT_COMMAND_CHAINING == AttributeInfoConstants.SUPPORT_COMMAND_CHAINING) {
            Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Command Chaining supported")
            supportCommandChaining = true
        }
        if (byte and AttributeInfoConstants.SUPPORT_EXTENDED_LENGTHS == AttributeInfoConstants.SUPPORT_EXTENDED_LENGTHS) {
            Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Extended Length supported")
            supportExtendedLength = true
        }
        if (byte and AttributeInfoConstants.EXTENDED_LENGTH_INFO_IN_ATRINFO == AttributeInfoConstants.EXTENDED_LENGTH_INFO_IN_ATRINFO) {
            Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Extended Length info in file")
            extendedLengthInfoInFile = true
        }
    }

    private fun byteArrayToInt(b : ByteArray?) : Int {
        if (b != null) {
            Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Byte Array to convert", b)
        }
        if (b == null || b.size > 4) {
            return Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, FILE_UNABLE_TO_READ, "Invalid byte array for convertion to integer")
        }
        var i = 0
        for (byte : Byte in b) {
            i = i*256 + byte.toInt()
        }
        Logger.log(AttributeInfoConstants.TAG, AttributeInfoConstants.ENABLE_LOGGING, "Integer is $i")
        return i
    }
}