package com.example.emrtdapplication.lds1

import android.content.Context
import android.text.Layout
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.ElementaryFilesToBeDefined
import com.example.emrtdapplication.utils.TLV

class DG8(apduControl: APDUControl) : ElementaryFilesToBeDefined<TLV>(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x08
    override val efTag: Byte = 0x68

    override fun createViews(context: Context, parent: Layout) {
        //TODO: Implement
    }

    override fun add(tlv: TLV, list: ArrayList<TLV>) {
        list.add(tlv)
    }

    override fun toTypedArray(list: ArrayList<TLV>) {
        tlvS = list.toTypedArray()
    }

}