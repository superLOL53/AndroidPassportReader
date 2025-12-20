package com.example.emrtdapplication.utils

class BiometricInformationTemplate(biometricInformation : TLV) {

    var biometricHeaderTemplate : BiometricHeaderTemplate
        private set
    var biometricDataBlock : BiometricDataBlock
        private set


    init {
        if (biometricInformation.tag.size != 2 || !biometricInformation.tag.contentEquals(byteArrayOf(0x7F, 0x60)) ||
            !biometricInformation.isConstruct() || biometricInformation.list == null ||
            biometricInformation.list!!.tlvSequence.size != 2) {
            throw IllegalArgumentException("TLV Structure does not conform to the Biometric Information Template (BIT)")
        }
        biometricHeaderTemplate = BiometricHeaderTemplate(biometricInformation.list!!.tlvSequence[0])
        biometricDataBlock = BiometricDataBlock(biometricInformation.list!!.tlvSequence[1])
    }



}