package com.example.emrtdapplication.common

import com.example.emrtdapplication.constants.DirectoryConstants.ADDITIONAL_BIOMETRICS_APPLICATION_ID
import com.example.emrtdapplication.constants.DirectoryConstants.AID
import com.example.emrtdapplication.constants.DirectoryConstants.AID_LENGTH
import com.example.emrtdapplication.constants.DirectoryConstants.APPLICATION_TEMPLATE_TAG
import com.example.emrtdapplication.constants.DirectoryConstants.DIR_ID_1
import com.example.emrtdapplication.constants.DirectoryConstants.DIR_ID_2
import com.example.emrtdapplication.constants.DirectoryConstants.INTERNATIONAL_AID_TAG
import com.example.emrtdapplication.constants.DirectoryConstants.LDS1_ID_1
import com.example.emrtdapplication.constants.DirectoryConstants.LDS1_ID_2
import com.example.emrtdapplication.constants.DirectoryConstants.LDS2_ID
import com.example.emrtdapplication.constants.DirectoryConstants.TEMPLATE_LENGTH
import com.example.emrtdapplication.constants.DirectoryConstants.TRAVEL_RECORDS_APPLICATION_ID
import com.example.emrtdapplication.constants.DirectoryConstants.VISA_RECORDS_APPLICATION_ID
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FILE_SUCCESSFUL_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV

/**
 * Implements the EF.DIR file. Required if LDS2 applications are supported
 *
 * @property APDUControl Used for sending and receiving APDUs
 * @property hasTravelRecordsApplication Indicates if the eMRTD supports the Travel Records application
 * @property hasVisaRecordsApplication Indicates if the eMRTD supports the Visa Record application
 * @property hasAdditionalBiometricsApplication Indicates if the eMRTD supports the Additional Biometric application
 */
class Directory() {
    private var hasTravelRecordsApplication = false
    private var hasVisaRecordsApplication = false
    private var hasAdditionalBiometricsApplication = false

    constructor(byteArray: ByteArray) : this() {
        parseData(byteArray)
    }

    /**
     * Reading the EF.DIR file from the eMRTD
     * @return [FILE_UNABLE_TO_SELECT], [FILE_UNABLE_TO_READ] or [SUCCESS]
     */
    fun read() : Int {
        var info = APDUControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(DIR_ID_1, DIR_ID_2)))
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        info = APDUControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 0
        ))
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        return parseData(APDUControl.removeRespondCodes(info))
    }

    /**
     * Parses the data from the EF.DIR file. Currently not implemented
     * @return [FILE_SUCCESSFUL_READ] if the file was parsed correctly, otherwise [FILE_UNABLE_TO_READ]
     */
    private fun parseData(byteArray: ByteArray) :Int {
        var tlv: TLV
        var l = 0
        do {
            tlv = TLV(byteArray.slice(l.. byteArray.size).toByteArray())
            if (parseTLV(tlv) != SUCCESS) {
                return FILE_UNABLE_TO_READ
            }
            l += tlv.toByteArray().size
        } while (l < byteArray.size)
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Parses the content of the file
     * @param tlv The TLV structure containing the file content
     * @return [FILE_UNABLE_TO_READ] if the content cannot be parsed correctly, otherwise [SUCCESS]
     */
    @OptIn(ExperimentalStdlibApi::class)
    private fun parseTLV(tlv: TLV) : Int {
        if (!tlv.isValid || tlv.tag.size != 1 || tlv.tag[0] != APPLICATION_TEMPLATE_TAG || tlv.length != TEMPLATE_LENGTH) {
            return FILE_UNABLE_TO_READ
        }
        val innerTLV = tlv.value?.let { TLV(it) }
        if (innerTLV == null || !innerTLV.isValid || innerTLV.tag.size != 1 || tlv.tag[0] != INTERNATIONAL_AID_TAG ||
            innerTLV.length != AID_LENGTH || innerTLV.value!!.toHexString().startsWith(AID)) {
            return FILE_UNABLE_TO_READ
        }
        when (innerTLV.value?.get(5)) {
            LDS1_ID_1 -> {
                if (innerTLV.value!![6] != LDS1_ID_2) {
                    return FILE_UNABLE_TO_READ
                }
            }
            LDS2_ID -> {
                when (innerTLV.value!![6]) {
                    TRAVEL_RECORDS_APPLICATION_ID -> hasTravelRecordsApplication = true
                    VISA_RECORDS_APPLICATION_ID -> hasVisaRecordsApplication = true
                    ADDITIONAL_BIOMETRICS_APPLICATION_ID -> hasAdditionalBiometricsApplication = true
                }
            }
        }
        return SUCCESS
    }
}