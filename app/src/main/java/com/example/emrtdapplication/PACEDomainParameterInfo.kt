package com.example.emrtdapplication

class PACEDomainParameterInfo {
    private var protocol : Byte = PACEInfoConstants.UNDEFINED
    private var domainParameter : Byte = PACEInfoConstants.UNDEFINED
    private var parameterId : Byte = PACEInfoConstants.UNDEFINED

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
