package com.example.emrtdapplication

import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.MasterList
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.DLSequence
import org.spongycastle.asn1.x509.DistributionPoint
import org.spongycastle.asn1.x509.GeneralNames
import java.io.File
import java.net.URL
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509CRL
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MockTest {
    val test = byteArrayOf(48, 53, 48, 51, -96, 49, -96, 47, -122, 45, 104, 116, 116, 112, 58, 47, 47, 119, 119, 119, 46, 98, 109, 105, 46, 103, 118, 46, 97, 116, 47, 99, 115, 99, 97, 47, 99, 114, 108, 47, 67, 83, 67, 65, 65, 85, 83, 84, 82, 73, 65, 46, 99, 114, 108)

    @Test
    fun test() {
        val seq = DLSequence.getInstance(test)
        for (i in 0..<seq.size()) {
            val dpseq = seq.getObjectAt(i) as DLSequence
            val dp = DistributionPoint.getInstance(dpseq)
            dp.distributionPoint
        }
    }

    @Test
    fun crlTest() {
        Security.addProvider(BouncyCastleProvider())
        val ml = File("/home/oliver/AndroidStudioProjects/eMRTDApplication/app/src/main/assets/MasterList/ICAO_ml_01April2025.ml")
        val ba = ml.readBytes()
        val ma = MasterList
        ma.decodeMasterList(ba)

        val distributionPointOID = ASN1ObjectIdentifier("2.5.29.31")
        for (certificate in ma.certificateMap!!) {
            val distributionPoint = certificate.tbsCertificate.extensions.getExtension(distributionPointOID)
            if (distributionPoint != null) {
                val seq = ASN1Sequence.getInstance(distributionPoint.extnValue.octets)
                for (i in 0..<seq.size()) {
                    val distributionPoint = DistributionPoint.getInstance(seq.getObjectAt(i))
                    if (distributionPoint.distributionPoint.type == 0) {
                        val names = distributionPoint.distributionPoint.name as GeneralNames
                        for (name in names.names) {
                            val string = name.name.toString()
                            if (string.startsWith("http")) {
                                val url = URL(string)
                                val conn = url.openConnection()
                                conn.connectTimeout = 60000
                                val input = conn.getInputStream()
                                val crl = CertificateFactory.getInstance("X.509").generateCRL(input) as X509CRL
                                val crlCertificate = ma.getCSCA(crl)
                                if (crlCertificate != null) {
                                    val pub = Crypto.generatePublicKey(crlCertificate)
                                    assertNotNull(pub)
                                    assertTrue {
                                        Crypto.verifySignature(
                                            crl.sigAlgName,
                                            pub,
                                            crl.tbsCertList,
                                            crl.signature
                                        )
                                    }
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}