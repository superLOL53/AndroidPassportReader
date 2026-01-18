package com.example.emrtdapplication

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import com.example.emrtdapplication.constants.SecurityInfoConstants.ACTIVE_AUTHENTICATION_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.ACTIVE_AUTHENTICATION_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.EF_DIR_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.EF_DIR_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_DOMAIN_PARAMETER_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_DOMAIN_PARAMETER_INFO_TYPE_SIZE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_INFO_TYPE_SIZE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.TERMINAL_AUTHENTICATION_OID
import com.example.emrtdapplication.constants.SecurityInfoConstants.TERMINAL_AUTHENTICATION_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.UNKNOWN_TYPE
import com.example.emrtdapplication.utils.TLV
import org.spongycastle.asn1.ASN1ObjectIdentifier

/**
 * Class representing the ASN1 Sequence SecurityInfo:
 *
 *      SecurityInfo ::= SEQUENCE {
 *          protocol    OBJECT IDENTIFIER
 *          requiredData ANY DEFINED BY protocol
 *          optionalData ANY DEFINED BY protocol OPTIONAL
 *      }
 *
 * The following security infos are supported in this project:
 * - PACEInfo
 * - PACEDomainParameterInfo
 * - ActiveAuthenticationInfo
 * - ChipAuthenticationInfo
 * - ChipAuthenticationPublicKeyInfo
 * - TerminalAuthenticationInfo
 *
 * @param tlv The [TLV] Structure representing an ASN1 SecurityInfo Sequence
 * @property objectIdentifier The Object Identifier of the protocol as a [String]
 * @property requiredData The required data of the protocol as a [TLV] Structure.
 * @property optionalData Optional data of the protocol as a [TLV] Structure. Can be null.
 * @property type The type of an instance of the class. Can be either unknown for unsupported protocols or one of the supported protocols
 * @property protocol The [objectIdentifier] represented as a [ByteArray]
 * @throws IllegalArgumentException If [tlv] does not contain any of the subclasses of a SecurityInfo
 */
open class SecurityInfo(tlv: TLV) {
    var objectIdentifier : String
        private set
    var requiredData : TLV
        private set
    var optionalData : TLV? = null
        private set
    var type : Int = UNKNOWN_TYPE
        private set
    var protocol : ByteArray
        private set
    protected var tableLayout : TableLayout? = null

    init {
        if (!tlv.isValid || !tlv.isConstruct() || tlv.list == null || tlv.list!!.tlvSequence.size < 2 || 3 < tlv.list!!.tlvSequence.size) {
            throw IllegalArgumentException("Invalid Sequence for type SecurityInfo")
        }
        try {
            objectIdentifier =
                ASN1ObjectIdentifier.getInstance(tlv.list!!.tlvSequence[0].toByteArray()).id
        } catch (_ : Exception) {
            throw IllegalArgumentException("Unable to decode object identifier!")
        }
        requiredData = tlv.list!!.tlvSequence[1]
        protocol = tlv.list!!.tlvSequence[0].value!!
        if (tlv.list!!.tlvSequence.size == 3) {
            optionalData = tlv.list!!.tlvSequence[2]
            if (!optionalData!!.isValid) {
                throw IllegalArgumentException("Invalid present optional data for type SecurityInfo")
            }
        }
        if (objectIdentifier.startsWith(PACE_OID)) {
            objectIdentifier.split(".").size
            if (objectIdentifier.split(".").size == PACE_DOMAIN_PARAMETER_INFO_TYPE_SIZE) {
                type = PACE_DOMAIN_PARAMETER_INFO_TYPE
            } else if (objectIdentifier.split(".").size == PACE_INFO_TYPE_SIZE) {
                type = PACE_INFO_TYPE
            }
        } else if (objectIdentifier.startsWith(ACTIVE_AUTHENTICATION_OID)) {
            type = ACTIVE_AUTHENTICATION_TYPE
        } else if (objectIdentifier.startsWith(CHIP_AUTHENTICATION_OID)) {
            type = CHIP_AUTHENTICATION_TYPE
        } else if (objectIdentifier.startsWith(CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID)) {
            type = CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
        } else if (objectIdentifier.startsWith(TERMINAL_AUTHENTICATION_OID)) {
            type = TERMINAL_AUTHENTICATION_TYPE
        } else if (objectIdentifier.startsWith(EF_DIR_OID)) {
            type = EF_DIR_TYPE
        }
    }

    /**
     * Create views to display contents of the file in the app
     */
    open fun <T : LinearLayout> createView(context: Context, parent : T) {
        tableLayout = TableLayout(context)
        tableLayout!!.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        val row = createRow(context, tableLayout!!)
        provideTextForRow(row, "Protocol OID:", objectIdentifier)
        tableLayout!!.addView(row)
        parent.addView(tableLayout)
    }

    /**
     * Fill in the text for [row] with [description] and [value]
     * @param row The row to fill in texts
     * @param description The meaning of the [value]
     * @param value The value of the row
     */
    protected fun provideTextForRow(row : TableRow, description : String, value : String) {
        var i = true
        for (t in row.children) {
            if (i) {
                (t as TextView).text = description
            } else {
                (t as TextView).text = value
            }
            i = !i
        }
    }

    /**
     * Creates a row in a [TableLayout] to display information in the file
     * @param context The context of the view
     * @param parent The parent layout of the created row
     * @return The created row
     */
    protected fun createRow(context : Context, parent: LinearLayout) : TableRow {
        val row = TableRow(context)
        row.gravity = Gravity.CENTER
        val description = TextView(context)
        description.gravity = Gravity.CENTER
        val value = TextView(context)
        value.gravity = Gravity.CENTER
        row.addView(description)
        row.addView(value)
        parent.addView(row)
        return row
    }
}