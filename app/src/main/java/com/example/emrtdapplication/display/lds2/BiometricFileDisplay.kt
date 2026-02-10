package com.example.emrtdapplication.display.lds2

import android.content.Context
import android.graphics.text.LineBreaker
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.R
import com.example.emrtdapplication.lds2.Biometric

class BiometricFileDisplay(private val biometricFile: Biometric) : CreateView() {
    @OptIn(ExperimentalStdlibApi::class)
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        createHeader(context, parent, "Biometric File ${biometricFile.fileID}")
        val table = createTable(context, parent)
        val row = createRow(context, table)
        var text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        text.text = context.getString(R.string.biometric_content)
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
        text.text = biometricFile.biometricData.toHexString(HexFormat { upperCase = true;bytes.byteSeparator=" " })
        parent.addView(text)
        provideTextForRow(row, "Certificate Reference:", "${biometricFile.certificateReference}")
        createSignatureView(context, parent, biometricFile.signature)
    }
}