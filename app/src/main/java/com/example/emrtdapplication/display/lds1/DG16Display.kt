package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD

object DG16Display : CreateView {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg16.persons == null) return
        var i = 0
        for (p in EMRTD.ldS1Application.dg16.persons) {
            val table = TableLayout(context)
            table.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            parent.addView(table)
            var row = TableRow(context)
            row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            table.addView(row)
            val text = TextView(context)
            val s = "Emergency Contact $i"
            text.text = s
            row.addView(text)
            i++
            row = createRow(context, table)
            provideTextForRow(row, "Name:", p.name)
            row = createRow(context, table)
            provideTextForRow(row, "Address:", p.address)
            row = createRow(context, table)
            provideTextForRow(row, "Telephone:", p.telephone)
            row = createRow(context, table)
            provideTextForRow(row, "Date data recorded:", p.date)
        }
    }
}