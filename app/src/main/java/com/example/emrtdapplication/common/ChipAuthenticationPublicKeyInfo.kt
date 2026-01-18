package com.example.emrtdapplication.common

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.example.emrtdapplication.R
import com.example.emrtdapplication.SecurityInfo
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import java.math.BigInteger

/**
 * Inherits from [SecurityInfo] and implements the ASN1 Sequence ChipAuthenticationPublicKeyInfo:
 *
 *      ChipAuthenticationInfo ::= SEQUENCE {
 *          protocol OBJECT IDENTIFIER(id-PK-DH | id-PK-ECDH),
 *          chipAuthenticationPublicKey SubjectPublicKeyInfo,
 *          keyId INTEGER OPTIONAL
 *      }
 *
 * @param tlv TLV structure containing an encoded instance of ChipAuthenticationPublicKeyInfo
 * @property publicKeyInfo The public key encoded as [SubjectPublicKeyInfo]
 * @property keyId Id of the public key if multiple public keys are present
 * @throws IllegalArgumentException If [tlv] does not contain an instance of ChipAuthenticationPublicKeyInfo
 */
class ChipAuthenticationPublicKeyInfo(tlv: TLV) : SecurityInfo(tlv, CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE) {
    var publicKeyInfo : SubjectPublicKeyInfo
        private set
    var keyId : BigInteger?
        private set

    init {
        try {
            publicKeyInfo = SubjectPublicKeyInfo.getInstance(requiredData.toByteArray())
        } catch (_ : Exception) {
            throw IllegalArgumentException("Required data does not contain a SubjectPublicKeyInfo for ChipAuthenticationPublicKeyInfo!")
        }
        keyId = if (optionalData != null) {
            if (optionalData.tag.size != 1 || optionalData.tag[0] != TlvTags.INTEGER ||
                optionalData.value == null) {
                throw IllegalArgumentException("Invalid key identifier for ChipAuthenticationPublicKeyInfo!")
            } else {
                BigInteger(optionalData.value!!)
            }
        } else {
            null
        }
    }

    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        super.createViews(context, parent)
        if (tableLayout != null) {
            var row = createRow(context, parent)
            provideTextForRow(row, "Public key algorithm OID:", publicKeyInfo.algorithm.algorithm.id)
            tableLayout!!.addView(row)
            if (keyId != null) {
                row = createRow(context, parent)
                provideTextForRow(row, "Key identifier:", keyId!!.toString(16))
                tableLayout!!.addView(row)
            }
        }
        var text = TextView(context)
        text.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        text.gravity = Gravity.CENTER
        text.text = context.getString(R.string.public_key)
        parent.addView(text)
        text = TextView(context)
        text.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        text.gravity = Gravity.CENTER
        text.isSingleLine = false
        text.text = publicKeyInfo.publicKeyData.toString()
        parent.addView(text)
    }
}