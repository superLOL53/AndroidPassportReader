package com.example.emrtdapplication.biometrics

import com.example.emrtdapplication.biometrics.face.FacialRecordData
import com.example.emrtdapplication.biometrics.face.FacialRecordHeader
import com.example.emrtdapplication.biometrics.fingerprint.FingerprintRecordData
import com.example.emrtdapplication.biometrics.fingerprint.FingerprintRecordHeader
import com.example.emrtdapplication.biometrics.iris.IrisRecordData
import com.example.emrtdapplication.biometrics.iris.IrisRecordHeader
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
    val biometricData : BiometricData

    init {
        if (biometricDataBlock.tag.size != 2 || biometricDataBlock.tag[1] != 0x2E.toByte() ||
            (biometricDataBlock.tag[0] != 0x5F.toByte() && biometricDataBlock.tag[0] != 0x7F.toByte())) {
            throw IllegalArgumentException("Invalid Tag for Biometric Data Block")
        }
        if (biometricDataBlock.value == null) {
            throw IllegalArgumentException("No value for Biometric Data Block (BDB)")
        }
        when (type) {
            BiometricType.FACE -> {
                biometricHeader =
                    FacialRecordHeader(biometricDataBlock.value!!.slice(0..13).toByteArray())
                biometricData = FacialRecordData(
                    biometricDataBlock.value!!.slice(14..<biometricDataBlock.value!!.size)
                        .toByteArray()
                )
            }
            BiometricType.IRIS -> {
                biometricHeader =
                    IrisRecordHeader(biometricDataBlock.value!!.slice(0..13).toByteArray())
                biometricData = IrisRecordData(
                    biometricDataBlock.value!!.slice(14..<biometricDataBlock.value!!.size)
                        .toByteArray()
                )
            }
            BiometricType.FINGERPRINT -> {
                biometricHeader =
                    FingerprintRecordHeader(biometricDataBlock.value!!.slice(0..13).toByteArray())
                biometricData = FingerprintRecordData(
                    biometricDataBlock.value!!.slice(14..<biometricDataBlock.value!!.size)
                        .toByteArray()
                )
            }
        }
    }
}