package com.example.emrtdapplication.biometrics

import com.example.emrtdapplication.utils.TLV

const val BIOMETRIC_INFORMATION_TEMPLATE_TAG_SIZE = 2
const val BIOMETRIC_INFORMATION_TEMPLATE_SEQUENCE_SIZE = 2
const val BIOMETRIC_INFORMATION_TEMPLATE_INVALID_SEQUENCE_SIZE_STRING = "TLV Structure does not conform to the Biometric Information Template (BIT)"
const val BIOMETRIC_INFORMATION_TEMPLATE_TAG_1 = 0x7F.toByte()
const val BIOMETRIC_INFORMATION_TEMPLATE_TAG_2 = 0x60.toByte()

/**
 * Class implementing a Biometric Information Template (BIT)
 * consisting of a Biometric Header Template (BHT)
 * and a Biometric Data Block (BDB)
 * @param biometricInformation TLV structure containing an encoded instance of a BIT
 * @param type Type of the encoded biometric feature
 * @property biometricHeaderTemplate Header of the BIT
 * @property biometricDataBlock BDB of the BIT
 * @throws IllegalArgumentException If [biometricInformation] does not contain a BIT
 */
class BiometricInformationTemplate(biometricInformation: TLV, type: BiometricType) {
    var biometricHeaderTemplate: BiometricHeaderTemplate
        private set
    var biometricDataBlock: BiometricDataBlock
        private set


    init {
        if (biometricInformation.tag.size != BIOMETRIC_INFORMATION_TEMPLATE_TAG_SIZE ||
            !biometricInformation.tag.contentEquals(
                byteArrayOf(
                    BIOMETRIC_INFORMATION_TEMPLATE_TAG_1,
                    BIOMETRIC_INFORMATION_TEMPLATE_TAG_2
                )
            ) ||
            !biometricInformation.isConstruct() || biometricInformation.list == null ||
            biometricInformation.list!!.tlvSequence.size !=
                BIOMETRIC_INFORMATION_TEMPLATE_SEQUENCE_SIZE
        ) {
            throw IllegalArgumentException(
                BIOMETRIC_INFORMATION_TEMPLATE_INVALID_SEQUENCE_SIZE_STRING
            )
        }
        biometricHeaderTemplate =
            BiometricHeaderTemplate(biometricInformation.list!!.tlvSequence[0])
        biometricDataBlock =
            BiometricDataBlock(biometricInformation.list!!.tlvSequence[1], type)
    }
}