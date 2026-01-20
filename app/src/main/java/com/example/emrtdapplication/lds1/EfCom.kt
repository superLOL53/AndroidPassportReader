package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.EFComConstants.LDS_VERSION_LENGTH
import com.example.emrtdapplication.constants.EFComConstants.UNICODE_VERSION_LENGTH
import com.example.emrtdapplication.constants.FILE_SUCCESSFUL_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.LDS_VERSION
import com.example.emrtdapplication.constants.TlvTags.TAG_LIST
import com.example.emrtdapplication.constants.TlvTags.UNICODE_VERSION
import com.example.emrtdapplication.constants.TlvTags.VERSION
import com.example.emrtdapplication.utils.TLV

/**
 * Implements the EF.COM file
 *
 * @property ldsVersion
 * @property ldsUpdateLevel
 * @property unicodeMajorVersion
 * @property unicodeMinorVersion
 * @property unicodeReleaseVersion
 * @property tagList A list of tags representing EF files present on the eMRTD
 * @property efTag The tag for the file
 * @property shortEFIdentifier The short EF identifier of the file
 * @property rawFileContent The contents of the file as a byte array
 */
class EfCom(): ElementaryFileTemplate() {
    private var ldsVersion = 0
    private var ldsUpdateLevel = 0
    private var unicodeMajorVersion = 0
    private var unicodeMinorVersion = 0
    private var unicodeReleaseVersion = 0
    private var tagList : ByteArray? = null

    override val efTag: Byte = 0x60
    override val shortEFIdentifier: Byte = 0x1E
    override var rawFileContent: ByteArray? = null

    /**
     * Parses and decodes the file content
     *
     * @return [SUCCESS] if the file could be decoded, otherwise an error code
     */
    override fun parse() : Int {
        if (rawFileContent == null) {
            return SUCCESS
        }
        val decode = TLV(rawFileContent!!)
        if (decode.tag[0] != efTag) {
            return FILE_UNABLE_TO_READ
        }
        for (tag in decode.list!!.tlvSequence) {
            if (!tag.isValid) {
                return FILE_UNABLE_TO_READ
            }
            val value = tag.value
            when (tag.tag[0]) {
                VERSION -> {
                    if (tag.tag.size != 2) {
                        return FILE_UNABLE_TO_READ
                    }
                    when (tag.tag[1]) {
                        LDS_VERSION -> {
                            if (tag.length != LDS_VERSION_LENGTH) {
                                return FILE_UNABLE_TO_READ
                            }
                            ldsVersion = computeVersion(value?.get(0) ?: 0, value?.get(1) ?: 0)
                            ldsUpdateLevel = computeVersion(value?.get(2) ?: 0, value?.get(3) ?: 0)
                        }
                        UNICODE_VERSION -> {
                            if (tag.length != UNICODE_VERSION_LENGTH) {
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
                TAG_LIST -> tagList = tag.value
                else -> return FILE_UNABLE_TO_READ
            }
        }
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Decodes the encoded version as bytes to an integer
     *
     * @param b1 First byte of the encoded version
     * @param b2 Second byte of the encoded version
     * @return The decoded version as integer
     */
    private fun computeVersion(b1: Byte, b2: Byte): Int {
        return (b1-48)*10+(b2-48)
    }
}