package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED

class DG15(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x0F
    override val EFTag: Byte = 0x6F

    override fun parse(): Int {
        return NOT_IMPLEMENTED
    }
}