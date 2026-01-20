package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.biometrics.face.FacialRecordData

object DG2Display : CreateView {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg2.biometricInformation != null && EMRTD.ldS1Application.dg2.biometricInformation!!.biometricInformationList != null) {
            for (bios in EMRTD.ldS1Application.dg2.biometricInformation!!.biometricInformationList) {
                if (bios == null) continue
                val biometricData = bios.biometricDataBlock.biometricData as FacialRecordData
                val image = biometricData.image
                val view = ImageView(context)
                view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                view.setImageBitmap(image)
                parent.addView(view)
                val facialInfo = biometricData.facialInformation
                val table = TableLayout(context)
                parent.addView(table)
                var row = createRow(context, table)
                provideTextForRow(row, "Eye color: ", facialInfo.eyeColor)
                row = createRow(context, table)
                provideTextForRow(row, "Hair color: ", facialInfo.hairColor)
                row = createRow(context, table)
                provideTextForRow(row, "Gender: ", facialInfo.gender)
                row = createRow(context, table)
                provideTextForRow(row, "Expression:", facialInfo.expression)
            }
        }
    }
}