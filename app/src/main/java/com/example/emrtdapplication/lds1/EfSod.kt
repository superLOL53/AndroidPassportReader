package com.example.emrtdapplication.lds1

import android.content.Context
import android.text.Layout
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.DERTaggedObject
import org.spongycastle.asn1.cms.SignedData
import org.spongycastle.asn1.icao.LDSSecurityObject
import org.spongycastle.asn1.x509.Certificate
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.KeyFactory
import java.security.Security
import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.spec.X509EncodedKeySpec

/**
 * Implements the EF.SOD file and inherits from [ElementaryFileTemplate]
 *
 * @property apduControl Used for sending and receiving APDUs
 * @property efTag The tag associated with the file
 * @property rawFileContent The content of the file as a ByteArray
 * @property shortEFIdentifier The short EF id of the file
 * @property ldsSecurityObject The LDS Security Object contained in the file
 * @property documentSignerCertificate The Document Signer Certificate contained in the file
 * @property certificate The file content as a certificate
 * @property isValid Indicates if the [documentSignerCertificate] was signed by the CSCA of the same entity
 */
class EfSod(apduControl: APDUControl): ElementaryFileTemplate(apduControl) {

    override val efTag: Byte = 77
    //override var rawFileContent: ByteArray? = BigInteger("778206453082064106092a864886f70d010702a08206323082062e020103310f300d06096086480165030402010500308201120606678108010101a0820106048201023081ff020100300d060960864801650304020105003081ea302502010104207c6309b203030a6e01f6e4667f61115d70e06b04ca45bd821bf3a3dae3dcae723025020102042066a9519dc6e35fd0fffa1fd43e55cb175c5180be2cc38b08657ce3c38bff04ce302502010304201e0f87d45a7060bd2951f956e40dd3af9f4e839a11a8b12c716e6d3dfb5e849e302502010b0420d362078d79dfcf06f36241db4ebe54df42117a24ba76dc71f49874fdd2078bc0302502010c0420d4dccf771a43cf16db00b762db61bae9b66f3917dead3364fd867032fb295d22302502010e042064cdd59244e65a4ae86490841e77bcbfd5a845f95ac362c0fb46bb6fa6fb2776a08203db308203d73082035ea003020102020870c67606a063fc20300a06082a8648ce3d040303303f310b3009060355040613024154310b3009060355040a0c024756310c300a060355040b0c03424d493115301306035504030c0c435343412d41555354524941301e170d3136303530333132343335395a170d3236303830373132343335395a3054310b3009060355040613024154310b3009060355040a0c024756310c300a060355040b0c03424d49310f300d060355040513063030333030393119301706035504030c1044532d415553545249412d6550617373308201333081ec06072a8648ce3d02013081e0020101302c06072a8648ce3d0101022100a9fb57dba1eea9bc3e660a909d838d726e3bf623d52620282013481d1f6e5377304404207d5a0975fc2c3057eef67530417affe7fb8055c126dc5c6ce94a4b44f330b5d9042026dc5c6ce94a4b44f330b5d9bbd77cbf958416295cf7e1ce6bccdc18ff8c07b60441048bd2aeb9cb7e57cb2c4b482ffc81b7afb9de27e1e3bd23c23a4453bd9ace3262547ef835c3dac4fd97f8461a14611dc9c27745132ded8e545c1d54c72f046997022100a9fb57dba1eea9bc3e660a909d838d718c397aa3b561a6f7901e0e82974856a702010103420004300ac083eb6644efc1e868ffc7bb5d9f8410503f1a60ea9baef7938c9de2628d39a7c9b0c0ec8aad3d9692361bba043bf002811fb3de94555d4696af46fae052a38201513082014d301d0603551d0e04160414daf72e8a2a12531c661c494efec09ac8f1ac6f2a301f0603551d23041830168014ff8dea86af18eee58ba2d6ba8cfaab39a169af5b301a0603551d1004133011810f32303231303530333132343335395a30160603551d20040f300d300b06092a28000a0102010101303e0603551d1f043730353033a031a02f862d687474703a2f2f7777772e626d692e67762e61742f637363612f63726c2f43534341415553545249412e63726c300e0603551d0f0101ff0404030207803015060767810801010602040a3008020100310313015030370603551d120430302ea410300e310c300a06035504070c03415554861a687474703a2f2f7777772e626d692e67762e61742f637363612f30370603551d110430302ea410300e310c300a06035504070c03415554861a687474703a2f2f7777772e626d692e67762e61742f637363612f300a06082a8648ce3d0403030367003064023056bfba08fb7d7115eca698622a5170b096ea4053be3243bd1a7ea337f56c4763a71aa96961a6e7ab7b99f04d0e888ffd0230463ff911d346b42f3671b96b8ea1b6179c996477f2bf037cf0de435d973609f4d3e2b2989af9786a413ac8fca78f497a318201213082011d020101304b303f310b3009060355040613024154310b3009060355040a0c024756310c300a060355040b0c03424d493115301306035504030c0c435343412d41555354524941020870c67606a063fc20300d06096086480165030402010500a066301506092a864886f70d01090331080606678108010101301c06092a864886f70d010905310f170d3136303830333036333535315a302f06092a864886f70d010904312204209ec7c7e77de097cc518ebc523e3f8c6ca84ecd324db49b20e4d35a99817b6e2c300c06082a8648ce3d040302050004463044022058e413126d9d73c3076fd0637d2127f37dea0063a0bf1d723a999f2e3cf2a959022077f28b47ca2c8cbc93fac08b8a85286bc945e5de25c4a5e6943a2a87f5dd866f", 16).toByteArray()
    override var rawFileContent : ByteArray? = BigInteger("7782064f3082064b06092a864886f70d010702a082063c30820638020103310f300d06096086480165030402010500308201120606678108010101a0820106048201023081ff020100300d060960864801650304020105003081ea302502010104203c1516a24d068875329b15a01ca31f14bf5dd1223ce1421c7e498997289348f0302502010204203a995b09c2a19254a381331d4be0059cbe45104fc1f5842b2ee213e263d6f07a30250201030420b75b9a69d75542e3c901b875607ca2418f2ca512785e7ae37afaee62025496fe302502010b0420d362078d79dfcf06f36241db4ebe54df42117a24ba76dc71f49874fdd2078bc0302502010c0420198b38ced4caf30787d55b5a4934cec286d185d5a8e945cc37ca2e810b0aebc9302502010e04202ac07eacbd062d1d98bd4276bf07960bb118f788dac1ac6ac06a0efb06a1e229a08203e4308203e030820366a0030201020208178c141b7154e925300a06082a8648ce3d040303303f310b3009060355040613024154310b3009060355040a0c024756310c300a060355040b0c03424d493115301306035504030c0c435343412d41555354524941301e170d3235303231303039303735395a170d3335303531373039303735395a3054310b3009060355040613024154310b3009060355040a0c024756310c300a060355040b0c03424d49310f300d060355040513063030353030333119301706035504030c1044532d415553545249412d654d525444308201333081ec06072a8648ce3d02013081e0020101302c06072a8648ce3d0101022100a9fb57dba1eea9bc3e660a909d838d726e3bf623d52620282013481d1f6e5377304404207d5a0975fc2c3057eef67530417affe7fb8055c126dc5c6ce94a4b44f330b5d9042026dc5c6ce94a4b44f330b5d9bbd77cbf958416295cf7e1ce6bccdc18ff8c07b60441048bd2aeb9cb7e57cb2c4b482ffc81b7afb9de27e1e3bd23c23a4453bd9ace3262547ef835c3dac4fd97f8461a14611dc9c27745132ded8e545c1d54c72f046997022100a9fb57dba1eea9bc3e660a909d838d718c397aa3b561a6f7901e0e82974856a7020101034200044666a9ed1f14e7e688cae6ebf8dab309793446cf90bc04f12c550348e095167669e5ee9df066eacc59de26e4014b9eefe9c79d7bd0d7b1d42b1d9e0b4603758aa382015930820155301d0603551d0e04160414986c7279d421db4ae7664e6e2a4860b81e0d9719301f0603551d23041830168014eeb6b3c86b867ba68e31a0b2bbe1b86d9b1c4ae1301a0603551d1004133011810f32303235303531363039303735395a30160603551d20040f300d300b06092a28000a0102010101303e0603551d1f043730353033a031a02f862d687474703a2f2f7777772e626d692e67762e61742f637363612f63726c2f43534341415553545249412e63726c300e0603551d0f0101ff04040302078030370603551d120430302ea410300e310c300a06035504070c03415554861a687474703a2f2f7777772e626d692e67762e61742f637363612f30370603551d110430302ea410300e310c300a06035504070c03415554861a687474703a2f2f7777772e626d692e67762e61742f637363612f301d06076781080101060204123010020100310b1301501302415213024944300a06082a8648ce3d040303036800306502303b0e4242987a39e10e9bd7a9a3b07a95347200dc49393d6657da164a715c553f4a0317cf0cbb0bd1fd27effeef6db02c0231008aa45443ec2ba122fc7a244405e82212cacc05f82c26de9e54723444427d7e9a958932bd3c9a005d1b1a84056cbe9d5f318201223082011e020101304b303f310b3009060355040613024154310b3009060355040a0c024756310c300a060355040b0c03424d493115301306035504030c0c435343412d415553545249410208178c141b7154e925300d06096086480165030402010500a066301506092a864886f70d01090331080606678108010101301c06092a864886f70d010905310f170d3235303430323137353435365a302f06092a864886f70d01090431220420a370ab3c2b2617b982672b731d7bbb9a8681cc66c5f9d71aa588811e81b7b381300c06082a8648ce3d040302050004473045022022dafd8af33da697ebef7d95c792d5d9ee86c2ae20363ff143f644e06a64b6fa0221009d5fe77de146e5a1862d0e29fda8e5702274bda9b8981a669bc38c4c48e8cb69", 16).toByteArray()

