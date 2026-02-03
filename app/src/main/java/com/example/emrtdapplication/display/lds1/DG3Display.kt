package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.biometrics.fingerprint.FingerprintRecordData

object DG3Display : CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg3.biometricInformation != null && EMRTD.ldS1Application.dg3.biometricInformation!!.biometricInformationList != null) {
            for (bios in EMRTD.ldS1Application.dg3.biometricInformation!!.biometricInformationList) {
                try {
                    if (bios == null) continue
                    val biometricData = bios.biometricDataBlock.biometricData as FingerprintRecordData
                    val image = biometricData.fingerprintData.representationBlocks[0].geImageData()
                    val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                    val view = ImageView(context)
                    view.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    view.setImageBitmap(bitmap)
                    parent.addView(view)
                } catch (_ : Exception) {
                }
            }
        }
    }
}