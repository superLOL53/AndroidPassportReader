package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ACTIVE_AUTHENTICATION_OID
import com.example.emrtdapplication.CHIP_AUTHENTICATION_OID
import com.example.emrtdapplication.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID
import com.example.emrtdapplication.EF_DIR_OID
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.FAILURE
import com.example.emrtdapplication.PACE_DOMAIN_PARAMETER_INFO_OID_SIZE
import com.example.emrtdapplication.PACE_INFO_OID_SIZE
import com.example.emrtdapplication.PACE_OID
import com.example.emrtdapplication.SUCCESS
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.TERMINAL_AUTHENTICATION_OID
import com.example.emrtdapplication.common.ActiveAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.common.EFDIRInfo
import com.example.emrtdapplication.common.PACEDomainParameterInfo
import com.example.emrtdapplication.common.PACEInfo
import com.example.emrtdapplication.common.TerminalAuthenticationInfo
import com.example.emrtdapplication.constants.TlvTags.DG14_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG14_SHORT_EF_ID
import com.example.emrtdapplication.utils.TLV
import org.bouncycastle.asn1.ASN1ObjectIdentifier


/**
 * Implements the DG14 file
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG14
 * @property efTag The tag of the DG14 file
 * @property securityInfos A list of [SecurityInfo] contained in DG14
 */
class DG14: ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier = DG14_SHORT_EF_ID
    override val efTag = DG14_FILE_TAG
    var securityInfos: Array<SecurityInfo>? = null
        private set

    /**
     * Parses the contents of [rawFileContent]
     *
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        isParsed = false
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null) {
            return FAILURE
        }
        val list = ArrayList<SecurityInfo>()
        if (tlv.list == null || tlv.list!!.tlvSequence.size != 1
            || tlv.list!!.tlvSequence[0].list == null) return FAILURE
        for (si in tlv.list!!.tlvSequence[0].list!!.tlvSequence) {
            try {
                var info: SecurityInfo? = null
                val oid = ASN1ObjectIdentifier.getInstance(
                    si.list!!.tlvSequence[0].toByteArray()
                )
                if (oid.id.startsWith(PACE_OID)) {
                    if (si.list!!.tlvSequence[0].value!!.size ==
                        PACE_DOMAIN_PARAMETER_INFO_OID_SIZE
                    ) {
                        info = PACEDomainParameterInfo(si)
                    } else if (si.list!!.tlvSequence[0].value!!.size
                        == PACE_INFO_OID_SIZE
                    ) {
                        info = PACEInfo(si)
                    }
                } else if (oid.id.startsWith(ACTIVE_AUTHENTICATION_OID)) {
                    info = ActiveAuthenticationInfo(si)
                } else if (oid.id.startsWith(CHIP_AUTHENTICATION_OID)) {
                    info = ChipAuthenticationInfo(si)
                } else if (oid.id.startsWith(CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID)) {
                    info = ChipAuthenticationPublicKeyInfo(si)
                } else if (oid.id.startsWith(TERMINAL_AUTHENTICATION_OID)) {
                    info = TerminalAuthenticationInfo(si)
                } else if (oid.id.startsWith(EF_DIR_OID)) {
                    info = EFDIRInfo(si)
                }
                if (info != null) {
                    list.add(info)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
        securityInfos = list.toTypedArray()
        isParsed = !securityInfos.isNullOrEmpty()
        return SUCCESS
    }
}