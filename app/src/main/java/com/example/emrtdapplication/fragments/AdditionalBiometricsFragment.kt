package com.example.emrtdapplication.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.emrtdapplication.R
import com.example.emrtdapplication.display.lds2.AdditionalBiometricApplicationDisplay

/**
 * Fragment for displaying contents of the Visa Record application
 *
 * @property view View for displaying Visa Record application content in the fragment
 */
class AdditionalBiometricsFragment() : Fragment(R.layout.additional_biometrics) {
    private var view : ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (view != null) {
            val toReplace = requireView().findViewById<ScrollView>(R.id.biometrics_scroll_view)
            val rootView = toReplace.parent as ViewGroup
            rootView.removeView(toReplace)
            rootView.addView(view)
        }
    }

    /**
     * Creates views to display the contents of files in the Additional Biometrics application
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val biometricsLayout = view.findViewById<LinearLayout>(R.id.biometrics_layout)
        this.view = view.findViewById(R.id.biometrics_scroll_view)
        AdditionalBiometricApplicationDisplay.createView(requireContext(), biometricsLayout)
    }
}