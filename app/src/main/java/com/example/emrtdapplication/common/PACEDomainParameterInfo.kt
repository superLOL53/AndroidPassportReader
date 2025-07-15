package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.INVALID_ARGUMENT
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLV_TAGS
import java.math.BigInteger

/**
 * Class representing the parameters for the supported cryptographic asymmetric PACE protocol
 */
class PACEDomainParameterInfo {
    private var algorithm: ByteArray? = null
    private var version: Int = -1
    private var primeField: ByteArray? = null
    private var p: BigInteger? = null
    private var q: BigInteger? = null
    private var g: BigInteger? = null
    private var a: BigInteger? = null
    private var b: BigInteger? = null
    private var x: BigInteger? = null
    private var y: BigInteger? = null
    private var n: BigInteger? = null
    private var f: BigInteger? = null

    fun setDomainParameter(tlv: TLV) : Int {
        if (!tlv.isConstruct() || tlv.getTLVSequence() == null) {
            return INVALID_ARGUMENT
        }
        for (tag in tlv.getTLVSequence()!!.getTLVSequence()) {
            when (tag.getTag()[0]) {
                TLV_TAGS.OID -> algorithm = tag.getValue()
                TLV_TAGS.SEQUENCE -> parseDomainParameters(tag)
            }
        }
        return SUCCESS
    }

    private fun parseDomainParameters(tlv: TLV) : Int {
        if (!tlv.isConstruct() || tlv.getTLVSequence() == null) {
            return INVALID_ARGUMENT
        }
        for (i in tlv.getTLVSequence()!!.getTLVSequence().indices) {
            val tag = tlv.getTLVSequence()!!.getTLVSequence()[i]

        }
        return NOT_IMPLEMENTED
    }
}
