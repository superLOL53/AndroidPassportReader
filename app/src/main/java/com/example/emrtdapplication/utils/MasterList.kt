package com.example.emrtdapplication.utils

import com.example.emrtdapplication.utils.MasterList.signedData
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1OctetString
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.DERTaggedObject
import org.spongycastle.asn1.DLSet
import org.spongycastle.asn1.cms.SignedData
import org.spongycastle.asn1.x500.X500Name
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier
import org.spongycastle.asn1.x509.Certificate
import java.security.cert.X509CRL

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
        val keyIDOID = ASN1ObjectIdentifier("2.5.29.35")
        val certificateKeyID = certificate.tbsCertificate.extensions.getExtension(keyIDOID)
        if (certificateMap != null) {
            for (c in certificateMap) {
                if (c.subject.equals(certificate.issuer) && certificate.startDate.date.time >= c.startDate.date.time &&
                    c.endDate.date.time >= certificate.endDate.date.time &&
                    c.signatureAlgorithm.algorithm.id == certificate.signatureAlgorithm.algorithm.id) {
                    val keyID = c.tbsCertificate.extensions.getExtension(keyIDOID)
                    if (keyID != null) {
                        if (certificateKeyID != null && keyID.extnValue.octets.contentEquals(
                                certificateKeyID.extnValue.octets
                            )) {
                            certs.onEach { c -> certs.remove(c) }
                            certs.add(c)
                            return certs.toTypedArray()
                        }
                    } else {
                        certs.add(c)
                    }
                }
            }
        }
        return certs.toTypedArray()
    }

    fun getCSCA(crl: X509CRL) : Certificate? {
        val keyIDOID = ASN1ObjectIdentifier("2.5.29.35")
        val crlkeyIDExtension = crl.getExtensionValue("2.5.29.35")
        val crlKeyID = if (crlkeyIDExtension != null) {
            try {
                val authorityKeyIdentifierOctetString = ASN1OctetString.getInstance(crlkeyIDExtension)
                AuthorityKeyIdentifier.getInstance(authorityKeyIdentifierOctetString.octets)
            } catch (_ : Exception) {
                null
            }
        } else {
            null
        }
        if (certificateMap != null) {
            for (certificate in certificateMap) {
                val name = X500Name.getInstance(crl.issuerX500Principal.encoded)
                if (certificate.subject.equals(name) &&
                    crl.thisUpdate.after(certificate.startDate.date) &&
                    crl.nextUpdate.before(certificate.endDate.date) &&
                    certificate.signatureAlgorithm.algorithm.id == crl.sigAlgOID) {
                    val keyID = certificate.tbsCertificate.extensions.getExtension(keyIDOID)
                    if (keyID != null) {
                        try {
                            if (crlKeyID != null &&
                                AuthorityKeyIdentifier.getInstance(keyID.extnValue.octets).keyIdentifier.
                                contentEquals(crlKeyID.keyIdentifier)) {
                                    return certificate
                            }
                        } catch (_ : Exception) {
                        }
                    }
                }
            }
        }
        return null
    }
}