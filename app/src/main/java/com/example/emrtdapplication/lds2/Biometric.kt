package com.example.emrtdapplication.lds2

import android.util.Log
import com.example.emrtdapplication.ANDROID_LOG_INFO_TAG
import com.example.emrtdapplication.constants.TlvTags.AUTHENTICITY_TOKEN
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC_1
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC_2
import com.example.emrtdapplication.constants.TlvTags.BIOMETRIC_DATA
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_REFERENCE
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.x509.Certificate

const val BIOMETRIC_RECORD_SIZE = 3
const val UNABLE_TO_VERIFY_SIGNATURE = "Unable to verify signature for biometric data!"
const val INVALID_CERTIFICATE_REFERENCE = "Empty or invalid certificate reference!"
const val EMPTY_SIGNATURE = "Empty signature for Additional Biometrics file!"
const val EMPTY_BIOMETRIC_DATA = "Empty Biometric Data!"
const val ILLEGAL_TAG = "Illegal tag for Biometric Data Template!"
const val BIOMETRIC_TAG_SIZE = 2

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
 * @property certificateReference A reference record number for a
 * certificate in the certificate store
 * @throws IllegalArgumentException If any property is missing or
 * invalid or if a tag is invalid
 */
class Biometric(record: TLV, val fileID: Int) {
    val biometricData: ByteArray
    val signature: ByteArray
    val certificateReference: Byte
    var isVerified = false
        private set

    init {
        var biometricData: ByteArray? = null
        var signature: ByteArray? = null
        var certificateReference: ByteArray? = null
        if (record.tag.size != BIOMETRIC_TAG_SIZE || record.tag[0] != BIOMETRIC_1 ||
            record.tag[1] != BIOMETRIC_2) {
            throw IllegalArgumentException(ILLEGAL_TAG)
        }
        if (record.list == null || record.list!!.tlvSequence.size != BIOMETRIC_RECORD_SIZE) {
            throw IllegalArgumentException(ILLEGAL_TAG)
        }
        for (tlv in record.list!!.tlvSequence) {
            if (tlv.tag.size != BIOMETRIC_TAG_SIZE || tlv.tag[0] != BIOMETRIC) {
                throw IllegalArgumentException(ILLEGAL_TAG)
            }
            when (tlv.tag[1]) {
                BIOMETRIC_DATA -> biometricData = tlv.value
                AUTHENTICITY_TOKEN -> signature = tlv.value
                CERTIFICATE_REFERENCE -> certificateReference = tlv.value
            }
        }
        if (biometricData == null) {
            throw IllegalArgumentException(EMPTY_BIOMETRIC_DATA)
        }
        this.biometricData = biometricData
        if (signature == null) {
            throw IllegalArgumentException(EMPTY_SIGNATURE)
        }
        this.signature = signature
        if (certificateReference == null || certificateReference.size != 1) {
            throw IllegalArgumentException(INVALID_CERTIFICATE_REFERENCE)
        }
        this.certificateReference = certificateReference[0]
    }

    fun verify(certificate: Certificate) {
        try {
            val publicKey = Crypto.generatePublicKey(certificate)
            isVerified = Crypto.verifySignature(
                certificate.signatureAlgorithm.algorithm.id,
                publicKey!!, biometricData, signature
            )
            return
        } catch (_: Exception) {
            Log.i(ANDROID_LOG_INFO_TAG, UNABLE_TO_VERIFY_SIGNATURE)
        }
        isVerified = false
    }
}