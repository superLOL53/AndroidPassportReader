package com.example.emrtdapplication.utils

/**
 * Class implementing Biometric Information Group Template
 * @param groupTemplate TLV structure containing an encoded instance of a Biometric Information Group Template
 * @param type Type of the encoded biometric feature(s)
 * @property biometricInformationList Array containing decoded Biometric Information Templates (BITs)
 * @throws IllegalArgumentException If [groupTemplate] does not contain a Biometric Information Group Template
 */
class BiometricInformationGroupTemplate(groupTemplate: TLV, type: BiometricType) {
    var biometricInformationList : Array<BiometricInformationTemplate?>?
        private set

    init {
        if (groupTemplate.tag.size != 2 || groupTemplate.tag[0] != 0x7F.toByte() || groupTemplate.tag[1] != 0x61.toByte() ||
            groupTemplate.list == null || groupTemplate.list!!.tlvSequence.isEmpty()
        ) {
            throw IllegalArgumentException("Invalid Biometric Group Template")
        }
        val instances = groupTemplate.list!!.tlvSequence[0]
        if (instances.tag.size != 1 || instances.tag[0] != 0x02.toByte() ||
            instances.value == null || instances.value!!.size != 1) {
            throw IllegalArgumentException("Invalid instance TLV")
        }
        if (instances.value!![0] == ZERO_BYTE) {
            biometricInformationList = null
        } else {
            if (groupTemplate.list!!.tlvSequence.size != instances.value!![0].toInt()+1) {
                throw IllegalArgumentException("Number of Biometric Templates is not equal to the actual number of templates")
            }
            val info = ArrayList<BiometricInformationTemplate>()
            for (i in 1..<groupTemplate.list!!.tlvSequence.size) {
                try {
                    info.add(BiometricInformationTemplate(groupTemplate.list!!.tlvSequence[i], type))
                } catch (e : Exception) {
                    println(e.message)
                }
            }
            biometricInformationList = info.toTypedArray()
        }
    }
}