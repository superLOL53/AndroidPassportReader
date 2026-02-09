package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object VerificationDisplay : CreateView() {
    override fun <T : LinearLayout> createView(
        context: Context,
        parent: T
    ) {
        val row = createRow(context, parent)
        provideTextForRow(row, "Authentication Status:", authenticationStatus())
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
            "Failure"
        } else {
            "Verified"
        }
    }
}