package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.graphics.text.LineBreaker
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD

object DG5Display : CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    @OptIn(ExperimentalStdlibApi::class)
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg5.rawFileContent == null) return
        val view = TextView(context)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        view.maxLines = 10
        view.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        view.ellipsize = TextUtils.TruncateAt.END
        view.text = EMRTD.ldS1Application.dg5.rawFileContent!!.toHexString(HexFormat { upperCase = true; bytes.byteSeparator = " "})
        parent.addView(view)
    }
}