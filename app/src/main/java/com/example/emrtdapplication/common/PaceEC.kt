package com.example.emrtdapplication.common

import android.annotation.SuppressLint
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.x509.Certificate
import org.spongycastle.util.io.pem.PemObject
import java.io.FileInputStream
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * Test class for PACE EC and other stuff. Not relevant for actual implementation
 */
class PaceEC {

    @SuppressLint("NewApi")
    @OptIn(ExperimentalStdlibApi::class)
    fun paceProtocol() {
        // ECDH Generic Mapping Example
        /*val bp256 = ECNamedCurveTable.getByName("brainpoolp256r1")

        val nonce = byteArrayOf(0x3F, 0x00, 0xC4.toByte(), 0xD3.toByte(), 0x9D.toByte(), 0x15, 0x3F, 0x2B, 0x2A, 0x21, 0x4A, 0x07, 0x8D.toByte(), 0x89.toByte(), 0x9B.toByte(), 0x22)
        val tprk = byteArrayOf(0x7F, 0x4E, 0xF0.toByte(), 0x7B , 0x9E.toByte(), 0xA8.toByte(), 0x2F, 0xD7.toByte() , 0x8A.toByte(), 0xD6.toByte(), 0x89.toByte(), 0xB3.toByte() , 0x8D.toByte(), 0x0B, 0xC7.toByte(), 0x8C.toByte(), 0xF2.toByte(), 0x1F, 0x24, 0x9D.toByte() , 0x95.toByte(), 0x3B, 0xC4.toByte(), 0x6F , 0x4C, 0x6E, 0x19, 0x25 , 0x9C.toByte(), 0x01, 0x0F, 0x99.toByte())
        val tpubx = byteArrayOf(0x7A, 0xCF.toByte(), 0x3E, 0xFC.toByte(), 0x98.toByte(), 0x2E, 0xC4.toByte(), 0x55, 0x65, 0xA4.toByte(), 0xB1.toByte(), 0x55, 0x12, 0x9E.toByte(), 0xFB.toByte(), 0xC7.toByte(), 0x46, 0x50, 0xDC.toByte(), 0xBF.toByte(), 0xA6.toByte(), 0x36, 0x2D, 0x89.toByte(), 0x6F, 0xC7.toByte(), 0x02, 0x62, 0xE0.toByte(), 0xC2.toByte(), 0xCC.toByte(), 0x5E)
        val tpuby = byteArrayOf(0x54, 0x45, 0x52, 0xDC.toByte() , 0xB6.toByte(), 0x72, 0x52, 0x18 , 0x79, 0x91.toByte(), 0x15, 0xB5.toByte() , 0x5C, 0x9B.toByte(), 0xAA.toByte(), 0x6D , 0x9F.toByte(), 0x6B, 0xC3.toByte(), 0xA9.toByte() , 0x61, 0x8E.toByte(), 0x70, 0xC2.toByte(), 0x5A, 0xF7.toByte(), 0x17, 0x77, 0xA9.toByte(), 0xC4.toByte(), 0x92.toByte(), 0x2D)
        val cprk = byteArrayOf(0x49, 0x8F.toByte(), 0xF4.toByte(), 0x97.toByte(), 0x56, 0xF2.toByte(), 0xDC.toByte(), 0x15, 0x87.toByte(), 0x84.toByte(), 0x00, 0x41, 0x83.toByte(), 0x9A.toByte(), 0x85.toByte(), 0x98.toByte(), 0x2B, 0xE7.toByte(), 0x76, 0x1D, 0x14, 0x71, 0x5F, 0xB0.toByte(), 0x91.toByte(), 0xEF.toByte(), 0xA7.toByte(), 0xBC.toByte(), 0xE9.toByte(), 0x05, 0x85.toByte(), 0x60)
        val cpubx = byteArrayOf(0x00, 0x82.toByte(), 0x4F, 0xBA.toByte(), 0x91.toByte() , 0xC9.toByte(), 0xCB.toByte(), 0xE2.toByte(), 0x6B , 0xEF.toByte(), 0x53, 0xA0.toByte(), 0xEB.toByte() , 0xE7.toByte(), 0x34, 0x2A, 0x3B, 0xF1.toByte(), 0x78, 0xCE.toByte(), 0xA9.toByte() , 0xF4.toByte(), 0x5D, 0xE0.toByte(), 0xB7.toByte() , 0x0A, 0xA6.toByte(), 0x01, 0x65 , 0x1F, 0xBA.toByte(), 0x3F, 0x57)
        val cpuby = byteArrayOf(0x30, 0xD8.toByte(), 0xC8.toByte(), 0x79 , 0xAA.toByte(), 0xA9.toByte(), 0xC9.toByte(), 0xF7.toByte() , 0x39, 0x91.toByte(), 0xE6.toByte(), 0x1B , 0x58, 0xF4.toByte(), 0xD5.toByte(), 0x2E, 0xB8.toByte(), 0x7A, 0x0A, 0x0C , 0x70, 0x9A.toByte(), 0x49, 0xDC.toByte() , 0x63, 0x71, 0x93.toByte(), 0x63 , 0xCC.toByte(), 0xD1.toByte(), 0x3C, 0x54)
        val tpub = bp256.curve.createPoint(BigInteger(tpubx), BigInteger(tpuby))
        val pub = ECPublicKeyParameters(tpub, ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h))
        val priv = ECPrivateKeyParameters(BigInteger(tprk), ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h))
        val cpub = bp256.curve.createPoint(BigInteger(cpubx), BigInteger(cpuby))
        val params = ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h)
        val Sa = Crypto.calculateECDHAgreement(priv, pub)
        val H = Crypto.getECPointFromBigInteger(Sa, params)
        val g = Crypto.genericMappingEC(bp256.g, nonce, H)

        //println("p: " + bp256.curve.field.characteristic.toByteArray().toHexString())
        //println("a: " + bp256.curve.a)
        //println("b: " + bp256.curve.b)
        //println("gx: " + bp256.g.xCoord)
        //println("gy: " + bp256.g.yCoord)
        //println("n: " + bp256.n.toByteArray().toHexString())
        //println("cofactor: " + bp256.curve.cofactor)
        //println("Chip private key: " + cprk.toHexString())
        //println("cpubx: " + cpubx.toHexString())
        //println("cpuby: " + cpuby.toHexString())
        //println("Terminal private key: " + tprk.toHexString())
        //println("tpubx: " + tpubx.toHexString())
        //println("tpuby: " + tpuby.toHexString())
        //println("Shared key: " + tpub.multiply(BigInteger(cprk)).xCoord + ", " + tpub.multiply(BigInteger(cprk)).yCoord)
        //println("Shared key: " + cpub.multiply(BigInteger(tprk)))
        /*val ka = ECDHBasicAgreement()
        ka.init(ECPrivateKeyParameters(BigInteger(cprk), ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h)))
        val SA = ka.calculateAgreement(ECPublicKeyParameters(bp256.curve.createPoint(BigInteger(tpubx), BigInteger(tpuby)), ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h)))
        println("Sa: " + SA.toByteArray().toHexString())
        //val public = ECPublicKeyParameters(bp256.curve.createPoint(BigInteger(tpubx), BigInteger(tpuby)), ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h))
        //val point = public.q.getEncoded(true)
        val H = bp256.curve.decodePoint(byteArrayOf(0x03) + SA.toByteArray())
        println("H: $H")
        val g = bp256.g.multiply(BigInteger(nonce)).add(H)
        //println("Gx: " + gx.toByteArray().toHexString())
        println("Gy: " + g.normalize().affineYCoord.toBigInteger().toByteArray().toHexString())
        //println("X: " + point.toHexString())
        //println("Y: " + point.yCoord.toBigInteger().toByteArray().toHexString())*/
        val ecdom = ECDomainParameters(bp256.curve, g, bp256.n, bp256.h)
        val tpr = ECPrivateKeyParameters(BigInteger("A73FB703AC1436A18E0CFA5ABB3F7BEC7A070E7A6788486BEE230C4A22762595", 16), ecdom)
        val tpb = ECPublicKeyParameters(bp256.curve.createPoint(BigInteger("2DB7A64C0355044EC9DF190514C625CBA2CEA48754887122F3A5EF0D5EDD301C", 16), BigInteger("3556F3B3B186DF10B857B58F6A7EB80F20BA5DC7BE1D43D9BF850149FBB36462", 16)), ecdom)
        val cpr = ECPrivateKeyParameters(BigInteger("107CF58696EF6155053340FD633392BA81909DF7B9706F226F32086C7AFF974A", 16), ecdom)
        val cpb = ECPublicKeyParameters(bp256.curve.createPoint(BigInteger("9E880F842905B8B3181F7AF7CAA9F0EFB743847F44A306D2D28C1D9EC65DF6DB", 16), BigInteger("7764B22277A2EDDC3C265A9F018F9CB852E111B768B326904B59A0193776F094", 16)), ecdom)
        val ss = Crypto.calculateECDHAgreement(cpr, tpb)
        val macKey = Crypto.computeKey("SHA-1", ss.toByteArray(), 2).slice(0..15).toByteArray()

        /*ka.init(tpr)
        val ss = ka.calculateAgreement(cpb)
        println("Shared Secret: " + ss.toByteArray().toHexString())
        val md = MessageDigest.getInstance("SHA-1")
        md.update(ss.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x01))
        val encKey = md.digest().slice(0..15).toByteArray()
        md.update(ss.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x02))
        val macKey = md.digest().slice(0..15).toByteArray()
        println("Encryption Key: " + encKey.toHexString())
        println("MAC Key: " + macKey.toHexString())
        val cmac = CMac(AESEngine(), 64)
        cmac.init(KeyParameter(macKey))*/
        val token = BigInteger("7F494F060A04007F000702020402028641049E880F842905B8B3181F7AF7CAA9F0EFB743847F44A306D2D28C1D9EC65DF6DB7764B22277A2EDDC3C265A9F018F9CB852E111B768B326904B59A0193776F094", 16).toByteArray()
        val tok = Crypto.computeCMAC(token, macKey)
        println("Token: " + tok.toHexString())*/
        /*cmac.update(token, 0, token.size)
        val authToken = ByteArray(cmac.macSize)
        cmac.doFinal(authToken, 0)
        println("Token: " + authToken.toHexString())*/


        // DH General Mapping example
        /*val dh1024 = DHStandardGroups.rfc5114_1024_160
        val nonce = BigInteger("FA5B7E3E49753A0DB9178B7B9BD898C8", 16)
        val tprk = BigInteger("5265030F751F4AD18B08AC565FC7AC952E41618D", 16)
        val tpub = BigInteger(
            "23FB3749EA030D2A25B278D2A562047ADE3F01B74F17A15402CB7352CA7D2B3EB71C343DB13D1DEBCE9A3666DBCFC920B49174A602CB47965CAA73DC702489A44D41DB914DE9613DC5E98C94160551C0DF86274B9359BC0490D01B03AD54022DCB4F57FAD6322497D7A1E28D46710F46FE710FBBBC5F8BA166F4311975EC6C",
            16
        )
        val cprk = BigInteger("66DDAFEAC1609CB5B963BB0CB3FF8B3E047F336C", 16)
        val cpub = BigInteger(
            "78879F57225AA8080D52ED0FC890A4B2" +
                    "5336F699AA89A2D3A189654AF70729E6" +
                    "23EA5738B26381E4DA19E004706FACE7" +
                    "B235C2DBF2F38748312F3C98C2DD4882" +
                    "A41947B324AA1259AC22579DB93F7085" +
                    "655AF30889DBB845D9E6783FE42C9F24" +
                    "49400306254C8AE8EE9DD812A804C0B6" +
                    "6E8CAFC14F84D8258950A91B44126EE6", 16
        )
        val H = Crypto().calculateDHAgreement(DHPrivateKeyParameters(tprk, dh1024), DHPublicKeyParameters(cpub, dh1024))
        val g = Crypto().genericMappingDH(dh1024.g, nonce.toByteArray(), dh1024.p, H)
        println("g: " + g.toByteArray().toHexString())
        /*val ka = DHBasicAgreement()
        ka.init(DHPrivateKeyParameters(tprk, dh1024))
        val H = ka.calculateAgreement(DHPublicKeyParameters(cpub, dh1024))
        println("H: " + H.toByteArray().toHexString())
        val G = dh1024.g.modPow(nonce, dh1024.p).multiply(H).mod(dh1024.p)
        println("G: " + G.toByteArray().toHexString())*/
        val ktprk = BigInteger("0089CCD99B0E8D3B1F11E1296DCA68EC53411CF2CA", 16)
        val ktpub = BigInteger(
            "00907D89E2D425A178AA81AF4A7774EC" +
                    "8E388C115CAE67031E85EECE520BD911" +
                    "551B9AE4D04369F29A02626C86FBC674" +
                    "7CC7BC352645B6161A2A42D44EDA80A0" +
                    "8FA8D61B76D3A154AD8A5A51786B0BC0" +
                    "7147057871A922212C5F67F431731722" +
                    "36B7747D1671E6D692A3C7D40A0C3C5C" +
                    "E397545D015C175EB5130551EDBC2EE5D4", 16
        )
        val kcprk = BigInteger("00A5B780126B7C980E9FCEA1D4539DA1D27C342DFA", 16)
        val kcpub = BigInteger(
            "075693D9AE941877573E634B6E644F8E" +
                    "60AF17A0076B8B123D9201074D36152B" +
                    "D8B3A213F53820C42ADC79AB5D0AEEC3" +
                    "AEFB91394DA476BD97B9B14D0A65C1FC" +
                    "71A0E019CB08AF55E1F729005FBA7E3F" +
                    "A5DC41899238A250767A6D46DB974064" +
                    "386CD456743585F8E5D90CC8B4004B1F" +
                    "6D866C79CE0584E49687FF61BC29AEA1", 16
        )
        val params = DHParameters(dh1024.p, g, dh1024.q)
        val secret = Crypto().calculateDHAgreement(DHPrivateKeyParameters(kcprk, params), DHPublicKeyParameters(ktpub, params))
        val macKey = Crypto().computeKey("SHA-1", secret.toByteArray(), 2).slice(0..15).toByteArray()

        /*val kka = DHBasicAgreement()
        kka.init(DHPrivateKeyParameters(kcprk, DHParameters(dh1024.p, G, dh1024.q)))
        val secret = kka.calculateAgreement(
            DHPublicKeyParameters(
                ktpub,
                DHParameters(dh1024.p, G, dh1024.q)
            )
        )
        println("Shared Secret: " + secret.toByteArray().toHexString())
        val md = MessageDigest.getInstance("SHA-1")
        //md.update(secret.toByteArray())
        //val seed = md.digest()
        md.update(secret.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x01))
        val encKey = md.digest()
        println("AES encryption key: " + encKey.toHexString())
        md.update(secret.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x02))
        val k = md.digest().slice(0..15).toByteArray()
        println("AES MAC key: " + k.toHexString() + ", length: " + k.size*8)*/
        val m = BigInteger("7F49818F060A04007F00070202040102848180075693D9AE941877573E634B6E644F8E60AF17A0076B8B123D9201074D36152BD8B3A213F53820C42ADC79AB5D0AEEC3AEFB91394DA476BD97B9B14D0A65C1FC71A0E019CB08AF55E1F729005FBA7E3FA5DC41899238A250767A6D46DB974064386CD456743585F8E5D90CC8B4004B1F6D866C79CE0584E49687FF61BC29AEA1", 16).toByteArray()
        val token = Crypto().computeCMAC(m, macKey)
        println("Token: " + token.toHexString())*/
        /*val cmac = CMac(AESEngine(), 64)
        cmac.init(KeyParameter(k))
        cmac.update(m, 0, m.size)
        val token = ByteArray(cmac.macSize)
        cmac.doFinal(token, 0)
        println("Token: " + token.toHexString()*/

        //Generate and Print DH key pair
        /*val keyGenParams = DHKeyGenerationParameters(SecureRandom(), DHStandardGroups.rfc5114_1024_160)
        val keyPair = DHKeyPairGenerator()
        keyPair.init(keyGenParams)
        val keys = keyPair.generateKeyPair()
        val privateKey = keys.private as DHPrivateKeyParameters
        val publicKey = keys.public as DHPublicKeyParameters
        println("Private key: " + privateKey.x)
        println("Public key: " + publicKey.y)*/

        //DH IM example
        //val dh1024 = DHStandardGroups.rfc5114_1024_160
        //val s = BigInteger("FA5B7E3E 49753A0D B9178B7B 9BD898C8", 16)
        //val t = BigInteger("B3A6DB3C 870C3E99 245E0D1C 06B747DE", 16)

        // DH Integrated Mapping example
        /*val dh1024 = DHStandardGroups.rfc5114_1024_160
        val s = BigInteger("FA5B7E3E49753A0DB9178B7B9BD898C8", 16).toByteArray().slice(1..16).toByteArray()
        val t = BigInteger("B3A6DB3C870C3E99245E0D1C06B747DE", 16).toByteArray().slice(1..16).toByteArray()
        val R = Crypto.integratedMappingPRNG(s, t, dh1024.p)
        val g = Crypto.integratedMappingDH(R, dh1024.p, dh1024.q)
        /*val c0 = BigInteger("a668892a7c41e3ca739f40b057d85904", 16).toByteArray().slice(1..16).toByteArray()
        val c1 = BigInteger("a4e136ac725f738b01c1f60217c188ad", 16).toByteArray().slice(1..16).toByteArray()
        println("s: " + s.toHexString())
        println("t: " + t.toHexString())
        println("c0: " + c0.toHexString())
        println("c1: " + c1.toHexString())
        println("p bit count: " + dh1024.p.bitLength())
        val n = (dh1024.p.bitLength() + 64)/(8*s.size)+1
        println("n: $n")
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(t, "AES"), IvParameterSpec(ByteArray(s.size)))
        var enc = cipher.doFinal(s)
        println("enc: " + enc.toHexString())
        val x = ByteArray(s.size*n)
        for (i in 0..<n) {
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, "AES"), IvParameterSpec(ByteArray(s.size)))
            cipher.doFinal(c1, 0, c1.size, x, i*s.size)
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, "AES"), IvParameterSpec(ByteArray(s.size)))
            enc = cipher.doFinal(c0)
        }
        println("x: " + x.toHexString())
        val R = BigInteger(1, x).mod(dh1024.p)
        println("Rp: " + R.toByteArray().toHexString())
        val g = R.modPow(dh1024.p.dec().divide(dh1024.q), dh1024.p)
        println("g: " + g.toByteArray().toHexString())*/
        val cprk = BigInteger("020F018C7284B047FA7721A337EFB7ACB1440BB30C5252BD41C97C30C994BB78E9F0C5B32744D84017D21FFA6878396A6469CA283EF5C000DAF7D261A39AB8860ED4610AB5343390897AAB5A7787E4FAEFA0649C6A94FDF82D991E8E3FC332F5142729E7040A3F7D5A4D3CD75CBEE1F043C1CAD2DD484FEB4ED22B597D36688E", 16)
        val tpub = BigInteger("0F0CC62945A8029251FB7EF3C094E12EC68E4EF07F27CB9D9CD04C5C4250FAE0E4F8A951557E929AEB48E5C6DD47F2F5CD7C351A9BD2CD722C07EDE166770F08FFCB370262CF308DD7B07F2E0DA9CAAA1492344C852906919538C98A4BA4187E76CE9D87832386D319CE2E043C3343AEAE6EDBA1A9894DC5094D22F7FE1351D5", 16)
        val params = DHParameters(dh1024.p, g, dh1024.q)
        val secret = Crypto.calculateDHAgreement(DHPrivateKeyParameters(cprk, params), DHPublicKeyParameters(tpub, params))
        val encKey = Crypto.computeKey("SHA-1", secret.toByteArray(), 0x01).slice(0..15).toByteArray()
        val macKey = Crypto.computeKey("SHA-1", secret.toByteArray(), 2).slice(0..15).toByteArray()

        /*val ba = DHBasicAgreement()
        ba.init(DHPrivateKeyParameters(cprk, DHParameters(dh1024.p, g, dh1024.q)))
        val secret = ba.calculateAgreement(DHPublicKeyParameters(tpub, DHParameters(dh1024.p, g, dh1024.q)))
        println("secret: " + secret.toByteArray().toHexString())
        val md = MessageDigest.getInstance("SHA-1")
        md.update(secret.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x01))
        val encKey = md.digest().slice(s.indices).toByteArray()
        md.update(secret.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x02))
        val macKey = md.digest().slice(s.indices).toByteArray()
        println("KEnc: " + encKey.toHexString())
        println("KMAC: " + macKey.toHexString())*/
        val m = BigInteger("7F49818F060A04007F000702020403028481800F0CC62945A8029251FB7EF3C094E12EC68E4EF07F27CB9D9CD04C5C4250FAE0E4F8A951557E929AEB48E5C6DD47F2F5CD7C351A9BD2CD722C07EDE166770F08FFCB370262CF308DD7B07F2E0DA9CAAA1492344C852906919538C98A4BA4187E76CE9D87832386D319CE2E043C3343AEAE6EDBA1A9894DC5094D22F7FE1351D5", 16).toByteArray()
        /*val cmac = CMac(AESEngine(), 64)
        cmac.init(KeyParameter(macKey))
        cmac.update(m, 0, m.size)
        val token = ByteArray(cmac.macSize)
        cmac.doFinal(token, 0)
        println("Token: " + token.toHexString())*/
        val token = Crypto.computeCMAC(m, macKey)
        println("Token: " + token.toHexString())*/

        //ECDH Integrated Mapping
        /*val bp256 = ECNamedCurveTable.getByName("brainpoolp256r1")
        val s = BigInteger("2923BE84E16CD6AE529049F1F1BBE9EB", 16).toByteArray()
        val t = BigInteger("5DD4CBFC96F5453B130D890A1CDBAE32", 16).toByteArray()
        val R = Crypto.integratedMappingPRNG(s, t, bp256.curve.field.characteristic)
        val x = Crypto.integratedMappingEC(R, bp256.curve.a.toBigInteger(), bp256.curve.b.toBigInteger(), bp256.curve.field.characteristic, bp256.curve.cofactor)
        //println("x: " + x.toByteArray().toHexString())
        val g = Crypto.getECPointFromBigInteger(x, ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h))
        //println("y: " + g.yCoord.toBigInteger().toByteArray().toHexString())

        /*val c0 = BigInteger("a668892a7c41e3ca739f40b057d85904", 16).toByteArray().slice(1..16).toByteArray()
        val c1 = BigInteger("a4e136ac725f738b01c1f60217c188ad", 16).toByteArray().slice(1..16).toByteArray()
        println("s: " + s.toHexString())
        println("t: " + t.toHexString())
        println("c0: " + c0.toHexString())
        println("c1: " + c1.toHexString())
        println("p bit count: " + bp256.curve.field.characteristic.bitLength())
        val n = (bp256.curve.field.characteristic.bitLength() + 64)/(8*s.size)+1
        println("n: $n")
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(t, "AES"), IvParameterSpec(ByteArray(s.size)))
        var enc = cipher.doFinal(s)
        println("enc: " + enc.toHexString())
        val x = ByteArray(s.size*n)
        for (i in 0..<n) {
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, "AES"), IvParameterSpec(ByteArray(s.size)))
            cipher.doFinal(c1, 0, c1.size, x, i*s.size)
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, "AES"), IvParameterSpec(ByteArray(s.size)))
            enc = cipher.doFinal(c0)
        }
        println("x: " + x.toHexString())
        val R = BigInteger(1, x).mod(bp256.curve.field.characteristic)
        println("Rp: " + R.toByteArray().toHexString())
        val alpha = R.pow(2).negate().mod(bp256.curve.field.characteristic)
        val X2 = bp256.curve.b.toBigInteger().multiply(bp256.curve.a.toBigInteger().modInverse(bp256.curve.field.characteristic)).negate().multiply(BigInteger.ONE.add(alpha.add(alpha.pow(2)).modInverse(bp256.curve.field.characteristic))).mod(bp256.curve.field.characteristic)
        val X3 = alpha.multiply(X2).mod(bp256.curve.field.characteristic)
        val h2 = X2.pow(3).add(bp256.curve.a.toBigInteger().multiply(X2)).add(bp256.curve.b.toBigInteger()).mod(bp256.curve.field.characteristic)
        val h3 = X3.pow(3).add(bp256.curve.a.toBigInteger().multiply(X3)).add(bp256.curve.b.toBigInteger()).mod(bp256.curve.field.characteristic)
        val U = R.pow(3).multiply(h2).mod(bp256.curve.b.toBigInteger())
        val A = h2.modPow(bp256.curve.field.characteristic.dec().subtract(bp256.curve.field.characteristic.inc().divide(
            BigInteger("4")
        )), bp256.curve.field.characteristic)
        val xp : BigInteger
        val y : BigInteger
        if (A.pow(2).multiply(h2).mod(bp256.curve.field.characteristic) == BigInteger.ONE) {
            println("x: " + X2.toByteArray().toHexString())
            xp = X2
            y = A.multiply(h2).mod(bp256.curve.field.characteristic)
            println("y: " + y.toByteArray().toHexString())
        } else {
            println("x: " + X3.toByteArray().toHexString())
            xp = X3
            y = A.multiply(U).mod(bp256.curve.field.characteristic)
            println("y: " + y.toByteArray().toHexString())
        }*/
        val cprk = BigInteger("107CF58696EF6155053340FD633392BA81909DF7B9706F226F32086C7AFF974A", 16)
        val tpubx = BigInteger("0089CBA23FFE96AA18D824627C3E934E54A9FD0B87A95D1471DC1C0ABFDCD640D4", 16)
        val tpuby = BigInteger("6755DE9B7B778280B6BEBD57439ADFEB0E21FD4ED6DF42578C13418A59B34C37", 16)
        val par = ECDomainParameters(bp256.curve, g, bp256.n, bp256.h)
        val priv = ECPrivateKeyParameters(cprk, par)
        val pub = ECPublicKeyParameters(bp256.curve.createPoint(tpubx, tpuby), par)
        val k = Crypto.calculateECDHAgreement(priv, pub)
        val encKey = Crypto.computeKey("SHA-1", k.toByteArray(), 1).slice(0..15).toByteArray()
        val macKey = Crypto.computeKey("SHA-1", k.toByteArray(), 2).slice(0..15).toByteArray()

        /*val ka = ECDHBasicAgreement()
        ka.init(priv)
        val k = ka.calculateAgreement(pub)
        println("k: " + k.toByteArray().toHexString())
        val md = MessageDigest.getInstance("SHA-1")
        md.update(k.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x01))
        val encKey = md.digest().slice(0..15).toByteArray()
        println(md.digestLength)
        println("EncKey: " + encKey.toHexString())
        md.update(k.toByteArray() + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, 0x02))
        val macKey = md.digest().slice(0..15).toByteArray()
        println("MACKey: " + macKey.toHexString())*/
        val tok = BigInteger("7F494F060A04007F0007020204040286410489CBA23FFE96AA18D824627C3E934E54A9FD0B87A95D1471DC1C0ABFDCD640D46755DE9B7B778280B6BEBD57439ADFEB0E21FD4ED6DF42578C13418A59B34C37", 16).toByteArray()
        /*val cmac = CMac(AESEngine(), 64)
        cmac.init(KeyParameter(macKey))
        cmac.update(tok, 0, tok.size)
        val token = ByteArray(cmac.macSize)
        cmac.doFinal(token, 0)
        println("AuthToken: " + token.toHexString())*/
        val token = Crypto.computeCMAC(tok, macKey)
        println("Token: " + token.toHexString())*/

        //ECCAM
        /*val crypto = Crypto()
        val bp256 = ECNamedCurveTable.getByName("brainpoolp256r1")
        val ss = java.math.BigInteger("658B860BC94DF6F044FCE6D5C82CF8E5", 16).toByteArray()
        val cprk = BigInteger("009E56A6B59C95D06ECE5CD10F983BB2F4F1943528E577F23881D89D8C3BBEE0AA", 16)
        val tpubx = BigInteger("7F1D410ADB7DDB3B84BF1030800981A9105D7457B4A3ADE002384F3086C67EDE", 16)
        val tpuby = BigInteger("1AB889104A27DB6D842B019020FBF3CEACB0DC627F7BDCAC29969E19D0E553C1", 16)
        var pub = ECPublicKeyParameters(bp256.curve.createPoint(tpubx, tpuby), ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h))
        var priv = ECPrivateKeyParameters(cprk, ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h))
        val x = crypto.calculateECDHAgreement(priv, pub)
        val h = crypto.getECPointFromBigInteger(x, ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h))
        val g = crypto.genericMappingEC(bp256.g, ss, h)
        val tprk = BigInteger("76ECFDAA9841C323A3F5FC5E88B88DB3EFF7E35EBF57A7E6946CB630006C2120", 16)
        val cpubx = BigInteger("02AD566F3C6EC7F9324509AD50A51FA52030782A4968FCFEDF737DAEA9933331", 16)
        val cpuby = BigInteger("11C3B9B4C2287789BD137E7F8AA882E2A3C633CCD6ECC2C63C57AD401A09C2E1", 16)
        val params = ECDomainParameters(bp256.curve, g, bp256.n, bp256.h)
        priv = ECPrivateKeyParameters(tprk, params)
        pub = ECPublicKeyParameters(bp256.curve.createPoint(cpubx, cpuby), params)
        val secret = crypto.calculateECDHAgreement(priv, pub)
        val encKey = crypto.computeKey("SHA-1", secret.toByteArray(), 1).slice(0..15).toByteArray()
        val macKey = crypto.computeKey("SHA-1", secret.toByteArray(), 2).slice(0..15).toByteArray()
        val t = BigInteger("7F494F060A04007F0007020204060286410402AD566F3C6EC7F9324509AD50A51FA52030782A4968FCFEDF737DAEA993333111C3B9B4C2287789BD137E7F8AA882E2A3C633CCD6ECC2C63C57AD401A09C2E1", 16).toByteArray()
        val token = crypto.computeCMAC(t, macKey)
        println("EncKey: " + encKey.toHexString())
        println("MacKey: " + macKey.toHexString())
        println("Token: " + token.toHexString())
        var c = Cipher.getInstance("AES/CBC/NoPadding")

        c.init(Cipher.ENCRYPT_MODE, SecretKeySpec(encKey, "AES"), IvParameterSpec(ByteArray(16)))
        val iv = c.doFinal(byteArrayOf(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1))
        //val iv = Crypto.encrypt3DES(byteArrayOf(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1), encKey + encKey.slice(0..7).toByteArray())
        println("iv: " + iv.toHexString())
        val info = BigInteger("3062060904007F000702020102305230" +
                "0C060704007F0007010202010D034200" +
                "041872709494399E7470A6431BE25E83" +
                "EEE24FEA568C2ED28DB48E05DB3A610D" +
                "C884D256A40E35EFCB59BF6753D3A489" +
                "D28C7A4D973C2DA138A6E7A4A08F68E1" +
                "6F02010D", 16).toByteArray()
        val input = ASN1InputStream(info)
        val seq = DERSequence.getInstance(input.readAllBytes())
        val publicKeyInfo = SubjectPublicKeyInfo.getInstance(DERSequence.getInstance(seq.getObjectAt(1)))
        /*val string = TreeSet<String>()
        for (p in Security.getProviders()) {
            p.services.stream().filter {it.type.equals("Cipher")}
            .map {it.algorithm}
                .forEach { string.add(it) }
        }
        string.forEach { println(it) }*/
        //val ec = org.spongycastle.crypto.ec.ECElGamalDecryptor()
        //ec.init(ECPublicKeyParameters())
        //ec.init()
        //Security.addProvider()
        //pub = ECPublicKeyParameters(bp256.curve.decodePoint(BigInteger("00041872709494399E7470A6431BE25E83EEE24FEA568C2ED28DB48E05DB3A610DC884D256A40E35EFCB59BF6753D3A489D28C7A4D973C2DA138A6E7A4A08F68E16F0201", 16).toByteArray()), params)

        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(encKey, "AES"), IvParameterSpec(iv))
        val decrypted = cipher.doFinal(BigInteger("1EEA964DAAE372AC990E3EFDE6333353" +
                "BFC89A6704D93DA8798CF77F5B7A54BD" +
                "10CBA372B42BE0B9B5F28AA8DE2F4F92", 16).toByteArray()).slice(0..31).toByteArray()
        println("Decrypted CA Data: " + decrypted.toHexString())
        println("CA Public Key: " + publicKeyInfo.publicKeyData.bytes.toHexString())
        val domainParams = ECDomainParameters(bp256.curve, bp256.g, bp256.n, bp256.h)
        val ka = ECDHBasicAgreement()
        ka.init(ECPrivateKeyParameters(BigInteger(decrypted), domainParams))
        val s = ka.calculateAgreement(ECPublicKeyParameters(bp256.curve.decodePoint(publicKeyInfo.publicKeyData.bytes), domainParams))
        println(crypto.getECPointFromBigInteger(s, domainParams))*/

