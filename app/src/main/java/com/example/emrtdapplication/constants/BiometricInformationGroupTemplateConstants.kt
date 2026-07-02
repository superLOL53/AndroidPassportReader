package com.example.emrtdapplication.constants

object BiometricInformationGroupTemplateConstants {
    const val BIOMETRIC_GROUP_TEMPLATE_TAG_SIZE = 2
    const val BIOMETRIC_GROUP_TEMPLATE_TAG_1 = 0x7F.toByte()
    const val BIOMETRIC_GROUP_TEMPLATE_TAG_2 = 0x61.toByte()
    const val BIOMETRIC_GROUP_TEMPLATE_INVALID_TEMPLATE_STRING = "Invalid Biometric Group Template"
    const val BIOMETRIC_GROUP_TEMPLATE_TEMPLATE_NUMBER_DIFFERENCE = "Number of Biometric Templates is not equal to the actual number of templates"
    const val BIOMETRIC_GROUP_TEMPLATE_NUMBER_OF_INSTANCE_TAG_SIZE = 1
    const val BIOMETRIC_GROUP_TEMPLATE_NUMBER_OF_INSTANCE_VALUE_SIZE = 1
    const val BIOMETRIC_GROUP_TEMPLATE_UNABLE_TO_DECODE = "Unable to decode Biometric Information Template: "
}