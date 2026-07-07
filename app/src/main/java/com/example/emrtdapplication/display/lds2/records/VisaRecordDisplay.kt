package com.example.emrtdapplication.display.lds2.records

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.MRZ_STRING
import com.example.emrtdapplication.lds2.VisaRecord

class VisaRecordDisplay(private val visaRecord: VisaRecord): CreateView() {

    override fun <T: LinearLayout> createView(context: Context, parent: T) {
        createHeader(
            context,
            parent,
            VISA_RECORD + "${visaRecord.recordNumber}"
        )
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(
            row,
            EXPIRATION_DATE,
            visaRecord.expirationDate
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            NATIONALITY,
            visaRecord.nationality
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            STATE,
            visaRecord.state
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            DOCUMENT_NUMBER,
            visaRecord.documentNumber
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            HOLDER_NAME,
            visaRecord.holderName
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            BIRTH_DATE,
            visaRecord.birthDate
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            MRZ_STRING,
            visaRecord.mrz
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            FIRST_NAME,
            visaRecord.givenName
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            SURNAME,
            visaRecord.surname
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            ISSUE_DATE,
            visaRecord.issuanceDate
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            ISSUE_PLACE,
            visaRecord.issuancePlace
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            DOCUMENT_TYPE,
            visaRecord.documentType
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            SEX,
            "${visaRecord.sex}"
        )
        row = createRow(context, table)
        provideTextForRow(
            row,
            CERTIFICATE_REFERENCE,
            "${visaRecord.certificateReference}"
        )
        if (visaRecord.machineReadableVisaTypeA != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                VISA_TYPE_A,
                visaRecord.machineReadableVisaTypeA
            )
        }
        if (visaRecord.machineReadableVisaTypeB != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                VISA_TYPE_B,
                visaRecord.machineReadableVisaTypeB
            )
        }
        if (visaRecord.numberOfEntries != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                NUMBER_OF_ENTRIES,
                "${visaRecord.numberOfEntries}"
            )
        }
        if (visaRecord.stayDurationDays != null ||
            visaRecord.stayDurationMonths != null ||
            visaRecord.stayDurationYears != null
        ) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                STAY_DURATION,
                "${visaRecord.stayDurationYears} Years, " +
                        "${visaRecord.stayDurationMonths} Months, " +
                        "${visaRecord.stayDurationDays} Days"
            )
        }
        if (visaRecord.passportNumber != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                PASSPORT_NUMBER,
                visaRecord.passportNumber
            )
        }
        if (visaRecord.visaType != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                VISA_TYPE,
                "${visaRecord.visaType}"
            )
        }
        if (visaRecord.territoryInformation != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                TERRITORY_INFORMATION,
                "${visaRecord.territoryInformation}"
            )
        }
        if (visaRecord.additionalInformation != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                ADDITIONAL_INFORMATION,
                visaRecord.additionalInformation
            )
        }
        if (visaRecord.additionalBiometricsReference != null) {
            row = createRow(context, table)
            provideTextForRow(
                row,
                BIOMETRIC_REFERENCE,
                "${visaRecord.additionalBiometricsReference}"
            )
        }
        createSignatureView(context, parent, visaRecord.signature)
    }
}