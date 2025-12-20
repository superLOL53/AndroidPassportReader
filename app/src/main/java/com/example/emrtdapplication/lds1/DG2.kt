package com.example.emrtdapplication.lds1

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.BiometricInformationGroupTemplate
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

class DG2(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x02
    override val efTag: Byte = 0x75
    var biometricInformation : BiometricInformationGroupTemplate? = null
        private set


    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        var tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null || tlv.list!!.tlvSequence.size != 1) {
            return FAILURE
        }
        tlv = tlv.list!!.tlvSequence[0]
        biometricInformation = BiometricInformationGroupTemplate(tlv)
        return SUCCESS
    }

    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        if (biometricInformation != null && biometricInformation!!.biometricInformations != null) {
            for (bios in biometricInformation!!.biometricInformations) {
                if (bios == null) continue
                val image = bios.biometricDataBlock.facialRecordData.image
                val view = ImageView(context)
                view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                view.setImageBitmap(image)
                parent.addView(view)
                val facialInfo = bios.biometricDataBlock.facialRecordData.facialInformation
                val table = TableLayout(context)
                parent.addView(table)
                var row = createRow(context, table)
                provideTextForRow(row, "Eye color: ", facialInfo.eyeColor)
                row = createRow(context, table)
                provideTextForRow(row, "Hair color: ", facialInfo.hairColor)
                row = createRow(context, table)
                provideTextForRow(row, "Gender: ", facialInfo.gender)
                row = createRow(context, table)
                provideTextForRow(row, "Expression:", facialInfo.expression)
            }
        }
    }

}