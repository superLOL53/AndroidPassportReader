package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.ElementaryFilesToBeDefined
import com.example.emrtdapplication.utils.TLV

class DG10(apduControl: APDUControl) : ElementaryFilesToBeDefined<TLV>(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0A
    override val EFTag: Byte = 0x6A

    override fun add(tlv: TLV, list: ArrayList<TLV>) {
        list.add(tlv)
    }

    override fun toTypedArray(list: ArrayList<TLV>) {
        tlvs = list.toTypedArray()
    }
}