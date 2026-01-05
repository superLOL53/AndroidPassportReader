package com.example.emrtdapplication.lds1

import android.content.Context
import android.widget.LinearLayout
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
 * Implements the EF.COM file and inherits from [ElementaryFileTemplate]
 *
 * @property apduControl Used for sending and receiving APDUs
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
class EfCom(apduControl: APDUControl): ElementaryFileTemplate(apduControl) {
    private var ldsVersion = 0
    private var ldsUpdateLevel = 0
    private var unicodeMajorVersion = 0
    private var unicodeMinorVersion = 0
    private var unicodeReleaseVersion = 0
    private var tagList : ByteArray? = null

    override val efTag: Byte = 0x60
    override val shortEFIdentifier: Byte = 0x1E
    override var rawFileContent: ByteArray? = null

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
                VERSION_TAG -> {
                    if (tag.tag.size != 2) {
                        return FILE_UNABLE_TO_READ
                    }
                    when (tag.tag[1]) {
                        LDS_VERSION_TAG -> {
                            if (tag.length != LDS_VERSION_LENGTH) {
                                return FILE_UNABLE_TO_READ
                            }
                            ldsVersion = computeVersion(value?.get(0) ?: 0, value?.get(1) ?: 0)
                            ldsUpdateLevel = computeVersion(value?.get(2) ?: 0, value?.get(3) ?: 0)
                        }
                        UNICODE_VERSION_TAG -> {
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
                TAG_LIST_TAG -> tagList = tag.value
                else -> return FILE_UNABLE_TO_READ
            }
        }
        return FILE_SUCCESSFUL_READ
    }

    /**
     * Dynamically create a view for every biometric information in this file.
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        // TODO: Implement
    }

    private fun computeVersion(b1: Byte, b2: Byte): Int {
        return (b1-48)*10+(b2-48)
    }
}