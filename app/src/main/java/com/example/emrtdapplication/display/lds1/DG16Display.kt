package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.utils.Person

object DG16Display: CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg16.persons == null) return
        for ((i, p) in (EMRTD.ldS1Application.dg16.persons as Array<out Person>).withIndex()) {
            val table = TableLayout(context)
            table.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            parent.addView(table)
            var row = TableRow(context)
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            table.addView(row)
            val text = TextView(context)
            val s = EMERGENCY_CONTACT + "$i"
            text.text = s
            row.addView(text)
            row = createRow(context, table)
            provideTextForRow(row, NAME, p.name)
            row = createRow(context, table)
            provideTextForRow(row, ADDRESS, p.address)
            row = createRow(context, table)
            provideTextForRow(row, TELEPHONE, p.telephone)
            row = createRow(context, table)
            provideTextForRow(row, DATE_RECORDED, p.date)
        }
    }
}