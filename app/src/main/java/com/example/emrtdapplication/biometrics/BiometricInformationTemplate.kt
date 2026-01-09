package com.example.emrtdapplication.biometrics

import com.example.emrtdapplication.utils.TLV

/**
 * Class implementing a Biometric Information Template (BIT) consisting of a Biometric Header Template (BHT)
 * and a Biometric Data Block (BDB)
 * @param biometricInformation TLV structure containing an encoded instance of a BIT
 * @param type Type of the encoded biometric feature
 * @property biometricHeaderTemplate Header of the BIT
 * @property biometricDataBlock BDB of the BIT
 * @throws IllegalArgumentException If [biometricInformation] does not contain a BIT
 */
class BiometricInformationTemplate(biometricInformation : TLV, type: BiometricType) {
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
        biometricDataBlock = BiometricDataBlock(biometricInformation.list!!.tlvSequence[1], type)
    }
}