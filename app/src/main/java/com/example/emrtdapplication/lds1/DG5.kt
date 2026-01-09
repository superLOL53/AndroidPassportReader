package com.example.emrtdapplication.lds1

import android.content.Context
import android.graphics.text.LineBreaker
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.DisplayedPortrait
import com.example.emrtdapplication.utils.ElementaryFilesToBeDefined
import com.example.emrtdapplication.utils.TLV


/**
 * Implements the DG2 file and inherits from [ElementaryFileTemplate]
 *
 * @property apduControl Class for communicating with the eMRTD
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG2
 * @property efTag The tag of the DG2 file
 *
 */
class DG5(apduControl: APDUControl) : ElementaryFilesToBeDefined<DisplayedPortrait>(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x05
    override val efTag: Byte = 0x65

    /**
     * Dynamically create a view for every biometric information in this file.
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    @OptIn(ExperimentalStdlibApi::class)
    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        if (rawFileContent == null) return
        val view = TextView(context)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        view.maxLines = 10
        view.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        view.ellipsize = TextUtils.TruncateAt.END
        view.text = rawFileContent!!.toHexString(HexFormat { upperCase = true; bytes.byteSeparator = " "})
        parent.addView(view)
    }

    override fun add(tlv: TLV, list: ArrayList<DisplayedPortrait>) {
        list.add(DisplayedPortrait(tlv))
    }

    override fun toTypedArray(list: ArrayList<DisplayedPortrait>) {
        tlvS = list.toTypedArray()
    }
}