package com.example.emrtdapplication.utils

import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.crypto.params.AsymmetricKeyParameter
import org.spongycastle.crypto.params.DHPublicKeyParameters

class SignatureAlgorithm(tlv: TLV) {
    val oid : String
    //val signature : ByteArray
    init {
        val seq = ASN1ObjectIdentifier.getInstance(tlv.getTLVSequence()!!.getTLVSequence()[0].toByteArray())
        //signature = tlv.getTLVSequence()!!.getTLVSequence()[1].getValue()!!
        oid = seq.id
    }
}