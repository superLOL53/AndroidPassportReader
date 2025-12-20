package com.example.emrtdapplication.common

import com.example.emrtdapplication.PACE_DOMAIN_PARAMETER_INFO_TYPE
import com.example.emrtdapplication.PACE_INFO_TYPE
import com.example.emrtdapplication.SecurityInfo
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

/**
 * Implements the EF.CardAccess file. This file is required if PACE is implemented.
 * The file contains ASN1 Sequences of [PACEInfo] and/or [PACEDomainParameterInfo]
 *
 * @property apduControl Used for sending and receiving APDUs
 * @property caID1 First byte of the file id
 * @property caID2 Second byte of the file id, which is also the short EF id
 * @property paceInfos Array list of [PACEInfo] contained in the file
 * @property paceDomainParams Array list of [PACEDomainParameterInfo] contained in the file
 */
class CardAccess(private var apduControl: APDUControl) {
    private val caID1: Byte = 0x01
    private val caID2: Byte = 0x1C
    val paceInfos = ArrayList<PACEInfo>()
    val paceDomainParams = ArrayList<PACEDomainParameterInfo>()

    /**
     * Reading the EF.CardAccess file from the EMRTD.
     * @return The return value indicating success(0), unable to select(-1) or unable to read from file(-2)
     */
    fun read() : Int {
        var info = apduControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(caID1, caID2)))
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
        val tlv = TLV(b)
        if (tlv.list == null || !tlv.isConstruct()) {
            return FILE_UNABLE_TO_READ
        }
        for (sequence in tlv.list!!.tlvSequence) {
            val si = SecurityInfo(sequence)
            if (si.type != PACE_INFO_TYPE && si.type != PACE_DOMAIN_PARAMETER_INFO_TYPE) {
                throw IllegalArgumentException()
            } else if (si.type == PACE_INFO_TYPE) {
                paceInfos.add(PACEInfo(sequence))
            } else {
                paceDomainParams.add(PACEDomainParameterInfo((sequence)))
            }
        }
        return FILE_SUCCESSFUL_READ
    }
}