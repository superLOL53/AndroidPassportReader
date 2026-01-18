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
 * Fragment for displaying contents of the Travel Record application
 *
 * @property view View for displaying Travel Record application content in the fragment
 */
class TravelRecordsFragment() : Fragment(R.layout.travel_records) {
    private var view : ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (view != null) {
            val toReplace = requireView().findViewById<ScrollView>(R.id.travel_records_scroll_view)
            val rootView = toReplace.parent as ViewGroup
            rootView.removeView(toReplace)
            rootView.addView(view)
        }
    }

    /**
     * Creates views to display the contents of files in the Travel Record application
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val travelRecordsLayout = view.findViewById<LinearLayout>(R.id.travel_records_layout)
        this.view = view.findViewById(R.id.travel_records_scroll_view)
        if (EMRTD.travelRecords.isPresent) {
            createEntryRecordsView(travelRecordsLayout)
            createExitRecordsView(travelRecordsLayout)
            createCertificateRecordsView(travelRecordsLayout)
        } else {
            travelRecordsLayout.removeAllViews()
            val view = TextView(requireContext())
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            view.gravity = Gravity.CENTER
            view.text = getString(R.string.application_is_not_implemented_on_the_emrtd)
            travelRecordsLayout.addView(view)
        }
    }

    /**
     * Creates views for every Entry Record read from the application
     *
     * @param view The parent for which views for a Entry Record are generated
     */
    private fun createEntryRecordsView(view: View) {
        val entryRecordLayout = view.findViewById<LinearLayout>(R.id.entry_records)
        if (EMRTD.travelRecords.entryRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
            entryRecordLayout.addView(unableReadView)
            return
        }
        for (entryRecord in EMRTD.travelRecords.entryRecords) {
            entryRecord.createView(requireContext(), entryRecordLayout)
        }
    }

    /**
     * Creates views for every Exit Record read from the application
     *
     * @param view The parent for which views for a Exit Record are generated
     */
    private fun createExitRecordsView(view: View) {
        val exitRecordLayout = view.findViewById<LinearLayout>(R.id.exit_records)
        if (EMRTD.travelRecords.exitRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
            exitRecordLayout.addView(unableReadView)
            return
        }
        for (exitRecord in EMRTD.travelRecords.exitRecords) {
            exitRecord.createView(requireContext(), exitRecordLayout)
        }
    }

    /**
     * Creates views for every Certificate Record read from the application
     *
     * @param view The parent for which views for a Certificate Record are generated
     */
    private fun createCertificateRecordsView(view: View) {
        val certificateRecordsLayout = view.findViewById<LinearLayout>(R.id.travel_certificate_records)
        if (EMRTD.travelRecords.certificateRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
            certificateRecordsLayout.addView(unableReadView)
            return
        }
        for (certificateRecord in EMRTD.travelRecords.certificateRecords) {
            certificateRecord.createView(requireContext(), certificateRecordsLayout)
        }
    }

}
