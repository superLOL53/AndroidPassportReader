package com.example.emrtdapplication.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.spongycastle.asn1.x509.Certificate
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.security.KeyFactory
import java.security.Security
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import kotlin.test.BeforeTest
import kotlin.test.Test

class CertificateTest {

    private val mockAPDUControl : APDUControl = mock()

    @BeforeTest
    fun setUp() {
        whenever(mockAPDUControl.checkResponse(any())).thenCallRealMethod()
        whenever(mockAPDUControl.removeRespondCodes(any())).thenCallRealMethod()
    }

    @Test
    fun testReadCertificate() {
        val path = FileInputStream("/home/oliver/StudioProjects/AndroidPassportReader/app/src/main/assets/CSCA/CSCAAUSTRIAcacert005.crt")
        val buffer = BufferedInputStream(path)
        val arr = buffer.readAllBytes()
        //val t = TLV(arr)
        //val tbs = t.getTLVSequence()!!.getTLVSequence()[0]
        //val algo = t.getTLVSequence()!!.getTLVSequence()[1]
        //val cert = Certificate(t)
        val cert = Certificate.getInstance(arr)
        //val cert = ASN1Sequence.getInstance(arr)
        cert.version
        //val iss = cert.tbsCert.serialNumber
        Security.addProvider(BouncyCastleProvider())
        for (p in Security.getProviders()) {
            println(p.name)
            for (k in p.keys) {
                if (k is String && k == "Alg.Alias.KeyFactory.1.2.840.10045.2.1") {
                    println(k)
                    val value = p[k]
                    println(value.toString())
                }
            }
        }
        val sign = Signature.getInstance(cert.signatureAlgorithm.algorithm.id, "BC")
        //val params = PublicKeyFactory.createKey(cert.subjectPublicKeyInfo)
        //val spe = EncodedKeySpec(cert.subjectPublicKeyInfo.encoded)
        val spec = X509EncodedKeySpec(cert.subjectPublicKeyInfo.encoded)
        val fac = KeyFactory.getInstance(cert.subjectPublicKeyInfo.algorithm.algorithm.id, "BC")
        val pub = fac.generatePublic(spec)
        //val pub = PublicKeyFactory.createKey(cert.subjectPublicKeyInfo.encoded)
        //val pub = PKCS8EncodedKeySpec(cert.subjectPublicKeyInfo.encoded)
        //val pub = com.example.emrtdapplication.utils.ECPublicKey(cert.subjectPublicKeyInfo, spec)
        sign.initVerify(pub)
        sign.update(cert.tbsCertificate.encoded)
        val isValid = sign.verify(cert.signature.bytes)
        println(isValid)
    }
}