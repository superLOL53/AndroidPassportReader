package com.example.emrtdapplication.display.lds2.records

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.lds2.CertificateRecord

class CertificateRecordDisplay(private val certificateRecord: CertificateRecord) : CreateView() {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        createHeader(context, parent, "Certificate Record ${certificateRecord.recordNumber}")
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(row, "Serial Number:", "${certificateRecord.serialNumber}")
        row = createRow(context, table)
        provideTextForRow(row, "Start Date:", certificateRecord.certificate.startDate.time)
        row = createRow(context, table)
        provideTextForRow(row, "End Date:", certificateRecord.certificate.endDate.time)
        row = createRow(context, table)
        provideTextForRow(row, "Signature Algorithm ID:", certificateRecord.certificate.signatureAlgorithm.algorithm.id)
        row = createRow(context, table)
        provideTextForRow(row, "Issuer:", certificateRecord.certificate.issuer.toString())
        row = createRow(context, table)
        provideTextForRow(row, "Subject:", certificateRecord.certificate.subject.toString())
        row = createRow(context, table)
        provideTextForRow(row, "Version:", "${certificateRecord.certificate.versionNumber}")
        createPublicKeyView(context, parent, certificateRecord.certificate.subjectPublicKeyInfo.publicKeyData.bytes)
        createSignatureView(context, parent, certificateRecord.certificate.signature.bytes)

    }
}