package com.example.emrtdapplication

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children

interface CreateView {

    /**
     * Fill in the text for [row] with [description] and [value]
     * @param row The row to fill in texts
     * @param description The meaning of the [value]
     * @param value The value of the row
     */
    fun provideTextForRow(row : TableRow, description : String, value : String) {
        var i = true
        for (t in row.children) {
            if (i) {
                (t as TextView).text = description
            } else {
                (t as TextView).text = value
            }
            i = !i
        }
    }

    /**
     * Creates a row in a [TableLayout] to display information in the file
     * @param context The context of the view
     * @param parent The parent layout of the created row
     * @return The created row
     */
    fun createRow(context : Context, parent: LinearLayout) : TableRow {
        val row = TableRow(context)
        row.gravity = Gravity.CENTER
        val description = TextView(context)
        description.gravity = Gravity.CENTER
        val value = TextView(context)
        value.gravity = Gravity.CENTER
        row.addView(description)
        row.addView(value)
        parent.addView(row)
        return row
    }

    /**
     * Create views to display contents of the file in the app
     */
    fun <T : LinearLayout> createView(context: Context, parent: T)
}