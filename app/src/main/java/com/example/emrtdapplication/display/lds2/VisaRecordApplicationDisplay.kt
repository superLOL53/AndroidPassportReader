package com.example.emrtdapplication.display.lds2

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.example.emrtdapplication.display.lds2.records.CertificateRecordDisplay
import com.example.emrtdapplication.display.lds2.records.VisaRecordDisplay

object VisaRecordApplicationDisplay : CreateView {

    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.visaRecords.isPresent) {
            createVisaRecordsView(context, parent)
            createCertificateRecordsView(context, parent)
        } else {
                parent.removeAllViews()
            val view = TextView(context)
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            view.gravity = Gravity.CENTER
            view.text = context.getString(R.string.application_is_not_implemented_on_the_emrtd)
            parent.addView(view)
        }
    }


    /**
     * Creates views for every Visa Record read from the application
     *
     * @param view The parent for which views for a Visa Record are generated
     */
    private fun createVisaRecordsView(context: Context, view: View) {
        val visaRecordLayout = view.findViewById<LinearLayout>(R.id.visa_records)
        if (EMRTD.visaRecords.visaRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
            visaRecordLayout.addView(unableReadView)
            return
        }
        for (visaRecord in EMRTD.visaRecords.visaRecords) {
            VisaRecordDisplay(visaRecord).createView(context, visaRecordLayout)
        }
    }

    /**
     * Creates views for every Certificate Record read from the application
     *
     * @param view The parent for which views for a Certificate Record are generated
     */
    private fun createCertificateRecordsView(context : Context, view: View) {
        val certificateRecordsLayout = view.findViewById<LinearLayout>(R.id.visa_certificates)
        if (EMRTD.visaRecords.certificateRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
            certificateRecordsLayout.addView(unableReadView)
            return
        }
        for (certificateRecord in EMRTD.visaRecords.certificateRecords) {
            CertificateRecordDisplay(certificateRecord).createView(context, certificateRecordsLayout)
        }
    }
}