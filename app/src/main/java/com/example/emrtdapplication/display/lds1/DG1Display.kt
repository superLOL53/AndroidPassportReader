package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableRow
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.display.lds2.records.BIRTH_DATE
import com.example.emrtdapplication.display.lds2.records.DOCUMENT_NUMBER
import com.example.emrtdapplication.display.lds2.records.EXPIRATION_DATE
import com.example.emrtdapplication.display.lds2.records.NATIONALITY
import com.example.emrtdapplication.display.lds2.records.SEX

object DG1Display: CreateView() {

    /**
     * Create the views for the information in the file
     *
     * @param context The context in which the view is generated
     * @param parent The parent of the to create views
     */
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        var row: TableRow
        if (EMRTD.ldS1Application.dg1.holderName != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                NAME,
                EMRTD.ldS1Application.dg1.holderName!!
            )
        }
        if (EMRTD.ldS1Application.dg1.dateOfBirth != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                BIRTH_DATE,
                EMRTD.ldS1Application.dg1.dateOfBirth!!
            )
        }
        if (EMRTD.ldS1Application.dg1.sex != 0.toChar()) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                SEX,
                EMRTD.ldS1Application.dg1.sex.toString()
            )
        }
        if (EMRTD.ldS1Application.dg1.dateOfExpiry != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                EXPIRATION_DATE,
                EMRTD.ldS1Application.dg1.dateOfExpiry!!
            )
        }
        if (EMRTD.ldS1Application.dg1.nationality != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                NATIONALITY,
                EMRTD.ldS1Application.dg1.nationality!!
            )
        }
        if (EMRTD.showDetails) {
            if (EMRTD.ldS1Application.dg1.issuerCode != null) {
                row = createRow(context, parent)
                provideTextForRow(
                    row,
                    ISSUER,
                    EMRTD.ldS1Application.dg1.issuerCode!!
                )
            }
            if (EMRTD.ldS1Application.dg1.documentCode != null) {
                row = createRow(context, parent)
                provideTextForRow(
                    row,
                    DOCUMENT_CODE,
                    EMRTD.ldS1Application.dg1.documentCode!!
                )
            }
            if (EMRTD.ldS1Application.dg1.documentNumber != null) {
                row = createRow(context, parent)
                provideTextForRow(
                    row,
                    DOCUMENT_NUMBER,
                    EMRTD.ldS1Application.dg1.documentNumber!!
                )
            }
            if (EMRTD.ldS1Application.dg1.optionalDataDocumentNumber != null) {
                row = createRow(context, parent)
                provideTextForRow(
                    row,
                    "$OPTIONAL_DATA/$DOCUMENT_NUMBER",
                    EMRTD.ldS1Application.dg1.optionalDataDocumentNumber!!
                )
            }
            if (EMRTD.ldS1Application.dg1.optionalData != null) {
                row = createRow(context, parent)
                provideTextForRow(
                    row,
                    OPTIONAL_DATA,
                    EMRTD.ldS1Application.dg1.optionalData!!
                )
            }
        }
    }
}