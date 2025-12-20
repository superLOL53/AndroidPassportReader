package com.example.emrtdapplication.lds1

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.SUCCESS

class DG13(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0D
    override val efTag: Byte = 0x6D

    override fun parse(): Int {
        return SUCCESS
    }

    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        //TODO: Implement
    }
}