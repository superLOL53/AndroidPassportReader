package com.example.emrtdapplication

/**
 * Class representing the parameters for the supported cryptographic asymmetric PACE protocol
 */
class PACEDomainParameterInfo {
    private var protocol : Byte = UNDEFINED
    private var domainParameter : Byte = UNDEFINED
    private var parameterId : Byte = UNDEFINED

    fun setProtocol(tlv: TLV) : Int {
        return NOT_IMPLEMENTED
    }

    fun getProtocol() : Byte {
        return protocol
    }

    fun setDomainParameter(tlv: TLV) : Int {
        return NOT_IMPLEMENTED
    }

    fun getDomainParameter() : Byte {
        return domainParameter
    }

    fun setParameterId(tlv: TLV) : Int {
        return NOT_IMPLEMENTED
    }

    fun getParameterId() : Byte {
        return parameterId
    }
}
