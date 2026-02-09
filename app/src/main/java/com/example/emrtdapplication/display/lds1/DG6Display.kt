package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.graphics.text.LineBreaker
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R

object DG6Display : CreateView() {
    @OptIn(ExperimentalStdlibApi::class)
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg6.rawFileContent == null) return
        if (EMRTD.showDetails) {
           var text = TextView(context)
           text.layoutParams = ViewGroup.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT
           )
           text.gravity = Gravity.CENTER
           text.text = context.getString(R.string.dg6_file_content)
           parent.addView(text)
           text = TextView(context)
           text.layoutParams = ViewGroup.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT
           )
           text.gravity = Gravity.CENTER
           text.maxLines = 10
           text.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
           text.text = EMRTD.ldS1Application.dg6.rawFileContent?.toHexString(HexFormat { upperCase=true; bytes.byteSeparator = " "})
        }
    }
}