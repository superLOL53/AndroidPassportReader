package com.example.emrtdapplication.biometrics

import android.util.Log
import com.example.emrtdapplication.ANDROID_LOG_INFO_TAG
import com.example.emrtdapplication.ZERO_BYTE
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.utils.TLV

const val BIOMETRIC_GROUP_TEMPLATE_TAG_SIZE = 2
const val BIOMETRIC_GROUP_TEMPLATE_TAG_1 = 0x7F.toByte()
const val BIOMETRIC_GROUP_TEMPLATE_TAG_2 = 0x61.toByte()
const val BIOMETRIC_GROUP_TEMPLATE_INVALID_TEMPLATE_STRING = "Invalid Biometric Group Template"
const val BIOMETRIC_GROUP_TEMPLATE_TEMPLATE_NUMBER_DIFFERENCE = "Number of Biometric Templates is not equal to the actual number of templates"
const val BIOMETRIC_GROUP_TEMPLATE_NUMBER_OF_INSTANCE_TAG_SIZE = 1
const val BIOMETRIC_GROUP_TEMPLATE_NUMBER_OF_INSTANCE_VALUE_SIZE = 1
const val BIOMETRIC_GROUP_TEMPLATE_UNABLE_TO_DECODE = "Unable to decode Biometric Information Template: "

/**
 * Class implementing Biometric Information Group Template
 * @param groupTemplate TLV structure containing an encoded instance
 * of a Biometric Information Group Template
 * @param type Type of the encoded biometric feature(s)
 * @property biometricInformationList Array containing
 * decoded Biometric Information Templates (BITs)
 * @throws IllegalArgumentException If [groupTemplate]
 * does not contain a Biometric Information Group Template
 */
class BiometricInformationGroupTemplate(groupTemplate: TLV, type: BiometricType) {
    var biometricInformationList: Array<BiometricInformationTemplate?>?
        private set

    init {
        if (groupTemplate.tag.size != BIOMETRIC_GROUP_TEMPLATE_TAG_SIZE ||
            groupTemplate.tag[0] != BIOMETRIC_GROUP_TEMPLATE_TAG_1 ||
            groupTemplate.tag[1] != BIOMETRIC_GROUP_TEMPLATE_TAG_2 ||
            groupTemplate.list == null || groupTemplate.list!!.tlvSequence.isEmpty()
        ) {
            throw IllegalArgumentException(BIOMETRIC_GROUP_TEMPLATE_INVALID_TEMPLATE_STRING)
        }
        val instances = groupTemplate.list!!.tlvSequence[0]
        if (instances.tag.size != BIOMETRIC_GROUP_TEMPLATE_NUMBER_OF_INSTANCE_TAG_SIZE ||
            instances.tag[0] != TlvTags.INTEGER ||
            instances.value == null ||
            instances.value!!.size != BIOMETRIC_GROUP_TEMPLATE_NUMBER_OF_INSTANCE_VALUE_SIZE
        ) {
            throw IllegalArgumentException(BIOMETRIC_GROUP_TEMPLATE_INVALID_TEMPLATE_STRING)
        }
        if (instances.value!![0] == ZERO_BYTE) {
            biometricInformationList = null
        } else {
            if (groupTemplate.list!!.tlvSequence.size != instances.value!![0].toInt()+1) {
                throw IllegalArgumentException(BIOMETRIC_GROUP_TEMPLATE_TEMPLATE_NUMBER_DIFFERENCE)
            }
            val info = ArrayList<BiometricInformationTemplate>()
            for (i in 1..<groupTemplate.list!!.tlvSequence.size) {
                try {
                    info.add(
                        BiometricInformationTemplate(groupTemplate.list!!.tlvSequence[i], type)
                    )
                } catch (e: IllegalArgumentException) {
                    Log.i(
                        ANDROID_LOG_INFO_TAG,
                        BIOMETRIC_GROUP_TEMPLATE_UNABLE_TO_DECODE + e.message
                    )
                }
            }
            biometricInformationList = info.toTypedArray()
        }
    }
}