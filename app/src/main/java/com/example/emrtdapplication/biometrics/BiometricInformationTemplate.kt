package com.example.emrtdapplication.biometrics

import com.example.emrtdapplication.constants.BiometricInformationTemplateConstants.BIOMETRIC_INFORMATION_TEMPLATE_INVALID_SEQUENCE_SIZE_STRING
import com.example.emrtdapplication.constants.BiometricInformationTemplateConstants.BIOMETRIC_INFORMATION_TEMPLATE_SEQUENCE_SIZE
import com.example.emrtdapplication.constants.BiometricInformationTemplateConstants.BIOMETRIC_INFORMATION_TEMPLATE_TAG_1
import com.example.emrtdapplication.constants.BiometricInformationTemplateConstants.BIOMETRIC_INFORMATION_TEMPLATE_TAG_2
import com.example.emrtdapplication.constants.BiometricInformationTemplateConstants.BIOMETRIC_INFORMATION_TEMPLATE_TAG_SIZE
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
        if (biometricInformation.tag.size != BIOMETRIC_INFORMATION_TEMPLATE_TAG_SIZE ||
            !biometricInformation.tag.contentEquals(byteArrayOf(BIOMETRIC_INFORMATION_TEMPLATE_TAG_1, BIOMETRIC_INFORMATION_TEMPLATE_TAG_2)) ||
            !biometricInformation.isConstruct() || biometricInformation.list == null ||
            biometricInformation.list!!.tlvSequence.size != BIOMETRIC_INFORMATION_TEMPLATE_SEQUENCE_SIZE) {
            throw IllegalArgumentException(BIOMETRIC_INFORMATION_TEMPLATE_INVALID_SEQUENCE_SIZE_STRING)
        }
        biometricHeaderTemplate = BiometricHeaderTemplate(biometricInformation.list!!.tlvSequence[0])
        biometricDataBlock = BiometricDataBlock(biometricInformation.list!!.tlvSequence[1], type)
    }
}