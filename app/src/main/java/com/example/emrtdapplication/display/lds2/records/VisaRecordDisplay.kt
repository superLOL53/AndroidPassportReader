package com.example.emrtdapplication.display.lds2.records

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.lds2.VisaRecord

class VisaRecordDisplay(private val visaRecord: VisaRecord) : CreateView() {
    @OptIn(ExperimentalStdlibApi::class)
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        createHeader(context, parent, "Visa Record ${visaRecord.recordNumber}")
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(row, "Expiration Date:", visaRecord.expirationDate)
        row = createRow(context, table)
        provideTextForRow(row, "Nationality:", visaRecord.nationality)
        row = createRow(context, table)
        provideTextForRow(row, "State:", visaRecord.state)
        row = createRow(context, table)
        provideTextForRow(row, "Document Number:", visaRecord.documentNumber)
        row = createRow(context, table)
        provideTextForRow(row, "Holder Name:", visaRecord.holderName)
        row = createRow(context, table)
        provideTextForRow(row, "Date of Birth:", visaRecord.birthDate)
        row = createRow(context, table)
        provideTextForRow(row, "MRZ:", visaRecord.mrz)
        row = createRow(context, table)
        provideTextForRow(row, "First Name:", visaRecord.givenName)
        row = createRow(context, table)
        provideTextForRow(row, "Surname:", visaRecord.surname)
        row = createRow(context, table)
        provideTextForRow(row, "Date of Issue:", visaRecord.issuanceDate)
        row = createRow(context, table)
        provideTextForRow(row, "Place of Issue:", visaRecord.issuancePlace)
        row = createRow(context, table)
        provideTextForRow(row, "Document Type:", visaRecord.documentType)
        row = createRow(context, table)
        provideTextForRow(row, "Sex:", "${visaRecord.sex}")
        row = createRow(context, table)
        provideTextForRow(row, "Certificate Reference:", "${visaRecord.certificateReference}")
        if (visaRecord.machineReadableVisaTypeA != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Visa Type A:", visaRecord.machineReadableVisaTypeA)
        }
        if (visaRecord.machineReadableVisaTypeB != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Visa Type B:", visaRecord.machineReadableVisaTypeB)
        }
        if (visaRecord.numberOfEntries != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Number of Entries:", "${visaRecord.numberOfEntries}")
        }
        if (visaRecord.stayDuration != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Stay Duration:", "${visaRecord.stayDuration}")
        }
        if (visaRecord.passportNumber != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Passport number:", visaRecord.passportNumber)
        }
        if (visaRecord.visaType != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Visa Type:", "${visaRecord.visaType}")
        }
        if (visaRecord.territoryInformation != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Territory Information:", "${visaRecord.territoryInformation}")
        }
        if (visaRecord.additionalInformation != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Additional Information:", visaRecord.additionalInformation)
        }
        if (visaRecord.additionalBiometricsReference != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Biometric Reference:", "${visaRecord.additionalBiometricsReference}")
        }
        createSignatureView(context, parent, visaRecord.signature)
    }
}