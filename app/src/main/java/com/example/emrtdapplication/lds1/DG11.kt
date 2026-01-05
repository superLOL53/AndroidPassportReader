package com.example.emrtdapplication.lds1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV


/**
 * Implements the DG2 file and inherits from [ElementaryFileTemplate]
 *
 * @property apduControl Class for communicating with the eMRTD
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG2
 * @property efTag The tag of the DG2 file
 * @property fullName Full name of document holder in national characters
 * @property personalNumber
 * @property fullDateOfBirth
 * @property placeOfBirth
 * @property permanentAddress
 * @property telephone
 * @property profession
 * @property title
 * @property personalSummary
 * @property custodyInformation
 * @property otherTDNumbers Other valid document numbers of other valid travel documents
 * @property image Image of citizenship document
 * @property otherNames Other names of the document holder
 */
class DG11(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0B
    override val efTag: Byte = 0x6B
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

    /**
     * Parses the contents of [rawFileContent]
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        val tlv = TLV(rawFileContent!!)
        if (tlv.tag.size != 1 || tlv.tag[0] != efTag ||
            tlv.list == null) {
            return FAILURE
        }
        for (tag in tlv.list!!.tlvSequence) {
            if (tag.tag.size == 2) {
                if (tag.tag[0] == 0x5F.toByte()) {
                    when (tag.tag[1].toInt()) {
                        0x0E -> fullName = tag.value?.decodeToString()?.replace('<', ' ')
                        0x10 -> personalNumber = tag.value?.decodeToString()
                        0x2B -> fullDateOfBirth = tag.value?.decodeToString()
                        0x11 -> placeOfBirth = tag.value?.decodeToString()?.replace('<', ' ')
                        0x42 -> permanentAddress = tag.value?.decodeToString()?.replace('<', ' ')
                        0x12 -> telephone = tag.value?.decodeToString()
                        0x13 -> profession = tag.value?.decodeToString()
                        0x14 -> title = tag.value?.decodeToString()
                        0x15 -> personalSummary = tag.value?.decodeToString()
                        0x16 -> decodeImage(tag)
                        0x17 -> decodeDocumentNumbers(tag)
                        0x18 -> custodyInformation = tag.value?.decodeToString()
                    }
                }
            } else if (tag.tag.size ==1) {
                if (tag.tag[0] == 0xA0.toByte()) {
                    readNames(tag)
                }
            }
        }
        return SUCCESS
    }

    /**
     * Dynamically create a view for every biometric information in this file.
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
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

    /**
     * Decodes a TLV structure into names
     * @param names A TLV structure containing additional names of the document holder
     */
    private fun readNames(names : TLV) {
        if (names.list == null || names.list!!.tlvSequence.size < 2) {
            return
        }
        val list = ArrayList<String>()
        val tlv = names.list!!.tlvSequence
        for (i in 1..<tlv.size) {
            if (tlv[i].value != null) {
                list.add(tlv[i].value!!.decodeToString().replace('<', ' '))
            }
        }
        otherNames = list.toTypedArray()
    }

    /**
     * Decodes an image contained in a TLV structure
     * @param image A TLV structure containing an image
     */
    private fun decodeImage(image : TLV) {
        if (image.value == null) return
        this.image = BitmapFactory.decodeByteArray(image.value!!, 0, image.value!!.size)
    }

    /**
     * Decodes the TLV structure into document numbers
     * @param numbers A TLV structure containing document numbers of other valid travel documents
     */
    private fun decodeDocumentNumbers(numbers: TLV) {
        if (numbers.value == null || numbers.value!!.isEmpty()) {
            return
        }
        otherTDNumbers = numbers.value!!.decodeToString().split('<').toTypedArray()
    }
}