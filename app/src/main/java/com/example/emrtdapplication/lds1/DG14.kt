package com.example.emrtdapplication.lds1

import android.content.Context
import android.text.Layout
import com.example.emrtdapplication.ACTIVE_AUTHENTICATION_OID
import com.example.emrtdapplication.CHIP_AUTHENTICATION_OID
import com.example.emrtdapplication.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID
import com.example.emrtdapplication.EF_DIR_OID
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.PACE_OID
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.TERMINAL_AUTHENTICATION_OID
import com.example.emrtdapplication.common.ActiveAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.common.EFDIRInfo
import com.example.emrtdapplication.common.PACEDomainParameterInfo
import com.example.emrtdapplication.common.PACEInfo
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV
import org.bouncycastle.asn1.ASN1ObjectIdentifier

class DG14(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0E
    override val efTag: Byte = 0x6E
    var securityInfos: Array<SecurityInfo>? = null
        private set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.getTag().size != 1 || tlv.getTag()[0] != efTag ||
            tlv.getTLVSequence() == null) {
            return FAILURE
        }
        val list = ArrayList<SecurityInfo>()
        if (tlv.getTLVSequence() == null || tlv.getTLVSequence()!!.getTLVSequence().size != 1
            || tlv.getTLVSequence()!!.getTLVSequence()[0].getTLVSequence() == null) return FAILURE
        for (si in tlv.getTLVSequence()!!.getTLVSequence()[0].getTLVSequence()!!.getTLVSequence()) {
            try {
                var info : SecurityInfo? = null
                val oid = ASN1ObjectIdentifier.getInstance(si.getTLVSequence()!!.getTLVSequence()[0].toByteArray())
                if (oid.id.startsWith(PACE_OID)) {
                    if (si.getTLVSequence()!!.getTLVSequence()[0].getValue()!!.size == 9) {
                        info = PACEDomainParameterInfo(si)
                    } else if (si.getTLVSequence()!!.getTLVSequence()[0].getValue()!!.size == 10) {
                        info = PACEInfo(si)
                    }
                } else if (oid.id.startsWith(ACTIVE_AUTHENTICATION_OID)) {
                    info = ActiveAuthenticationInfo(si)
                } else if (oid.id.startsWith(CHIP_AUTHENTICATION_OID)) {
                    info = ChipAuthenticationInfo(si)
                } else if (oid.id.startsWith(CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID)) {
                    info = ChipAuthenticationPublicKeyInfo(si)
                } else if (oid.id.startsWith(TERMINAL_AUTHENTICATION_OID)) {
                    info = com.example.emrtdapplication.common.TerminalAuthenticationInfo(si)
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
        return SUCCESS
    }

    override fun createViews(context: Context, parent: Layout) {
        //TODO: Implement
    }
}