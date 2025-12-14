package com.example.emrtdapplication.lds1

import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.security.KeyFactory
import java.security.Security
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import kotlin.test.BeforeTest
import kotlin.test.Test

class ActiveAuthenticationTest {

    @BeforeTest
    fun setUp() {

    }

    @Test
    fun testActiveAuthentication() {
        val prov = BouncyCastleProvider()
        Security.removeProvider("BC")
        Security.addProvider(prov)
        //val stream = assets.open("certificates/CSCAAUSTRIAcacert005.crt")
        val stream = FileInputStream("/home/oliver/StudioProjects/AndroidPassportReader/app/src/main/assets/certificates/CSCAAUSTRIAcacert005.crt")
        //val buffer = BufferedInputStream(stream)
        //val arr = buffer.readAllBytes()
        //val cert = org.spongycastle.asn1.x509.Certificate.getInstance(arr)
        val cf = java.security.cert.CertificateFactory.getInstance("X.509", "BC")
        val cert = cf.generateCertificate(stream)
        val isValid = cert.verify(cert.publicKey, "BC")
        /*val spec = X509EncodedKeySpec(cert.subjectPublicKeyInfo.encoded)
        val fac = KeyFactory.getInstance(cert.subjectPublicKeyInfo.algorithm.algorithm.id, "BC")
        val pub = fac!!.generatePublic(spec)
        val sign = Signature.getInstance(cert.signatureAlgorithm.algorithm.id, "BC")
        sign.initVerify(pub)
        sign.update(cert.tbsCertificate.encoded)
        val isValid = sign.verify(cert.signature.bytes)
        println(isValid)*/
    }
}