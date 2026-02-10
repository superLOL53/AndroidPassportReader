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
import com.example.emrtdapplication.display.lds2.records.EntryExitRecordDisplay

object TravelRecordApplicationDisplay : CreateView() {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.travelRecords.isPresent) {
            createEntryRecordsView(context, parent)
            createExitRecordsView(context, parent)
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
     * Creates views for every Entry Record read from the application
     *
     * @param view The parent for which views for a Entry Record are generated
     */
    private fun createEntryRecordsView(context : Context, view: View) {
        val entryRecordLayout = view.findViewById<LinearLayout>(R.id.entry_records)
        if (EMRTD.travelRecords.entryRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
            entryRecordLayout.addView(unableReadView)
            return
        }
        for (entryRecord in EMRTD.travelRecords.entryRecords) {
            EntryExitRecordDisplay(entryRecord, true).createView(context, entryRecordLayout)
        }
    }

    /**
     * Creates views for every Exit Record read from the application
     *
     * @param view The parent for which views for a Exit Record are generated
     */
    private fun createExitRecordsView(context: Context, view: View) {
        val exitRecordLayout = view.findViewById<LinearLayout>(R.id.exit_records)
        if (EMRTD.travelRecords.exitRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
            exitRecordLayout.addView(unableReadView)
            return
        }
        for (exitRecord in EMRTD.travelRecords.exitRecords) {
            EntryExitRecordDisplay(exitRecord, false).createView(context, exitRecordLayout)
        }
    }

    /**
     * Creates views for every Certificate Record read from the application
     *
     * @param view The parent for which views for a Certificate Record are generated
     */
    private fun createCertificateRecordsView(context : Context, view: View) {
        val certificateRecordsLayout = view.findViewById<LinearLayout>(R.id.travel_certificate_records)
        if (EMRTD.travelRecords.certificateRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
            certificateRecordsLayout.addView(unableReadView)
            return
        }
        for (certificateRecord in EMRTD.travelRecords.certificateRecords) {
            CertificateRecordDisplay(certificateRecord).createView(context, certificateRecordsLayout)
        }
    }
}