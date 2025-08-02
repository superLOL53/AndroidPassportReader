package com.example.emrtdapplication.utils

class BiometricInformationTemplate(private val biometricInformation : TLV) {

    var biometricHeaderTemplate : BiometricHeaderTemplate
        private set
    var biometricDataBlock : BiometricDataBlock
        private set


    init {
        if (biometricInformation.getTag().size != 2 || !biometricInformation.getTag().contentEquals(byteArrayOf(0x7F, 0x60)) ||
            !biometricInformation.isConstruct() || biometricInformation.getTLVSequence() == null ||
            biometricInformation.getTLVSequence()!!.getTLVSequence().size != 2) {
            throw IllegalArgumentException("TLV Structure does not conform to the Biometric Information Template (BIT)")
        }
        biometricHeaderTemplate = BiometricHeaderTemplate(biometricInformation.getTLVSequence()!!.getTLVSequence()[0])
        biometricDataBlock = BiometricDataBlock(biometricInformation.getTLVSequence()!!.getTLVSequence()[1])
    }



}