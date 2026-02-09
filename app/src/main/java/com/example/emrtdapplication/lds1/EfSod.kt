package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1GeneralizedTime
import org.spongycastle.asn1.x509.Certificate
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1UTCTime
import org.spongycastle.asn1.DEROctetString
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.DERTaggedObject
import org.spongycastle.asn1.cms.Attributes
import org.spongycastle.asn1.cms.SignedData
import org.spongycastle.asn1.cms.SignerInfo
import org.spongycastle.asn1.icao.LDSSecurityObject
import java.io.ByteArrayInputStream
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Date

/**
 * Implements the EF.SOD file and performs passive authentication against provided CSCAs
 *
 * @property efTag The tag associated with the file
 * @property rawFileContent The content of the file as a ByteArray
 * @property shortEFIdentifier The short EF id of the file
 * @property ldsSecurityObject The LDS Security Object contained in the file
 * @property documentSignerCertificate The Document Signer Certificate contained in the file
 * @property certificate The file content as a certificate
 * @property isValid Indicates if the [documentSignerCertificate] was signed by the CSCA of the same entity
 * @property cert The decoded Document Signer Certificate read from the eMRTD
 * @property isDocumentSignerCertificateValid If the signature of the Document Signer Certificate is valid
 * @property isDocumentSignerCertificateExpired If the Document Signer Certificate is expired by the time of reading
 * @property isCSCAValid If the signature on the CSCA is valid
 * @property isCSCAExpired If the CSCA is expired by the time of reading
 * @property isSignerInfoValid If the signature of the signer infos is valid
 * @property isSigningTimeValid If the signer infos are in their signed time period
 * @property doesHashMatch If the signed hash matches the hash of the [ldsSecurityObject]
 * @property validContentType If the content of the [ldsSecurityObject] is actually an LDS Security Object
 */
class EfSod(): ElementaryFileTemplate() {

    override val efTag: Byte = 77
    override var rawFileContent: ByteArray? = null

    override val shortEFIdentifier: Byte = 0x1D
    var ldsSecurityObject : LDSSecurityObject? = null
        private set
    private var certificate : SignedData? = null
    var documentSignerCertificate : Certificate? = null
        private set
    var isValid = false
        private set

    private var cert : java.security.cert.Certificate? = null
    var isDocumentSignerCertificateValid = false
        private set
    var isDocumentSignerCertificateExpired = true
        private set
    var isCSCAValid = false
        private set
    var isCSCAExpired = true
        private set
    var isSignerInfoValid = false
        private set
    var isSigningTimeValid = false
        private set
    var doesHashMatch = false
        private set
    var validContentType = false
        private set

    /**
     * Parses the file content. Creates the [certificate], [documentSignerCertificate] and [ldsSecurityObject]
     * @return [SUCCESS] or [FAILURE]
     */
    override fun parse(): Int {
        isParsed = false
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
            ldsSecurityObject = LDSSecurityObject.getInstance(ASN1InputStream(TLV(content).value!!).readAllBytes())
            isParsed = true
            return SUCCESS
        } catch (_: Exception) {
            return FAILURE
        }
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
     * @param cscas The trusted root certificate(s) from the State/organization that issued the eMRTD
     * @return [SUCCESS] or [FAILURE]
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun passiveAuthentication(cscas: Array<Certificate>?) : Int {
        if (cscas == null || cscas.isEmpty() || documentSignerCertificate == null ||
            certificate == null || ldsSecurityObject == null) return FAILURE
        try {
            val signerInfo = SignerInfo.getInstance(certificate!!.signerInfos.getObjectAt(0).toASN1Primitive().encoded)
            validateSignerInfoSignature(signerInfo)
            validateLDSSecurityObject(signerInfo)
        } catch (_ : Exception) {

        }
        val listCSCA = findPossibleCSCA(cscas)
        if (listCSCA.isEmpty()){
            return FAILURE
        } else {
            for (c in listCSCA) {
                if (validateDocumentSignerCertificate(c)) {
                    validateCSCA(c)
                    break
                }
            }
        }
        isValid = isCSCAValid && isDocumentSignerCertificateValid && isSignerInfoValid &&
                isSigningTimeValid && !isCSCAExpired && ! isDocumentSignerCertificateExpired && isRead && isPresent
        return SUCCESS
    }

    /**
     * Finds possible CSCA for the eMRTD among the CSCA certificates a State/Organization issued over time
     * @param cscas A list of CSCA a State/Organization issued
     * @return A list of CSCA where the DSA matches with the [documentSignerCertificate] and
     * the time period of the [documentSignerCertificate] falls within the time period of the CSCA
     */
    private fun findPossibleCSCA(cscas: Array<Certificate>) : ArrayList<Certificate> {
        val cscaList = ArrayList<Certificate>()
        for (c in cscas) {
            if (documentSignerCertificate!!.startDate.date.time < c.startDate.date.time ||
                c.endDate.date.time < documentSignerCertificate!!.endDate.date.time ||
                c.signatureAlgorithm.algorithm.id != documentSignerCertificate!!.signatureAlgorithm.algorithm.id) {
                continue
            } else {
                cscaList.add(c)
            }
        }
        return cscaList
    }

