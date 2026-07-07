package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.example.emrtdapplication.biometrics.face.FacialRecordData

object DG2Display: CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.efSod.isValid &&
            EMRTD.ldS1Application.dg1.matchHash
        ) {
            parent.setBackgroundColor(
                context.resources.getColor(R.color.green, null)
            )
        } else if (EMRTD.ldS1Application.efSod.isValid ||
            EMRTD.ldS1Application.dg1.matchHash
        ) {
            parent.setBackgroundColor(
                context.resources.getColor(R.color.yellow, null)
            )
        } else {
            parent.setBackgroundColor(
                context.resources.getColor(R.color.red, null)
            )
        }
        if (EMRTD.ldS1Application.dg2.biometricInformation != null &&
            EMRTD.ldS1Application.dg2.biometricInformation!!.biometricInformationList != null
        ) {
            for (
                bios in EMRTD.ldS1Application.dg2.biometricInformation!!.biometricInformationList
            ) {
                if (bios == null) continue
                val biometricData = bios.biometricDataBlock.biometricData as FacialRecordData
                val box = LinearLayout(context)
                box.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                if (alternate) {
                    box.setBackgroundColor(
                        context.resources.getColor(R.color.gray, null)
                    )
                } else {
                    box.setBackgroundColor(
                        context.resources.getColor(R.color.black, null)
                    )
                }
                alternate = !alternate
                parent.addView(box)
                val image = biometricData.image
                val view = ImageView(context)
                view.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                view.setImageBitmap(image)
                box.addView(view)
                if (EMRTD.showDetails) {
                    val facialInfo = biometricData.facialInformation
                    val table = TableLayout(context)
                    table.layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                    table.isStretchAllColumns = true
                    parent.addView(table)
                    var row = createRow(context, table)
                    provideTextForRow(row, EYE_COLOR, facialInfo.eyeColor)
                    row = createRow(context, table)
                    provideTextForRow(row, HAIR_COLOR, facialInfo.hairColor)
                    row = createRow(context, table)
                    provideTextForRow(row, GENDER, facialInfo.gender)
                    row = createRow(context, table)
                    provideTextForRow(row, EXPRESSION, facialInfo.expression)
                }
            }
        }
    }
}