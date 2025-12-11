package com.example.emrtdapplication.utils

class SubjectPublicKeyInfo(tlv: TLV) {
    val algorithmIdentifier = SignatureAlgorithm(tlv.getTLVSequence()!!.getTLVSequence()[0])
    val publicKey = tlv.getTLVSequence()!!.getTLVSequence()[1].getValue()!!
}