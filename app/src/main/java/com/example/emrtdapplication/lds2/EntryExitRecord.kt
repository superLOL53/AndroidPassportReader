package com.example.emrtdapplication.lds2

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.constants.EntryExitRecordConstants.AUTHENTICITY_TOKEN_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.CERTIFICATE_REFERENCE_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.CONDITIONS_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.DATE_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.INSPECTION_AUTHORITY_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.INSPECTION_LOCATION_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.INSPECTION_RESULT_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.INSPECTOR_REFERENCE_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.ISSUING_STATE_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.SIGNED_INFO_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.STAY_DURATION_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.TRAVEL_MODE_TAG
import com.example.emrtdapplication.constants.EntryExitRecordConstants.TRAVEL_RECORD_SIZE
import com.example.emrtdapplication.constants.EntryExitRecordConstants.TRAVEL_TAG_1
import com.example.emrtdapplication.constants.EntryExitRecordConstants.VISA_STATUS_TAG
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
        if (record.tlvSequence.size != TRAVEL_RECORD_SIZE) {
            throw IllegalArgumentException("Record sequence must be of size $TRAVEL_RECORD_SIZE!")
        }
        for (tlv in record.tlvSequence) {
            if (tlv.tag.size == 1) {
                if (tlv.tag[0] != SIGNED_INFO_TAG || tlv.list == null) {
                    throw IllegalArgumentException("Invalid tag for signed info in an Entry/Exit Record!")
                }
                for (t in tlv.list!!.tlvSequence) {
                    if (t.tag.size != 2 || t.tag[0] != TRAVEL_TAG_1 || t.value == null) {
                        continue
                    }
                    when (t.tag[1]) {
                        ISSUING_STATE_TAG -> state2 = t.value.toString()
                        VISA_STATUS_TAG -> visaStatus = t.value.toString().replace('<', ' ')
                        DATE_TAG -> date = t.value.toString()
                        INSPECTION_AUTHORITY_TAG -> inspectionAuthority = t.value.toString().replace('<', ' ')
                        INSPECTION_LOCATION_TAG -> inspectionLocation = t.value.toString().replace('<', ' ')
                        INSPECTOR_REFERENCE_TAG -> inspectorReference = t.value.toString().replace('<', ' ')
                        INSPECTION_RESULT_TAG -> inspectionResult = t.value.toString().replace('<', ' ')
                        TRAVEL_MODE_TAG -> travelMode = when (t.value!![0]) {
                                                            'A'.code.toByte() -> "Air"
                                                            'S'.code.toByte() -> "Sea"
                                                            'L'.code.toByte() -> "Land"
                                                            else -> "Unknown"
                                                        }
                        STAY_DURATION_TAG -> stayDuration = run {
                            var tmp = 0
                            for (b in tlv.value!!) tmp = tmp*256 + b
                            tmp
                        }
                        CONDITIONS_TAG -> conditions = t.value.toString().replace('<', ' ')
                    }
                }
            } else if (tlv.tag.size == 2) {
                if (tlv.tag[0] != TRAVEL_TAG_1 || (tlv.tag[1] != ISSUING_STATE_TAG &&
                            tlv.tag[1] != AUTHENTICITY_TOKEN_TAG && tlv.tag[1] != CERTIFICATE_REFERENCE_TAG)) {
                    throw IllegalArgumentException("Invalid tag in an Entry/Exit Record!")
                }
                when (tlv.tag[1]) {
                    ISSUING_STATE_TAG -> state1 = tlv.value?.toString()
                    AUTHENTICITY_TOKEN_TAG -> signature = tlv.value
                    CERTIFICATE_REFERENCE_TAG -> certificateReference = tlv.value
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

    fun createView(context: Context, parent: LinearLayout) {
        //TODO: Implement
    }

    fun verify() {
        //TODO: Implement
    }
}