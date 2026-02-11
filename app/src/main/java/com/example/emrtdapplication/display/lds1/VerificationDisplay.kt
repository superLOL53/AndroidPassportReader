package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.display.utils.CertificateDisplay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object VerificationDisplay : CreateView() {
    override fun <T : LinearLayout> createView(
        context: Context,
        parent: T
    ) {
        var row = createRow(context, parent)
        provideTextForRow(row, "Passive Authentication Status:", authenticationStatus())
        row = createRow(context, parent)
        provideTextForRow(row, "Chip Authentication Status:", chipAuthenticationStatus())
        if (EMRTD.showDetails) {
            if (EMRTD.ldS1Application.efSod.usedCSCA != null) {
                createHeader(context, parent, "Country Signing Certificate Authority")
                CertificateDisplay(EMRTD.ldS1Application.efSod.usedCSCA!!).createView(context, parent)
            }
            if (EMRTD.ldS1Application.efSod.documentSignerCertificate != null) {
                createHeader(context, parent, "Country Signing Certificate Authority")
                CertificateDisplay(EMRTD.ldS1Application.efSod.documentSignerCertificate!!).createView(context, parent)
            }
            EMRTD.ldS1Application.efSod.certificate
            EMRTD.ldS1Application.efSod.ldsSecurityObject
        }
    }

    private fun authenticationStatus() : String {
        val string = EMRTD.ldS1Application.dg1.dateOfExpiry
        val formatter = DateTimeFormatter.ofPattern("yyMMdd", Locale.ENGLISH)
        val expirationDate = LocalDate.parse(string, formatter)
        return if (EMRTD.ldS1Application.efSod.isCSCAExpired || EMRTD.ldS1Application.efSod.isDocumentSignerCertificateExpired ||
            !EMRTD.ldS1Application.efSod.isSigningTimeValid || expirationDate.toEpochDay() < LocalDate.now().toEpochDay()
        ) {
            "Expired"
        } else if (!EMRTD.ldS1Application.efSod.isValid || !EMRTD.ldS1Application.efSod.isSignerInfoValid || !EMRTD.ldS1Application.efSod.isCSCAValid) {
            "Invalid signature"
        } else if(!EMRTD.ldS1Application.efSod.doesHashMatch || !EMRTD.ldS1Application.efSod.validContentType) {
            "Invalid hash/content"
        } else {
            "Verified"
        }
    }

    private fun chipAuthenticationStatus() : String {
        return if (EMRTD.ldS1Application.chipAuthenticationResult == SUCCESS  || EMRTD.ldS1Application.activeAuthenticationResult == SUCCESS) {
            "Verified"
        } else {
            "Failure"
        }
    }
}