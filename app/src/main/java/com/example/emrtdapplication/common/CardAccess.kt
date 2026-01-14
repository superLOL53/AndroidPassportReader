package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FILE_SUCCESSFUL_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_DOMAIN_PARAMETER_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_INFO_TYPE
import com.example.emrtdapplication.utils.TLV

/**
 * Implements the EF.CardAccess file. This file is required if PACE is implemented.
 * The file contains ASN1 Sequences of [PACEInfo] and/or [PACEDomainParameterInfo]
 *
 * @property caID1 First byte of the file id
 * @property caID2 Second byte of the file id, which is also the short EF id
 * @property paceInfos Array list of [PACEInfo] contained in the file
 * @property paceDomainParams Array list of [PACEDomainParameterInfo] contained in the file
 * @throws IllegalArgumentException If the file does not contain [PACEInfo] or [PACEDomainParameterInfo]
 */
class CardAccess() {
    private val caID1: Byte = 0x01
    private val caID2: Byte = 0x1C
    val paceInfos = ArrayList<PACEInfo>()
    val paceDomainParams = ArrayList<PACEDomainParameterInfo>()

    /**
     * Reading the EF.CardAccess file from the eMRTD.
     * @return [FILE_UNABLE_TO_SELECT], [FILE_UNABLE_TO_READ] or [FILE_SUCCESSFUL_READ]
     */
    fun read() : Int {
        var info = APDUControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(caID1, caID2)))
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        info = APDUControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 256
        ))
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        return parse(APDUControl.removeRespondCodes(info))
    }

    /**
     * Parsing the contents of the EF.CardAccess file read from the eMRTD
     * @param b: The contents of the EF.CardAccess file
     * @return [FILE_UNABLE_TO_READ] or [FILE_SUCCESSFUL_READ]
     * @throws IllegalArgumentException If [b] does not contain [PACEInfo] or [PACEDomainParameterInfo]
     */
    fun parse(b : ByteArray) : Int {
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