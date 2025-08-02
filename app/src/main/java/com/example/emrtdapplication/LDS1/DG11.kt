package com.example.emrtdapplication.LDS1

import android.media.Image
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

class DG11(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0B
    override val EFTag: Byte = 0x6B
    var fullName : String? = null
        private set
    var personalNumber : String? = null
        private set
    var fullDateOfBirth : String? = null
        private set
    var placeOfBirth : String? = null
        private set
    var permanentAddress : String? = null
        private set
    var telephone : String? = null
        private set
    var profession : String? = null
        private set
    var title : String? = null
        private set
    var personalSummary : String? = null
        private set
    var custodyInformation : String? = null
        private set
    var otherTDNumbers : Array<String>? = null
        private set
    var image : Image? = null
        private set
    var otherNames : Array<String>? = null
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
            if (tag.getTag().size == 2) {
                if (tag.getTag()[0] == 0x5F.toByte()) {
                    when (tag.getTag()[1].toInt()) {
                        0x0E -> fullName = tag.getValue()?.decodeToString()
                        0x10 -> personalNumber = tag.getValue()?.decodeToString()
                        0x2B -> fullDateOfBirth = tag.getValue()?.decodeToString()
                        0x11 -> placeOfBirth = tag.getValue()?.decodeToString()?.replace('<', ' ')
                        0x42 -> permanentAddress = tag.getValue()?.decodeToString()?.replace('<', ' ')
                        0x12 -> telephone = tag.getValue()?.decodeToString()
                        0x13 -> profession = tag.getValue()?.decodeToString()
                        0x14 -> title = tag.getValue()?.decodeToString()
                        0x15 -> personalSummary = tag.getValue()?.decodeToString()
                        0x16 -> decodeImage(tag)
                        0x17 -> decodeDocumentNumbers(tag)
                        0x18 -> custodyInformation = tag.getValue()?.decodeToString()
                    }
                }
            } else if (tag.getTag().size ==1) {
                if (tag.getTag()[0] == 0xA0.toByte()) {
                    readNames(tag)
                }
            }
        }
        return SUCCESS
    }

    private fun readNames(names : TLV) {
        if (names.getTLVSequence() == null || names.getTLVSequence()!!.getTLVSequence().size < 2) {
            return
        }
        val list = ArrayList<String>()
        val tlv = names.getTLVSequence()!!.getTLVSequence()
        for (i in 1..<tlv.size) {
            if (tlv[i].getValue() != null) {
                list.add(tlv[i].getValue()!!.decodeToString().replace('<', ' '))
            }
        }
        otherNames = list.toTypedArray()
    }

    private fun decodeImage(image : TLV) {
        //TODO: Implement
    }

    private fun decodeDocumentNumbers(numbers: TLV) {
        if (numbers.getValue() == null || numbers.getValue()!!.isEmpty()) {
            return
        }
        otherTDNumbers = numbers.getValue()!!.decodeToString().split('<').toTypedArray()
    }
}