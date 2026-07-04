package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CHIP_AUTHENTICATION_STATUS
import com.example.emrtdapplication.CRL_STATUS
import com.example.emrtdapplication.CSCA_STRING
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.DATE_FORMAT
import com.example.emrtdapplication.DOCUMENT_SIGNER_CERTIFICATE
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.EXPIRED
import com.example.emrtdapplication.FAILURE_STRING
import com.example.emrtdapplication.INVALID_HASH
import com.example.emrtdapplication.INVALID_SIGNATURE
import com.example.emrtdapplication.PASSIVE_AUTHENTICATION_STATUS
import com.example.emrtdapplication.REVOKED
import com.example.emrtdapplication.SUCCESS
import com.example.emrtdapplication.UNDETERMINED
import com.example.emrtdapplication.UNREVOKED
import com.example.emrtdapplication.VERIFIED
import com.example.emrtdapplication.CertificationRevocationStatus
import com.example.emrtdapplication.display.utils.CertificateDisplay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object VerificationDisplay: CreateView() {
    override fun <T: LinearLayout> createView(
        context: Context,
        parent: T
    ) {
        var row = createRow(context, parent)
        provideTextForRow(
            row,
            PASSIVE_AUTHENTICATION_STATUS,
            authenticationStatus()
        )
        row = createRow(context, parent)
        provideTextForRow(
            row,
            CHIP_AUTHENTICATION_STATUS,
            chipAuthenticationStatus()
        )
        row = createRow(context, parent)
        provideTextForRow(
            row,
            CRL_STATUS,
            crlStatus()
        )
        if (EMRTD.showDetails) {
            if (EMRTD.ldS1Application.efSod.usedCSCA != null) {
                createHeader(context, parent, CSCA_STRING)
                CertificateDisplay(
                    EMRTD.ldS1Application.efSod.usedCSCA!!
                ).createView(context, parent)
            }
            if (EMRTD.ldS1Application.efSod.documentSignerCertificate != null) {
                createHeader(
                    context,
                    parent,
                    DOCUMENT_SIGNER_CERTIFICATE
                )
                CertificateDisplay(
                    EMRTD.ldS1Application.efSod.documentSignerCertificate!!
                ).createView(context, parent)
            }
            EMRTD.ldS1Application.efSod.certificate
            EMRTD.ldS1Application.efSod.ldsSecurityObject
        }
    }

    private fun authenticationStatus(): String {
        val string = EMRTD.ldS1Application.dg1.dateOfExpiry
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH)
        val expirationDate = LocalDate.parse(string, formatter)
        return if (EMRTD.ldS1Application.efSod.isCSCAExpired ||
            EMRTD.ldS1Application.efSod.isDocumentSignerCertificateExpired ||
            !EMRTD.ldS1Application.efSod.isSigningTimeValid ||
            expirationDate.toEpochDay() < LocalDate.now().toEpochDay()
        ) {
            EXPIRED
        } else if (!EMRTD.ldS1Application.efSod.isValid ||
            !EMRTD.ldS1Application.efSod.isSignerInfoValid ||
            !EMRTD.ldS1Application.efSod.isCSCAValid
        ) {
            INVALID_SIGNATURE
        } else if(!EMRTD.ldS1Application.efSod.doesHashMatch ||
            !EMRTD.ldS1Application.efSod.validContentType
        ) {
            INVALID_HASH
        } else {
            VERIFIED
        }
    }

    private fun chipAuthenticationStatus(): String {
        return if (EMRTD.ldS1Application.chipAuthenticationResult == SUCCESS  ||
            EMRTD.ldS1Application.activeAuthenticationResult == SUCCESS
        ) {
            VERIFIED
        } else {
            FAILURE_STRING
        }
    }

    private fun crlStatus(): String {
        return when (EMRTD.ldS1Application.efSod.certificationRevocationStatus) {
            CertificationRevocationStatus.UNDETERMINED -> UNDETERMINED
            CertificationRevocationStatus.REVOKED -> REVOKED
            CertificationRevocationStatus.UNREVOKED -> UNREVOKED
        }
    }
}