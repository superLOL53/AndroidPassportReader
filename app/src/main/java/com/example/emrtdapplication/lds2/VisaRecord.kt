package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.constants.VisaRecordConstants.ADDITIONAL_BIOMETRICS_REFERENCE_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.ADDITIONAL_INFORMATION_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.AUTHENTICITY_TOKEN_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.BIRTHDATE_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.CERTIFICATE_REFERENCE_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.DOCUMENT_NUMBER_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.DOCUMENT_TYPE_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.EXPIRATION_DATE_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.GIVEN_NAME_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.HOLDER_NAME_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.ISSUANCE_DATE_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.ISSUANCE_PLACE_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.ISSUING_AUTHORITY_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.MRZ_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.NATIONALITY_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.NUMBER_OF_ENTRIES_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.PASSPORT_NUMBER_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.SEX_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.SIGNED_INFO_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.STAY_DURATION_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.SURNAME_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.TERRITORY_INFORMATION_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.VISA_RECORD_SIZE
import com.example.emrtdapplication.constants.VisaRecordConstants.VISA_TAG_1
import com.example.emrtdapplication.constants.VisaRecordConstants.VISA_TYPE_A_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.VISA_TYPE_B_TAG
import com.example.emrtdapplication.constants.VisaRecordConstants.VISA_TYPE_TAG
import com.example.emrtdapplication.utils.TLVSequence

/**
 * Class representing a single Visa Record. The format is as follows:
 *
 *      Tag     Content
 *      '5F28'  Issuing State/Organization (3-letter code)
 *      '71'    Signed info in the Visa Record
 *      '5F37'  Signature value
 *      '5F38'  Certificate reference record number
 *
 * @property record A TLV sequence containing a Visa Record
 * @property signature The signature over the signed info in the Visa Record
 * @property certificateReference Certificate reference record number in the certificate store
 * @throws IllegalArgumentException If any of the mandatory fields are missing/invalid or if any tags are invalid
 */
class VisaRecord(val record: TLVSequence) {
    val state : String
    val documentType : String
    val machineReadableVisaTypeA : String?
    val machineReadableVisaTypeB : String?
    val numberOfEntries : Int?
    val stayDuration : ByteArray?
    val passportNumber : String?
    val visaType : ByteArray?
    val territoryInformation : ByteArray?
    val issuancePlace : String
    val issuanceDate : String
    val expirationDate : String
    val documentNumber : String
    val additionalInformation : String?
    val holderName : String
    val surname : String
    val givenName : String
    val sex : Char
    val birthDate : String
    val nationality : String
    val mrz : String
    val additionalBiometricsReference : Byte?
    val signature : ByteArray
    val certificateReference : Byte

