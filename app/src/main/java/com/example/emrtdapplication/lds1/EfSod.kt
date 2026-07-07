package com.example.emrtdapplication.lds1

import android.util.Log
import com.example.emrtdapplication.ANDROID_LOG_INFO_TAG
import com.example.emrtdapplication.CertificationRevocationStatus
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.FAILURE
import com.example.emrtdapplication.SUCCESS
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.MasterList
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1GeneralizedTime
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1ObjectIdentifier
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.ASN1UTCTime
import org.spongycastle.asn1.DEROctetString
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.DERTaggedObject
import org.spongycastle.asn1.cms.Attributes
import org.spongycastle.asn1.cms.SignedData
import org.spongycastle.asn1.cms.SignerInfo
import org.spongycastle.asn1.icao.LDSSecurityObject
import org.spongycastle.asn1.x509.Certificate
import org.spongycastle.asn1.x509.DistributionPoint
import org.spongycastle.asn1.x509.GeneralNames
import java.lang.Thread.sleep
import java.net.URL
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509CRL
import java.util.Date
import kotlin.time.Clock

/**
 * Implements the EF.SOD file and performs passive authentication against provided CSCAs
 *
 * @property efTag The tag associated with the file
 * @property rawFileContent The content of the file as a ByteArray
 * @property shortEFIdentifier The short EF id of the file
 * @property ldsSecurityObject The LDS Security Object contained in the file
 * @property documentSignerCertificate The Document Signer
 * Certificate contained in the file
 * @property certificate The file content as a certificate
 * @property isValid Indicates if the [documentSignerCertificate]
 * was signed by the CSCA of the same entity
 * @property isDocumentSignerCertificateValid If the signature of
 * the Document Signer Certificate is valid
 * @property isDocumentSignerCertificateExpired If the Document
 * Signer Certificate is expired by the time of reading
 * @property isCSCAValid If the signature on the CSCA is valid
 * @property isCSCAExpired If the CSCA is expired by the time of reading
 * @property isSignerInfoValid If the signature of the signer infos is valid
 * @property isSigningTimeValid If the signer infos are in their signed time period
 * @property doesHashMatch If the signed hash matches the hash of the [ldsSecurityObject]
 * @property validContentType If the content of the [ldsSecurityObject]
 * is actually an LDS Security Object
 * @property usedCSCA
 * @property validCRL
 * @property certificationRevocationStatus
 * @property isCRLLocationInCSCA
 */
class EfSod: ElementaryFileTemplate() {

    override val efTag: Byte = EF_SOD_TAG
    override var rawFileContent: ByteArray? = null

    override val shortEFIdentifier: Byte = EF_SOD_SHORT_EF_ID
    var ldsSecurityObject: LDSSecurityObject? = null
        private set
    var certificate: SignedData? = null
        private set
    var documentSignerCertificate: Certificate? = null
        private set
    var isValid = false
        private set
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
    var usedCSCA: Certificate? = null
        private set
    var validCRL = false
        private set
    var certificationRevocationStatus = CertificationRevocationStatus.UNDETERMINED
        private set
    var isCRLLocationInCSCA = false
        private set

    /**
     * Parses the file content. Creates the [certificate],
     * [documentSignerCertificate] and [ldsSecurityObject]
     * @return [SUCCESS] or [FAILURE]
     */
    override fun parse(): Int {
        isParsed = false
        if (rawFileContent == null || contentStart >= rawFileContent!!.size) {
            return SUCCESS
        }
        try {
            val input = ASN1InputStream(
                rawFileContent!!.
                slice(contentStart..<rawFileContent!!.size).
                toByteArray()
            )
            val cert = DERTaggedObject.getInstance(
                DERSequence.getInstance(
                    input.readObject().encoded
                ).getObjectAt(1)
            )
            certificate = SignedData.getInstance(cert.`object`)
            val content = certificate!!.encapContentInfo.content.toASN1Primitive().encoded
            documentSignerCertificate = Certificate.getInstance(
                certificate!!.certificates.getObjectAt(0).toASN1Primitive().encoded
            )
            ldsSecurityObject = LDSSecurityObject.getInstance(
                ASN1InputStream(
                    TLV(content).value!!
                ).readObject().encoded
            )
            isParsed = true
            return SUCCESS
        } catch (_: Exception) {
            return FAILURE
        }
    }

