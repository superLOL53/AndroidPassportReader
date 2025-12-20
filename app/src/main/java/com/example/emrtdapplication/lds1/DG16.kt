package com.example.emrtdapplication.lds1

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.Person
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV
import kotlin.experimental.and

class DG16(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x10
    override val efTag: Byte = 0x70
    var persons: Array<Person>? = null
            private set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null) {
            return FAILURE
        }
        val list = ArrayList<Person>()
        for (tag in tlv.list!!.tlvSequence) {
            if (tag.tag.size == 1 && (tag.tag[0] and 0xF0.toByte()) == 0xA0.toByte()) {
                val p = getPerson(tag)
                if (p != null) {
                    list.add(p)
                }
            }
        }
        persons = list.toTypedArray()
        return SUCCESS
    }

    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        if (persons == null) return
        var i = 0
        for (p in persons) {
            val table = TableLayout(context)
            table.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            parent.addView(table)
            var row = TableRow(context)
            row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            table.addView(row)
            val text = TextView(context)
            text.text = "Person $i"
            row.addView(text)
            i++
            row = createRow(context, table)
            provideTextForRow(row, "Name:", p.name)
            row = createRow(context, table)
            provideTextForRow(row, "Address:", p.address)
            row = createRow(context, table)
            provideTextForRow(row, "Telephone:", p.telephone)
            row = createRow(context, table)
            provideTextForRow(row, "Date data recorded:", p.date)
        }
    }

    private fun getPerson(person: TLV) : Person? {
        if (person.list == null || person.list!!.tlvSequence.size != 4) {
            return null
        }
        var dateRecorded: String? = null
        var name: String? = null
        var telephone: String? = null
        var address: String? = null
        for (tag in person.list!!.tlvSequence) {
            if (tag.tag[0] == 0x5F.toByte()) {
                when (tag.tag[1].toInt()) {
                    0x50 -> dateRecorded = tag.value?.decodeToString()
                    0x51 -> name = tag.value?.decodeToString()
                    0x52 -> telephone = tag.value?.decodeToString()
                    0x53 -> address = tag.value?.decodeToString()
                }
            }
        }
        return if (dateRecorded != null && name != null && telephone != null && address!= null) {
            Person(name, address, telephone, dateRecorded)
        } else {
            null
        }
    }
}