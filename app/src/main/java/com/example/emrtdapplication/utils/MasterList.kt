package com.example.emrtdapplication.utils

import com.example.emrtdapplication.utils.MasterList.signedData
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.DERTaggedObject
import org.spongycastle.asn1.DLSet
import org.spongycastle.asn1.cms.SignedData
import org.spongycastle.asn1.x509.Certificate

/**
 * Implements the Master List specified in ICAO Doc 9303-12
 *
 * @property signedData Decoded Master List represented as a Signed Data object
 */
object MasterList {
    var certificateMap : Array<Certificate>? = null
        private set
    var signedData : SignedData? = null
        private set
    var isDecoded = false
        private set
    var isFinished = false
        private set

    fun decodeMasterList(masterList: ByteArray) {
        val map = ArrayList<Certificate>()
        try {
            signedData = SignedData.getInstance(
                DERTaggedObject.getInstance(
                    DERSequence.getInstance(
                        ASN1InputStream(masterList).readBytes())
                        .getObjectAt(1)
                ).`object`
            )
        } catch (_ : Exception) {
            throw IllegalArgumentException("Byte array does not contain an encoded Master List!")
        }
        if (signedData == null) {
            isFinished = true
            return
        }
        for (certificate in signedData!!.certificates) {
            try {
                val certificateInstance = Certificate.getInstance(certificate.toASN1Primitive().encoded)
                map.add(certificateInstance)
            } catch (_ : Exception) {
            }
        }
        val contentInfo = TLV(signedData!!.encapContentInfo.content.toASN1Primitive().encoded)
        if (contentInfo.value == null) {
            isFinished = true
            throw IllegalArgumentException("Signed data does not contain expected content!")
        }
        val list = DERSequence.getInstance(contentInfo.value)
        val certList = list.getObjectAt(1) as DLSet
        for (certificate in certList.objects) {
            map.add(Certificate.getInstance(certificate))
        }
        certificateMap = map.toTypedArray()
        isDecoded = true
        isFinished = true
    }

    fun getCSCA(certificate: Certificate) : Array<Certificate> {
        val certs = ArrayList<Certificate>()
        if (certificateMap != null) {
            for (c in certificateMap) {
                if (c.subject.equals(certificate.issuer) && certificate.startDate.date.time >= c.startDate.date.time &&
                    c.endDate.date.time >= certificate.endDate.date.time &&
                    c.signatureAlgorithm.algorithm.id == certificate.signatureAlgorithm.algorithm.id) {
                    certs.add(c)
                }
            }
        }
        return certs.toTypedArray()
    }
}