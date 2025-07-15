package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.SUCCESS
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.DERTaggedObject
import org.spongycastle.asn1.cms.SignedData
import org.spongycastle.asn1.x509.Certificate

class EfSod(apduControl: APDUControl): ElementaryFileTemplate(apduControl) {

    override val EFTag: Byte = 77
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x1D

    override fun parse(): Int {
        if (rawFileContent == null) {
            return SUCCESS
        }
        val certStart = if (rawFileContent!![1] < 0) {
            rawFileContent!![1]+128+2
        } else {
            2
        }
        val input = ASN1InputStream(rawFileContent!!.slice(certStart..<rawFileContent!!.size).toByteArray())
        val seq = DERSequence.getInstance(input.readObject())
        //println(ASN1Dump.dumpAsString(seq))
        val tag = DERTaggedObject.getInstance(seq.getObjectAt(1)).`object`
        val der = DERSequence.getInstance(tag)
        //println(ASN1Dump.dumpAsString(der))
        val cert = SignedData.getInstance(der)
        val actualCert = Certificate.getInstance(cert.certificates.getObjectAt(0))
        //println(ASN1Dump.dumpAsString(actualCert))

        return NOT_IMPLEMENTED
    }
}