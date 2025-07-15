package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED

class DG1(apduControl: APDUControl): ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x01
    override val EFTag: Byte = 0x61


    override fun parse() : Int {
        return NOT_IMPLEMENTED
    }
}