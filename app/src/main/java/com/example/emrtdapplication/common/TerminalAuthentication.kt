package com.example.emrtdapplication.common

import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TerminalAuthenticationConstants.ID_TA_ECDSA_SHA_224
import com.example.emrtdapplication.constants.TerminalAuthenticationConstants.ID_TA_ECDSA_SHA_256
import com.example.emrtdapplication.constants.TerminalAuthenticationConstants.ID_TA_ECDSA_SHA_384
import com.example.emrtdapplication.constants.TerminalAuthenticationConstants.ID_TA_ECDSA_SHA_512
import com.example.emrtdapplication.constants.TerminalAuthenticationConstants.ID_TA_RSA_PSS_SHA_256
import com.example.emrtdapplication.constants.TerminalAuthenticationConstants.ID_TA_RSA_PSS_SHA_512
import com.example.emrtdapplication.constants.TlvTags.AUTHENTICITY_TOKEN
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_BODY
import com.example.emrtdapplication.constants.TlvTags.LENGTH_MULTIPLE_BYTES
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.x509.Certificate
import java.security.PrivateKey
import java.security.Signature

class TerminalAuthentication(
    private val terminalAuthenticationInfo: TerminalAuthenticationInfo,
    private val privateKey: PrivateKey) {

    fun authenticate(certificateChain : Array<Certificate>) : Int {
        for (certificate in certificateChain) {
            if (doMSESetDST(certificate) != SUCCESS ||
                doPerformSecurityOperation(certificate) != SUCCESS) {
                return FAILURE
            }
        }
        doMSESetAT(certificateChain.last())
        val challenge = getChallenge()
        return if (challenge != null) {
            val signature = signChallenge(challenge)
            if (signature != null) {
                externalAuthenticate(signature)
            } else {
                FAILURE
            }
        } else {
            FAILURE
        }
    }

    private fun doMSESetDST(certificate: Certificate) : Int {
        //TODO: Find out public key reference
        val data = TLV(0x83.toByte(), certificate.tbsCertificate.encoded)
        val response = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
                NfcP1Byte.SET_DIGITAL_SIGNATURE_TEMPLATE_FOR_VERIFICATION,
                NfcP2Byte.SET_DIGITAL_SIGNATURE_TEMPLATE_FOR_VERIFICATION,
                data.toByteArray()
            )
        )
        return if (APDUControl.checkResponse(response)) {
            SUCCESS
        } else {
            FAILURE
        }
    }

    private fun doPerformSecurityOperation(certificate: Certificate) : Int {
        val certificateBody = TLV(byteArrayOf(LENGTH_MULTIPLE_BYTES, CERTIFICATE_BODY), certificate.tbsCertificate.encoded)
        val signature = TLV(byteArrayOf(0x5F, AUTHENTICITY_TOKEN), certificate.signature.encoded)
        val response = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.PERFORM_SECURITY_OPERATION,
                NfcP1Byte.ZERO,
                NfcP2Byte.VERIFY_SELF_DESCRIPTIVE_CERTIFICATE,
                certificateBody.toByteArray() + signature.toByteArray()
            )
        )
        return if (APDUControl.checkResponse(response)) {
            SUCCESS
        } else {
            FAILURE
        }
    }

    private fun doMSESetAT(certificate: Certificate) : Int {
        //TODO: Find out public key reference
        val publicKeyReference = TLV(0x83.toByte(), certificate.tbsCertificate.encoded)
        val response = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.MANAGE_SECURITY_ENVIRONMENT,
                NfcP1Byte.SET_DIGITAL_SIGNATURE_TEMPLATE_FOR_VERIFICATION,
                NfcP2Byte.TERMINAL_AUTHENTICATION,
                publicKeyReference.toByteArray()
            )
        )
        return if (APDUControl.checkResponse(response)) {
            SUCCESS
        } else {
            FAILURE
        }
    }

    private fun getChallenge() : ByteArray? {
        val response = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.REQUEST_RANDOM_NUMBER,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                8
            )
        )
        return if (!APDUControl.checkResponse(response) ||
            response.size != 10) {
            null
        } else {
            APDUControl.removeRespondCodes(response)
        }
    }

    private fun externalAuthenticate(signature: ByteArray) : Int {
        val response = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.EXTERNAL_AUTHENTICATE,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                signature
            )
        )
        return if (APDUControl.checkResponse(response)) {
            SUCCESS
        } else {
            FAILURE
        }
    }

    private fun signChallenge(challenge: ByteArray) : ByteArray? {
        val signatureAlgorithm = when(terminalAuthenticationInfo.objectIdentifier) {
            ID_TA_RSA_PSS_SHA_256 -> "SHA256withRSA/PSS"
            ID_TA_RSA_PSS_SHA_512 -> "SHA512withRSA/PSS"
            ID_TA_ECDSA_SHA_224 -> "SHA224withECDSA"
            ID_TA_ECDSA_SHA_256 -> "SHA256withECDSA"
            ID_TA_ECDSA_SHA_384 -> "SHA384withECDSA"
            ID_TA_ECDSA_SHA_512 -> "SHA512withECDSA"
            else -> return null
        }
        try {
            val sigAlg = Signature.getInstance(signatureAlgorithm)
            sigAlg.initSign(privateKey)
            sigAlg.update(challenge)
            return sigAlg.sign()
        } catch (_ : Exception) {
            return null
        }
    }
}