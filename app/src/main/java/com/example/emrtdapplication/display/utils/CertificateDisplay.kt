package com.example.emrtdapplication.display.utils

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.R
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
        provideTextForRow(row, "Version:", "${certificate.versionNumber}")
        createHeader(context, parent, "Issuer")
        var text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        text.text = certificate.issuer.toString()
        parent.addView(text)
        createHeader(context, parent, "Subject")
        text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        text.text = certificate.subject.toString()
        parent.addView(text)
        createPublicKeyView(context, parent, certificate.subjectPublicKeyInfo.publicKeyData.bytes)
        createSignatureView(context, parent, certificate.signature.bytes)
    }
}