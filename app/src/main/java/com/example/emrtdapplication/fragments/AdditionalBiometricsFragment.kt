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
        if (EMRTD.additionalBiometrics.isPresent) {
            createBiometricsView(biometricsLayout)
            createCertificateRecordsView(biometricsLayout)
        } else {
            biometricsLayout.removeAllViews()
            val view = TextView(requireContext())
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            view.gravity = Gravity.CENTER
            view.text = getString(R.string.application_is_not_implemented_on_the_emrtd)
            biometricsLayout.addView(view)
        }
    }

    /**
     * Creates views for every Biometric File read from the application
     *
     * @param view The parent for which views for a Biometric File are generated
     */
    private fun createBiometricsView(view: View) {
        val biometricFileLayout = view.findViewById<LinearLayout>(R.id.biometric_files)
        if (EMRTD.additionalBiometrics.biometricFiles.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
            biometricFileLayout.addView(unableReadView)
            return
        }
        for (biometricFile in EMRTD.additionalBiometrics.biometricFiles) {
            biometricFile.createView(requireContext(), biometricFileLayout)
        }
    }

    /**
     * Creates views for every Certificate Record read from the application
     *
     * @param view The parent for which views for a Certificate Record are generated
     */
    private fun createCertificateRecordsView(view: View) {
        val certificateRecordsLayout = view.findViewById<LinearLayout>(R.id.biometrics_certificates)
        if (EMRTD.additionalBiometrics.certificateRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
            certificateRecordsLayout.addView(unableReadView)
            return
        }
        for (certificateRecord in EMRTD.additionalBiometrics.certificateRecords) {
            certificateRecord.createView(requireContext(), certificateRecordsLayout)
        }
    }
}