    /**
     * Checks the hashes of the files DG1 to DG16.
     */
    fun checkHashes(dgs: Map<Byte, ElementaryFileTemplate>) {
        if (ldsSecurityObject == null) {
            return
        }
        val groupHashes = ldsSecurityObject!!.datagroupHash
        for (i in groupHashes.indices) {
            val id = groupHashes[i].dataGroupNumber.toByte()
            val ef = dgs[id] ?: continue
            val hash1 = ef.hash(
                ldsSecurityObject!!.digestAlgorithmIdentifier.algorithm.id
            ) ?: continue
            val hash2 = groupHashes[i].dataGroupHashValue.octets
            if (hash1.contentEquals(hash2)) {
                ef.matchHash = true
            }
        }
    }

    /**
     * Validates the [documentSignerCertificate] by checking the signature in it with the CSCA
     * @return [SUCCESS] or [FAILURE]
     */
    fun passiveAuthentication(): Int {
        isCSCAValid = false
        isCSCAExpired = true
        isValid = false
        isSignerInfoValid = false
        isSigningTimeValid = false
        isDocumentSignerCertificateExpired = true
        isDocumentSignerCertificateValid = false
        validCRL = false
        isCRLLocationInCSCA = false
        certificationRevocationStatus = CertificationRevocationStatus.UNDETERMINED
        if (documentSignerCertificate == null ||
            certificate == null || ldsSecurityObject == null) return FAILURE
        try {
            val signerInfo = SignerInfo.getInstance(
                certificate!!.signerInfos.getObjectAt(0).toASN1Primitive().encoded
            )
            validateSignerInfoSignature(signerInfo)
            validateLDSSecurityObject(signerInfo)
        } catch (_: Exception) {
        }
        while (!MasterList.isFinished) {
            sleep(500)
        }
        val listCSCA = MasterList.getCSCA(documentSignerCertificate!!)
        if (listCSCA.isEmpty()){
            return FAILURE
        } else {
            for (c in listCSCA) {
                if (validateDocumentSignerCertificate(c)) {
                    usedCSCA = c
                    validateCSCA(c)
                    break
                }
            }
        }
        checkCRLs()
        isValid = isCSCAValid &&
                isDocumentSignerCertificateValid &&
                isSignerInfoValid &&
                isSigningTimeValid &&
                !isCSCAExpired &&
                !isDocumentSignerCertificateExpired &&
                isRead &&
                isPresent
        return SUCCESS
    }

    /**
     * Validates the CSCA certificate by using its own public key
     * @param csca The CSCA certificate to verify
     * @return True if [csca] is verified, otherwise false
     */
    private fun validateCSCA(csca: Certificate): Boolean {
        val time = Date().time
        isCSCAExpired = time < csca.startDate.date.time ||
                csca.endDate.date.time < time
        isCSCAValid = Crypto.verifyCertificate(
            csca,
            csca
        )
        return isCSCAValid
    }

    /**
     * Validates the [documentSignerCertificate] by using the
     * public key of the [csca] X509 certificate
     * @param csca The CSCA certificate
     * @return True if [documentSignerCertificate] is verified, otherwise false
     */
    private fun validateDocumentSignerCertificate(csca: Certificate): Boolean {
        val time = Date().time
        if (documentSignerCertificate != null) {
            isDocumentSignerCertificateExpired =
                        time < documentSignerCertificate!!.startDate.date.time ||
                        documentSignerCertificate!!.endDate.date.time < time
            isDocumentSignerCertificateValid = Crypto.verifyCertificate(
                documentSignerCertificate!!,
                csca
            )
            return isDocumentSignerCertificateValid
        } else {
            return false
        }
    }

