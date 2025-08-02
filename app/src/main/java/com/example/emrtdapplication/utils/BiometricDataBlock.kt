package com.example.emrtdapplication.utils

//TODO: Implement
class BiometricDataBlock(private val biometricDataBlock : TLV) {

    init {
        if (biometricDataBlock.getTag().size != 2 || biometricDataBlock.getTag()[1] != 0x2E.toByte() ||
            (biometricDataBlock.getTag()[0] != 0x5F.toByte() && biometricDataBlock.getTag()[0] != 0x7F.toByte())) {
            throw IllegalArgumentException("Invalid Tag for Biometric Data Block")
        }
    }
}