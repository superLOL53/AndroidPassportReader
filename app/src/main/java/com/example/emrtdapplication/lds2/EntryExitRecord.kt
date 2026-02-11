package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.constants.EntryExitRecordConstants.TRAVEL_RECORD_SIZE
import com.example.emrtdapplication.constants.TlvTags.AUTHENTICITY_TOKEN
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_REFERENCE
import com.example.emrtdapplication.constants.TlvTags.CONDITIONS
import com.example.emrtdapplication.constants.TlvTags.DATE
import com.example.emrtdapplication.constants.TlvTags.INSPECTION_AUTHORITY
import com.example.emrtdapplication.constants.TlvTags.INSPECTION_LOCATION
import com.example.emrtdapplication.constants.TlvTags.INSPECTION_RESULT
import com.example.emrtdapplication.constants.TlvTags.INSPECTOR_REFERENCE
import com.example.emrtdapplication.constants.TlvTags.ISSUING_STATE
import com.example.emrtdapplication.constants.TlvTags.SIGNED_INFO_TRAVEL_RECORD
import com.example.emrtdapplication.constants.TlvTags.STAY_DURATION_TRAVEL_RECORD
import com.example.emrtdapplication.constants.TlvTags.TRAVEL_1
import com.example.emrtdapplication.constants.TlvTags.TRAVEL_MODE
import com.example.emrtdapplication.constants.TlvTags.VISA_STATUS
import com.example.emrtdapplication.utils.TLVSequence
import org.spongycastle.asn1.x509.Certificate
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * Class representing a Entry or Exit Record. Both have the same format:
 *
 *      Tag     Content
 *      '5F44'  Embarkation/Debarkation State
 *      '73'    Signed info in the Entry/Exit Record
 *      '5F37'  Signature value
 *      '5F38'  Certificate reference record number
 *
 * @property record A TLV sequence containing an Entry or Exit Record
 * @property state Embarkation or Debarkation State
 * @property signature The signature over the signer info in the record
 * @property certificateReference The reference record number to a certificate in the certificate store
 * @throws IllegalArgumentException If any mandatory field is missing/invalid or if a tag is invalid
 */
class EntryExitRecord(val record: TLVSequence, val recordNumber: Byte) {
    val signedInfo : ByteArray
    val state : String
    val visaStatus : String?
    val date : String
    val inspectionAuthority : String
    val inspectionLocation : String
    val inspectorReference : String
    val inspectionResult : String?
    val travelMode : String?
    val stayDuration : Int?
    val conditions : String?
    val signature : ByteArray
    val certificateReference : Byte
    var isVerified = false
        private set

