package com.example.emrtdapplication.utils

/**
 * Class representing a Biometric Data Block (BDB) according to ISO/IEC19794-4
 */
//TODO: Refine for different types of biometrics
class BiometricDataBlock(biometricDataBlock : TLV) {
    val facialRecordHeader : FacialRecordHeader
    val facialRecordData : FacialRecordData

    init {
        if (biometricDataBlock.tag.size != 2 || biometricDataBlock.tag[1] != 0x2E.toByte() ||
            (biometricDataBlock.tag[0] != 0x5F.toByte() && biometricDataBlock.tag[0] != 0x7F.toByte())) {
            throw IllegalArgumentException("Invalid Tag for Biometric Data Block")
        }
        if (biometricDataBlock.value == null) {
            throw IllegalArgumentException("No value for Biometric Data Block (BDB)")
        }
        facialRecordHeader = FacialRecordHeader(biometricDataBlock.value!!.slice(0..13).toByteArray())
        facialRecordData = FacialRecordData(biometricDataBlock.value!!.slice(14..<biometricDataBlock.value!!.size).toByteArray())
    }
}