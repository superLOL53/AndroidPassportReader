package com.example.emrtdapplication.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.emrtdapplication.R
import com.example.emrtdapplication.display.lds1.LDS1Display

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
        val display = LDS1Display(lds1ViewLayout)
        display.createView(requireContext(), lds1ViewLayout)
    }
}