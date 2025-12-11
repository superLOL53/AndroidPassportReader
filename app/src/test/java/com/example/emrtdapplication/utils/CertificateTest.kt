package com.example.emrtdapplication.utils

import android.content.res.AssetManager
import android.content.res.Resources
import com.example.emrtdapplication.EMRTD
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.ess.SigningCertificate
import org.spongycastle.asn1.x509.Certificate
import org.spongycastle.asn1.x509.RSAPublicKeyStructure
import org.spongycastle.asn1.x509.TBSCertificate
import org.spongycastle.asn1.x509.X509CertificateStructure
import org.spongycastle.crypto.Signer
import org.spongycastle.crypto.encodings.PKCS1Encoding
import org.spongycastle.crypto.engines.RSAEngine
import org.spongycastle.crypto.io.SignerInputStream
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.tls.SignatureAlgorithm
import org.spongycastle.crypto.util.PublicKeyFactory
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.security.Key
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.spec.EncodedKeySpec
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
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
        val path = FileInputStream("/home/oliver/StudioProjects/AndroidPassportReader/app/src/main/assets/certificates/CSCAAUSTRIAcacert005.crt")
        val buffer = BufferedInputStream(path)
        val arr = buffer.readAllBytes()
        val t = TLV(arr)
        val tbs = t.getTLVSequence()!!.getTLVSequence()[0]
        val algo = t.getTLVSequence()!!.getTLVSequence()[1]
        //val cert = Certificate(t)
        val cert = Certificate.getInstance(arr)
        //val cert = ASN1Sequence.getInstance(arr)
        cert.version
        //val iss = cert.tbsCert.serialNumber
        val sign = Signature.getInstance(cert.signatureAlgorithm.algorithm.id)
        val params = PublicKeyFactory.createKey(cert.subjectPublicKeyInfo)
        //val spe = EncodedKeySpec(cert.subjectPublicKeyInfo.encoded)
        val spec = X509EncodedKeySpec(cert.subjectPublicKeyInfo.encoded)
        //val fac = KeyFactory.getInstance(cert.subjectPublicKeyInfo.algorithm.algorithm.id)
        //val pub = fac.generatePublic(spec)
        //val pub = PublicKeyFactory.createKey(cert.subjectPublicKeyInfo.encoded)
        //val pub = PKCS8EncodedKeySpec(cert.subjectPublicKeyInfo.encoded)
        val pub = com.example.emrtdapplication.utils.ECPublicKey(cert.subjectPublicKeyInfo, spec)
        sign.initVerify(pub)
        sign.update(cert.tbsCertificate.encoded)
        //val isValid = sign.verify(cert.signature.bytes)
        //TBSCertificate
        val ec = ECDSASigner()
        ec.init(false, params)
        val isValid = ec.verifySignature(cert.tbsCertificate.encoded, pub.w.affineX, pub.w.affineY)

    }
}