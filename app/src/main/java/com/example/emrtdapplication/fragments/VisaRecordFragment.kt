package com.example.emrtdapplication.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R

/**
 * Fragment for displaying contents of the Visa Record application
 *
 * @property view View for displaying Visa Record application content in the fragment
 */
class VisaRecordFragment() : Fragment(R.layout.visa_records) {
    private var view : ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (view != null) {
            val toReplace = requireView().findViewById<ScrollView>(R.id.visa_records_scroll_view)
            val rootView = toReplace.parent as ViewGroup
            rootView.removeView(toReplace)
            rootView.addView(view)
        }
    }

    /**
     * Creates views to display the contents of files in the Visa Record application
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val visaRecordsLayout = view.findViewById<LinearLayout>(R.id.visa_records_layout)
        this.view = view.findViewById(R.id.visa_records_scroll_view)
        if (EMRTD.visaRecords.isPresent) {
            createVisaRecordsView(visaRecordsLayout)
            createCertificateRecordsView(visaRecordsLayout)
        } else {
            visaRecordsLayout.removeAllViews()
            val view = TextView(requireContext())
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            view.gravity = Gravity.CENTER
            view.text = getString(R.string.application_is_not_implemented_on_the_emrtd)
            visaRecordsLayout.addView(view)
        }
    }

    /**
     * Creates views for every Visa Record read from the application
     *
     * @param view The parent for which views for a Visa Record are generated
     */
    private fun createVisaRecordsView(view: View) {
        val visaRecordLayout = view.findViewById<LinearLayout>(R.id.visa_records)
        if (EMRTD.visaRecords.visaRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
            visaRecordLayout.addView(unableReadView)
            return
        }
        for (visaRecord in EMRTD.visaRecords.visaRecords) {
            visaRecord.createView(requireContext(), visaRecordLayout)
        }
    }

    /**
     * Creates views for every Certificate Record read from the application
     *
     * @param view The parent for which views for a Certificate Record are generated
     */
    private fun createCertificateRecordsView(view: View) {
        val certificateRecordsLayout = view.findViewById<LinearLayout>(R.id.visa_certificates)
        if (EMRTD.visaRecords.certificateRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
            certificateRecordsLayout.addView(unableReadView)
            return
        }
        for (certificateRecord in EMRTD.visaRecords.certificateRecords) {
            certificateRecord.createView(requireContext(), certificateRecordsLayout)
        }
    }
}