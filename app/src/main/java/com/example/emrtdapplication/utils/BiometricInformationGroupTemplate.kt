package com.example.emrtdapplication.utils

class BiometricInformationGroupTemplate(groupTemplate: TLV) {
    var biometricInformations : Array<BiometricInformationTemplate?>?
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
            biometricInformations = null
        } else {
            if (groupTemplate.list!!.tlvSequence.size != instances.value!![0].toInt()+1) {
                throw IllegalArgumentException("Number of Biometric Templates is not equal to the actual number of templates")
            }
            val info = ArrayList<BiometricInformationTemplate>()
            for (i in 1..<groupTemplate.list!!.tlvSequence.size) {
                try {
                    info.add(BiometricInformationTemplate(groupTemplate.list!!.tlvSequence[i]))
                } catch (e : Exception) {
                    println(e.message)
                }
            }
            biometricInformations = info.toTypedArray()
        }
    }
}