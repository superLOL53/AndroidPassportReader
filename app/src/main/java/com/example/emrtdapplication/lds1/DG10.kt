package com.example.emrtdapplication.lds1

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
class DG10() : ElementaryFilesTypeTemplate<TLV>() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x0A
    override val efTag: Byte = 0x6A

    override fun add(tlv: TLV, list: ArrayList<TLV>) {
        list.add(tlv)
    }

    override fun toTypedArray(list: ArrayList<TLV>) {
        tlvS = list.toTypedArray()
    }
}