    /**
     * Validates the [SignerInfo] field in the [SignedData] [certificate]
     * by verifying the signature with the
     * [documentSignerCertificate] public key
     * @param signerInfo The signed attributes of the [SignedData] [certificate]
     */
    private fun validateSignerInfoSignature(signerInfo: SignerInfo) {
        if (documentSignerCertificate != null) {
            val pub = Crypto.generatePublicKey(documentSignerCertificate!!)
            isSignerInfoValid = if (pub != null) {
                Crypto.verifySignature(
                    signerInfo.digestEncryptionAlgorithm.algorithm.id,
                    pub,
                    signerInfo.authenticatedAttributes.encoded,
                    signerInfo.encryptedDigest.octets
                )
            } else {
                false
            }
        }
    }

    /**
     * Validates the [ldsSecurityObject] by comparing the hash
     * of it to the signed hash in the [SignerInfo] field
     * of the SignedData [certificate] and comparing the signing
     * time in the [SignerInfo] to the
     * validity period of the [documentSignerCertificate]
     * @param signerInfo The signed attributes of the [certificate]
     */
    private fun validateLDSSecurityObject(signerInfo: SignerInfo) {
        val md = MessageDigest.getInstance(
            signerInfo.digestAlgorithm.algorithm.id
        )
        md.update(ldsSecurityObject!!.encoded)
        val hash = md.digest()

        val attr = Attributes.getInstance(
            signerInfo.authenticatedAttributes.encoded
        )
        var originalHash: ByteArray? = null
        var signingTime: Long = 0
        var contentType: ASN1ObjectIdentifier? = null
        for (a in attr.attributes) {
            when(a.attrType.id) {
                ORIGINAL_HASH_OID ->
                    originalHash = DEROctetString.getInstance(
                        a.attrValues.getObjectAt(0).toASN1Primitive().encoded
                    ).octets
                SIGNING_TIME_OID -> {
                    if (a.attrValues.getObjectAt(0) is ASN1UTCTime) {
                        signingTime = ASN1UTCTime.getInstance(
                            a.attrValues.getObjectAt(0).toASN1Primitive().encoded
                        ).date.time
                    } else if (a.attrValues.getObjectAt(0) is ASN1GeneralizedTime) {
                        signingTime = ASN1GeneralizedTime.getInstance(
                            a.attrValues.getObjectAt(0).toASN1Primitive().encoded
                        ).date.time
                    }
                }
                CONTENT_TYPE_OID ->
                    contentType = ASN1ObjectIdentifier.getInstance(
                        a.attrValues.getObjectAt(0).toASN1Primitive().encoded
                    )
            }
        }

        validContentType = contentType != null &&
                contentType.id.contentEquals(CONTENT_TYPE_ID)
        doesHashMatch = originalHash != null &&
                hash.contentEquals(originalHash)
        isSigningTimeValid = !(signingTime < documentSignerCertificate!!.startDate.date.time ||
                documentSignerCertificate!!.endDate.date.time < signingTime)
    }

