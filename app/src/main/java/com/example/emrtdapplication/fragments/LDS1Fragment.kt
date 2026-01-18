package com.example.emrtdapplication.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R

/**
 * Fragment for displaying contents of the LDS1 application
 *
 * @property view View for displaying LDS1 application content in the fragment
 */
class LDS1Fragment() : Fragment(R.layout.lds1) {
    private var view : ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (view != null) {
            val toReplace = requireView().findViewById<ScrollView>(R.id.lds1scroll)
            val rootView = toReplace.parent as ViewGroup
            rootView.removeView(toReplace)
            rootView.addView(view)
        }
    }

    /**
     * Creates views to display the contents of files in the LDS1 application
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lds1ViewLayout = view.findViewById<LinearLayout>(R.id.lds1layout)
        this.view = view.findViewById(R.id.lds1scroll)
        for (ef in EMRTD.ldS1Application.efMap) {
            if (!ef.value.isPresent) {
                try {
                    when (ef.key) {
                        0x01.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg1layout))
                        0x02.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg2layout))
                        0x03.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg3layout))
                        0x04.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg4layout))
                        0x05.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg5layout))
                        0x06.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg6layout))
                        0x07.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg7layout))
                        0x08.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg8layout))
                        0x09.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg9layout))
                        0x0A.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg10layout))
                        0x0B.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg11layout))
                        0x0C.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg12layout))
                        0x0D.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg13layout))
                        0x0E.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg14layout))
                        0x0F.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg15layout))
                        0x10.toByte() -> lds1ViewLayout.removeView(view.findViewById<LinearLayout>(R.id.dg16layout))
                    }
                } catch (_: Exception) {
                }
            } else if (!ef.value.isRead) {
                val unableReadView = TextView(context)
                unableReadView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                unableReadView.text = getString(R.string.unable_to_read_file_from_passport)
                when (ef.key) {
                    0x01.toByte() -> view.findViewById<LinearLayout>(R.id.dg1layout)
                        .addView(unableReadView)

                    0x02.toByte() -> view.findViewById<LinearLayout>(R.id.dg2layout)
                        .addView(unableReadView)

                    0x03.toByte() -> view.findViewById<LinearLayout>(R.id.dg3layout)
                        .addView(unableReadView)

                    0x04.toByte() -> view.findViewById<LinearLayout>(R.id.dg4layout)
                        .addView(unableReadView)

                    0x05.toByte() -> view.findViewById<LinearLayout>(R.id.dg5layout)
                        .addView(unableReadView)

                    0x06.toByte() -> view.findViewById<LinearLayout>(R.id.dg6layout)
                        .addView(unableReadView)

                    0x07.toByte() -> view.findViewById<LinearLayout>(R.id.dg7layout)
                        .addView(unableReadView)

                    0x08.toByte() -> view.findViewById<LinearLayout>(R.id.dg8layout)
                        .addView(unableReadView)

                    0x09.toByte() -> view.findViewById<LinearLayout>(R.id.dg9layout)
                        .addView(unableReadView)

                    0x0A.toByte() -> view.findViewById<LinearLayout>(R.id.dg10layout)
                        .addView(unableReadView)

                    0x0B.toByte() -> view.findViewById<LinearLayout>(R.id.dg11layout)
                        .addView(unableReadView)

                    0x0C.toByte() -> view.findViewById<LinearLayout>(R.id.dg12layout)
                        .addView(unableReadView)

                    0x0D.toByte() -> view.findViewById<LinearLayout>(R.id.dg13layout)
                        .addView(unableReadView)

                    0x0E.toByte() -> view.findViewById<LinearLayout>(R.id.dg14layout)
                        .addView(unableReadView)

                    0x0F.toByte() -> view.findViewById<LinearLayout>(R.id.dg15layout)
                        .addView(unableReadView)

                    0x10.toByte() -> view.findViewById<LinearLayout>(R.id.dg16layout)
                        .addView(unableReadView)
                }
            } else {
                when (ef.key) {
                    0x01.toByte() -> EMRTD.ldS1Application.dg1.createViews<LinearLayout>(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg1table)
                    )

                    0x02.toByte() -> EMRTD.ldS1Application.dg2.createViews(
                        requireContext(),
                        view.findViewById(R.id.dg2layout)
                    )

                    0x03.toByte() -> EMRTD.ldS1Application.dg3.createViews(
                        requireContext(),
                        view.findViewById(R.id.dg3layout)
                    )

                    0x04.toByte() -> EMRTD.ldS1Application.dg4.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg4layout)
                    )

                    0x05.toByte() -> EMRTD.ldS1Application.dg5.createViews(
                        requireContext(),
                        view.findViewById(R.id.dg5layout)
                    )

                    0x06.toByte() -> EMRTD.ldS1Application.dg6.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg6layout)
                    )

                    0x07.toByte() -> EMRTD.ldS1Application.dg7.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg7layout)
                    )

                    0x08.toByte() -> EMRTD.ldS1Application.dg8.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg8layout)
                    )

                    0x09.toByte() -> EMRTD.ldS1Application.dg9.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg9layout)
                    )

                    0x0A.toByte() -> EMRTD.ldS1Application.dg10.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg10layout)
                    )

                    0x0B.toByte() -> EMRTD.ldS1Application.dg11.createViews(
                        requireContext(),
                        view.findViewById(R.id.dg11table)
                    )

                    0x0C.toByte() -> EMRTD.ldS1Application.dg12.createView(
                        requireContext(),
                        view.findViewById(R.id.dg12table)
                    )

                    0x0D.toByte() -> EMRTD.ldS1Application.dg13.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg13layout)
                    )

                    0x0E.toByte() -> EMRTD.ldS1Application.dg14.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg14layout)
                    )

                    0x0F.toByte() -> EMRTD.ldS1Application.dg15.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg15layout)
                    )

                    0x10.toByte() -> EMRTD.ldS1Application.dg16.createViews(
                        requireContext(),
                        view.findViewById<TableLayout>(R.id.dg16layout)
                    )
                }
            }
        }
    }
}