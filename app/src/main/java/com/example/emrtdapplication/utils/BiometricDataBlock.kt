package com.example.emrtdapplication.utils

/**
 * Class representing a Biometric Data Block (BDB) according to ISO/IEC19794-4
 */
//TODO: Refine for different types of biometrics
class BiometricDataBlock(biometricDataBlock : TLV) {
    val facialRecordHeader : FacialRecordHeader
    val facialRecordData : FacialRecordData

    init {
        if (biometricDataBlock.getTag().size != 2 || biometricDataBlock.getTag()[1] != 0x2E.toByte() ||
            (biometricDataBlock.getTag()[0] != 0x5F.toByte() && biometricDataBlock.getTag()[0] != 0x7F.toByte())) {
            throw IllegalArgumentException("Invalid Tag for Biometric Data Block")
        }
        if (biometricDataBlock.getValue() == null) {
            throw IllegalArgumentException("No value for Biometric Data Block (BDB)")
        }
        facialRecordHeader = FacialRecordHeader(biometricDataBlock.getValue()!!.slice(0..13).toByteArray())
        facialRecordData = FacialRecordData(biometricDataBlock.getValue()!!.slice(14..<biometricDataBlock.getValue()!!.size).toByteArray())
    }
}