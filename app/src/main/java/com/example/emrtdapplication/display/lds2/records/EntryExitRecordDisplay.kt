package com.example.emrtdapplication.display.lds2.records

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.lds2.EntryExitRecord

class EntryExitRecordDisplay(
    private val entryExitRecord: EntryExitRecord,
    val isEntryRecord: Boolean
): CreateView() {
    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        if (isEntryRecord) {
            createHeader(
                context,
                parent,
                ENTRY_RECORD + "${entryExitRecord.recordNumber}"
            )
        } else {
            createHeader(
                context,
                parent,
                EXIT_RECORD + "${entryExitRecord.recordNumber}"
            )
        }
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            STATE,
            entryExitRecord.state
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            DATE,
            entryExitRecord.date
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            INSPECTION_AUTHORITY,
            entryExitRecord.inspectionAuthority
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            INSPECTION_LOCATION,
            entryExitRecord.inspectionLocation
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            INSPECTION_REFERENCE,
            entryExitRecord.inspectorReference
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            CERTIFICATE_REFERENCE,
            "${entryExitRecord.certificateReference}"
        )
        if (entryExitRecord.visaStatus != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                VISA_STATUS,
                entryExitRecord.visaStatus
            )
        }
        if (entryExitRecord.inspectionResult != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                INSPECTION_RESULT,
                entryExitRecord.inspectionResult
            )
        }
        if (entryExitRecord.travelMode != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                TRAVEL_MODE,
                entryExitRecord.travelMode
            )
        }
        if (entryExitRecord.stayDuration != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                STAY_DURATION,
                "${entryExitRecord.stayDuration}"
            )
        }
        if (entryExitRecord.conditions != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                CONDITIONS,
                entryExitRecord.conditions
            )
        }
        createSignatureView(context, parent, entryExitRecord.signature)
    }
}