package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte

/**
 * Constants for the CardSecurity class
 */
const val CS_TAG = "cs"
const val CS_ENABLE_LOGGING = true
const val CS_ID_1: Byte = 0x01
const val CS_ID_2: Byte = 0x1D

/**
 * Class for reading, parsing and storing information from the EF.CardSecurity file
 */
class CardSecurity(private var apduControl: APDUControl) {

    /**
     * Reading the EF.CardSecurity file from the EMRTD.
     * @return The return value indicating Success(0), unable to select the file(-1) or unable to read from the file(-2)
     */
    fun read() : Int {
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(CS_ID_1, CS_ID_2)))
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 0xff
        ))
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        return parseData(info)
    }

    //TODO: Implement
    private fun parseData(byteArray: ByteArray) : Int {
        return NOT_IMPLEMENTED
    }
}