package com.example.emrtdapplication.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.emrtdapplication.R
import com.example.emrtdapplication.display.lds2.TravelRecordApplicationDisplay

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
        TravelRecordApplicationDisplay.createView(requireContext(), travelRecordsLayout)
    }
}