    override val shortEFIdentifier: Byte = 0x1D
    var ldsSecurityObject : LDSSecurityObject? = null
        private set
    private var certificate : SignedData? = null
    private var documentSignerCertificate : Certificate? = null
    var isValid = false
        private set

    private var cert : java.security.cert.Certificate? = null

    /**
     * Parses the file content. Creates the [certificate], [documentSignerCertificate] and [ldsSecurityObject]
     * @return [SUCCESS] or [FAILURE]
     */
    override fun parse(): Int {
        if (rawFileContent == null) {
            return SUCCESS
        }
        try {
            val input = ASN1InputStream(rawFileContent!!.slice(contentStart..<rawFileContent!!.size).toByteArray())
            val cert = DERTaggedObject.getInstance(DERSequence.getInstance(input.readAllBytes()).getObjectAt(1))
            certificate = SignedData.getInstance(cert.`object`)
            val content = certificate!!.encapContentInfo.content.toASN1Primitive().encoded
            documentSignerCertificate = Certificate.getInstance(certificate!!.certificates.getObjectAt(0).toASN1Primitive().encoded)
            this.cert = CertificateFactory.getInstance("X.509", "BC").generateCertificate(
                ByteArrayInputStream(documentSignerCertificate!!.encoded))
            ldsSecurityObject = LDSSecurityObject.getInstance(ASN1InputStream(TLV(content).getValue()!!).readAllBytes())
            return SUCCESS
        } catch (_: Exception) {
            return FAILURE
        }
    }

