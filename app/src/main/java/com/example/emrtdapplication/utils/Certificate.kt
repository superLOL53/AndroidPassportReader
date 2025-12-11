package com.example.emrtdapplication.utils


class Certificate(private val tlv: TLV) {
    val tbsCert : TBSCertificate
    val signatureAlgorithm :  SignatureAlgorithm
    val signature : ByteArray

    init {
        tbsCert = TBSCertificate(tlv.getTLVSequence()!!.getTLVSequence()[0])
        signatureAlgorithm = SignatureAlgorithm(tlv.getTLVSequence()!!.getTLVSequence()[1])
        signature = tlv.getTLVSequence()!!.getTLVSequence()[2].getValue()!!
    }
}