package com.example.emrtdapplication.display.lds2.records

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.lds2.EntryExitRecord

class EntryExitRecordDisplay(private val entryExitRecord: EntryExitRecord, val isEntryRecord : Boolean) : CreateView() {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (isEntryRecord) {
            createHeader(context, parent, "Entry Record ${entryExitRecord.recordNumber}")
        } else {
            createHeader(context, parent, "Exit Record ${entryExitRecord.recordNumber}")
        }
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(row, "State:", entryExitRecord.state)
        row = createRow(context, table)
        provideTextForRow(row, "Date:", entryExitRecord.date)
        row = createRow(context, table)
        provideTextForRow(row, "Inspection Authority:", entryExitRecord.inspectionAuthority)
        row = createRow(context, table)
        provideTextForRow(row, "Inspection Location:", entryExitRecord.inspectionLocation)
        row = createRow(context, table)
        provideTextForRow(row, "Inspection Reference:", entryExitRecord.inspectorReference)
        row = createRow(context, table)
        provideTextForRow(row, "Certificate Reference:", "${entryExitRecord.certificateReference}")
        if (entryExitRecord.visaStatus != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Visa Status:", entryExitRecord.visaStatus)
        }
        if (entryExitRecord.inspectionResult != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Inspection Result:", entryExitRecord.inspectionResult)
        }
        if (entryExitRecord.travelMode != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Travel Mode:", entryExitRecord.travelMode)
        }
        if (entryExitRecord.stayDuration != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Stay Duration:", "${entryExitRecord.stayDuration}")
        }
        if (entryExitRecord.conditions != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Conditions:", entryExitRecord.conditions)
        }
        createSignatureView(context, parent, entryExitRecord.signature)
    }
}