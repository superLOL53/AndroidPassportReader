package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD

object DG12Display : CreateView {

    fun createView(context: Context, parent : TableLayout) {
        var row : TableRow
        if (EMRTD.ldS1Application.dg12.issuingAuthority != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Issuing Authority: ", EMRTD.ldS1Application.dg12.issuingAuthority!!)
        }
        if (EMRTD.ldS1Application.dg12.dateOfIssue != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Date of issue: ", EMRTD.ldS1Application.dg12.dateOfIssue!!)
        }
        if (EMRTD.ldS1Application.dg12.otherPersons != null) {
            for (p in EMRTD.ldS1Application.dg12.otherPersons) {
                row = createRow(context, parent)
                provideTextForRow(row, "Other Person: ", p)
            }
        }
        if (EMRTD.ldS1Application.dg12.endorsements != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Endorsement: ", EMRTD.ldS1Application.dg12.endorsements!!)
        }
        if (EMRTD.ldS1Application.dg12.taxExitRequirements != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Tax/Exit Requirements: ", EMRTD.ldS1Application.dg12.taxExitRequirements!!)
        }
        if (EMRTD.ldS1Application.dg12.documentPersonalizationTime != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Document personalization time: ", EMRTD.ldS1Application.dg12.documentPersonalizationTime!!)
        }
        if (EMRTD.ldS1Application.dg12.personalizationSystemSerialNumber != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Personalization device serial number: ", EMRTD.ldS1Application.dg12.personalizationSystemSerialNumber!!)
        }
        if (EMRTD.ldS1Application.dg12.front != null) {
            val imageView = ImageView(context)
            imageView.setImageBitmap(EMRTD.ldS1Application.dg12.front)
            row = TableRow(context)
            row.addView(imageView)
            row.gravity = Gravity.CENTER
            parent.addView(row)
        }
        if (EMRTD.ldS1Application.dg12.rear != null) {
            val imageView = ImageView(context)
            imageView.setImageBitmap(EMRTD.ldS1Application.dg12.rear)
            row = TableRow(context)
            row.addView(imageView)
            row.gravity = Gravity.CENTER
            parent.addView(row)
        }
    }

    override fun <T : LinearLayout> createView(
        context: Context,
        parent: T
    ) {
        TODO("Not yet implemented")
    }
}