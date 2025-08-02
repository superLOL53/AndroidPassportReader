package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

class DG14(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0E
    override val EFTag: Byte = 0x6E
    var securityInfos: Array<SecurityInfo>? = null
        private set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.getTag().size != 1 || tlv.getTag()[0] != EFTag ||
            tlv.getTLVSequence() == null) {
            return FAILURE
        }
        val list = ArrayList<SecurityInfo>()
        for (si in tlv.getTLVSequence()!!.getTLVSequence()) {
            try {
                list.add(SecurityInfo(si.toByteArray()))
            } catch (e: Exception) {
                println(e.message)
            }
        }
        securityInfos = list.toTypedArray()
        return SUCCESS
    }
}