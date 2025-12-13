package com.example.emrtdapplication.lds1

import android.content.Context
import android.text.Layout
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.DisplayedPortrait
import com.example.emrtdapplication.utils.ElementaryFilesToBeDefined
import com.example.emrtdapplication.utils.TLV

class DG5(apduControl: APDUControl) : ElementaryFilesToBeDefined<DisplayedPortrait>(apduControl) {

    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x05
    override val efTag: Byte = 0x65
    override fun createViews(context: Context, parent: Layout) {
        //TODO: Implement
    }

    override fun add(tlv: TLV, list: ArrayList<DisplayedPortrait>) {
        list.add(DisplayedPortrait(tlv))
    }

    override fun toTypedArray(list: ArrayList<DisplayedPortrait>) {
        tlvS = list.toTypedArray()
    }
}