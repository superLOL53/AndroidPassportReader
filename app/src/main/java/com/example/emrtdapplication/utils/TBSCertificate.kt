package com.example.emrtdapplication.utils

import org.spongycastle.util.test.FixedSecureRandom
import java.math.BigInteger

class TBSCertificate(tlv: TLV) {
    val version : Int
    val serialNumber : BigInteger
    val signature : SignatureAlgorithm
    val issuer : String
    val validityStart : String
    val validityEnd : String
    val subject : String
    val subjectPublicKeyInfo : SubjectPublicKeyInfo

    init {
        val seq = tlv.getTLVSequence()!!.getTLVSequence()
        var v = 0
        for (b in seq[0].getTLVSequence()!!.getTLVSequence()[0].getValue()!!) {
            v += v*256 + b
        }
        version = v
        serialNumber = BigInteger(seq[1].getValue()!!)
        signature = SignatureAlgorithm(seq[2])
        var sb = StringBuilder()
        for (i in seq[3].getTLVSequence()!!.getTLVSequence().size-1 downTo 0) {
            sb.append(seq[3].getTLVSequence()!!.getTLVSequence()[i].getTLVSequence()!!.getTLVSequence()[0].getTLVSequence()!!.getTLVSequence()[1].getValue()!!.decodeToString())
            if (i != 0) {
                sb.append('.')
            }
        }
        issuer = sb.toString()
        validityStart = seq[4].getTLVSequence()!!.getTLVSequence()[0].getValue()!!.decodeToString()
        validityEnd = seq[4].getTLVSequence()!!.getTLVSequence()[1].getValue()!!.decodeToString()
        sb = StringBuilder()
        for (i in seq[5].getTLVSequence()!!.getTLVSequence().size-1 downTo 0) {
            sb.append(seq[3].getTLVSequence()!!.getTLVSequence()[i].getTLVSequence()!!.getTLVSequence()[0].getTLVSequence()!!.getTLVSequence()[1].getValue()!!.decodeToString())
            if (i != 0) {
                sb.append('.')
            }
        }
        subject = sb.toString()
        subjectPublicKeyInfo = SubjectPublicKeyInfo(seq[6])
    }
}