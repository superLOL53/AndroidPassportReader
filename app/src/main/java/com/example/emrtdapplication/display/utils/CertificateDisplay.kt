package com.example.emrtdapplication.display.utils

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import org.spongycastle.asn1.x509.Certificate

class CertificateDisplay(val certificate: Certificate) : CreateView() {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(row, "Serial Number:", "${certificate.serialNumber}")
        row = createRow(context, table)
        provideTextForRow(row, "Start Date:", certificate.startDate.time)
        row = createRow(context, table)
        provideTextForRow(row, "End Date:", certificate.endDate.time)
        row = createRow(context, table)
        provideTextForRow(row, "Signature Algorithm ID:", certificate.signatureAlgorithm.algorithm.id)
        row = createRow(context, table)
        provideTextForRow(row, "Issuer:", certificate.issuer.toString())
        row = createRow(context, table)
        provideTextForRow(row, "Subject:", certificate.subject.toString())
        row = createRow(context, table)
        provideTextForRow(row, "Version:", "${certificate.versionNumber}")
        createPublicKeyView(context, parent, certificate.subjectPublicKeyInfo.publicKeyData.bytes)
        createSignatureView(context, parent, certificate.signature.bytes)
    }
}