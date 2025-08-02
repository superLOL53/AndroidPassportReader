package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.DisplayedSignature
import com.example.emrtdapplication.utils.ElementaryFilesToBeDefined
import com.example.emrtdapplication.utils.TLV

class DG7(apduControl: APDUControl) : ElementaryFilesToBeDefined<DisplayedSignature>(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x07
    override val EFTag: Byte = 0x67

    override fun toTypedArray(list: ArrayList<DisplayedSignature>) {
        tlvs = list.toTypedArray()
    }

    override fun add(tlv: TLV, list: ArrayList<DisplayedSignature>) {
        list.add(DisplayedSignature(tlv))
    }
}