package com.example.emrtdapplication.lds1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.DG12_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG12_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DOCUMENT_PERSONALIZATION_TIME
import com.example.emrtdapplication.constants.TlvTags.ENDORSEMENTS
import com.example.emrtdapplication.constants.TlvTags.FRONT_IMAGE
import com.example.emrtdapplication.constants.TlvTags.ISSUANCE_DATE_DG12
import com.example.emrtdapplication.constants.TlvTags.ISSUING_AUTHORITY_DG12
import com.example.emrtdapplication.constants.TlvTags.PERSONALIZATION_SYSTEM_SERIAL_NUMBER
import com.example.emrtdapplication.constants.TlvTags.PERSON_TAG_TEMPLATE
import com.example.emrtdapplication.constants.TlvTags.REAR_IMAGE
import com.example.emrtdapplication.constants.TlvTags.TAX_EXIT_REQUIREMENTS
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
    override val shortEFIdentifier = DG12_SHORT_EF_ID
    override val efTag = DG12_FILE_TAG
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
        isParsed = false
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
                if (tag.tag[0] == PERSON_TAG_TEMPLATE) {
                    otherPersons(tag)
                }
            } else if (tag.tag.size == 2) {
                if (tag.tag[0] == 0x5F.toByte()) {
                    when (tag.tag[1]) {
                        ISSUING_AUTHORITY_DG12 -> issuingAuthority = tag.value?.decodeToString()
                        //Date of issuance is represented as hex string?
                        ISSUANCE_DATE_DG12 -> dateOfIssue = tag.value?.decodeToString()
                        ENDORSEMENTS -> endorsements = tag.value?.decodeToString()
                        TAX_EXIT_REQUIREMENTS -> taxExitRequirements = tag.value?.decodeToString()
                        FRONT_IMAGE -> front = decodeImage(tag)
                        REAR_IMAGE -> rear = decodeImage(tag)
                        DOCUMENT_PERSONALIZATION_TIME -> documentPersonalizationTime = tag.value?.decodeToString()
                        PERSONALIZATION_SYSTEM_SERIAL_NUMBER -> personalizationSystemSerialNumber = tag.value?.decodeToString()
                    }
                }
            }
        }
        isParsed = true
        return SUCCESS
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