package com.example.emrtdapplication.utils

class BiometricInformationGroupTemplate(groupTemplate: TLV) {
    var biometricInformations : Array<BiometricInformationTemplate?>?
        private set

    init {
        if (groupTemplate.getTag().size != 2 || groupTemplate.getTag()[0] != 0x7F.toByte() || groupTemplate.getTag()[1] != 0x61.toByte() ||
            groupTemplate.getTLVSequence() == null || groupTemplate.getTLVSequence()!!.getTLVSequence().size < 1) {
            throw IllegalArgumentException("Invalid Biometric Group Template")
        }
        val instances = groupTemplate.getTLVSequence()!!.getTLVSequence()[0]
        if (instances.getTag().size != 1 || instances.getTag()[0] != 0x02.toByte() ||
            instances.getValue() == null || instances.getValue()!!.size != 1) {
            throw IllegalArgumentException("Invalid instance TLV")
        }
        if (instances.getValue()!![0] == ZERO_BYTE) {
            biometricInformations = null
        } else {
            if (groupTemplate.getTLVSequence()!!.getTLVSequence().size != instances.getValue()!![0].toInt()+1) {
                throw IllegalArgumentException("Number of Biometric Templates is not equal to the actual number of templates")
            }
            val info = ArrayList<BiometricInformationTemplate>()
            for (i in 1..groupTemplate.getTLVSequence()!!.getTLVSequence().size) {
                try {
                    info.add(BiometricInformationTemplate(groupTemplate.getTLVSequence()!!.getTLVSequence()[i]))
                } catch (e : Exception) {
                    println(e.message)
                }
            }
            biometricInformations = info.toTypedArray()
        }

    }
}