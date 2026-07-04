package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.example.emrtdapplication.constants.TlvTags.DG10_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG11_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG12_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG13_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG14_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG15_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG16_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG1_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG2_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG3_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG4_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG5_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG6_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG7_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG8_SHORT_EF_ID
import com.example.emrtdapplication.constants.TlvTags.DG9_SHORT_EF_ID

class LDS1Display(private val lds1ViewLayout: LinearLayout): CreateView() {

    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        VerificationDisplay.createView(
            context,
            lds1ViewLayout.findViewById<TableLayout>(R.id.verification_table)
        )
        VerificationDisplay.setBackgroundColor(
            context,
            lds1ViewLayout.findViewById(R.id.verification_layout),
            EMRTD.ldS1Application.efSod.isValid
        )
        for (ef in EMRTD.ldS1Application.efMap) {
            if (!ef.value.isPresent ||
                (!EMRTD.showUnparsedContent && !ef.value.isParsed)
            ) {
                try {
                    when (ef.key) {
                        DG1_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg1layout)
                            )
                        DG2_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg2layout)
                            )
                        DG3_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg3layout)
                            )
                        DG4_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg4layout)
                            )
                        DG5_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg5layout)
                            )
                        DG6_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg6layout)
                            )
                        DG7_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg7layout)
                            )
                        DG8_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg8layout)
                            )
                        DG9_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg9layout)
                            )
                        DG10_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg10layout)
                            )
                        DG11_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg11layout)
                            )
                        DG12_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg12layout)
                            )
                        DG13_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg13layout)
                            )
                        DG14_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg14layout)
                            )
                        DG15_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg15layout)
                            )
                        DG16_SHORT_EF_ID ->
                            lds1ViewLayout.removeView(
                                parent.findViewById<LinearLayout>(R.id.dg16layout)
                            )
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
                    DG1_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg1layout).
                        addView(unableReadView)
                    DG2_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg2layout).
                        addView(unableReadView)
                    DG3_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg3layout).
                        addView(unableReadView)
                    DG4_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg4layout).
                        addView(unableReadView)
                    DG5_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg5layout).
                        addView(unableReadView)
                    DG6_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg6layout).
                        addView(unableReadView)
                    DG7_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg7layout).
                        addView(unableReadView)
                    DG8_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg8layout).
                        addView(unableReadView)
                    DG9_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg9layout).
                        addView(unableReadView)
                    DG10_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg10layout).
                        addView(unableReadView)
                    DG11_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg11layout).
                        addView(unableReadView)
                    DG12_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg12layout).
                        addView(unableReadView)
                    DG13_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg13layout).
                        addView(unableReadView)
                    DG14_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg14layout).
                        addView(unableReadView)
                    DG15_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg15layout).
                        addView(unableReadView)
                    DG16_SHORT_EF_ID ->
                        parent.findViewById<LinearLayout>(R.id.dg16layout).
                        addView(unableReadView)
                }
            } else {
                when (ef.key) {
                    DG1_SHORT_EF_ID -> {
                        DG1Display.createView<LinearLayout>(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg1table)
                        )
                        DG1Display.setBackgroundColor(
                            context,
                            parent.findViewById(R.id.dg1layout),
                            EMRTD.ldS1Application.dg1.matchHash
                        )
                    }
                    DG2_SHORT_EF_ID -> {
                        DG2Display.createView(
                            context,
                            parent.findViewById(R.id.dg2layout)
                        )
                        DG2Display.setBackgroundColor(
                            context,
                            parent.findViewById(R.id.dg2layout),
                            EMRTD.ldS1Application.dg2.matchHash
                        )
                    }
                    DG3_SHORT_EF_ID -> {
                        DG3Display.createView(
                            context,
                            parent.findViewById(R.id.dg3layout)
                        )
                        DG3Display.setBackgroundColor(
                            context,
                            parent.findViewById(R.id.dg3layout),
                            EMRTD.ldS1Application.dg3.matchHash
                        )
                    }
                    DG4_SHORT_EF_ID -> {
                        DG4Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg4layout)
                        )
                        DG4Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg4layout),
                            EMRTD.ldS1Application.dg4.matchHash
                        )
                    }
                    DG5_SHORT_EF_ID -> {
                        DG5Display.createView(
                            context,
                            parent.findViewById(R.id.dg5layout)
                        )
                        DG5Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg5layout),
                            EMRTD.ldS1Application.dg5.matchHash
                        )
                    }
                    DG6_SHORT_EF_ID -> {
                        DG6Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg6layout)
                        )
                        DG6Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg6layout),
                            EMRTD.ldS1Application.dg6.matchHash
                        )
                    }
                    DG7_SHORT_EF_ID -> {
                        DG7Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg7layout)
                        )
                        DG7Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg7layout),
                            EMRTD.ldS1Application.dg7.matchHash
                        )
                    }
                    DG8_SHORT_EF_ID -> {
                        DG8Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg8layout)
                        )
                        DG8Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg8layout),
                            EMRTD.ldS1Application.dg8.matchHash
                        )
                    }
                    DG9_SHORT_EF_ID -> {
                        DG9Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg9layout)
                        )
                        DG9Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg9layout),
                            EMRTD.ldS1Application.dg9.matchHash
                        )
                    }
                    DG10_SHORT_EF_ID -> {
                        DG10Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg10layout)
                        )
                        DG10Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg10layout),
                            EMRTD.ldS1Application.dg10.matchHash
                        )
                    }
                    DG11_SHORT_EF_ID -> {
                        DG11Display.createView(
                            context,
                            parent.findViewById(R.id.dg11table)
                        )
                        DG11Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg11layout),
                            EMRTD.ldS1Application.dg11.matchHash
                        )
                    }
                    DG12_SHORT_EF_ID -> {
                        DG12Display.createView(
                            context,
                            parent.findViewById(R.id.dg12table)
                        )
                        DG12Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg12layout),
                            EMRTD.ldS1Application.dg12.matchHash
                        )
                    }
                    DG13_SHORT_EF_ID -> {
                        DG13Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg13layout)
                        )
                        DG13Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg13layout),
                            EMRTD.ldS1Application.dg13.matchHash
                        )
                    }
                    DG14_SHORT_EF_ID -> {
                        DG14Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg14layout)
                        )
                        DG14Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg14layout),
                            EMRTD.ldS1Application.dg14.matchHash
                        )
                    }
                    DG15_SHORT_EF_ID -> {
                        DG15Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg15layout)
                        )
                        DG15Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg15layout),
                            EMRTD.ldS1Application.dg15.matchHash
                        )
                    }
                    DG16_SHORT_EF_ID -> {
                        DG16Display.createView(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg16layout)
                        )
                        DG16Display.setBackgroundColor(
                            context,
                            parent.findViewById<TableLayout>(R.id.dg16layout),
                            EMRTD.ldS1Application.dg16.matchHash
                        )
                    }
                }
            }
        }
    }
}