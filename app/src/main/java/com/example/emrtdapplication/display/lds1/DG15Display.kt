package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.ALGORITHM_IDENTIFIER
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.PUBLIC_KEY

object DG15Display: CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg15.rawFileContent == null ||
            EMRTD.ldS1Application.dg15.publicKeyInfo == null
        ) {
            return
        }
        if (EMRTD.showDetails) {
            var row = createRow(context, parent)
            provideTextForRow(
                row,
                ALGORITHM_IDENTIFIER,
                EMRTD.ldS1Application.dg15.publicKeyInfo!!.algorithm.algorithm.id
            )
            row = createRow(context, parent)
            provideTextForRow(
                row,
                PUBLIC_KEY,
                EMRTD.ldS1Application.dg15.publicKeyInfo!!.publicKeyData.string
            )
        }
    }
}