    init {
        var signedInfo : ByteArray? = null
        var state1 : String? = null
        var state2 : String? = null
        var visaStatus : String? = null
        var date : String? = null
        var inspectionAuthority : String? = null
        var inspectionLocation : String? = null
        var inspectorReference : String? = null
        var inspectionResult : String? = null
        var travelMode : String? = null
        var stayDuration : Int? = null
        var conditions : String? = null
        var signature : ByteArray? = null
        var certificateReference : ByteArray? = null
        if (record.tlvSequence.size != TRAVEL_RECORD_SIZE) {
            throw IllegalArgumentException("Record sequence must be of size $TRAVEL_RECORD_SIZE!")
        }
        for (tlv in record.tlvSequence) {
            if (tlv.tag.size == 1) {
                if (tlv.tag[0] != SIGNED_INFO_TRAVEL_RECORD || tlv.list == null) {
                    throw IllegalArgumentException("Invalid tag for signed info in an Entry/Exit Record!")
                }
                signedInfo = tlv.toByteArray()
                for (t in tlv.list!!.tlvSequence) {
                    if (t.tag.size != 2 || t.tag[0] != TRAVEL_1 || t.value == null) {
                        continue
                    }
                    when (t.tag[1]) {
                        ISSUING_STATE -> state2 = t.value!!.decodeToString()
                        VISA_STATUS -> visaStatus = t.value!!.decodeToString().replace('<', ' ')
                        DATE -> date = t.value!!.decodeToString()
                        INSPECTION_AUTHORITY -> inspectionAuthority = t.value!!.decodeToString().replace('<', ' ')
                        INSPECTION_LOCATION -> inspectionLocation = t.value!!.decodeToString().replace('<', ' ')
                        INSPECTOR_REFERENCE -> inspectorReference = t.value!!.decodeToString().replace('<', ' ')
                        INSPECTION_RESULT -> inspectionResult = t.value!!.decodeToString().replace('<', ' ')
                        TRAVEL_MODE -> travelMode = when (t.value!![0]) {
                                                            'A'.code.toByte() -> "Air"
                                                            'S'.code.toByte() -> "Sea"
                                                            'L'.code.toByte() -> "Land"
                                                            else -> null
                        }
                        STAY_DURATION_TRAVEL_RECORD -> stayDuration = run {
                            var tmp = 0
                            for (b in t.value!!) {
                                tmp = tmp*256 + b.toUByte().toInt()
                            }
                            tmp
                        }
                        CONDITIONS -> conditions = t.value!!.decodeToString().replace('<', ' ')
                    }
                }
            } else if (tlv.tag.size == 2) {
                if (tlv.tag[0] != TRAVEL_1 || (tlv.tag[1] != ISSUING_STATE &&
                            tlv.tag[1] != AUTHENTICITY_TOKEN && tlv.tag[1] != CERTIFICATE_REFERENCE)) {
                    throw IllegalArgumentException("Invalid tag in an Entry/Exit Record!")
                }
                when (tlv.tag[1]) {
                    ISSUING_STATE -> state1 = tlv.value!!.decodeToString()
                    AUTHENTICITY_TOKEN -> signature = tlv.value
                    CERTIFICATE_REFERENCE -> certificateReference = tlv.value
                }
            }
        }
        if (signedInfo == null) {
            throw IllegalArgumentException("No signed information in the record!")
        }
        this.signedInfo = signedInfo
        if (state1 == null || !state1.contentEquals(state2)) {
            throw IllegalArgumentException("State entries are not present or mismatch!")
        }
        this.state = state1
        this.visaStatus = visaStatus
        if (date == null) {
            throw IllegalArgumentException("Unspecified date in Entry/Exit Record!")
        }
        this.date = date
        if (inspectionAuthority == null) {
            throw IllegalArgumentException("Unspecified inspection authority in Entry/Exit Record!")
        }
        this.inspectionAuthority = inspectionAuthority
        if (inspectionLocation == null) {
            throw IllegalArgumentException("Unspecified inspection location in Entry/Exit Record!")
        }
        this.inspectionLocation = inspectionLocation
        if (inspectorReference == null) {
            throw IllegalArgumentException("Unspecified inspector reference in Entry/Exit Record!")
        }
        this.inspectorReference = inspectorReference
        this.inspectionResult = inspectionResult
        this.travelMode = travelMode
        this.stayDuration = stayDuration
        this.conditions = conditions
        if (signature == null) {
            throw IllegalArgumentException("Unspecified signature in Entry/Exit Record!")
        }
        this.signature = signature
        if (certificateReference == null || certificateReference.isEmpty()) {
            throw IllegalArgumentException("Unspecified certificate reference in Entry/Exit Record!")
        }
        this.certificateReference = certificateReference[0]
    }

    fun verify(certificate: Certificate) {
        try {
            val spec = X509EncodedKeySpec(certificate.subjectPublicKeyInfo.encoded)
            val fac = KeyFactory.getInstance(certificate.subjectPublicKeyInfo.algorithm.algorithm.id)
            val pub = fac!!.generatePublic(spec)
            val sigAlg = Signature.getInstance(certificate.signatureAlgorithm.algorithm.id, "BC")
            sigAlg.initVerify(pub)
            sigAlg.update(signedInfo)
            isVerified = sigAlg.verify(signature)
        } catch (_ : Exception) {
        }
    }
}