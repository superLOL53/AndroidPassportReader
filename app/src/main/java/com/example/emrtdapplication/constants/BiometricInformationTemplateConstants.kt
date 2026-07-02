package com.example.emrtdapplication.constants

object BiometricInformationTemplateConstants {
    const val BIOMETRIC_INFORMATION_TEMPLATE_TAG_SIZE = 2
    const val BIOMETRIC_INFORMATION_TEMPLATE_SEQUENCE_SIZE = 2
    const val BIOMETRIC_INFORMATION_TEMPLATE_INVALID_SEQUENCE_SIZE_STRING = "TLV Structure does not conform to the Biometric Information Template (BIT)"
    const val BIOMETRIC_INFORMATION_TEMPLATE_TAG_1 = 0x7F.toByte()
    const val BIOMETRIC_INFORMATION_TEMPLATE_TAG_2 = 0x60.toByte()
}