    /**
     * Validates the CSCA certificate by using its own public key
     * @param csca The CSCA certificate to verify
     * @return True if [csca] is verified, otherwise false
     */
    private fun validateCSCA(csca: Certificate) : Boolean {
        val time = Date().time
        isCSCAExpired = time < csca.startDate.date.time || csca.endDate.date.time < time
        try {
            val spec = X509EncodedKeySpec(csca.subjectPublicKeyInfo.encoded)
            val fac = KeyFactory.getInstance(csca.subjectPublicKeyInfo.algorithm.algorithm.id, "BC")
            val pub = fac!!.generatePublic(spec)
            val sign = Signature.getInstance(csca.signatureAlgorithm.algorithm.id, "BC")
            sign.initVerify(pub)
            sign.update(csca.tbsCertificate.encoded)
            isCSCAValid = sign.verify(csca.signature.bytes)
            return true
        } catch (e : Exception) {
            println(e)
        }
        isCSCAValid = false
        return false
    }

    /**
     * Validates the [documentSignerCertificate] by using the public key of the [csca] X509 certificate
     * @param csca The CSCA certificate
     * @return True if [documentSignerCertificate] is verified, otherwise false
     */
    private fun validateDocumentSignerCertificate(csca : Certificate) : Boolean {
        val time = Date().time
        isDocumentSignerCertificateExpired = time < documentSignerCertificate!!.startDate.date.time || documentSignerCertificate!!.endDate.date.time < time
        try {
            val spec = X509EncodedKeySpec(csca.subjectPublicKeyInfo.encoded)
            val fac = KeyFactory.getInstance(csca.subjectPublicKeyInfo.algorithm.algorithm.id, "BC")
            val pub = fac!!.generatePublic(spec)
            val sign = Signature.getInstance(
                documentSignerCertificate!!.signatureAlgorithm.algorithm.id,
                "BC"
            )
            sign.initVerify(pub)
            sign.update(documentSignerCertificate!!.tbsCertificate.encoded)
            isDocumentSignerCertificateValid = sign.verify(documentSignerCertificate!!.signature.bytes)
            return isDocumentSignerCertificateValid
        } catch (e: Exception) {
            println(e)
        }
        isDocumentSignerCertificateValid = false
        return false
    }

    /**
     * Validates the [SignerInfo] field in the [SignedData] [certificate] by verifying the signature with the
     * [documentSignerCertificate] public key
     * @param signerInfo The signed attributes of the [SignedData] [certificate]
     */
    private fun validateSignerInfoSignature(signerInfo: SignerInfo) {
        try {
            val dsc = X509EncodedKeySpec(documentSignerCertificate!!.subjectPublicKeyInfo.encoded)
            val fac = KeyFactory.getInstance(documentSignerCertificate!!.subjectPublicKeyInfo.algorithm.algorithm.id, "BC")
            val pub = fac.generatePublic(dsc)
            val sign = Signature.getInstance(signerInfo.digestEncryptionAlgorithm.algorithm.id, "BC")
            sign.initVerify(pub)
            sign.update(signerInfo.authenticatedAttributes.encoded)
            isSignerInfoValid = sign.verify(signerInfo.encryptedDigest.octets)
            return
        } catch (e: Exception) {
            println(e)
        }
        isSignerInfoValid = false
    }

    /**
     * Validates the [ldsSecurityObject] by comparing the hash of it to the signed hash in the [SignerInfo] field
     * of the SignedData [certificate] and comparing the signing time in the [SignerInfo] to the
     * validity period of the [documentSignerCertificate]
     * @param signerInfo The signed attributes of the [certificate]
     */
    private fun validateLDSSecurityObject(signerInfo: SignerInfo) {
        val md = MessageDigest.getInstance(signerInfo.digestAlgorithm.algorithm.id)
        md.update(ldsSecurityObject!!.encoded)
        val hash = md.digest()

        val attr = Attributes.getInstance(signerInfo.authenticatedAttributes.encoded)
        var originalHash : ByteArray? = null
        var signingTime : Long = 0
        var contentType : ASN1ObjectIdentifier? = null
        for (a in attr.attributes) {
            when(a.attrType.id) {
                "1.2.840.113549.1.9.4" -> originalHash = DEROctetString.getInstance(a.attrValues.getObjectAt(0).toASN1Primitive().encoded).octets
                "1.2.840.113549.1.9.5" -> {
                    if (a.attrValues.getObjectAt(0) is ASN1UTCTime) {
                        signingTime = ASN1UTCTime.getInstance(a.attrValues.getObjectAt(0).toASN1Primitive().encoded).date.time
                    } else if (a.attrValues.getObjectAt(0) is ASN1GeneralizedTime) {
                        signingTime = ASN1GeneralizedTime.getInstance(a.attrValues.getObjectAt(0).toASN1Primitive().encoded).date.time
                    }
                }
                "1.2.840.113549.1.9.3" -> contentType = ASN1ObjectIdentifier.getInstance(a.attrValues.getObjectAt(0).toASN1Primitive().encoded)
            }
        }

        validContentType = contentType != null && contentType.id.contentEquals("2.23.136.1.1.1")
        doesHashMatch = originalHash != null && hash.contentEquals(originalHash)
        isSigningTimeValid = !(signingTime < documentSignerCertificate!!.startDate.date.time || documentSignerCertificate!!.endDate.date.time < signingTime)
    }
}