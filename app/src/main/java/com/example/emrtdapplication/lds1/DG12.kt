package com.example.emrtdapplication.lds1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NOT_IMPLEMENTED
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV


/**
 * Implements the DG12 file and inherits from [ElementaryFileTemplate]
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG12
 * @property efTag The tag of the DG12 file
 * @property issuingAuthority The issuing State or Organization of the eMRTD
 * @property dateOfIssue The issuing date of the eMRTD
 * @property otherPersons Name of other person(s)
 * @property endorsements Endorsements, observations
 * @property taxExitRequirements Tax and/or Exit requirements
 * @property front Image of the front of the eMRTD
 * @property rear Image of the rear of the eMRTD
 * @property documentPersonalizationTime Date and time of personalization
 * @property personalizationSystemSerialNumber The serial number of the personalization system
 */
class DG12() : ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x0C
    override val efTag: Byte = 0x6C
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
    var front : Bitmap? = null
        private set
    var rear : Bitmap? = null
        private set
    var documentPersonalizationTime: String? = null
        private set
    var personalizationSystemSerialNumber: String? = null
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
        for (tag in tlv.list!!.tlvSequence) {
            if (tag.tag.size == 1) {
                if (tag.tag[0] == 0xA0.toByte()) {
                    otherPersons(tag)
                }
            } else if (tag.tag.size == 2) {
                if (tag.tag[0] == 0x5F.toByte()) {
                    when (tag.tag[1].toInt()) {
                        0x19 -> issuingAuthority = tag.value?.decodeToString()
                        0x26 -> dateOfIssue = tag.value?.decodeToString()
                        0x1B -> endorsements = tag.value?.decodeToString()
                        0x1C -> taxExitRequirements = tag.value?.decodeToString()
                        0x1D -> front = decodeImage(tag)
                        0x1E -> rear = decodeImage(tag)
                        0x55 -> documentPersonalizationTime = tag.value?.decodeToString()
                        0x56 -> personalizationSystemSerialNumber = tag.value?.decodeToString()
                    }
                }
            }
        }
        return NOT_IMPLEMENTED
    }

    /**
     * Decodes the TLV structure into a string
     *
     * @param persons A TLV structure containing the name of a person
     * @return A list of names
     */
    private fun otherPersons(persons: TLV) {
        if (persons.list == null || persons.list!!.tlvSequence.size < 2) {
            return
        }
        val list = ArrayList<String>()
        val tlv = persons.list!!.tlvSequence
        for (i in 1..<tlv.size) {
            if (tlv[i].value != null) {
                list.add(tlv[i].value!!.decodeToString().replace('<', ' '))
            }
        }
        otherPersons = list.toTypedArray()
    }

    /**
     * Decodes the image contained in the value of [tlv]
     *
     * @param tlv A TLV structure containing an image
     * @return Decoded image as a Bitmap or null
     */
    private fun decodeImage(tlv: TLV) : Bitmap? {
        if (tlv.value == null) return null
        return BitmapFactory.decodeByteArray(tlv.value!!, 0, tlv.value!!.size)
    }
}