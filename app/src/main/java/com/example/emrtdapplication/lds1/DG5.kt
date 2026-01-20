package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.utils.DisplayedPortrait
import com.example.emrtdapplication.utils.ElementaryFilesTypeTemplate
import com.example.emrtdapplication.utils.TLV


/**
 * Implements the DG2 file
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG2
 * @property efTag The tag of the DG2 file
 *
 */
class DG5() : ElementaryFilesTypeTemplate<DisplayedPortrait>() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x05
    override val efTag: Byte = 0x65

    override fun add(tlv: TLV, list: ArrayList<DisplayedPortrait>) {
        list.add(DisplayedPortrait(tlv))
    }

    override fun toTypedArray(list: ArrayList<DisplayedPortrait>) {
        tlvS = list.toTypedArray()
    }
}