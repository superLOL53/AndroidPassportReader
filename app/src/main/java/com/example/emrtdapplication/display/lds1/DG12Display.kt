package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.example.emrtdapplication.display.lds2.records.ISSUE_DATE

object DG12Display: CreateView() {

    fun createView(context: Context, parent: TableLayout) {
        var row: TableRow
        if (EMRTD.ldS1Application.dg12.issuingAuthority != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                ISSUING_AUTHORITY,
                EMRTD.ldS1Application.dg12.issuingAuthority!!
            )
        }
        if (EMRTD.ldS1Application.dg12.dateOfIssue != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                ISSUE_DATE,
                EMRTD.ldS1Application.dg12.dateOfIssue!!
            )
        }
        if (EMRTD.ldS1Application.dg12.otherPersons != null) {
            for (p in EMRTD.ldS1Application.dg12.otherPersons) {
                row = createRow(context, parent)
                provideTextForRow(row, OTHER_PERSON, p)
            }
        }
        if (EMRTD.ldS1Application.dg12.endorsements != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                ENDORSEMENT,
                EMRTD.ldS1Application.dg12.endorsements!!
            )
        }
        if (EMRTD.ldS1Application.dg12.taxExitRequirements != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                TAX_EXIT_REQUIREMENTS,
                EMRTD.ldS1Application.dg12.taxExitRequirements!!
            )
        }
        if (EMRTD.ldS1Application.dg12.documentPersonalizationTime != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                PERSONALIZATION_TIME,
                EMRTD.ldS1Application.dg12.documentPersonalizationTime!!
            )
        }
        if (EMRTD.ldS1Application.dg12.personalizationSystemSerialNumber != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                PERSONALIZATION_DEVICE_SERIAL_NUMBER,
                EMRTD.ldS1Application.dg12.personalizationSystemSerialNumber!!
            )
        }
        if (EMRTD.ldS1Application.dg12.front != null) {
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
            val imageView = ImageView(context)
            imageView.setImageBitmap(EMRTD.ldS1Application.dg12.front)
            row = TableRow(context)
            row.addView(imageView)
            row.gravity = Gravity.CENTER
            box.addView(row)
        }
        if (EMRTD.ldS1Application.dg12.rear != null) {
            val box = LinearLayout(context)
            box.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (alternate) {
                box.setBackgroundColor(
                    context.resources.getColor(R.color.gray,
                        null)
                )
            } else {
                box.setBackgroundColor(
                    context.resources.getColor(R.color.black, null)
                )
            }
            alternate = !alternate
            parent.addView(box)
            val imageView = ImageView(context)
            imageView.setImageBitmap(EMRTD.ldS1Application.dg12.rear)
            row = TableRow(context)
            row.addView(imageView)
            row.gravity = Gravity.CENTER
            box.addView(row)
        }
    }

    override fun <T: LinearLayout> createView(
        context: Context,
        parent: T
    ) {
        TODO("Not yet implemented")
    }
}