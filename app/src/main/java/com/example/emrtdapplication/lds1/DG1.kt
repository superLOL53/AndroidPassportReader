package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.DG1Constants.TD1_SIZE
import com.example.emrtdapplication.constants.DG1Constants.TD2_SIZE
import com.example.emrtdapplication.constants.DG1Constants.TD3_SIZE
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.DG1_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG1_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.MRZ
import com.example.emrtdapplication.constants.TlvTags.MULTIPLE_BYTES_TAG
import com.example.emrtdapplication.utils.TLV

/**
 * Implements the DG1 file
 *
 * @property rawFileContent The file content represented as a byte array
 * @property shortEFIdentifier The short EF id
 * @property efTag The tag of the EF
 * @property documentCode The code/type of the document
 * @property issuerCode The issuing State or organization
 * @property documentNumber The document number
 * @property checkDigitDocumentNumber The check digit of the [documentNumber]
 * @property optionalDataDocumentNumber Optional data or the least significant characters of the [documentNumber] if it exceeds 9 characters
 * @property dateOfBirth The date of birth of the eMRTD holder
 * @property checkDigitDateOfBirth The check digit of the [dateOfBirth]
 * @property sex The sex of the eMRTD holder
 * @property dateOfExpiry The expiration date of the eMRTD
 * @property checkDigitDateOfExpiry The check digit of the expiration date
 * @property nationality The nationality of the eMRTD holder
 * @property optionalData Optional data
 * @property compositeCheckDigit Check digit in the MRZ
 * @property holderName The name of the eMRTD holder
 */
class DG1(): ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier = DG1_SHORT_EF_ID
    override val efTag = DG1_FILE_TAG
    var documentCode : String? = null
        private set
    var issuerCode : String? = null
        private set
    var documentNumber : String? = null
        private set
    var checkDigitDocumentNumber = 0.toChar()
        private set
    var optionalDataDocumentNumber : String? = null
        private set
    var dateOfBirth : String? = null
        private set
    var checkDigitDateOfBirth = 0.toChar()
        private set
    var sex : Char = 0.toChar()
        private set
    var dateOfExpiry : String? = null
        private set
    var checkDigitDateOfExpiry = 0.toChar()
        private set
    var nationality : String? = null
        private set
    var optionalData : String? = null
        private set
    var compositeCheckDigit = 0.toChar()
        private set
    var holderName : String? = null
        private set
    var checkDigit = 0.toChar()
        private set

    /**
     * Parses the contents of [rawFileContent]
     *
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse() : Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        var tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != DG1_FILE_TAG) {
            return FAILURE
        }
        if (tlv.list == null || tlv.list!!.tlvSequence.size != 1) {
            return FAILURE
        }
        tlv = tlv.list!!.tlvSequence[0]
        if (tlv.tag.size != 2 || tlv.tag[0] != MULTIPLE_BYTES_TAG || tlv.tag[1] != MRZ || tlv.value == null) {
            return FAILURE
        }
        val mrz = tlv.value!!
        return when(mrz.size) {
            TD1_SIZE -> decodeTD1MRZ(mrz)
            TD2_SIZE -> decodeTD2MRZ(mrz)
            TD3_SIZE -> decodeTD3MRZ(mrz)
            else -> FAILURE
        }
    }

    /**
     * Decodes the MRZ for TD1 size eMRTDs
     *
     * @param mrz The MRZ of the eMRTD
     * @return [SUCCESS]
     */
    private fun decodeTD1MRZ(mrz : ByteArray) : Int {
        documentCode = mrz.slice(0..1).toByteArray().decodeToString().replace("<", "")
        issuerCode = mrz.slice(2..4).toByteArray().decodeToString().replace("<", "")
        documentNumber = mrz.slice(5..13).toByteArray().decodeToString().replace("<", "")
        checkDigitDocumentNumber = mrz[14].toInt().toChar()
        optionalDataDocumentNumber = mrz.slice(15..29).toByteArray().decodeToString().replace("<", "")
        dateOfBirth = mrz.slice(30..35).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfBirth = mrz[36].toInt().toChar()
        sex = mrz[37].toInt().toChar()
        dateOfExpiry = mrz.slice(38..43).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfExpiry = mrz[44].toInt().toChar()
        nationality = mrz.slice(45..47).toByteArray().decodeToString().replace("<", "")
        optionalData = mrz.slice(48..58).toByteArray().decodeToString().replace("<", "")
        compositeCheckDigit = mrz[59].toInt().toChar()
        holderName = mrz.slice(60..89).toByteArray().decodeToString().replace("<", " ")
        return SUCCESS
    }

    /**
     * Decodes the MRZ for TD2 size eMRTDs
     *
     * @param mrz The MRZ of the eMRTD
     * @return [SUCCESS]
     */
    private fun decodeTD2MRZ(mrz : ByteArray) : Int {
        documentCode = mrz.slice(0..1).toByteArray().decodeToString().replace("<", "")
        issuerCode = mrz.slice(2..4).toByteArray().decodeToString().replace("<", "")
        holderName = mrz.slice(5..35).toByteArray().decodeToString().replace("<", " ")
        documentNumber = mrz.slice(36..44).toByteArray().decodeToString().replace("<", "")
        checkDigitDocumentNumber = mrz[45].toInt().toChar()
        nationality = mrz.slice(46..48).toByteArray().decodeToString().replace("<", "")
        dateOfBirth = mrz.slice(49..54).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfBirth = mrz[55].toInt().toChar()
        sex = mrz[56].toInt().toChar()
        dateOfExpiry = mrz.slice(57..62).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfExpiry = mrz[63].toInt().toChar()
        optionalData = mrz.slice(64..70).toByteArray().decodeToString().replace("<", "")
        compositeCheckDigit = mrz[71].toInt().toChar()
        return SUCCESS
    }

    /**
     * Decodes the MRZ for TD3 size eMRTDs
     *
     * @param mrz The MRZ of the eMRTD
     * @return [SUCCESS]
     */
    private fun decodeTD3MRZ(mrz : ByteArray) : Int {
        documentCode = mrz.slice(0..1).toByteArray().decodeToString().replace("<", "")
        issuerCode = mrz.slice(2..4).toByteArray().decodeToString().replace("<", "")
        holderName = mrz.slice(5..43).toByteArray().decodeToString().replace("<", " ")
        documentNumber = mrz.slice(44..52).toByteArray().decodeToString().replace("<", "")
        checkDigitDocumentNumber = mrz[53].toInt().toChar()
        nationality = mrz.slice(54..56).toByteArray().decodeToString().replace("<", "")
        dateOfBirth = mrz.slice(57..62).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfBirth = mrz[63].toInt().toChar()
        sex = mrz[64].toInt().toChar()
        dateOfExpiry = mrz.slice(65..70).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfExpiry = mrz[71].toInt().toChar()
        optionalData = mrz.slice(72..85).toByteArray().decodeToString().replace("<", "")
        checkDigit = mrz[86].toInt().toChar()
        compositeCheckDigit = mrz[87].toInt().toChar()
        return SUCCESS
    }
}