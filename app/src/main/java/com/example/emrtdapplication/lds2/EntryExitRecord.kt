package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.utils.TLVSequence

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
class EntryExitRecord(val record: TLVSequence) {
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

    init {
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
        if (record.tlvSequence.size != 4) {
            throw IllegalArgumentException("Record sequence must be of size 4!")
        }
        for (tlv in record.tlvSequence) {
            if (tlv.tag.size == 1) {
                if (tlv.tag[0] != 0x73.toByte() || tlv.list == null) {
                    throw IllegalArgumentException("Invalid tag for signed info in an Entry/Exit Record!")
                }
                for (t in tlv.list!!.tlvSequence) {
                    if (t.tag.size != 2 || t.tag[0] != 0x5F.toByte() || t.value == null) {
                        continue
                    }
                    when (t.tag[1]) {
                        0x44.toByte() -> state2 = t.value.toString()
                        0x4C.toByte() -> visaStatus = t.value.toString().replace('<', ' ')
                        0x45.toByte() -> date = t.value.toString()
                        0x4B.toByte() -> inspectionAuthority = t.value.toString().replace('<', ' ')
                        0x46.toByte() -> inspectionLocation = t.value.toString().replace('<', ' ')
                        0x4A.toByte() -> inspectorReference = t.value.toString().replace('<', ' ')
                        0x4D.toByte() -> inspectionResult = t.value.toString().replace('<', ' ')
                        0x49.toByte() -> travelMode = when (t.value!![0]) {
                                                            'A'.code.toByte() -> "Air"
                                                            'S'.code.toByte() -> "Sea"
                                                            'L'.code.toByte() -> "Land"
                                                            else -> "Unknown"
                                                        }
                        0x48.toByte() -> stayDuration = if (t.value == null) {
                                                            null
                                                        } else {
                                                            var tmp = 0
                                                            for (b in tlv.value!!) tmp = tmp*256 + b
                                                            tmp
                                                        }
                        0x4E.toByte() -> conditions = t.value.toString().replace('<', ' ')
                    }
                }
            } else if (tlv.tag.size == 2) {
                if (tlv.tag[0] != 0x5F.toByte() || (tlv.tag[1] != 0x44.toByte() &&
                            tlv.tag[1] != 0x37.toByte() && tlv.tag[1] != 0x38.toByte())) {
                    throw IllegalArgumentException("Invalid tag in an Entry/Exit Record!")
                }
                when (tlv.tag[1]) {
                    0x44.toByte() -> state1 = tlv.value?.toString()
                    0x37.toByte() -> signature = tlv.value
                    0x38.toByte() -> certificateReference = tlv.value
                }
            }
        }
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
}