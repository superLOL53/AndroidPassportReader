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
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

const val DIR_ID_1: Byte = 0x2F
const val DIR_ID_2: Byte = 0x00
const val APPLICATION_TEMPLATE_TAG: Byte = 0x61
const val TEMPLATE_LENGTH = 9
const val INTERNATIONAL_AID_TAG:Byte = 0x4F
const val AID_LENGTH = 7
const val AID = "A000000247"
const val LDS1_ID_1: Byte = 0x10
const val LDS1_ID_2: Byte = 0x01
const val LDS2_ID: Byte = 0x20
const val TRAVEL_RECORDS_APPLICATION_ID: Byte = 0x01
const val VISA_RECORDS_APPLICATION_ID: Byte = 0x02
const val ADDITIONAL_BIOMETRICS_APPLICATION_ID: Byte = 0x03
/**
 * Implements the EF.DIR file. Required if LDS2 applications are supported
 *
 * @property apduControl Used for sending and receiving APDUs
 * @property hasTravelRecordsApplication Indicates if the eMRTD supports the Travel Records application
 * @property hasVisaRecordsApplication Indicates if the eMRTD supports the Visa Record application
 * @property hasAdditionalBiometricsApplication Indicates if the eMRTD supports the Additional Biometric application
 */
class Directory() {
    private var apduControl: APDUControl? = null
    private var hasTravelRecordsApplication = false
    private var hasVisaRecordsApplication = false
    private var hasAdditionalBiometricsApplication = false

    constructor(apduControl: APDUControl) : this() {
        this.apduControl = apduControl
    }

    constructor(byteArray: ByteArray) : this() {
        parseData(byteArray)
    }

    /**
     * Reading the EF.DIR file from the EMRTD
     * @return The return value to indicate success(0), unable to select the file(-1) or unable to read the file(-2)
     */
    fun read() : Int {
        var info = apduControl!!.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(DIR_ID_1, DIR_ID_2)))
        if (!apduControl!!.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        info = apduControl!!.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 0
        ))
        if (!apduControl!!.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        return parseData(apduControl!!.removeRespondCodes(info))
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
        if (innerTLV == null || !innerTLV.isValid || innerTLV.tag.size != 1 || tlv.tag[0] != INTERNATIONAL_AID_TAG || innerTLV.length != AID_LENGTH || innerTLV.value!!.toHexString().startsWith(
                AID)) {
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