    override fun createViews(context: Context, parent: Layout) {
        //TODO: Implement
    }

    /**
     * Checks the hashes of the files DG1 to DG16.
     */
    fun checkHashes(dgs : Map<Byte, ElementaryFileTemplate>) {
        if (ldsSecurityObject == null) {
            return
        }
        val groupHashes = ldsSecurityObject!!.datagroupHash
        for (i in groupHashes.indices) {
            val id = groupHashes[i].dataGroupNumber.toByte()
            val ef = dgs[id] ?: continue
            val hash1 = ef.hash(ldsSecurityObject!!.digestAlgorithmIdentifier.algorithm.id) ?: continue
            val hash2 = groupHashes[i].dataGroupHashValue.octets
            if (hash1.contentEquals(hash2)) {
                ef.matchHash = true
            }
        }
    }

    /**
     * Validates the [documentSignerCertificate] by checking the signature in it with the CSCA
     * @param csca The trusted root certificate from the State/organization that issued the eMRTD
     * @return [SUCCESS] or [FAILURE]
     */
    fun passiveAuthentication(csca: Certificate) : Int {
        /*try {
            val spec = X509EncodedKeySpec(csca.subjectPublicKeyInfo.encoded)
            val fac = KeyFactory.getInstance(csca.subjectPublicKeyInfo.algorithm.algorithm.id, "BC")
            val pub = fac!!.generatePublic(spec)
            val sign = Signature.getInstance(csca.signatureAlgorithm.algorithm.id, "BC")
            sign.initVerify(pub)
            sign.update(csca.tbsCertificate.encoded)
            val isValid = sign.verify(csca.signature.bytes)
            println(isValid)
        } catch (e : Exception) {
            println(e)
        }*/

        if (documentSignerCertificate == null) return FAILURE
        if (csca.issuer != documentSignerCertificate!!.issuer) {
            return FAILURE
        }
        val providers = Security.getProviders()
        val spec = X509EncodedKeySpec(documentSignerCertificate!!.subjectPublicKeyInfo.encoded)
        for (p in providers) {
            try {
                val fac = KeyFactory.getInstance(documentSignerCertificate!!.subjectPublicKeyInfo.algorithm.algorithm.id, p.name)
                val pub = fac!!.generatePublic(spec)
                cert?.verify(pub)
                /*val sign = Signature.getInstance(documentSignerCertificate!!.signatureAlgorithm.algorithm.id, p.name)
                sign.initVerify(pub)
                sign.update(documentSignerCertificate!!.tbsCertificate.encoded)
                isValid = sign.verify(documentSignerCertificate!!.signature.bytes)
                return if (isValid) {
                    SUCCESS
                } else {
                    FAILURE
                }*/
                return SUCCESS
            } catch (e : Exception) {
                println(e)
            }
        }
        return FAILURE
    }
}