    init {
        var state1 : String? = null
        var state2 : String? = null
        var documentType : String? = null
        var machineReadableVisaTypeA : String? = null
        var machineReadableVisaTypeB : String? = null
        var numberOfEntries : ByteArray? = null
        var stayDuration : ByteArray? = null
        var passportNumber : String? = null
        var visaType : ByteArray? = null
        var territoryInformation : ByteArray? = null
        var issuancePlace : String? = null
        var issuanceDate : String? = null
        var expirationDate : String? = null
        var documentNumber : String? = null
        var additionalInformation : String? = null
        var holderName : String? = null
        var surname : String? = null
        var givenName : String? = null
        var sex : String? = null
        var birthDate : String? = null
        var nationality : String? = null
        var mrz : String? = null
        var additionalBiometricsReference : ByteArray? = null
        var signature : ByteArray? = null
        var certificateReference : ByteArray? = null
        if (record.tlvSequence.size != VISA_RECORD_SIZE) {
            throw IllegalArgumentException("Record sequence must be of size $VISA_RECORD_SIZE!")
        }
        for (tlv in record.tlvSequence) {
            if (tlv.tag.size == 1) {
                if (tlv.tag[0] != SIGNED_INFO_TAG) {
                    throw IllegalArgumentException("Invalid tag in record sequence!")
                }
                if (tlv.list == null || tlv.list!!.tlvSequence.isEmpty()) {
                    throw IllegalArgumentException("Empty visa record!")
                }
                for (t in tlv.list!!.tlvSequence) {
                    if (t.value == null) {
                        continue
                    }
                    if (t.tag.size == 1) {
                        when (t.tag[0]) {
                            DOCUMENT_TYPE_TAG -> documentType = t.value.toString()
                            ISSUANCE_PLACE_TAG -> issuancePlace = t.value.toString()
                            DOCUMENT_NUMBER_TAG -> documentNumber = t.value.toString()
                            HOLDER_NAME_TAG -> holderName = t.value.toString()
                        }
                    } else if (t.tag.size == 2) {
                        if (t.tag[0] != VISA_TAG_1) {
                            continue
                        }
                        when (t.tag[1]) {
                            ISSUING_AUTHORITY_TAG -> state2 = t.value.toString()
                            VISA_TYPE_A_TAG -> machineReadableVisaTypeA = t.value.toString()
                            VISA_TYPE_B_TAG -> machineReadableVisaTypeB = t.value.toString()
                            NUMBER_OF_ENTRIES_TAG -> numberOfEntries = t.value
                            STAY_DURATION_TAG -> stayDuration = t.value
                            PASSPORT_NUMBER_TAG -> passportNumber = t.value.toString()
                            VISA_TYPE_TAG -> visaType = t.value
                            TERRITORY_INFORMATION_TAG -> territoryInformation = t.value
                            ISSUANCE_DATE_TAG -> issuanceDate = t.value.toString()
                            EXPIRATION_DATE_TAG -> expirationDate = t.value.toString()
                            ADDITIONAL_INFORMATION_TAG -> additionalInformation = t.value.toString()
                            SURNAME_TAG -> surname = t.value.toString()
                            GIVEN_NAME_TAG -> givenName = t.value.toString()
                            SEX_TAG -> sex = t.value.toString()
                            BIRTHDATE_TAG -> birthDate = t.value.toString()
                            NATIONALITY_TAG -> nationality = t.value.toString()
                            MRZ_TAG -> mrz = t.value.toString()
                            ADDITIONAL_BIOMETRICS_REFERENCE_TAG -> additionalBiometricsReference = t.value
                        }
                    }
                }
            } else if (tlv.tag.size == 2) {
                if (tlv.tag[0] != VISA_TAG_1 || (tlv.tag[1] != ISSUING_AUTHORITY_TAG &&
                        tlv.tag[1] != AUTHENTICITY_TOKEN_TAG && tlv.tag[1] != CERTIFICATE_REFERENCE_TAG)) {
                    throw IllegalArgumentException("Invalid tag in record sequence!")
                }
                when (tlv.tag[1]) {
                    ISSUING_AUTHORITY_TAG -> state1 = tlv.value?.toString()
                    AUTHENTICITY_TOKEN_TAG -> signature = tlv.value
                    CERTIFICATE_REFERENCE_TAG -> certificateReference = tlv.value
                }
            }
        }
        if (state1 == null || !state1.contentEquals(state2)) {
            throw IllegalArgumentException("State entries are not present or mismatch!")
        }
        this.state = state1
        if (documentType == null) {
            throw IllegalArgumentException("Unspecified document type in Visa Record!")
        }
        this.documentType = documentType
        this.machineReadableVisaTypeA = machineReadableVisaTypeA
        this.machineReadableVisaTypeB = machineReadableVisaTypeB
        this.numberOfEntries = if (numberOfEntries == null || numberOfEntries.size != 1) {
                                    null
                                } else {
                                    numberOfEntries[0].toInt()
                                }
        if (stayDuration == null) {
            throw IllegalArgumentException("Unspecified document type in Visa Record!")
        }
        this.stayDuration = stayDuration
        this.passportNumber = passportNumber
        this.visaType = visaType
        this.territoryInformation = territoryInformation
        if (issuancePlace == null) {
            throw IllegalArgumentException("Unspecified issuance place in Visa Record!")
        }
        this.issuancePlace = issuancePlace
        if (issuanceDate == null) {
            throw IllegalArgumentException("Unspecified issuance date in Visa Record!")
        }
        this.issuanceDate = issuanceDate
        if (expirationDate == null) {
            throw IllegalArgumentException("Unspecified expiration date in Visa Record!")
        }
        this.expirationDate = expirationDate
        if (documentNumber == null) {
            throw IllegalArgumentException("Unspecified document number in Visa Record!")
        }
        this.documentNumber = documentNumber
        this.additionalInformation = additionalInformation
        if (holderName == null) {
            throw IllegalArgumentException("Unspecified holder name in Visa Record!")
        }
        this.holderName = holderName
        if (surname == null) {
            throw IllegalArgumentException("Unspecified surname in Visa Record!")
        }
        this.surname = surname
        if (givenName == null) {
            throw IllegalArgumentException("Unspecified given name in Visa Record!")
        }
        this.givenName = givenName
        if (sex == null || sex.length != 1) {
            throw IllegalArgumentException("Unspecified sex in Visa Record!")
        }
        this.sex = sex[0]
        if (birthDate == null) {
            throw IllegalArgumentException("Unspecified birth date in Visa Record!")
        }
        this.birthDate = birthDate
        if (nationality == null) {
            throw IllegalArgumentException("Unspecified nationality in Visa Record!")
        }
        this.nationality = nationality
        if (mrz == null) {
            throw IllegalArgumentException("Unspecified MRZ in Visa Record!")
        }
        this.mrz = mrz
        this.additionalBiometricsReference = additionalBiometricsReference?.get(0)
        if (signature == null) {
            throw IllegalArgumentException("Unspecified document type in Visa Record!")
        }
        this.signature = signature
        if (certificateReference == null || certificateReference.size != 1) {
            throw IllegalArgumentException("Unspecified or invalid certificate reference in Visa Record!")
        }
        this.certificateReference = certificateReference[0]
    }
}