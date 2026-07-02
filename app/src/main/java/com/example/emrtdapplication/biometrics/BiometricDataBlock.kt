package com.example.emrtdapplication.biometrics

import com.example.emrtdapplication.biometrics.face.FacialRecordData
import com.example.emrtdapplication.biometrics.face.FacialRecordHeader
import com.example.emrtdapplication.biometrics.fingerprint.FingerprintRecordHeader
import com.example.emrtdapplication.biometrics.iris.IrisRecordHeader
import com.example.emrtdapplication.constants.BiometricDataBlockConstants.BIOMETRIC_DATA_BLOCK_INVALID_TAG_STRING
import com.example.emrtdapplication.constants.BiometricDataBlockConstants.BIOMETRIC_DATA_BLOCK_NO_VALUE_STRING
import com.example.emrtdapplication.constants.BiometricDataBlockConstants.BIOMETRIC_DATA_BLOCK_TAG_1
import com.example.emrtdapplication.constants.BiometricDataBlockConstants.BIOMETRIC_DATA_BLOCK_TAG_2_1
import com.example.emrtdapplication.constants.BiometricDataBlockConstants.BIOMETRIC_DATA_BLOCK_TAG_2_2
import com.example.emrtdapplication.constants.BiometricDataBlockConstants.BIOMETRIC_DATA_BLOCK_TAG_SIZE
import com.example.emrtdapplication.constants.FacialRecordHeaderConstants.FACIAL_RECORD_HEADER_SIZE
import com.example.emrtdapplication.utils.TLV


/**
 * Class representing a Biometric Data Block (BDB) according to ISO/IEC19794-4.
 * @param biometricDataBlock A TLV structure containing a BDB for decoding
 * @property type The type of the biometric feature.
 * @property biometricHeader Header information about the BDB
 * @property biometricData The actual biometric information
 * @throws IllegalArgumentException If the TLV structure does not contain an encoded BDB.
 */
class BiometricDataBlock(biometricDataBlock : TLV, val type : BiometricType) {

    val biometricHeader : BiometricHeader
    val biometricData : BiometricData?

    init {
        if (biometricDataBlock.tag.size != BIOMETRIC_DATA_BLOCK_TAG_SIZE || biometricDataBlock.tag[1] != BIOMETRIC_DATA_BLOCK_TAG_1 ||
            (biometricDataBlock.tag[0] != BIOMETRIC_DATA_BLOCK_TAG_2_1 && biometricDataBlock.tag[0] != BIOMETRIC_DATA_BLOCK_TAG_2_2)) {
            throw IllegalArgumentException(BIOMETRIC_DATA_BLOCK_INVALID_TAG_STRING)
        }
        if (biometricDataBlock.value == null) {
            throw IllegalArgumentException(BIOMETRIC_DATA_BLOCK_NO_VALUE_STRING)
        }
        when (type) {
            BiometricType.FACE -> {
                biometricHeader =
                    FacialRecordHeader(biometricDataBlock.value!!.slice(0..<FACIAL_RECORD_HEADER_SIZE).toByteArray())
                biometricData = FacialRecordData(
                    biometricDataBlock.value!!.slice(FACIAL_RECORD_HEADER_SIZE..<biometricDataBlock.value!!.size)
                        .toByteArray()
                )
            }
            BiometricType.IRIS -> {
                biometricHeader =
                    IrisRecordHeader(biometricDataBlock.value!!)
                biometricData = null
            }
            BiometricType.FINGERPRINT -> {
                biometricHeader =
                    FingerprintRecordHeader(biometricDataBlock.value!!)
                biometricData = null
            }
        }
    }
}