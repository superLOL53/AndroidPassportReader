package com.example.emrtdapplication.common

import com.example.emrtdapplication.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.CHIP_AUTHENTICATION_TYPE
import com.example.emrtdapplication.EF_DIR_TYPE
import com.example.emrtdapplication.PACE_DOMAIN_PARAMETER_INFO_TYPE
import com.example.emrtdapplication.PACE_INFO_TYPE
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.TERMINAL_AUTHENTICATION_TYPE
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLVSequence

/**
 * Class for reading, parsing and storing information from the EF.CardSecurity file
 */
class CardSecurity(private var apduControl: APDUControl) {
    private val CS_ID_1: Byte = 0x01
    private val CS_ID_2: Byte = 0x1D
    val securityInfos = ArrayList<SecurityInfo>()

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

    private fun parseData(byteArray: ByteArray) : Int {
        val tlv = TLVSequence(byteArray)
        for (sequence in tlv.getTLVSequence()) {
            val si = SecurityInfo(sequence.toByteArray())
            when (si.type) {
                CHIP_AUTHENTICATION_TYPE -> securityInfos.add(ChipAuthenticationInfo(sequence.toByteArray()))
                CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE -> securityInfos.add(ChipAuthenticationPublicKeyInfo(sequence.toByteArray()))
                TERMINAL_AUTHENTICATION_TYPE -> securityInfos.add(TerminalAuthenticationInfo(sequence.toByteArray()))
                EF_DIR_TYPE -> securityInfos.add(EFDIRInfo(sequence.toByteArray()))
                PACE_INFO_TYPE -> securityInfos.add(PACEInfo(sequence.toByteArray()))
                PACE_DOMAIN_PARAMETER_INFO_TYPE -> securityInfos.add(PACEDomainParameterInfo(sequence.toByteArray()))
            }
        }
        return SUCCESS
    }
}