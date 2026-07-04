package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.example.emrtdapplication.biometrics.iris.IrisRecordHeader

object DG4Display: CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg4.biometricInformation != null &&
            EMRTD.ldS1Application.dg4.biometricInformation!!.biometricInformationList != null
        ) {
            for (
                bios in EMRTD.ldS1Application.dg4.biometricInformation!!.biometricInformationList
            ) {
                try {
                    if (bios == null) continue
                    val biometricData =
                        bios.biometricDataBlock.biometricHeader as IrisRecordHeader
                    for (subtype in biometricData.irisHeader.irisBiometricSubtypeInfos) {
                        for (image in subtype.irisImageInfos) {
                            val box = LinearLayout(context)
                            box.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            if (alternate) {
                                box.setBackgroundColor(
                                    context.resources.getColor(
                                        R.color.gray,
                                        null
                                    )
                                )
                            } else {
                                box.setBackgroundColor(
                                    context.resources.getColor(
                                        R.color.black,
                                        null
                                    )
                                )
                            }
                            alternate = !alternate
                            parent.addView(box)
                            val im = image.imageInputStream.readBytes()
                            val bitmap = BitmapFactory.decodeByteArray(
                                im,
                                0,
                                im.size
                            )
                            val view = ImageView(context)
                            view.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            view.setImageBitmap(bitmap)
                            box.addView(view)
                            image.imageInputStream.close()
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }
}