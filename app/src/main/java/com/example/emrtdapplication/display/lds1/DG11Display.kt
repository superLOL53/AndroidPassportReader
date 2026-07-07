package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.example.emrtdapplication.display.lds2.records.BIRTH_DATE

object DG11Display: CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        var row: TableRow
        if (EMRTD.ldS1Application.dg11.fullName != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                FULL_NAME,
                EMRTD.ldS1Application.dg11.fullName!!
            )
        }
        if (EMRTD.ldS1Application.dg11.otherNames != null) {
            for (s in EMRTD.ldS1Application.dg11.otherNames) {
                row = createRow(context, parent)
                provideTextForRow(row, OTHER_NAME, s)
            }
        }
        if (EMRTD.ldS1Application.dg11.personalNumber != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                PERSONAL_NUMBER,
                EMRTD.ldS1Application.dg11.personalNumber!!
            )
        }
        if (EMRTD.ldS1Application.dg11.fullDateOfBirth != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                BIRTH_DATE,
                EMRTD.ldS1Application.dg11.fullDateOfBirth!!
            )
        }
        if (EMRTD.ldS1Application.dg11.placeOfBirth != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                BIRTH_PLACE,
                EMRTD.ldS1Application.dg11.placeOfBirth!!
            )
        }
        if (EMRTD.ldS1Application.dg11.permanentAddress != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                ADDRESS,
                EMRTD.ldS1Application.dg11.permanentAddress!!
            )
        }
        if (EMRTD.ldS1Application.dg11.telephone != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                TELEPHONE,
                EMRTD.ldS1Application.dg11.telephone!!
            )
        }
        if (EMRTD.ldS1Application.dg11.profession != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                PROFESSION,
                EMRTD.ldS1Application.dg11.profession!!
            )
        }
        if (EMRTD.ldS1Application.dg11.title != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                TITLE,
                EMRTD.ldS1Application.dg11.title!!
            )
        }
        if (EMRTD.ldS1Application.dg11.personalSummary != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                SUMMARY,
                EMRTD.ldS1Application.dg11.personalSummary!!
            )
        }
        if (EMRTD.ldS1Application.dg11.otherTDNumbers != null) {
            for (td in EMRTD.ldS1Application.dg11.otherTDNumbers) {
                row = createRow(context, parent)
                provideTextForRow(row, OTHER_TD_NUMBERS, td)
            }
        }
        if (EMRTD.ldS1Application.dg11.custodyInformation != null) {
            row = createRow(context, parent)
            provideTextForRow(
                row,
                CUSTODY_INFORMATION,
                EMRTD.ldS1Application.dg11.custodyInformation!!
            )
        }
        if (EMRTD.ldS1Application.dg11.image != null) {
            val box = LinearLayout(context)
            box.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (alternate) {
                box.setBackgroundColor(
                    context.resources.getColor(R.color.gray, null)
                )
            } else {
                box.setBackgroundColor(
                    context.resources.getColor(R.color.black, null)
                )
            }
            alternate = !alternate
            parent.addView(box)
            val imageView = ImageView(context)
            imageView.setImageBitmap(EMRTD.ldS1Application.dg11.image)
            row = TableRow(context)
            row.addView(imageView)
            row.gravity = Gravity.CENTER
            box.addView(row)
        }
    }
}