package com.example.emrtdapplication.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.emrtdapplication.R
import com.example.emrtdapplication.display.lds2.VisaRecordApplicationDisplay

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
        VisaRecordApplicationDisplay.createView(requireContext(), visaRecordsLayout)
    }
}