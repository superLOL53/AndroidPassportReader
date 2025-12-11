package com.example.emrtdapplication.lds1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.compose.material3.TabRow
import androidx.compose.ui.graphics.vector.Group
import androidx.core.view.children
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

class DG11(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0B
    override val EFTag: Byte = 0x6B
    var fullName : String? = null
        private set
    var personalNumber : String? = null
        private set
    var fullDateOfBirth : String? = null
        private set
    var placeOfBirth : String? = null
        private set
    var permanentAddress : String? = null
        private set
    var telephone : String? = null
        private set
    var profession : String? = null
        private set
    var title : String? = null
        private set
    var personalSummary : String? = null
        private set
    var custodyInformation : String? = null
        private set
    var otherTDNumbers : Array<String>? = null
        private set
    var image : Bitmap? = null
        private set
    var otherNames : Array<String>? = null
        private set

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.getTag().size != 1 || tlv.getTag()[0] != EFTag ||
            tlv.getTLVSequence() == null) {
            return FAILURE
        }
        for (tag in tlv.getTLVSequence()!!.getTLVSequence()) {
            if (tag.getTag().size == 2) {
                if (tag.getTag()[0] == 0x5F.toByte()) {
                    when (tag.getTag()[1].toInt()) {
                        0x0E -> fullName = tag.getValue()?.decodeToString()?.replace('<', ' ')
                        0x10 -> personalNumber = tag.getValue()?.decodeToString()
                        0x2B -> fullDateOfBirth = tag.getValue()?.decodeToString()
                        0x11 -> placeOfBirth = tag.getValue()?.decodeToString()?.replace('<', ' ')
                        0x42 -> permanentAddress = tag.getValue()?.decodeToString()?.replace('<', ' ')
                        0x12 -> telephone = tag.getValue()?.decodeToString()
                        0x13 -> profession = tag.getValue()?.decodeToString()
                        0x14 -> title = tag.getValue()?.decodeToString()
                        0x15 -> personalSummary = tag.getValue()?.decodeToString()
                        0x16 -> decodeImage(tag)
                        0x17 -> decodeDocumentNumbers(tag)
                        0x18 -> custodyInformation = tag.getValue()?.decodeToString()
                    }
                }
            } else if (tag.getTag().size ==1) {
                if (tag.getTag()[0] == 0xA0.toByte()) {
                    readNames(tag)
                }
            }
        }
        return SUCCESS
    }

    fun createViews(parent : TableLayout, context: Context) {
        var row : TableRow
        if (fullName != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Full Name: ", fullName!!)
        }
        if (otherNames != null) {
            for (s in otherNames) {
                row = createRow(context, parent)
                provideTextForRow(row, "Other Name: ", s)
            }
        }
        if (personalNumber != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Personal Number: ", personalNumber!!)
        }
        if (fullDateOfBirth != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Full date of birth: ", fullDateOfBirth!!)
        }
        if (placeOfBirth != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Place of birth: ", placeOfBirth!!)
        }
        if (permanentAddress != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Address: ", permanentAddress!!)
        }
        if (telephone != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Telephone: ", telephone!!)
        }
        if (profession != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Profession: ", profession!!)
        }
        if (title != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Title: ", title!!)
        }
        if (personalSummary != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Summary: ", personalSummary!!)
        }
        if (otherTDNumbers != null) {
            for (td in otherTDNumbers) {
                row = createRow(context, parent)
                provideTextForRow(row, "Other TD number: ", td)
            }
        }
        if (custodyInformation != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Custody information: ", custodyInformation!!)
        }
        if (image != null) {
            val imageView = ImageView(context)
            imageView.setImageBitmap(image)
            row = TableRow(context)
            row.addView(imageView)
            row.gravity = Gravity.CENTER
            parent.addView(row)
        }
    }

    private fun readNames(names : TLV) {
        if (names.getTLVSequence() == null || names.getTLVSequence()!!.getTLVSequence().size < 2) {
            return
        }
        val list = ArrayList<String>()
        val tlv = names.getTLVSequence()!!.getTLVSequence()
        for (i in 1..<tlv.size) {
            if (tlv[i].getValue() != null) {
                list.add(tlv[i].getValue()!!.decodeToString().replace('<', ' '))
            }
        }
        otherNames = list.toTypedArray()
    }

    private fun decodeImage(image : TLV) {
        if (image.getValue() == null) return
        this.image = BitmapFactory.decodeByteArray(image.getValue()!!, 0, image.getValue()!!.size)
    }

    private fun decodeDocumentNumbers(numbers: TLV) {
        if (numbers.getValue() == null || numbers.getValue()!!.isEmpty()) {
            return
        }
        otherTDNumbers = numbers.getValue()!!.decodeToString().split('<').toTypedArray()
    }
}