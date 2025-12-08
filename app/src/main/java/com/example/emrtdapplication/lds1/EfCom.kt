package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FILE_SUCCESSFUL_READ
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

/**
 * Constants for EfCom
 */
const val VERSION_TAG : Byte = 0x5F
const val LDS_VERSION_TAG : Byte = 0x01
const val LDS_VERSION_LENGTH = 0x04
const val UNICODE_VERSION_TAG : Byte = 0x36
const val UNICODE_VERSION_LENGTH = 0x06
const val TAG_LIST_TAG : Byte = 0x5C

/**
 * Class for reading, parsing and storing information of EF.COM File
 */
class EfCom(apduControl: APDUControl): ElementaryFileTemplate(apduControl) {
    //Variables for storing the information from EF.COM
    private var ldsVersion = 0
    private var ldsUpdateLevel = 0
    private var unicodeMajorVersion = 0
    private var unicodeMinorVersion = 0
    private var unicodeReleaseVersion = 0
    private var tagList : ByteArray? = null

    override val EFTag: Byte = 0x60
    override val shortEFIdentifier: Byte = 0x1E
    override var rawFileContent: ByteArray? = null

    override fun parse() : Int {
        if (rawFileContent == null) {
            return SUCCESS
        }
        val decode = TLV(rawFileContent!!)
        if (decode.getTag()[0] != EFTag) {
            return FILE_UNABLE_TO_READ
        }
        for (tag in decode.getTLVSequence()!!.getTLVSequence()) {
            if (!tag.getIsValid()) {
                return FILE_UNABLE_TO_READ
            }
            val value = tag.getValue()
            when (tag.getTag()[0]) {
                VERSION_TAG -> {
                    if (tag.getTag().size != 2) {
                        return FILE_UNABLE_TO_READ
                    }
                    when (tag.getTag()[1]) {
                        LDS_VERSION_TAG -> {
                            if (tag.getLength() != LDS_VERSION_LENGTH) {
                                return FILE_UNABLE_TO_READ
                            }
                            ldsVersion = computeVersion(value?.get(0) ?: 0, value?.get(1) ?: 0)
                            ldsUpdateLevel = computeVersion(value?.get(2) ?: 0, value?.get(3) ?: 0)
                        }
                        UNICODE_VERSION_TAG -> {
                            if (tag.getLength() != UNICODE_VERSION_LENGTH) {
                                return FILE_UNABLE_TO_READ
                            }
                            unicodeMajorVersion = computeVersion(value?.get(0) ?: 0,
                                value?.get(1) ?: 0
                            )
                            unicodeMinorVersion = computeVersion(value?.get(2) ?: 0,
                                value?.get(3) ?: 0
                            )
                            unicodeReleaseVersion = computeVersion(value?.get(4) ?: 0,
                                value?.get(5) ?: 0
                            )
                        }
                        else -> FILE_UNABLE_TO_READ
                    }
                }
                TAG_LIST_TAG -> tagList = tag.getValue()
                else -> return FILE_UNABLE_TO_READ
            }
        }
        return FILE_SUCCESSFUL_READ
    }

    private fun computeVersion(b1: Byte, b2: Byte): Int {
        return (b1-48)*10+(b2-48)
    }
}