package com.example.emrtdapplication.LDS1

import android.media.Image
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.TLV

class DG12(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0C
    override val EFTag: Byte = 0x6C
    var issuingAuthority : String? = null
        private set
    var dateOfIssue : String? = null
        private set
    var otherPersons : Array<String>? = null
        private set
    var endorsements : String? = null
        private set
    var taxExitRequirements : String? = null
        private set
    var front : Image? = null
        private set
    var rear : Image? = null
        private set
    var documentPersonalizationTime: String? = null
        private set
    var personalizationSystemSerialNumber: String? = null
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
        for (tag in tlv.getTLVSequence()!!.getTLVSequence()) {
            if (tag.getTag().size == 1) {
                if (tag.getTag()[0] == 0xA0.toByte()) {
                    otherPersons(tag)
                }
            } else if (tag.getTag().size == 2) {
                if (tag.getTag()[0] == 0x5F.toByte()) {
                    when (tag.getTag()[1].toInt()) {
                        0x19 -> issuingAuthority = tag.getValue()?.decodeToString()
                        0x26 -> dateOfIssue = tag.getValue()?.decodeToString()
                        0x1B -> endorsements = tag.getValue()?.decodeToString()
                        0x1C -> taxExitRequirements = tag.getValue()?.decodeToString()
                        0x1D -> decodeImage(tag)
                        0x1E -> decodeImage(tag)
                        0x55 -> documentPersonalizationTime = tag.getValue()?.decodeToString()
                        0x56 -> personalizationSystemSerialNumber = tag.getValue()?.decodeToString()
                    }
                }
            }
        }
        return NOT_IMPLEMENTED
    }

    private fun otherPersons(persons: TLV) {
        if (persons.getTLVSequence() == null || persons.getTLVSequence()!!.getTLVSequence().size < 2) {
            return
        }
        val list = ArrayList<String>()
        val tlv = persons.getTLVSequence()!!.getTLVSequence()
        for (i in 1..<tlv.size) {
            if (tlv[i].getValue() != null) {
                list.add(tlv[i].getValue()!!.decodeToString().replace('<', ' '))
            }
        }
        otherPersons = list.toTypedArray()
    }

    private fun decodeImage(tlv: TLV) {
        //TODO: Implement
    }
}