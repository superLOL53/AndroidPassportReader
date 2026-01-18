package com.example.emrtdapplication.common

import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.SecurityInfoConstants.ACTIVE_AUTHENTICATION_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.EF_DIR_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_DOMAIN_PARAMETER_INFO_TYPE_SIZE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_INFO_TYPE_SIZE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.TERMINAL_AUTHENTICATION_OID
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.cms.SignedData

/**
 * Implements the EF.CardSecurity file. Required if PACE with CAM, Terminal Authentication or Chip Authentication is supported.
 *
 * @property csID1 First byte of the file id
 * @property csID2 Second byte of the file id, also the short EF id
 * @property securityInfos Array list of [SecurityInfo] contained in the file
 * @property signedData
 */
class CardSecurity() {
    private val csID1: Byte = 0x01
    private val csID2: Byte = 0x1D
    val securityInfos = ArrayList<SecurityInfo>()
    var signedData : SignedData? = null
        private set

    /**
     * Reading the EF.CardSecurity file from the eMRTD.
     * @return [FILE_UNABLE_TO_SELECT], [SUCCESS]
     */
    fun read() : Int {
        var info = APDUControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(csID1, csID2)))
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        info = APDUControl.sendAPDU(APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 0x00
        ))
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        return parseData(info)
    }

    /**
     * Parses the content of the file
     * @param byteArray The content of the file
     * @return [SUCCESS]
     */
    private fun parseData(byteArray: ByteArray) : Int {
        val tlv = TLVSequence(byteArray)
        signedData = SignedData.getInstance(tlv.tlvSequence[1].toByteArray())
        val encodedSecurityInfos = TLV(signedData!!.encapContentInfo.content.toASN1Primitive().encoded)
        for (sequence in encodedSecurityInfos.list!!.tlvSequence) {
            try {
                var securityInfo : SecurityInfo? = null
                val objectIdentifier = ASN1ObjectIdentifier.getInstance(sequence.list!!.tlvSequence[0].toByteArray()).id
                if (objectIdentifier.startsWith(PACE_OID)) {
                    objectIdentifier.split(".").size
                    if (objectIdentifier.split(".").size == PACE_DOMAIN_PARAMETER_INFO_TYPE_SIZE) {
                        securityInfo = PACEDomainParameterInfo(sequence)
                    } else if (objectIdentifier.split(".").size == PACE_INFO_TYPE_SIZE) {
                        securityInfo = PACEInfo(sequence)
                    }
                } else if (objectIdentifier.startsWith(ACTIVE_AUTHENTICATION_OID)) {
                    securityInfo = ActiveAuthenticationInfo(sequence)
                } else if (objectIdentifier.startsWith(CHIP_AUTHENTICATION_OID)) {
                    securityInfo = ChipAuthenticationInfo(sequence)
                } else if (objectIdentifier.startsWith(CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID)) {
                    securityInfo = ChipAuthenticationPublicKeyInfo(sequence)
                } else if (objectIdentifier.startsWith(TERMINAL_AUTHENTICATION_OID)) {
                    securityInfo = TerminalAuthenticationInfo(sequence)
                } else if (objectIdentifier.startsWith(EF_DIR_OID)) {
                    securityInfo = EFDIRInfo(sequence)
                }
                if (securityInfo != null) {
                    securityInfos.add(securityInfo)
                }
            } catch (_ : Exception) {
            }
        }
        return SUCCESS
    }
}