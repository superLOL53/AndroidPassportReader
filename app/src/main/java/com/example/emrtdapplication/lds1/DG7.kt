package com.example.emrtdapplication.lds1

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.DisplayedSignature
import com.example.emrtdapplication.utils.ElementaryFilesTypeTemplate
import com.example.emrtdapplication.utils.TLV


/**
 * Implements the DG2 file and inherits from [ElementaryFileTemplate]
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG2
 * @property efTag The tag of the DG2 file
 *
 */
class DG7() : ElementaryFilesTypeTemplate<DisplayedSignature>() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x07
    override val efTag: Byte = 0x67

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        //TODO: Implement
    }

    override fun toTypedArray(list: ArrayList<DisplayedSignature>) {
        tlvS = list.toTypedArray()
    }

    override fun add(tlv: TLV, list: ArrayList<DisplayedSignature>) {
        list.add(DisplayedSignature(tlv))
    }
}