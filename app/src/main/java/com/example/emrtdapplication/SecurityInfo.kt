package com.example.emrtdapplication

import com.example.emrtdapplication.common.PACE_INFO_TAG
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1ObjectIdentifier

const val UNKNOWN_TYPE = -1
const val PACE_INFO_TYPE = 0
const val ACTIVE_AUTHENTICATION_TYPE = 1
const val CHIP_AUTHENTICATION_TYPE = 2
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE = 3
const val TERMINAL_AUTHENTICATION_TYPE = 4
const val EF_DIR_TYPE = 5
const val PACE_DOMAIN_PARAMETER_INFO_TYPE = 6
const val PACE_OID = "0.4.0.127.0.7.2.2.4"
const val ACTIVE_AUTHENTICATION_OID = "2.23.136.1.1.5"
const val CHIP_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.3"
const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID = "0.4.0.127.0.7.2.2.1"
const val TERMINAL_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.2"
const val EF_DIR_OID = "2.23.136.1.1.13"

open class SecurityInfo(rawFileContent : ByteArray) {
    var rawFileContent : ByteArray = rawFileContent
        private set
    var objectIdentifier : TLV
        private set
    var requiredData : TLV
        private set
    var optionalData : TLV? = null
        private set
    var type : Int = UNKNOWN_TYPE
        private set
    var protocol : ByteArray
        private set

    init {
        val tlv = TLV(rawFileContent)
        if (!tlv.getIsValid() || !tlv.isConstruct() || tlv.getTLVSequence() == null || tlv.getTLVSequence()!!.getTLVSequence().size < 2 || 3 < tlv.getTLVSequence()!!.getTLVSequence().size) {
            throw IllegalArgumentException("Invalid Sequence for type SecurityInfo")
        }
        objectIdentifier = tlv.getTLVSequence()!!.getTLVSequence()[0]
        requiredData = tlv.getTLVSequence()!!.getTLVSequence()[1]
        if (!objectIdentifier.getIsValid() || objectIdentifier.getValue() == null || !requiredData.getIsValid() || objectIdentifier.getTag().size != 1 || objectIdentifier.getTag()[0] == PACE_INFO_TAG) {
            throw IllegalArgumentException("Invalid OID Sequence for type SecurityInfo")
        } else {
            protocol = objectIdentifier.getValue()!!
        }
        if (tlv.getTLVSequence()!!.getTLVSequence().size == 3) {
            optionalData = tlv.getTLVSequence()!!.getTLVSequence()[2]
            if (!optionalData!!.getIsValid()) {
                throw IllegalArgumentException("Invalid present optional data for type SecurityInfo")
            }
        }
        val oid = ASN1ObjectIdentifier.getInstance(ASN1InputStream(objectIdentifier.toByteArray()).readAllBytes())
        if (oid.id.startsWith(PACE_OID)) {
            if (objectIdentifier.getValue()!!.size == 9) {
                type = PACE_DOMAIN_PARAMETER_INFO_TYPE
            } else if (objectIdentifier.getValue()!!.size == 10) {
                type = PACE_INFO_TYPE
            }
        } else if (oid.id.startsWith(ACTIVE_AUTHENTICATION_OID)) {
            type = ACTIVE_AUTHENTICATION_TYPE
        } else if (oid.id.startsWith(CHIP_AUTHENTICATION_OID)) {
            type = CHIP_AUTHENTICATION_TYPE
        } else if (oid.id.startsWith(CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID)) {
            type = CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
        } else if (oid.id.startsWith(TERMINAL_AUTHENTICATION_OID)) {
            type = TERMINAL_AUTHENTICATION_TYPE
        } else if (oid.id.startsWith(EF_DIR_OID)) {
            type = EF_DIR_TYPE
        }
    }
}