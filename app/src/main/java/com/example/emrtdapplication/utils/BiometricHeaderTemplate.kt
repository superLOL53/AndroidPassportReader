package com.example.emrtdapplication.utils

class BiometricHeaderTemplate(private val biometricHeaderTemplate : TLV) {
    var headerVersion : Short? = null
    var biometricType : ByteArray? = null
    var biometricSubType : Byte? = null
    var creationTime : ByteArray? = null
    var validityPeriod : ByteArray? = null
    var biometricReferenceDataCreator : Int? = null
    val formatOwner : Short
    val formatType : Short

    init {
        if (biometricHeaderTemplate.getTag().size != 1 || biometricHeaderTemplate.getTag()[0] != 0xA1.toByte() ||
            biometricHeaderTemplate.getTLVSequence() == null || biometricHeaderTemplate.getTLVSequence()!!.getTLVSequence().size < 2 ||
            8 < biometricHeaderTemplate.getTLVSequence()!!.getTLVSequence().size) {
            throw IllegalArgumentException("Biometric Header Template does not conform to the Specification")
        }
        var owner : Short? = null
        var type : Short? = null
        for (tlv in biometricHeaderTemplate.getTLVSequence()!!.getTLVSequence()) {
            if (tlv.getTag().size != 1) {
                throw IllegalArgumentException("Illegal Tag in the Biometric Header Template")
            }
            when (tlv.getTag()[0]) {
                0x80.toByte() -> setHeaderVersion(tlv.getValue())
                0x81.toByte() -> biometricType = tlv.getValue()
                0x82.toByte() -> setSubType(tlv.getValue())
                0x83.toByte() -> creationTime = tlv.getValue()
                0x84.toByte() -> validityPeriod = tlv.getValue()
                0x86.toByte() -> setCreator(tlv.getValue())
                0x87.toByte() -> owner = setOwner(tlv.getValue())
                0x88.toByte() -> type = setFormatType(tlv.getValue())
            }
        }
        if (owner == null || type == null) {
            throw IllegalArgumentException("Owner and/or Type is not present")
        }
        formatOwner = owner
        formatType = type
    }

    private fun setHeaderVersion(header : ByteArray?) {
        if (header == null) return
        if (header.size != 2) {
            throw IllegalArgumentException("Header version has invalid length")
        }
        headerVersion = (header[0]*256 + header[1]).toShort()
    }

    private fun setSubType(subType : ByteArray?) {
        if (subType == null) return
        if (subType.size != 1) {
            throw IllegalArgumentException("Invalid Subtype value")
        }
        biometricSubType = subType[0]
    }

    private fun setCreator(creator : ByteArray?) {
        if (creator == null) return
        /*if (creator.size != 2) {
            throw IllegalArgumentException("Invalid length of the biometric reference creator")
        }*/
        biometricReferenceDataCreator = 0
        for (b in creator) {
            biometricReferenceDataCreator = biometricReferenceDataCreator!!*256 + b
        }
    }

    private fun setOwner(owner : ByteArray?) : Short {
        if (owner == null) return 0
        if (owner.size != 2) {
            throw IllegalArgumentException("Invalid length for the owner field")
        }
        return (owner[0]*256 + owner[1]).toShort()
    }

    private fun setFormatType(type : ByteArray?) : Short {
        if (type == null || type.size != 2) {
            throw IllegalArgumentException("Invalid length for the format type field")
        }
        return (type[0]*256+type[1]).toShort()
    }
}