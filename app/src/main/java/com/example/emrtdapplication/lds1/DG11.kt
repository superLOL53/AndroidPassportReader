package com.example.emrtdapplication.lds1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.BIRTHDATE
import com.example.emrtdapplication.constants.TlvTags.BIRTH_PLACE
import com.example.emrtdapplication.constants.TlvTags.CUSTODY_INFORMATION
import com.example.emrtdapplication.constants.TlvTags.DG11_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG11_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DOCUMENT_NUMBERS
import com.example.emrtdapplication.constants.TlvTags.FULL_NAME
import com.example.emrtdapplication.constants.TlvTags.IMAGE
import com.example.emrtdapplication.constants.TlvTags.MULTIPLE_BYTES_TAG
import com.example.emrtdapplication.constants.TlvTags.PERMANENT_ADDRESS
import com.example.emrtdapplication.constants.TlvTags.PERSONAL_NUMBER
import com.example.emrtdapplication.constants.TlvTags.PERSONAL_SUMMARY
import com.example.emrtdapplication.constants.TlvTags.PERSON_TAG_TEMPLATE
import com.example.emrtdapplication.constants.TlvTags.PROFESSION
import com.example.emrtdapplication.constants.TlvTags.TELEPHONE_NUMBER
import com.example.emrtdapplication.constants.TlvTags.TITLE
import com.example.emrtdapplication.utils.TLV


/**
 * Implements the DG2 file
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG2
 * @property efTag The tag of the DG2 file
 * @property fullName Full name of document holder in national characters
 * @property personalNumber Personal number of the document holder
 * @property fullDateOfBirth Date of birth of the document holder
 * @property placeOfBirth Place of birth of the document holder
 * @property permanentAddress Address of the document holder
 * @property telephone Telephone number of the document holder
 * @property profession Profession of the document holder
 * @property title Title of the document holder
 * @property personalSummary Summary of the document holder
 * @property custodyInformation Custody information of the document holder
 * @property otherTDNumbers Other valid document numbers of other valid travel documents
 * @property image Image of citizenship document
 * @property otherNames Other names of the document holder
 */
class DG11() : ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier = DG11_SHORT_EF_ID
    override val efTag = DG11_FILE_TAG
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
    var image : Bitmap? = null
        private set
    var otherNames : Array<String>? = null
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
            if (tag.tag.size == 2) {
                if (tag.tag[0] == MULTIPLE_BYTES_TAG) {
                    when (tag.tag[1]) {
                        FULL_NAME -> fullName = tag.value?.decodeToString()?.replace('<', ' ')
                        PERSONAL_NUMBER -> personalNumber = tag.value?.decodeToString()
                        BIRTHDATE -> fullDateOfBirth = tag.value?.decodeToString()
                        BIRTH_PLACE -> placeOfBirth = tag.value?.decodeToString()?.replace('<', ' ')
                        PERMANENT_ADDRESS -> permanentAddress = tag.value?.decodeToString()?.replace('<', ' ')
                        TELEPHONE_NUMBER -> telephone = tag.value?.decodeToString()
                        PROFESSION -> profession = tag.value?.decodeToString()
                        TITLE -> title = tag.value?.decodeToString()
                        PERSONAL_SUMMARY -> personalSummary = tag.value?.decodeToString()
                        IMAGE -> decodeImage(tag)
                        DOCUMENT_NUMBERS -> decodeDocumentNumbers(tag)
                        CUSTODY_INFORMATION -> custodyInformation = tag.value?.decodeToString()
                    }
                }
            } else if (tag.tag.size ==1) {
                if (tag.tag[0] == PERSON_TAG_TEMPLATE) {
                    readNames(tag)
                }
            }
        }
        isParsed = true
        return SUCCESS
    }

    /**
     * Decodes a TLV structure into names
     *
     * @param names A TLV structure containing additional names of the document holder
     */
    private fun readNames(names : TLV) {
        if (names.list == null || names.list!!.tlvSequence.size < 2) {
            return
        }
        val list = ArrayList<String>()
        val tlv = names.list!!.tlvSequence
        for (i in 1..<tlv.size) {
            if (tlv[i].value != null) {
                list.add(tlv[i].value!!.decodeToString().replace('<', ' '))
            }
        }
        otherNames = list.toTypedArray()
    }

    /**
     * Decodes an image contained in a TLV structure
     *
     * @param image A TLV structure containing an image
     */
    private fun decodeImage(image : TLV) {
        if (image.value == null) return
        this.image = BitmapFactory.decodeByteArray(image.value!!, 0, image.value!!.size)
    }

    /**
     * Decodes the TLV structure into document numbers
     *
     * @param numbers A TLV structure containing document numbers of other valid travel documents
     */
    private fun decodeDocumentNumbers(numbers: TLV) {
        if (numbers.value == null || numbers.value!!.isEmpty()) {
            return
        }
        otherTDNumbers = numbers.value!!.decodeToString().split('<').toTypedArray()
    }
}