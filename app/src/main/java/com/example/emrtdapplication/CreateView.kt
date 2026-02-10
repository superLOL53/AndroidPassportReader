package com.example.emrtdapplication

import android.content.Context
import android.graphics.text.LineBreaker
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children

abstract class CreateView {
    protected var alternate = false

    fun setBackgroundColor(context: Context, parent: LinearLayout, hashMatch : Boolean) {
        if (EMRTD.ldS1Application.efSod.isValid && hashMatch) {
            parent.setBackgroundColor(context.resources.getColor(R.color.green, null))
        } else if (EMRTD.ldS1Application.efSod.isValid || hashMatch) {
            parent.setBackgroundColor(context.resources.getColor(R.color.yellow, null))
        } else {
            parent.setBackgroundColor(context.resources.getColor(R.color.red, null))
        }
    }

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
        row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        row.gravity = Gravity.CENTER
        if (alternate) {
            row.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            row.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        val description = TextView(context)
        description.gravity = Gravity.START
        description.setPadding(10, 0, 0, 0)
        val value = TextView(context)
        value.gravity = Gravity.END
        value.setPadding(0, 0, 10, 0)
        row.addView(description)
        row.addView(value)
        parent.addView(row)
        return row
    }

    protected fun <T : LinearLayout> createTable(context: Context, parent: T) : TableLayout {TableLayout(context)
        val table = TableLayout(context)
        table.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
        table.isStretchAllColumns = true
        parent.addView(table)
        return table
    }

    protected fun <T : LinearLayout> createHeader(context: Context, parent: T, headerLine : String) {
        val text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        text.text = headerLine
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        parent.addView(text)
    }

    @OptIn(ExperimentalStdlibApi::class)
    protected fun <T : LinearLayout> createSignatureView(context: Context, parent: T, signature: ByteArray) {
        var text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        text.text = context.getString(R.string.signature)
        parent.addView(text)
        text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        text.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        text.maxLines = 10
        text.text = signature.toHexString(HexFormat { upperCase = true;bytes.byteSeparator=" " })
        parent.addView(text)
    }

    @OptIn(ExperimentalStdlibApi::class)
    protected fun <T : LinearLayout> createPublicKeyView(context: Context, parent: T, publicKey: ByteArray) {
        var text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        text.text = context.getString(R.string.public_key)
        parent.addView(text)
        text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        text.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        text.maxLines = 10
        text.text = publicKey.toHexString(HexFormat { upperCase = true;bytes.byteSeparator=" " })
        parent.addView(text)
    }

    /**
     * Create views to display contents of the file in the app
     */
    abstract fun <T : LinearLayout> createView(context: Context, parent: T)
}