    /**
     * Checks the CRL for its validity if the CRL
     * distribution point(s) are in the CSCA certificate
     */
    fun checkCRLs() {
        certificationRevocationStatus =
            CertificationRevocationStatus.UNDETERMINED
        usedCSCA?.let {
            val crlDistributionPoints =
                it.tbsCertificate.extensions.getExtension(
                    ASN1ObjectIdentifier(CRL_DISTRIBUTION_POINT_OID)
                )
            if (crlDistributionPoints == null) {
                certificationRevocationStatus =
                    CertificationRevocationStatus.UNDETERMINED
                return
            }
            isCRLLocationInCSCA = true
            val crl = getCRL(crlDistributionPoints.extnValue.octets)
            if (crl != null) {
                val csca = MasterList.getCSCA(crl)
                if (csca == null) {
                    certificationRevocationStatus =
                        CertificationRevocationStatus.UNDETERMINED
                    return
                }
                if (!Crypto.verifyCertificate(
                        csca,
                        csca
                )) {
                    certificationRevocationStatus =
                        CertificationRevocationStatus.UNDETERMINED
                    return
                }
                val publicKey = Crypto.generatePublicKey(csca)
                if (publicKey != null) {
                    validCRL = Crypto.verifySignature(
                            crl.sigAlgName,
                            publicKey,
                            crl.tbsCertList,
                            crl.signature
                        ) && crl.thisUpdate.time <=
                            Clock.System.now().toEpochMilliseconds() &&
                            Clock.System.now().toEpochMilliseconds() <=
                            crl.nextUpdate.time
                    if (!validCRL) {
                        certificationRevocationStatus =
                            CertificationRevocationStatus.UNDETERMINED
                        return
                    }
                }
                if (documentSignerCertificate != null) {
                    try {
                        if (crl.isRevoked(
                            CertificateFactory.getInstance(X509)
                                .generateCertificate(
                                    documentSignerCertificate!!.encoded.inputStream()
                                )
                        )) {
                            certificationRevocationStatus =
                                CertificationRevocationStatus.REVOKED
                            return
                        }
                    } catch (_: Exception) {
                        certificationRevocationStatus =
                            CertificationRevocationStatus.UNDETERMINED
                        return
                    }
                }
                try {
                    if (crl.isRevoked(
                        CertificateFactory.
                            getInstance(X509).
                            generateCertificate(csca.encoded.inputStream())
                    )) {
                        certificationRevocationStatus =
                            CertificationRevocationStatus.REVOKED
                        return
                    }
                } catch (_: Exception) {
                    certificationRevocationStatus =
                        CertificationRevocationStatus.UNDETERMINED
                    return
                }
                certificationRevocationStatus =
                    CertificationRevocationStatus.UNREVOKED
                return
            } else {
                certificationRevocationStatus =
                    CertificationRevocationStatus.UNDETERMINED
            }
            certificationRevocationStatus =
                CertificationRevocationStatus.UNDETERMINED
        }
        certificationRevocationStatus =
            CertificationRevocationStatus.UNDETERMINED
    }

    /**
     * Gets the CRL from the encoded distribution points. Currently only
     * supports fetching via the HTTP(S) protocol
     *
     * @param crlDistributionPoints The distribution points of the CRL
     * as it is in the certificate
     * @return An X.509 CRL or null if the CRL could not be fetched
     */
    fun getCRL(crlDistributionPoints: ByteArray): X509CRL? {
        val seq = ASN1Sequence.getInstance(crlDistributionPoints)
        for (i in 0..<seq.size()) {
            val distributionPoint = DistributionPoint.getInstance(
                (seq.getObjectAt(i))
            )
            if (distributionPoint.distributionPoint.type == 0) {
                try {
                    val names = distributionPoint.distributionPoint.name as GeneralNames
                    for (name in names.names) {
                        val string = name.name.toString()
                        if (string.startsWith(HTTP)) {
                            return fetchCRLListWithHTTP(string)
                        }
                    }
                } catch (_: Exception) {
                    Log.i(ANDROID_LOG_INFO_TAG, UNABLE_TO_CHECK_CRL)
                }
            }
        }
        return null
    }

    /**
     *  Fetches the CRL via an HTTP connection. Tries to connect to
     *  the URL with HTTP and HTTPS.
     *
     *  @param url The URL of the CRL
     *  @return An X.509 CRL or null if the CRL could not be fetched
     */
    fun fetchCRLListWithHTTP(url: String): X509CRL? {
        return try {
            fetchCRList(url)
        } catch (_: Exception) {
            try {
                if (url.startsWith(HTTP_COLUMN)) {
                    fetchCRList(
                        url.replaceFirst(
                            HTTP_COLUMN,
                            HTTPS_COLUMN
                        )
                    )
                } else {
                    fetchCRList(
                        url.replaceFirst(
                            HTTPS_COLUMN,
                            HTTP_COLUMN
                        )
                    )
                }
            } catch (_: Exception) {
                return null
            }
        }
    }

    /**
     * Fetches the CRL from the given URL or throws
     * exceptions if something goes wrong
     *
     * @param url The URL from which to download the CRL
     * @return The CRL from the URL
     */
    fun fetchCRList(url: String): X509CRL {
        val url = URL(url)
        val conn = url.openConnection()
        conn.connectTimeout = 60000
        val input = conn.getInputStream()
        return CertificateFactory.
            getInstance(X509).
            generateCRL(input) as X509CRL
    }
}