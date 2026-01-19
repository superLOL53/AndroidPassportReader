package com.example.emrtdapplication.lds2

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.constants.BiometricConstants.BIOMETRIC_RECORD_SIZE
import com.example.emrtdapplication.constants.TlvTags.AUTHENTICITY_TOKEN
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC_1
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC_2
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC_DATA
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_REFERENCE
import com.example.emrtdapplication.utils.TLV

/**
 * Class representing a biometric elementary file in the Additional Biometrics application.
 * The structure for the file is as follows:
 *
 *      Tag     Tag     Content
 *      '7F2E'          Biometric Data Template
 *              '5F2E'  Additional Biometric data
 *              '5F37'  Signature value over the Additional Biometric data including the tag and length
 *              '5F38'  Certificate reference record number
 *
 * @param record A TLV structure containing an EF.Biometrics file
 * @property biometricData Byte array containing the biometric data
 * @property signature The signature over [biometricData]
 * @property certificateReference A reference record number for a certificate in the certificate store
 * @throws IllegalArgumentException If any property is missing or invalid or if a tag is invalid
 */
class Biometric(record: TLV) {
    val biometricData : ByteArray
    val signature : ByteArray
    val certificateReference : Byte

    init {
        var biometricData : ByteArray? = null
        var signature : ByteArray? = null
        var certificateReference : ByteArray? = null
        if (record.tag.size != 2 || record.tag[0] != BIOMETRIC_1 ||
            record.tag[1] != BIOMETRIC_2) {
            throw IllegalArgumentException("Illegal tag for Biometric Data Template!")
        }
        if (record.list == null || record.list!!.tlvSequence.size != BIOMETRIC_RECORD_SIZE) {
            throw IllegalArgumentException("Invalid sequence for Biometric Data Template!")
        }
        for (tlv in record.list!!.tlvSequence) {
            if (tlv.tag.size != 2 || tlv.tag[0] != BIOMETRIC) {
                throw IllegalArgumentException("Illegal tag for Biometric Data Template!")
            }
            when (tlv.tag[1]) {
                BIOMETRIC_DATA -> biometricData = tlv.toByteArray()
                AUTHENTICITY_TOKEN -> signature = tlv.value
                CERTIFICATE_REFERENCE -> certificateReference = tlv.value
            }
        }
        if (biometricData == null) {
            throw IllegalArgumentException("Empty Biometric Data!")
        }
        this.biometricData = biometricData
        if (signature == null) {
            throw IllegalArgumentException("Empty signature for Additional Biometrics file!")
        }
        this.signature = signature
        if (certificateReference == null || certificateReference.size != 1) {
            throw IllegalArgumentException("Empty or invalid certificate reference!")
        }
        this.certificateReference = certificateReference[0]
    }

    fun<T : LinearLayout> createView(context: Context, parent: T) {
        //TODO: Implement
    }
}