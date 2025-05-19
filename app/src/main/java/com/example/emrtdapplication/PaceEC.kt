package com.example.emrtdapplication

import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.x9.ECNamedCurveTable
import org.spongycastle.crypto.agreement.DHStandardGroups.rfc5114_2048_256

class PaceEC {

    @OptIn(ExperimentalStdlibApi::class)
    fun paceProtocol() {
        val asn = ASN1ObjectIdentifier("0.4.0.127.0.7.2.2.4")
        println(asn.toString())
        val crypt = rfc5114_2048_256
        println(crypt.g.toByteArray().toHexString())
        println(crypt.p.toByteArray().toHexString())
        println(crypt.q.toByteArray().toHexString())
        println(crypt.toString())
        val ec = ECNamedCurveTable.getNames()
        if (ec != null) {
            println(ec.toString())
        }
        for (e in ec) {
            println(e.toString())
        }
        val bp192 = ECNamedCurveTable.getByName("brainpoolp192r1")

    }
}