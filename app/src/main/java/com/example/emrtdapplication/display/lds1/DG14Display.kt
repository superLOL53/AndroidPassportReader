package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R
import com.example.emrtdapplication.common.ActiveAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationInfo
import com.example.emrtdapplication.common.ChipAuthenticationPublicKeyInfo
import com.example.emrtdapplication.common.EFDIRInfo
import com.example.emrtdapplication.common.PACEDomainParameterInfo
import com.example.emrtdapplication.common.PACEInfo
import com.example.emrtdapplication.common.TerminalAuthenticationInfo
import com.example.emrtdapplication.constants.SecurityInfoConstants.ACTIVE_AUTHENTICATION_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.CHIP_AUTHENTICATION_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.EF_DIR_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_DOMAIN_PARAMETER_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.PACE_INFO_TYPE
import com.example.emrtdapplication.constants.SecurityInfoConstants.TERMINAL_AUTHENTICATION_TYPE

object DG14Display : CreateView() {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg14.securityInfos == null) return
        if (!EMRTD.showDetails) return
        for (si in EMRTD.ldS1Application.dg14.securityInfos) {
            when (si.type) {
                PACE_INFO_TYPE -> displayPIType(context, parent, si as PACEInfo)
                PACE_DOMAIN_PARAMETER_INFO_TYPE -> displayPDType(context, parent, si as PACEDomainParameterInfo)
                ACTIVE_AUTHENTICATION_TYPE -> displayAAType(context, parent, si as ActiveAuthenticationInfo)
                CHIP_AUTHENTICATION_TYPE -> displayCAType(context, parent, si as ChipAuthenticationInfo)
                CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE -> displayCAPKIType(context, parent, si as ChipAuthenticationPublicKeyInfo)
                TERMINAL_AUTHENTICATION_TYPE -> displayTAType(context, parent, si as TerminalAuthenticationInfo)
                EF_DIR_TYPE -> displayEDIType(context, parent, si as EFDIRInfo)
            }
        }
    }

    private fun <T : LinearLayout> displayPIType(context: Context, parent: T, paceInfo: PACEInfo) {
        createHeader(context, parent, context.getString(R.string.pace_protocol_information))
        val table = createTable(context, parent)
        var row = createRow(context, table)
        provideTextForRow(row, "Protocol ID:", paceInfo.objectIdentifier)
        row = createRow(context, parent)
        provideTextForRow(row, "Version:", paceInfo.version.toString(10))
        row = createRow(context, table)
        provideTextForRow(row, "Asymmetric protocol:", decodeAsymmetricProtocol(paceInfo.asymmetricProtocol))
        row = createRow(context, table)
        provideTextForRow(row, "Symmetric protocol:", decodeSymmetricProtocol(paceInfo.symmetricProtocol))
        if (paceInfo.parameterId != null) {
            row = createRow(context, table)
            provideTextForRow(row, "Domain Parameter ID:", paceInfo.parameterId!!.toInt().toChar().toString())
            row = createRow(context, table)
            provideTextForRow(row, "Domain Parameters:", decodeParameters(paceInfo.parameterId!!))
        }
    }

    private fun decodeSymmetricProtocol(byte: Byte) : String {
        return when(byte) {
            1.toByte() -> "3DES-CBC-CBC"
            2.toByte() -> "AES-CBC-CMAC-128"
            3.toByte() -> "AES-CBC-CMAC-192"
            4.toByte() -> "AES-CBC-CMAC-256"
            else -> "Unknown"
        }
    }

    private fun decodeAsymmetricProtocol(byte: Byte) : String {
        return when(byte) {
            1.toByte() -> "PACE-DH-GM"
            2.toByte() -> "PACE-ECDH-GM"
            3.toByte() -> "PACE-DH-IM"
            4.toByte() -> "PACE-ECDH-IM"
            6.toByte() -> "PACE-ECDH-CAM"
            else -> "Unknown"
        }
    }

    private fun decodeParameters(byte: Byte) : String {
        return when(byte) {
            0.toByte() -> "1024-bit MODP Group with 160-bit Prime Order Subgroup"
            1.toByte() -> "2048-bit MODP Group with 224-bit Prime Order Subgroup"
            2.toByte() -> "2048-bit MODP Group with 256-bit Prime Order Subgroup"
            8.toByte() -> "NIST P-192 (secp192r1)"
            9.toByte() -> "BrainpoolP192r1"
            10.toByte() -> "NIST P-224 (secp224r1)"
            11.toByte() -> "BrainpoolP224r1"
            12.toByte() -> "NIST P-256 (secp256r1)"
            13.toByte() -> "BrainpoolP256r1"
            14.toByte() -> "BrainpoolP320r1"
            15.toByte() -> "NIST P-384 (secp384r1)"
            16.toByte() -> "BrainpoolP384r1"
            17.toByte() -> "BrainpoolP512r1"
            18.toByte() -> "NIST P-521 (secp521r1)"
            else -> "Unknown"
        }
    }

    private fun <T : LinearLayout> displayPDType(context: Context, parent: T, paceDomainParameterInfo: PACEDomainParameterInfo) {
        //TODO: Display information
        createHeader(context, parent, "Domain Parameters")
    }

    private fun <T : LinearLayout> displayAAType(context: Context, parent: T,activeAuthenticationInfo: ActiveAuthenticationInfo) {
        createHeader(context, parent, "Active Authentication Information")
        var row = createRow(context, parent)
        provideTextForRow(row, "Protocol ID:", activeAuthenticationInfo.objectIdentifier)
        row = createRow(context, parent)
        provideTextForRow(row, "Version:", activeAuthenticationInfo.version.toString(10))
        row = createRow(context, parent)
        provideTextForRow(row, "Signature Algorithm ID:", activeAuthenticationInfo.signatureAlgorithm)
    }

    private fun <T : LinearLayout> displayCAType(context: Context, parent: T, chipAuthenticationInfo: ChipAuthenticationInfo) {
        createHeader(context, parent, "Chip Authentication Information")
        var row = createRow(context, parent)
        provideTextForRow(row, "Protocol ID:", chipAuthenticationInfo.objectIdentifier)
        row = createRow(context, parent)
        provideTextForRow(row, "Version:", chipAuthenticationInfo.version.toString(10))
        if (chipAuthenticationInfo.keyId != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Key ID:", chipAuthenticationInfo.keyId!!.toString(10))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun <T : LinearLayout> displayCAPKIType(context: Context, parent: T, chipAuthenticationPublicKeyInfo: ChipAuthenticationPublicKeyInfo) {
        createHeader(context, parent, "Chip Authentication Public Key Information")
        var row = createRow(context, parent)
        provideTextForRow(row, "Protocol ID:", chipAuthenticationPublicKeyInfo.objectIdentifier)
        row = createRow(context, parent)
        provideTextForRow(row, "Public Key Algorithm ID:", chipAuthenticationPublicKeyInfo.publicKeyInfo.algorithm.algorithm.id)
        row = createRow(context, parent)
        provideTextForRow(row, "Public Key:", chipAuthenticationPublicKeyInfo.publicKeyInfo.publicKeyData.bytes.toHexString(HexFormat { upperCase = true;bytes.byteSeparator=" " }))
        if (chipAuthenticationPublicKeyInfo.keyId != null) {
            row = createRow(context, parent)
            provideTextForRow(row, "Key ID:", chipAuthenticationPublicKeyInfo.keyId!!.toString())
        }
    }

    private fun <T : LinearLayout> displayTAType(context: Context, parent: T, terminalAuthenticationInfo: TerminalAuthenticationInfo) {
        createHeader(context, parent, "Terminal Authentication Information")
        var row = createRow(context, parent)
        provideTextForRow(row, "Protocol ID:", terminalAuthenticationInfo.objectIdentifier)
        row = createRow(context, parent)
        provideTextForRow(row, "Protocol version:", terminalAuthenticationInfo.version.toString())
    }

    private fun <T : LinearLayout> displayEDIType(context: Context, parent: T, efDirInfo: EFDIRInfo) {
        createHeader(context, parent, "EF DIR Information")
        var row = createRow(context, parent)
        var status = if (efDirInfo.efDir.hasVisaRecordsApplication) {
            "Present"
        } else {
            "Absent"
        }
        provideTextForRow(row, "Visa Records Application:", status)
        row = createRow(context, parent)
        status = if (efDirInfo.efDir.hasTravelRecordsApplication) {
            "Present"
        } else {
            "Absent"
        }
        provideTextForRow(row, "Travel Records Application:", status)
        row = createRow(context, parent)
        status = if (efDirInfo.efDir.hasAdditionalBiometricsApplication) {
            "Present"
        } else {
            "Absent"
        }
        provideTextForRow(row, "Additional Biometrics Application:", status)
    }

    private fun <T : LinearLayout> createTable(context: Context, parent: T) : TableLayout {TableLayout(context)
        val table = TableLayout(context)
        table.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
        table.isStretchAllColumns = true
        parent.addView(table)
        return table
    }

    private fun <T : LinearLayout> createHeader(context: Context, parent: T, headerLine : String) {
        val text = TextView(context)
        text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        text.gravity = Gravity.CENTER
        text.text = headerLine
        if (alternate) {
            text.setBackgroundColor(context.resources.getColor(R.color.gray, null))
        } else {
            text.setBackgroundColor(context.resources.getColor(R.color.black, null))
        }
        alternate = !alternate
        parent.addView(text)
    }
}