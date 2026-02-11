package com.example.emrtdapplication.display.lds2.records

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.display.utils.CertificateDisplay
import com.example.emrtdapplication.lds2.CertificateRecord

class CertificateRecordDisplay(private val certificateRecord: CertificateRecord) : CreateView() {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        createHeader(context, parent, "Certificate Record ${certificateRecord.recordNumber}")
        CertificateDisplay(certificateRecord.certificate).createView(context, parent)
    }
}