        //Decrypting nonce
        /*val z = byteArrayOf(0x85.toByte(), 0x4D, 0x8D.toByte(), 0xF5.toByte(), 0x82.toByte(), 0x7F, 0xA6.toByte(), 0x85.toByte(), 0x2D, 0x1A, 0x4F, 0xA7.toByte(), 0x01, 0xCD.toByte(), 0xDD.toByte(), 0xCA.toByte())
        val mrz = "C11T002JM496081222310314"
        //val mrz = BigInteger("7E2D2A41C74EA0B38CD36F863939BFA8E9032AAD", 16)
        val crypto = Crypto()
        //val k = crypto.hash("SHA-1", mrz.toByteArray())
        val k = byteArrayOf(0x00) + java.math.BigInteger("36D272F5C350ACAC50C3F572D23600", 16).toByteArray()
        println(k.toHexString())
        val kpi = crypto.computeKey("SHA-1", k, 1, true).slice(0..15).toByteArray()
        //kpi += kpi.slice(0..7).toByteArray()
        println(kpi.toHexString())
        println(crypto.computeKey("SHA-1", k, 2, true).slice(0..15).toByteArray().toHexString())*/
        //val key = SecretKeySpec(kpi, "AES")
        //val c = Cipher.getInstance("AES/CBC/NoPadding")
        //val i = IvParameterSpec(ByteArray(16))
        //c.init(Cipher.DECRYPT_MODE, key, i)
        //val s = c.doFinal(z)
        //println(s.toHexString())

