package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.Person
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV
import kotlin.experimental.and

class DG16(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x10
    override val EFTag: Byte = 0x70
    var persons: Array<Person>? = null
            private set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.getTag().size != 1 || tlv.getTag()[0] != EFTag ||
            tlv.getTLVSequence() == null) {
            return FAILURE
        }
        val list = ArrayList<Person>()
        for (tag in tlv.getTLVSequence()!!.getTLVSequence()) {
            if (tag.getTag().size == 1 && (tag.getTag()[0] and 0xF0.toByte()) == 0xA0.toByte()) {
                val p = getPerson(tag)
                if (p != null) {
                    list.add(p)
                }
            }
        }
        persons = list.toTypedArray()
        return SUCCESS
    }

    private fun getPerson(person: TLV) : Person? {
        if (person.getTLVSequence() == null || person.getTLVSequence()!!.getTLVSequence().size != 4) {
            return null
        }
        var dateRecorded: String? = null
        var name: String? = null
        var telephone: String? = null
        var address: String? = null
        for (tag in person.getTLVSequence()!!.getTLVSequence()) {
            if (tag.getTag()[0] == 0x5F.toByte()) {
                when (tag.getTag()[1].toInt()) {
                    0x50 -> dateRecorded = tag.getValue()?.decodeToString()
                    0x51 -> name = tag.getValue()?.decodeToString()
                    0x52 -> telephone = tag.getValue()?.decodeToString()
                    0x53 -> address = tag.getValue()?.decodeToString()
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