package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import kotlin.collections.iterator

class LDS1Display(private val lds1ViewLayout: LinearLayout) : CreateView() {

    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        VerificationDisplay.createView(context, lds1ViewLayout.findViewById<TableLayout>(R.id.verification_table))
        VerificationDisplay.setBackgroundColor(context, lds1ViewLayout.findViewById(R.id.verification_layout), EMRTD.ldS1Application.efSod.isValid)
        for (ef in EMRTD.ldS1Application.efMap) {
            if (!ef.value.isPresent || (!EMRTD.showUnparsedContent && !ef.value.isParsed)) {
                try {
                    when (ef.key) {
                        0x01.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg1layout))
                        0x02.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg2layout))
                        0x03.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg3layout))
                        0x04.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg4layout))
                        0x05.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg5layout))
                        0x06.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg6layout))
                        0x07.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg7layout))
                        0x08.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg8layout))
                        0x09.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg9layout))
                        0x0A.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg10layout))
                        0x0B.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg11layout))
                        0x0C.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg12layout))
                        0x0D.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg13layout))
                        0x0E.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg14layout))
                        0x0F.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg15layout))
                        0x10.toByte() -> lds1ViewLayout.removeView(parent.findViewById<LinearLayout>(R.id.dg16layout))
                    }
                } catch (_: Exception) {
                }
            } else if (!ef.value.isRead) {
                val unableReadView = TextView(context)
                unableReadView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                unableReadView.text = context.getString(R.string.unable_to_read_file_from_passport)
                when (ef.key) {
                    0x01.toByte() -> parent.findViewById<LinearLayout>(R.id.dg1layout)
                        .addView(unableReadView)
                    0x02.toByte() -> parent.findViewById<LinearLayout>(R.id.dg2layout)
                        .addView(unableReadView)
                    0x03.toByte() -> parent.findViewById<LinearLayout>(R.id.dg3layout)
                        .addView(unableReadView)
                    0x04.toByte() -> parent.findViewById<LinearLayout>(R.id.dg4layout)
                        .addView(unableReadView)
                    0x05.toByte() -> parent.findViewById<LinearLayout>(R.id.dg5layout)
                        .addView(unableReadView)
                    0x06.toByte() -> parent.findViewById<LinearLayout>(R.id.dg6layout)
                        .addView(unableReadView)
                    0x07.toByte() -> parent.findViewById<LinearLayout>(R.id.dg7layout)
                        .addView(unableReadView)
                    0x08.toByte() -> parent.findViewById<LinearLayout>(R.id.dg8layout)
                        .addView(unableReadView)
                    0x09.toByte() -> parent.findViewById<LinearLayout>(R.id.dg9layout)
                        .addView(unableReadView)
                    0x0A.toByte() -> parent.findViewById<LinearLayout>(R.id.dg10layout)
                        .addView(unableReadView)
                    0x0B.toByte() -> parent.findViewById<LinearLayout>(R.id.dg11layout)
                        .addView(unableReadView)
                    0x0C.toByte() -> parent.findViewById<LinearLayout>(R.id.dg12layout)
                        .addView(unableReadView)
                    0x0D.toByte() -> parent.findViewById<LinearLayout>(R.id.dg13layout)
                        .addView(unableReadView)
                    0x0E.toByte() -> parent.findViewById<LinearLayout>(R.id.dg14layout)
                        .addView(unableReadView)
                    0x0F.toByte() -> parent.findViewById<LinearLayout>(R.id.dg15layout)
                        .addView(unableReadView)
                    0x10.toByte() -> parent.findViewById<LinearLayout>(R.id.dg16layout)
                        .addView(unableReadView)
                }
            } else {
                when (ef.key) {
                    0x01.toByte() -> {
                        DG1Display.createView<LinearLayout>(context, parent.findViewById<TableLayout>(R.id.dg1table))
                        DG1Display.setBackgroundColor(context, parent.findViewById(R.id.dg1layout), EMRTD.ldS1Application.dg1.matchHash)
                    }
                    0x02.toByte() -> {
                        DG2Display.createView(context, parent.findViewById(R.id.dg2layout))
                        DG2Display.setBackgroundColor(context, parent.findViewById(R.id.dg2layout), EMRTD.ldS1Application.dg2.matchHash)
                    }
                    0x03.toByte() -> {
                        DG3Display.createView(context, parent.findViewById(R.id.dg3layout))
                        DG3Display.setBackgroundColor(context, parent.findViewById(R.id.dg3layout), EMRTD.ldS1Application.dg3.matchHash)
                    }
                    0x04.toByte() -> {
                        DG4Display.createView(context, parent.findViewById<TableLayout>(R.id.dg4layout))
                        DG4Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg4layout), EMRTD.ldS1Application.dg4.matchHash)
                    }
                    0x05.toByte() -> {
                        DG5Display.createView(context, parent.findViewById(R.id.dg5layout))
                        DG5Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg5layout), EMRTD.ldS1Application.dg5.matchHash)
                    }
                    0x06.toByte() -> {
                        DG6Display.createView(context, parent.findViewById<TableLayout>(R.id.dg6layout))
                        DG6Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg6layout), EMRTD.ldS1Application.dg6.matchHash)
                    }
                    0x07.toByte() -> {
                        DG7Display.createView(context, parent.findViewById<TableLayout>(R.id.dg7layout))
                        DG7Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg7layout), EMRTD.ldS1Application.dg7.matchHash)
                    }
                    0x08.toByte() -> {
                        DG8Display.createView(context, parent.findViewById<TableLayout>(R.id.dg8layout))
                        DG8Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg8layout), EMRTD.ldS1Application.dg8.matchHash)
                    }
                    0x09.toByte() -> {
                        DG9Display.createView(context, parent.findViewById<TableLayout>(R.id.dg9layout))
                        DG9Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg9layout), EMRTD.ldS1Application.dg9.matchHash)
                    }
                    0x0A.toByte() -> {
                        DG10Display.createView(context, parent.findViewById<TableLayout>(R.id.dg10layout))
                        DG10Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg10layout), EMRTD.ldS1Application.dg10.matchHash)
                    }
                    0x0B.toByte() -> {
                        DG11Display.createView(context, parent.findViewById(R.id.dg11table))
                        DG11Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg11layout), EMRTD.ldS1Application.dg11.matchHash)
                    }
                    0x0C.toByte() -> {
                        DG12Display.createView(context, parent.findViewById(R.id.dg12table))
                        DG12Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg12layout), EMRTD.ldS1Application.dg12.matchHash)
                    }
                    0x0D.toByte() -> {
                        DG13Display.createView(context, parent.findViewById<TableLayout>(R.id.dg13layout))
                        DG13Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg13layout), EMRTD.ldS1Application.dg13.matchHash)
                    }
                    0x0E.toByte() -> {
                        DG14Display.createView(context, parent.findViewById<TableLayout>(R.id.dg14layout))
                        DG14Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg14layout), EMRTD.ldS1Application.dg14.matchHash)
                    }
                    0x0F.toByte() -> {
                        DG15Display.createView(context, parent.findViewById<TableLayout>(R.id.dg15layout))
                        DG15Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg15layout), EMRTD.ldS1Application.dg15.matchHash)
                    }
                    0x10.toByte() -> {
                        DG16Display.createView(context, parent.findViewById<TableLayout>(R.id.dg16layout))
                        DG16Display.setBackgroundColor(context, parent.findViewById<TableLayout>(R.id.dg16layout), EMRTD.ldS1Application.dg16.matchHash)
                    }
                }
            }
        }
    }
}