        val csca = FileInputStream("sampledata/CSCAAUSTRIAcacert005.crt")
        val certs = csca.readAllBytes()
        val input = ASN1InputStream(certs)
        val seq = DERSequence.getInstance(input.readObject())
        //println(ASN1Dump.dumpAsString(seq))
        val tag = DERSequence.getInstance(seq.getObjectAt(0))
        val ml = Certificate.getInstance(seq)
        val kf = KeyFactory.getInstance(ml.subjectPublicKeyInfo.algorithm.algorithm.id)
        //val pubKey = PublicKeyFactory.createKey(ml.subjectPublicKeyInfo.encoded)
        val po = PemObject(ml.subjectPublicKeyInfo.algorithm.algorithm.id, ml.subjectPublicKeyInfo.publicKeyData.bytes)
        println(po.content.toHexString())
        //ml.subjectPublicKeyInfo.parsePublicKey().encoded
        val pubKey = kf.generatePublic(X509EncodedKeySpec(po.content))
        val sign = Signature.getInstance(ml.signatureAlgorithm.algorithm.id)
        sign.initVerify(pubKey)
        sign.update(ml.tbsCertificate.encoded)
        sign.verify(ml.signature.bytes)

        //val cert = CertificateFactory.getInstance("X.509")
        //val ml = cert.generateCertificate(csca)
        //ml.verify(ml.publicKey)

   }
}