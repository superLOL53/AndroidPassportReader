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

object AdditionalBiometricApplicationDisplay : CreateView {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.additionalBiometrics.isPresent) {
            createBiometricsView(context, parent)
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
     * Creates views for every Biometric File read from the application
     *
     * @param view The parent for which views for a Biometric File are generated
     */
    private fun createBiometricsView(context : Context, view: View) {
        val biometricFileLayout = view.findViewById<LinearLayout>(R.id.biometric_files)
        if (EMRTD.additionalBiometrics.biometricFiles.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
            biometricFileLayout.addView(unableReadView)
            return
        }
        for (biometricFile in EMRTD.additionalBiometrics.biometricFiles) {
            BiometricFileDisplay(biometricFile).createView(context, biometricFileLayout)
        }
    }

    /**
     * Creates views for every Certificate Record read from the application
     *
     * @param view The parent for which views for a Certificate Record are generated
     */
    private fun createCertificateRecordsView(context: Context, view: View) {
        val certificateRecordsLayout = view.findViewById<LinearLayout>(R.id.biometrics_certificates)
        if (EMRTD.additionalBiometrics.certificateRecords.isNullOrEmpty()) {
            val unableReadView = TextView(context)
            unableReadView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
            certificateRecordsLayout.addView(unableReadView)
            return
        }
        for (certificateRecord in EMRTD.additionalBiometrics.certificateRecords) {
            CertificateRecordDisplay(certificateRecord).createView(context, certificateRecordsLayout)
        }
    }
}