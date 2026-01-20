package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.utils.Person
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV
import kotlin.experimental.and


/**
 * Implements the DG16 file
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG16
 * @property efTag The tag of the DG16 file
 * @property persons A list of [Person] for emergency contacts
 */
class DG16() : ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x10
    override val efTag: Byte = 0x70
    var persons: Array<Person>? = null
            private set

    /**
     * Parses the contents of [rawFileContent]
     *
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null) {
            return FAILURE
        }
        val list = ArrayList<Person>()
        for (tag in tlv.list!!.tlvSequence) {
            if (tag.tag.size == 1 && (tag.tag[0] and 0xF0.toByte()) == 0xA0.toByte()) {
                val p = getPerson(tag)
                if (p != null) {
                    list.add(p)
                }
            }
        }
        persons = list.toTypedArray()
        return SUCCESS
    }

    /**
     * Decodes a TLV structure into a [Person]
     *
     * @param person A TLV structure containing [Person]
     * @return [Person] if [person] could be decoded correctly, otherwise null
     */
    private fun getPerson(person: TLV) : Person? {
        if (person.list == null || person.list!!.tlvSequence.size != 4) {
            return null
        }
        var dateRecorded: String? = null
        var name: String? = null
        var telephone: String? = null
        var address: String? = null
        for (tag in person.list!!.tlvSequence) {
            if (tag.tag[0] == 0x5F.toByte()) {
                when (tag.tag[1].toInt()) {
                    0x50 -> dateRecorded = tag.value?.decodeToString()
                    0x51 -> name = tag.value?.decodeToString()
                    0x52 -> telephone = tag.value?.decodeToString()
                    0x53 -> address = tag.value?.decodeToString()
                }
            }
        }
        return if (dateRecorded != null && name != null && telephone != null && address!= null) {
            Person(name, address, telephone, dateRecorded)
        } else {
            null
        }
    }
}