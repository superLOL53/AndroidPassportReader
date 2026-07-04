package com.example.emrtdapplication.lds1

import android.util.Log
import com.example.emrtdapplication.ANDROID_LOG_INFO_TAG
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.FAILURE
import com.example.emrtdapplication.FILLER_CHARACTER
import com.example.emrtdapplication.MESSAGE_STRING
import com.example.emrtdapplication.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.DG1_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG1_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.MRZ
import com.example.emrtdapplication.constants.TlvTags.MULTIPLE_BYTES_TAG
import com.example.emrtdapplication.utils.TLV

const val TD1_SIZE = 90
const val TD2_SIZE = 72
const val TD3_SIZE = 88
const val DOCUMENT_CODE_START_INDEX = 0
const val DOCUMENT_CODE_END_INDEX = 1
const val ISSUER_CODE_START_INDEX = 2
const val ISSUER_CODE_END_INDEX = 4
const val HOLDER_NAME_START_INDEX = 5
const val HOLDER_NAME_TD3_END_INDEX = 43
const val DOCUMENT_NUMBER_TD3_START_INDEX = 44
const val DOCUMENT_NUMBER_TD3_END_INDEX = 52
const val CHECK_DIGIT_DOCUMENT_NUMBER_TD3_INDEX = 53
const val NATIONALITY_TD3_START_INDEX = 54
const val NATIONALITY_TD3_END_INDEX = 56
const val BIRTH_DATE_TD3_START_INDEX = 57
const val BIRTH_DATE_TD3_END_INDEX = 62
const val CHECK_DIGIT_BIRTH_DATE_TD3_INDEX = 63
const val SEX_TD3_INDEX = 64
const val EXPIRATION_DATE_TD3_START_INDEX = 65
const val EXPIRATION_DATE_TD3_END_INDEX = 70
const val CHECK_DIGIT_EXPIRATION_DATE_TD3_INDEX = 71
const val OPTIONAL_DATA_TD3_START_INDEX = 72
const val OPTIONAL_DATA_TD3_END_INDEX = 85
const val CHECK_DIGIT_TD3_INDEX = 86
const val COMPOSITE_CHECK_DIGIT_TD3_INDEX = 87
const val HOLDER_NAME_TD2_END_INDEX = 35
const val DOCUMENT_NUMBER_TD2_START_INDEX = 36
const val DOCUMENT_NUMBER_TD2_END_INDEX = 44
const val CHECK_DIGIT_DOCUMENT_NUMBER_TD2 = 45
const val NATIONALITY_TD2_START_INDEX = 46
const val NATIONALITY_TD2_END_INDEX = 48
const val BIRTH_DATE_TD2_START_INDEX = 49
const val BIRTH_DATE_TD2_END_INDEX = 54
const val CHECK_DIGIT_BIRTH_DATE_TD2 = 55
const val SEX_TD2_INDEX = 56
const val EXPIRATION_DATE_TD2_START_INDEX = 57
const val EXPIRATION_DATE_TD2_END_INDEX = 62
const val CHECK_DIGIT_EXPIRATION_DATE_TD2_INDEX = 63
const val OPTIONAL_DATA_TD2_START_INDEX = 64
const val OPTIONAL_DATA_TD2_END_INDEX = 70
const val COMPOSITE_CHECK_DIGIT_TD2_INDEX = 71
const val DOCUMENT_NUMBER_TD1_START_INDEX = 5
const val DOCUMENT_NUMBER_TD1_END_INDEX = 13
const val CHECK_DIGIT_DOCUMENT_NUMBER_TD1 = 14
const val OPTIONAL_DATA_DOCUMENT_NUMBER_TD1_START_INDEX = 15
const val OPTIONAL_DATA_DOCUMENT_NUMBER_TD1_END_INDEX = 29
const val BIRTH_DATE_TD1_START_INDEX = 30
const val BIRTH_DATE_TD1_END_INDEX = 35
const val CHECK_DIGIT_BIRTH_DATE_TD1 = 36
const val SEX_TD1_INDEX = 37
const val EXPIRATION_DATE_TD1_START_INDEX = 38
const val EXPIRATION_DATE_TD1_END_INDEX = 43
const val CHECK_DIGIT_EXPIRATION_DATE_TD1_INDEX = 44
const val NATIONALITY_TD1_START_INDEX = 45
const val NATIONALITY_TD1_END_INDEX = 47
const val OPTIONAL_DATA_TD1_START_INDEX = 48
const val OPTIONAL_DATA_TD1_END_INDEX = 58
const val COMPOSITE_CHECK_DIGIT_TD1_INDEX = 59
const val HOLDER_NAME_TD1_START_INDEX = 60
const val HOLDER_NAME_TD1_END_INDEX = 89

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
 * @property optionalDataDocumentNumber Optional data or the least significant
 * characters of the [documentNumber] if it exceeds 9 characters
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
class DG1: ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier = DG1_SHORT_EF_ID
    override val efTag = DG1_FILE_TAG
    var documentCode: String? = null
        private set
    var issuerCode: String? = null
        private set
    var documentNumber: String? = null
        private set
    var checkDigitDocumentNumber = 0.toChar()
        private set
    var optionalDataDocumentNumber: String? = null
        private set
    var dateOfBirth: String? = null
        private set
    var checkDigitDateOfBirth = 0.toChar()
        private set
    var sex: Char = 0.toChar()
        private set
    var dateOfExpiry: String? = null
        private set
    var checkDigitDateOfExpiry = 0.toChar()
        private set
    var nationality: String? = null
        private set
    var optionalData: String? = null
        private set
    var compositeCheckDigit = 0.toChar()
        private set
    var holderName: String? = null
        private set
    var checkDigit = 0.toChar()
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
        var tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != DG1_FILE_TAG) {
            return FAILURE
        }
        if (tlv.list == null || tlv.list!!.tlvSequence.size != 1) {
            return FAILURE
        }
        tlv = tlv.list!!.tlvSequence[0]
        if (tlv.tag.size != 2 ||
            tlv.tag[0] != MULTIPLE_BYTES_TAG ||
            tlv.tag[1] != MRZ ||
            tlv.value == null) {
                return FAILURE
        }
        val mrz = tlv.value!!
        try {
            when(mrz.size) {
                TD1_SIZE -> decodeTD1MRZ(mrz)
                TD2_SIZE -> decodeTD2MRZ(mrz)
                TD3_SIZE -> decodeTD3MRZ(mrz)
                else -> FAILURE
            }
        } catch (e: Exception) {
            Log.i(ANDROID_LOG_INFO_TAG, MESSAGE_STRING + e.message)
            return FAILURE
        }
        isParsed = true
        return SUCCESS
    }

    /**
     * Decodes the MRZ for TD1 size eMRTDs
     *
     * @param mrz The MRZ of the eMRTD
     * @return [SUCCESS]
     */
    private fun decodeTD1MRZ(mrz: ByteArray) {
        documentCode =
            mrz.slice(DOCUMENT_CODE_START_INDEX..
                    DOCUMENT_CODE_END_INDEX).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        issuerCode =
            mrz.slice(ISSUER_CODE_START_INDEX..
                    ISSUER_CODE_END_INDEX).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        documentNumber =
            mrz.slice(DOCUMENT_NUMBER_TD1_START_INDEX..
                    DOCUMENT_NUMBER_TD1_END_INDEX).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDocumentNumber =
            mrz[CHECK_DIGIT_DOCUMENT_NUMBER_TD1].toInt().toChar()
        optionalDataDocumentNumber =
            mrz.slice(
                OPTIONAL_DATA_DOCUMENT_NUMBER_TD1_START_INDEX..
                        OPTIONAL_DATA_DOCUMENT_NUMBER_TD1_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        dateOfBirth =
            mrz.slice(
                BIRTH_DATE_TD1_START_INDEX..
                    BIRTH_DATE_TD1_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDateOfBirth =
            mrz[CHECK_DIGIT_BIRTH_DATE_TD1].toInt().toChar()
        sex =
            mrz[SEX_TD1_INDEX].toInt().toChar()
        dateOfExpiry =
            mrz.slice(
                EXPIRATION_DATE_TD1_START_INDEX..
                    EXPIRATION_DATE_TD1_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDateOfExpiry =
            mrz[CHECK_DIGIT_EXPIRATION_DATE_TD1_INDEX].toInt().toChar()
        nationality =
            mrz.slice(
                NATIONALITY_TD1_START_INDEX..
                    NATIONALITY_TD1_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        optionalData =
            mrz.slice(
                OPTIONAL_DATA_TD1_START_INDEX..
                    OPTIONAL_DATA_TD1_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        compositeCheckDigit =
            mrz[COMPOSITE_CHECK_DIGIT_TD1_INDEX].toInt().toChar()
        holderName =
            mrz.slice(HOLDER_NAME_TD1_START_INDEX..HOLDER_NAME_TD1_END_INDEX).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER, ' ').trim()
    }

    /**
     * Decodes the MRZ for TD2 size eMRTDs
     *
     * @param mrz The MRZ of the eMRTD
     * @return [SUCCESS]
     */
    private fun decodeTD2MRZ(mrz: ByteArray) {
        documentCode =
            mrz.slice(
                DOCUMENT_CODE_START_INDEX..
                        DOCUMENT_CODE_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        issuerCode =
            mrz.slice(
                ISSUER_CODE_START_INDEX..
                        ISSUER_CODE_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        holderName =
            mrz.slice(
                HOLDER_NAME_START_INDEX..
                        HOLDER_NAME_TD2_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER, ' ').trim()
        documentNumber =
            mrz.slice(
                DOCUMENT_NUMBER_TD2_START_INDEX..
                        DOCUMENT_NUMBER_TD2_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDocumentNumber =
            mrz[CHECK_DIGIT_DOCUMENT_NUMBER_TD2].toInt().toChar()
        nationality =
            mrz.slice(
                NATIONALITY_TD2_START_INDEX..
                        NATIONALITY_TD2_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        dateOfBirth =
            mrz.slice(
                BIRTH_DATE_TD2_START_INDEX..
                        BIRTH_DATE_TD2_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDateOfBirth =
            mrz[CHECK_DIGIT_BIRTH_DATE_TD2].toInt().toChar()
        sex =
            mrz[SEX_TD2_INDEX].toInt().toChar()
        dateOfExpiry =
            mrz.slice(
                EXPIRATION_DATE_TD2_START_INDEX..
                        EXPIRATION_DATE_TD2_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDateOfExpiry =
            mrz[CHECK_DIGIT_EXPIRATION_DATE_TD2_INDEX].toInt().toChar()
        optionalData =
            mrz.slice(
                OPTIONAL_DATA_TD2_START_INDEX..
                        OPTIONAL_DATA_TD2_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        compositeCheckDigit =
            mrz[COMPOSITE_CHECK_DIGIT_TD2_INDEX].toInt().toChar()
    }

    /**
     * Decodes the MRZ for TD3 size eMRTDs
     *
     * @param mrz The MRZ of the eMRTD
     * @return [SUCCESS]
     */
    private fun decodeTD3MRZ(mrz: ByteArray) {
        documentCode =
            mrz.slice(
                DOCUMENT_CODE_START_INDEX..
                        DOCUMENT_CODE_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        issuerCode =
            mrz.slice(
                ISSUER_CODE_START_INDEX..
                        ISSUER_CODE_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        holderName =
            mrz.slice(
                HOLDER_NAME_START_INDEX..
                        HOLDER_NAME_TD3_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER, ' ').trim()
        documentNumber =
            mrz.slice(
                DOCUMENT_NUMBER_TD3_START_INDEX..
                        DOCUMENT_NUMBER_TD3_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDocumentNumber =
            mrz[CHECK_DIGIT_DOCUMENT_NUMBER_TD3_INDEX].toInt().toChar()
        nationality =
            mrz.slice(
                NATIONALITY_TD3_START_INDEX..
                        NATIONALITY_TD3_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        dateOfBirth =
            mrz.slice(
                BIRTH_DATE_TD3_START_INDEX..
                        BIRTH_DATE_TD3_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDateOfBirth =
            mrz[CHECK_DIGIT_BIRTH_DATE_TD3_INDEX].toInt().toChar()
        sex =
            mrz[SEX_TD3_INDEX].toInt().toChar()
        dateOfExpiry =
            mrz.slice(
                EXPIRATION_DATE_TD3_START_INDEX..
                        EXPIRATION_DATE_TD3_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigitDateOfExpiry =
            mrz[CHECK_DIGIT_EXPIRATION_DATE_TD3_INDEX].toInt().toChar()
        optionalData =
            mrz.slice(
                OPTIONAL_DATA_TD3_START_INDEX..
                        OPTIONAL_DATA_TD3_END_INDEX
            ).
            toByteArray().
            decodeToString().
            replace(FILLER_CHARACTER.toString(), "")
        checkDigit =
            mrz[CHECK_DIGIT_TD3_INDEX].toInt().toChar()
        compositeCheckDigit =
            mrz[COMPOSITE_CHECK_DIGIT_TD3_INDEX].toInt().toChar()
    }
}