package com.example.emrtdapplication.lds2

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.constants.TlvTags.ADDITIONAL_BIOMETRICS_REFERENCE
import com.example.emrtdapplication.constants.TlvTags.ADDITIONAL_INFORMATION
import com.example.emrtdapplication.constants.TlvTags.AUTHENTICITY_TOKEN
import com.example.emrtdapplication.constants.TlvTags.BIRTHDATE
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_REFERENCE
import com.example.emrtdapplication.constants.TlvTags.DOCUMENT_NUMBER
import com.example.emrtdapplication.constants.TlvTags.DOCUMENT_TYPE
import com.example.emrtdapplication.constants.TlvTags.EXPIRATION_DATE
import com.example.emrtdapplication.constants.TlvTags.GIVEN_NAME
import com.example.emrtdapplication.constants.TlvTags.HOLDER_NAME
import com.example.emrtdapplication.constants.TlvTags.ISSUANCE_DATE
import com.example.emrtdapplication.constants.TlvTags.ISSUANCE_PLACE
import com.example.emrtdapplication.constants.TlvTags.ISSUING_AUTHORITY
import com.example.emrtdapplication.constants.TlvTags.MRZ
import com.example.emrtdapplication.constants.TlvTags.NATIONALITY
import com.example.emrtdapplication.constants.TlvTags.NUMBER_OF_ENTRIES
import com.example.emrtdapplication.constants.TlvTags.PASSPORT_NUMBER
import com.example.emrtdapplication.constants.TlvTags.SEX
import com.example.emrtdapplication.constants.TlvTags.SIGNED_INFO_VISA_RECORD
import com.example.emrtdapplication.constants.TlvTags.STAY_DURATION_VISA_RECORD
import com.example.emrtdapplication.constants.TlvTags.SURNAME
import com.example.emrtdapplication.constants.TlvTags.TERRITORY_INFORMATION
import com.example.emrtdapplication.constants.TlvTags.VISA_1
import com.example.emrtdapplication.constants.TlvTags.VISA_TYPE
import com.example.emrtdapplication.constants.TlvTags.VISA_TYPE_A
import com.example.emrtdapplication.constants.TlvTags.VISA_TYPE_B
import com.example.emrtdapplication.constants.VisaRecordConstants.VISA_RECORD_SIZE
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
                if (tlv.tag[0] != SIGNED_INFO_VISA_RECORD) {
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
                            DOCUMENT_TYPE -> documentType = t.value.toString()
                            ISSUANCE_PLACE -> issuancePlace = t.value.toString()
                            DOCUMENT_NUMBER -> documentNumber = t.value.toString()
                            HOLDER_NAME -> holderName = t.value.toString()
                        }
                    } else if (t.tag.size == 2) {
                        if (t.tag[0] != VISA_1) {
                            continue
                        }
                        when (t.tag[1]) {
                            ISSUING_AUTHORITY -> state2 = t.value.toString()
                            VISA_TYPE_A -> machineReadableVisaTypeA = t.value.toString()
                            VISA_TYPE_B -> machineReadableVisaTypeB = t.value.toString()
                            NUMBER_OF_ENTRIES -> numberOfEntries = t.value
                            STAY_DURATION_VISA_RECORD -> stayDuration = t.value
                            PASSPORT_NUMBER -> passportNumber = t.value.toString()
                            VISA_TYPE -> visaType = t.value
                            TERRITORY_INFORMATION -> territoryInformation = t.value
                            ISSUANCE_DATE -> issuanceDate = t.value.toString()
                            EXPIRATION_DATE -> expirationDate = t.value.toString()
                            ADDITIONAL_INFORMATION -> additionalInformation = t.value.toString()
                            SURNAME -> surname = t.value.toString()
                            GIVEN_NAME -> givenName = t.value.toString()
                            SEX -> sex = t.value.toString()
                            BIRTHDATE -> birthDate = t.value.toString()
                            NATIONALITY -> nationality = t.value.toString()
                            MRZ -> mrz = t.value.toString()
                            ADDITIONAL_BIOMETRICS_REFERENCE -> additionalBiometricsReference = t.value
                        }
                    }
                }
            } else if (tlv.tag.size == 2) {
                if (tlv.tag[0] != VISA_1 || (tlv.tag[1] != ISSUING_AUTHORITY &&
                        tlv.tag[1] != AUTHENTICITY_TOKEN && tlv.tag[1] != CERTIFICATE_REFERENCE)) {
                    throw IllegalArgumentException("Invalid tag in record sequence!")
                }
                when (tlv.tag[1]) {
                    ISSUING_AUTHORITY -> state1 = tlv.value?.toString()
                    AUTHENTICITY_TOKEN -> signature = tlv.value
                    CERTIFICATE_REFERENCE -> certificateReference = tlv.value
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

    fun <T : LinearLayout> createView(context: Context, parent: T) {
        //TODO: Implement
    }

    fun verify() {
        //TODO: Implement
    }
}