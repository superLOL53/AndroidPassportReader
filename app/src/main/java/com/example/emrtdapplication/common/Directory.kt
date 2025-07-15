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
 * Class representing the EF.DIR file from the EMRTD
 */
class Directory(private var apduControl: APDUControl) {
    private var hasTravelRecordsApplication = false
    private var hasVisaRecordsApplication = false
    private var hasAdditionalBiometricsApplication = false

    /**
     * Reading the EF.DIR file from the EMRTD
     * @return The return value to indicate success(0), unable to select the file(-1) or unable to read the file(-2)
     */
    fun read() : Int {
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(DIR_ID_1, DIR_ID_2)))
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 0
        ))
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        return parseData(info)
    }

    /**
     * Parses the data from the EF.DIR file. Currently not implemented
     * @return Not implemented(-3)
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

    @OptIn(ExperimentalStdlibApi::class)
    private fun parseTLV(tlv: TLV) : Int {
        if (!tlv.getIsValid() || tlv.getTag().size != 1 || tlv.getTag()[1] != APPLICATION_TEMPLATE_TAG || tlv.getLength() != TEMPLATE_LENGTH) {
            return FILE_UNABLE_TO_READ
        }
        val innerTLV = tlv.getValue()?.let { TLV(it) }
        if (!innerTLV!!.getIsValid() || tlv.getTag().size != 1 || tlv.getTag()[0] != INTERNATIONAL_AID_TAG || innerTLV.getLength() != AID_LENGTH || innerTLV.getValue()!!.toHexString().startsWith(
                AID)) {
            return FILE_UNABLE_TO_READ
        }
        when (innerTLV.getValue()?.get(5)) {
            LDS1_ID_1 -> {
                if (innerTLV.getValue()!![6] != LDS1_ID_2) {
                    return FILE_UNABLE_TO_READ
                }
            }
            LDS2_ID -> {
                when (innerTLV.getValue()!![6]) {
                    TRAVEL_RECORDS_APPLICATION_ID -> hasTravelRecordsApplication = true
                    VISA_RECORDS_APPLICATION_ID -> hasVisaRecordsApplication = true
                    ADDITIONAL_BIOMETRICS_APPLICATION_ID -> hasAdditionalBiometricsApplication = true
                }
            }
        }
        return SUCCESS
    }
}