package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.BiometricInformationGroupTemplate
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

class DG4(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x04
    override val EFTag: Byte = 0x76
    var biometricInformation : BiometricInformationGroupTemplate? = null
        private set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        var tlv = TLV(rawFileContent!!)
        if (tlv.getTag().size != 1 || tlv.getTag()[0] != EFTag ||
            tlv.getTLVSequence() == null || tlv.getTLVSequence()!!.getTLVSequence().size < 1) {
            return FAILURE
        }
        tlv = tlv.getTLVSequence()!!.getTLVSequence()[0]
        biometricInformation = BiometricInformationGroupTemplate(tlv)
        if (biometricInformation != null && biometricInformation!!.biometricInformations != null) {
            for (bit in biometricInformation!!.biometricInformations!!) {
                if (bit != null && bit.biometricHeaderTemplate.biometricSubType == null) {
                    biometricInformation = null
                    return FAILURE
                }
            }
        }
        return SUCCESS
    }
}