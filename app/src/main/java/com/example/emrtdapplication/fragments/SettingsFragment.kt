package com.example.emrtdapplication.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment(R.layout.settings), CompoundButton.OnCheckedChangeListener {
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
        this.view = view.findViewById(R.id.settingsScroll)
        val details = view.findViewById<SwitchMaterial>(R.id.detail_button)
        details.isChecked = EMRTD.showDetails
        details.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        EMRTD.showDetails = isChecked
    }
}