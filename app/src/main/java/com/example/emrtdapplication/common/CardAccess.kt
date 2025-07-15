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
import com.example.emrtdapplication.utils.TLVSequence

/**
 * Constants for CardAccess class
 */
const val CA_TAG = "ca"
const val CA_ENABLE_LOGGING = true
const val CA_ID_1: Byte = 0x01
const val CA_ID_2: Byte = 0x1C

/**
 * Class for reading, parsing and storing the information of EF.CardAccess from the EMRTD
 */
class CardAccess(private var apduControl: APDUControl) {
    //Variables containing the information from EF.CardAccess
    private var paceInfo = PACEInfo()
    private var paceDomainParams = PACEDomainParameterInfo()

    /**
     * Reading the EF.CardAccess file from the EMRTD.
     * @return The return value indicating success(0), unable to select(-1) or unable to read from file(-2)
     */
    fun read() : Int {
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(CA_ID_1, CA_ID_2)))
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
        return parse(apduControl.removeRespondCodes(info))
    }

    /**
     * Parsing the contents of the EF.CardAccess file read from the EMRTD
     * @param b: The contents of the EF.CardAccess file
     * @return The return value indicating Success(0) or unable to read from file(-2)
     */
    private fun parse(b : ByteArray) : Int {
        val tlv = TLVSequence(b)
        if (tlv.getTLVSequence().isEmpty() || tlv.getTLVSequence()[0].getTLVSequence() == null) {
            return FILE_UNABLE_TO_READ
        }
        paceInfo.setInfo(tlv.getTLVSequence()[0].getTLVSequence()!!.getTLVSequence()[0])
        if (tlv.getTLVSequence().size >= 2) {
            paceDomainParams.setDomainParameter(tlv.getTLVSequence()[1])
        }
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Returns the PACE information stored on EF.CardAccess
     * @return The PACE information
     */
    fun getPACEInfo() : PACEInfo {
        return paceInfo
    }
}