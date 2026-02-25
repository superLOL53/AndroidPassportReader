package com.example.emrtdapplication.utils

import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.DERTaggedObject
import org.spongycastle.asn1.cms.SignedData
import org.spongycastle.asn1.x500.X500Name
import org.spongycastle.asn1.x509.Certificate

/**
 * Implements the Master List specified in ICAO Doc 9303-12
 *
 * @param masterList A Master List encoded as byte array
 * @property certificateMap Maps X.500 names to certificates contained in [masterList]
 * @property signedData Decoded Master List represented as a Signed Data object
 */
class MasterList(masterList: ByteArray, issuerCode : X500Name) {
    val certificateMap : Array<Certificate>
    val signedData : SignedData

    init {
        val map = ArrayList<Certificate>()
        try {
            signedData = SignedData.getInstance(
                DERTaggedObject.getInstance(
                    DERSequence.getInstance(
                        ASN1InputStream(masterList).readAllBytes())
                        .getObjectAt(1)
                ).`object`
            )
        } catch (_ : Exception) {
            throw IllegalArgumentException("Byte array does not contain an encoded Master List!")
        }
        for (certificate in signedData.certificates) {
            try {
                val certificateInstance = Certificate.getInstance(certificate.toASN1Primitive().encoded)
                if (certificateInstance.issuer.equals(issuerCode)) {
                    map.add(certificateInstance)
                }
            } catch (_ : Exception) {
            }
        }
        val contentInfo = TLV(signedData.encapContentInfo.content.toASN1Primitive().encoded)
        if (contentInfo.value == null) {
            throw IllegalArgumentException("Signed data does not contain expected content!")
        }
        val certificateList = TLVSequence(contentInfo.value!!)
        if (certificateList.tlvSequence.isEmpty() || certificateList.tlvSequence[0].list == null || certificateList.tlvSequence[0].list!!.tlvSequence.isEmpty()) {
            throw IllegalArgumentException("Signed Data does not contain any certificates!")
        }
        for (certificate in certificateList.tlvSequence[0].list!!.tlvSequence[1].list!!.tlvSequence) {
            try {
                val certificateInstance = Certificate.getInstance(certificate.toByteArray())
                map.add(certificateInstance)
            } catch (_ : Exception) {

            }
        }
        certificateMap = map.toTypedArray()
    }
}